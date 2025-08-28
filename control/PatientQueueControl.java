package control;

import entity.*;
import adt.*;
import dao.PatientQueueDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Control class for patient queue management
 * @author Your Name
 */
public class PatientQueueControl {
    private HashMapInterface<String, PatientQueueEntry> queueMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private HashMapInterface<String, Patient> patientMap;
    private ConsultationControl consultationControl;
    private Scanner sc;

    public PatientQueueControl(ConsultationControl consultationControl) {
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.patientMap = PatientDAO.loadPatients();
        this.consultationControl = consultationControl;
        this.sc = new Scanner(System.in);
    }

    // === NEW BUSINESS METHODS ===

    /**
     * Add walk-in patient to queue
     */
    public void addWalkIn(String patientId, String specialty) {
        String queueId = PatientQueueDAO.generateQueueId();
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, 
                                                        QueueType.WALK_IN, LocalDateTime.now());
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);
        System.out.println("✅ Walk-in patient added to queue: " + queueId);
    }

    /**
     * Check-in appointment patient from schedule
     */
    public void checkInAppointment(String scheduleId, DoctorScheduleControl scheduleControl) {
        // Get schedule info
        entity.DoctorSchedule schedule = scheduleControl.getScheduleById(scheduleId);
        if (schedule == null) {
            System.out.println("❌ Schedule not found: " + scheduleId);
            return;
        }
        
        if (!schedule.getAppointmentDate().equals(java.time.LocalDate.now())) {
            System.out.println("❌ This appointment is not for today");
            return;
        }

        if (!schedule.isBooked()) {
            System.out.println("❌ This slot is not booked");
            return;
        }

        // Create queue entry
        String queueId = PatientQueueDAO.generateQueueId();
        PatientQueueEntry entry = new PatientQueueEntry(queueId, schedule.getPatientId(), 
                                                        schedule.getSpecialty(), QueueType.APPOINTMENT, 
                                                        LocalDateTime.now());
        
        // Set scheduled start time from appointment
        java.time.LocalDateTime startDateTime = java.time.LocalDateTime.of(
            schedule.getAppointmentDate(), schedule.getStartTime());
        entry.setScheduledStartTime(startDateTime);
        
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);
        
        // Mark schedule as checked in
        scheduleControl.markCheckedIn(scheduleId);
        
        System.out.println("✅ Appointment patient checked in: " + queueId);
    }

    /**
     * Assign doctor to waiting patient
     */
    public void assignPatientToDoctor(String queueId, String doctorId) {
        PatientQueueEntry entry = queueMap.get(queueId);
        if (entry == null) {
            System.out.println("❌ Queue entry not found: " + queueId);
            return;
        }

        if (entry.getQueueStatus() != QueueStatus.WAITING) {
            System.out.println("❌ Patient is not waiting: " + entry.getQueueStatus());
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("❌ Doctor not found: " + doctorId);
            return;
        }

        // Update queue entry
        entry.assignToDoctor(doctorId);
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);

        // Create consultation
        consultationControl.createConsultationForQueue(queueId);
        
        System.out.println("✅ Patient assigned to Dr. " + doctorId + " - Consultation created");
    }

    /**
     * Get next eligible patient for doctor to call
     */
    public PatientQueueEntry getNextEligiblePatient() {
        ListInterface<PatientQueueEntry> sortedQueue = getSortedQueue();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < sortedQueue.size(); i++) {
            PatientQueueEntry entry = sortedQueue.get(i);
            if (entry.getQueueStatus() == QueueStatus.WAITING) {
                if (entry.getQueueType() == QueueType.WALK_IN) {
                    return entry; // Walk-ins always eligible
                }
                if (entry.getQueueType() == QueueType.APPOINTMENT &&
                    entry.getScheduledStartTime() != null &&
                    !now.isBefore(entry.getScheduledStartTime())) {
                    return entry; // Appointment time has arrived
                }
            }
        }
        return null; // No eligible patients
    }

    /**
     * Get queue sorted by scheduled time then arrival time
     */
    private ListInterface<PatientQueueEntry> getSortedQueue() {
        ListInterface<PatientQueueEntry> queue = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            queue.add(queueMap.get(key));
        }
        
        // Simple bubble sort by scheduled time / arrival time
        for (int i = 0; i < queue.size() - 1; i++) {
            for (int j = 0; j < queue.size() - i - 1; j++) {
                if (shouldSwap(queue.get(j), queue.get(j + 1))) {
                    PatientQueueEntry temp = queue.get(j);
                    queue.set(j, queue.get(j + 1));
                    queue.set(j + 1, temp);
                }
            }
        }
        return queue;
    }

    private boolean shouldSwap(PatientQueueEntry a, PatientQueueEntry b) {
        // Appointments with scheduled times come first, sorted by time
        if (a.getScheduledStartTime() != null && b.getScheduledStartTime() != null) {
            return a.getScheduledStartTime().isAfter(b.getScheduledStartTime());
        }
        // Appointments before walk-ins
        if (a.getScheduledStartTime() != null && b.getScheduledStartTime() == null) {
            return false;
        }
        if (a.getScheduledStartTime() == null && b.getScheduledStartTime() != null) {
            return true;
        }
        // Both walk-ins, sort by arrival time
        return a.getArrivalTime().isAfter(b.getArrivalTime());
    }

    public HashMapInterface<String, PatientQueueEntry> getQueueMap() {
        return queueMap;
    }

    // --- Add patient to queue ---
    public void addPatientToQueue() {
        System.out.println("\n--- Add Patient to Queue ---");
        System.out.println("Type 'exit' at any point to cancel.\n");

        // Show available patients
        System.out.println("Available Patients:");
        displayPatientList();

        // --- Select Patient ID ---
        String patientId = selectPatient();
        if (patientId == null) return;

        // --- Select Specialty ---
        String specialty = selectSpecialty();
        if (specialty == null) return;

        // --- Select Queue Type ---
        QueueType queueType = selectQueueType();
        if (queueType == null) return;

        // --- Create queue entry ---
        String queueId = PatientQueueDAO.generateQueueId();
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, queueType, LocalDateTime.now());
        
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);

        System.out.println("\n✅ Patient added to queue successfully!");
        System.out.println("Queue ID: " + queueId);
        System.out.println("Patient: " + patientId);
        System.out.println("Specialty: " + specialty);
        System.out.println("Type: " + queueType);
        System.out.println("Status: WAITING");
    }

    // --- View current queue ---
    public void viewCurrentQueue() {
        if (queueMap.isEmpty()) {
            System.out.println("\nNo patients in queue.");
            return;
        }

        System.out.println("\n--- Current Patient Queue ---");
        
        // Beautiful table header with assigned doctor column
        String borderLine = "+---------+------------+---------------------------+----------------------+-----------------+-----------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-7s | %-10s | %-25s | %-20s | %-15s | %-15s | %-10s | %-10s |%n",
                "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Status", "Arrival", "Doctor");
        System.out.println(borderLine);

        // Get all queue entries and sort by arrival time
        ListInterface<PatientQueueEntry> entries = queueMap.toList();
        entries.sort((e1, e2) -> e1.getArrivalTime().compareTo(e2.getArrivalTime()));

        for (int i = 0; i < entries.size(); i++) {
            PatientQueueEntry entry = entries.get(i);
            Patient patient = patientMap.get(entry.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown";
            
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String arrivalTime = entry.getArrivalTime().format(timeFormatter);
            
            // Get doctor name if assigned
            String doctorInfo = "N/A";
            if (entry.getAssignedDoctorId() != null) {
                Doctor doctor = doctorMap.get(entry.getAssignedDoctorId());
                if (doctor != null) {
                    doctorInfo = doctor.getName();
                } else {
                    doctorInfo = entry.getAssignedDoctorId(); // Show ID if name not found
                }
            }
            
            System.out.printf("| %-7s | %-10s | %-25s | %-20s | %-15s | %-15s | %-10s | %-10s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    entry.getQueueStatus(),
                    arrivalTime,
                    doctorInfo);
            System.out.println(borderLine);
        }
    }

    // --- Assign doctor to patient ---
    public void assignDoctorToPatient() {
        System.out.println("\n--- Assign Doctor to Patient ---");
        
        // Show waiting patients
        ListInterface<PatientQueueEntry> waitingPatients = getWaitingPatients();
        if (waitingPatients.isEmpty()) {
            System.out.println("No patients waiting for doctor assignment.");
            return;
        }

        System.out.println("\nPatients waiting for doctor assignment:");
        displayWaitingPatients(waitingPatients);

        // --- Select queue entry ---
        String queueId = selectQueueEntry(waitingPatients);
        if (queueId == null) return;

        PatientQueueEntry entry = queueMap.get(queueId);
        String specialty = entry.getSpecialty();

        // Check if this is an appointment patient
        if (entry.getQueueType() == QueueType.APPOINTMENT) {
            System.out.println("📅 This is an appointment patient. They should already be assigned to a doctor.");
            System.out.println("   If no doctor is assigned, please check the appointment booking in Doctor Schedule Management.");
            return;
        }

        // For walk-in patients, proceed with manual assignment
        System.out.println("🚶 This is a walk-in patient. Proceeding with manual doctor assignment...");

        // --- Show available doctors for specialty ---
        ListInterface<Doctor> availableDoctors = getAvailableDoctors(specialty);
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors available for " + specialty + " specialty.");
            return;
        }

        System.out.println("\nAvailable doctors for " + specialty + ":");
        displayDoctorList(availableDoctors);

        // --- Select doctor ---
        String doctorId = selectDoctor(availableDoctors);
        if (doctorId == null) return;

        // --- Assign doctor ---
        entry.assignToDoctor(doctorId);
        PatientQueueDAO.savePatientQueue(queueMap);

        // --- Create consultation automatically ---
        consultationControl.createConsultationForQueue(queueId);

        System.out.println("\n✅ Walk-in patient assigned to doctor successfully!");
        System.out.println("Patient: " + entry.getPatientId());
        System.out.println("Doctor: " + doctorId);
        System.out.println("Specialty: " + specialty);
    }

    // --- Complete patient consultation ---
    public void completePatientConsultation() {
        System.out.println("\n--- Complete Patient Consultation ---");
        
        // Show assigned patients
        ListInterface<PatientQueueEntry> assignedPatients = getAssignedPatients();
        if (assignedPatients.isEmpty()) {
            System.out.println("No patients currently assigned to doctors.");
            return;
        }

        System.out.println("\nPatients assigned to doctors:");
        displayAssignedPatients(assignedPatients);

        // --- Select queue entry ---
        String queueId = selectQueueEntry(assignedPatients);
        if (queueId == null) return;

        PatientQueueEntry entry = queueMap.get(queueId);
        entry.complete();
        PatientQueueDAO.savePatientQueue(queueMap);

        System.out.println("\n✅ Patient consultation completed!");
        System.out.println("Patient: " + entry.getPatientId());
        System.out.println("Doctor: " + entry.getAssignedDoctorId());
        System.out.println("Queue ID: " + entry.getQueueId());
    }

    // --- Helper methods ---
    private void displayPatientList() {
        ListInterface<Patient> patients = patientMap.toList();
        patients.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));

        String borderLine = "+------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s |%n", "PatientID", "Name");
        System.out.println(borderLine);
        
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            if (!p.isDeleted()) {
                System.out.printf("| %-10s | %-25s |%n", p.getPatientId(), p.getName());
            }
        }
        System.out.println(borderLine);
    }

    private String selectPatient() {
        while (true) {
            System.out.print("Enter Patient ID: ");
            String patientId = sc.nextLine().trim();

            if (patientId.equalsIgnoreCase("exit")) {
                System.out.println("Patient selection cancelled.");
                return null;
            }

            Patient patient = patientMap.get(patientId);
            if (patient != null && !patient.isDeleted()) {
                return patientId;
            }

            System.out.println("Invalid Patient ID. Please choose from the list above.");
        }
    }

    private String selectSpecialty() {
        System.out.println("\nAvailable Specialties:");
        Specialty[] specialties = Specialty.values();
        for (int i = 0; i < specialties.length; i++) {
            System.out.println((i + 1) + ". " + specialties[i]);
        }

        while (true) {
            System.out.print("Enter specialty number: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Specialty selection cancelled.");
                return null;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= specialties.length) {
                    return specialties[choice - 1].toString();
                }
            } catch (NumberFormatException e) {
                // Continue to error message
            }
            System.out.println("Invalid choice. Please enter a number between 1 and " + specialties.length);
        }
    }

    private QueueType selectQueueType() {
        System.out.println("\nQueue Type:");
        System.out.println("1. WALK IN");
        System.out.println("2. APPOINTMENT");

        while (true) {
            System.out.print("Enter choice (1 or 2): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Queue type selection cancelled.");
                return null;
            }

            switch (input) {
                case "1": return QueueType.WALK_IN;
                case "2": return QueueType.APPOINTMENT;
                default: System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    private ListInterface<PatientQueueEntry> getWaitingPatients() {
        ListInterface<PatientQueueEntry> waiting = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.isWaiting()) {
                waiting.add(entry);
            }
        }
        return waiting;
    }

    private ListInterface<PatientQueueEntry> getAssignedPatients() {
        ListInterface<PatientQueueEntry> assigned = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.isAssigned()) {
                assigned.add(entry);
            }
        }
        return assigned;
    }

    private void displayWaitingPatients(ListInterface<PatientQueueEntry> waiting) {
        String borderLine = "+--------+------------+---------------------------+----------------+----------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Doctor");
        System.out.println(borderLine);

        for (int i = 0; i < waiting.size(); i++) {
            PatientQueueEntry entry = waiting.get(i);
            Patient patient = patientMap.get(entry.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown";
            
            // Get doctor name if assigned (should be N/A for waiting patients)
            String doctorInfo = "N/A";
            if (entry.getAssignedDoctorId() != null) {
                Doctor doctor = doctorMap.get(entry.getAssignedDoctorId());
                if (doctor != null) {
                    doctorInfo = doctor.getName();
                } else {
                    doctorInfo = entry.getAssignedDoctorId();
                }
            }
            
            System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    doctorInfo);
            System.out.println(borderLine);
        }
    }

    private void displayAssignedPatients(ListInterface<PatientQueueEntry> assigned) {
        String borderLine = "+--------+------------+---------------------------+----------------+----------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Doctor");
        System.out.println(borderLine);

        for (int i = 0; i < assigned.size(); i++) {
            PatientQueueEntry entry = assigned.get(i);
            Patient patient = patientMap.get(entry.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown";
            
            System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    entry.getAssignedDoctorId());
            System.out.println(borderLine);
        }
    }

    private String selectQueueEntry(ListInterface<PatientQueueEntry> entries) {
        while (true) {
            System.out.print("Enter Queue ID: ");
            String queueId = sc.nextLine().trim();

            if (queueId.equalsIgnoreCase("exit")) {
                System.out.println("Queue selection cancelled.");
                return null;
            }

            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getQueueId().equals(queueId)) {
                    return queueId;
                }
            }

            System.out.println("Invalid Queue ID. Please choose from the list above.");
        }
    }

    private ListInterface<Doctor> getAvailableDoctors(String specialty) {
        ListInterface<Doctor> available = new ArrayList<>();
        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted() && doctor.getSpecialty().toString().equals(specialty)) {
                available.add(doctor);
            }
        }
        return available;
    }

    private void displayDoctorList(ListInterface<Doctor> doctors) {
        String borderLine = "+------------+---------------------------+----------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-14s |%n", "DoctorID", "Name", "Specialty");
        System.out.println(borderLine);

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("| %-10s | %-25s | %-14s |%n",
                    doctor.getDoctorId(),
                    doctor.getName(),
                    doctor.getSpecialty());
            System.out.println(borderLine);
        }
    }

    private String selectDoctor(ListInterface<Doctor> doctors) {
        while (true) {
            System.out.print("Enter Doctor ID: ");
            String doctorId = sc.nextLine().trim();

            if (doctorId.equalsIgnoreCase("exit")) {
                System.out.println("Doctor selection cancelled.");
                return null;
            }

            for (int i = 0; i < doctors.size(); i++) {
                if (doctors.get(i).getDoctorId().equals(doctorId)) {
                    return doctorId;
                }
            }

            System.out.println("Invalid Doctor ID. Please choose from the list above.");
        }
    }
}
