package control;

import entity.*;
import adt.*;
import dao.PatientQueueDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class PatientQueueControl {
    private HashMapInterface<String, PatientQueueEntry> queueMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private HashMapInterface<String, Patient> patientMap;
    private Scanner sc;

    public PatientQueueControl() {
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.patientMap = PatientDAO.loadPatients();
        this.sc = new Scanner(System.in);
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
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, queueType);
        
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

        System.out.println("\n✅ Doctor assigned successfully!");
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
