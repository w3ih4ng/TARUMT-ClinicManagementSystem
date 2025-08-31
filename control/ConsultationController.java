package control;

import entity.*;
import adt.*;
import dao.ConsultationDAO;
import dao.PatientQueueDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import dao.DoctorScheduleDAO;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Control class for consultation management
 * @author Your Name
 */
public class ConsultationController {
    private HashMapInterface<String, Consultation> consultationMap;
    private HashMapInterface<String, PatientQueueEntry> queueMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private HashMapInterface<String, Patient> patientMap;
    private HashMapInterface<String, Medicine> medicineMap;
    private HashMapInterface<String, DoctorSchedule> scheduleMap;
    private static int scheduleCounter = 1000; // Start from SCH1000
    private Scanner sc;

    public ConsultationController() {
        this.consultationMap = ConsultationDAO.loadConsultations();
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.patientMap = PatientDAO.loadPatients();
        this.medicineMap = dao.MedicineDAO.loadMedicines();
        this.scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();
        this.sc = new Scanner(System.in);
    }

    // ==================== CONSULTATION CRUD METHODS ====================

    public boolean updateConsultationStatus(String consultationId, String newStatus) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            consultation.setStatus(newStatus);
            ConsultationDAO.saveConsultations(consultationMap);
            return true;
        }
        return false;
    }

    public boolean deleteConsultation(String consultationId) {
        Consultation consultation = consultationMap.remove(consultationId);
        if (consultation != null) {
            ConsultationDAO.saveConsultations(consultationMap);
            return true;
        }
        return false;
    }

    // ==================== GETTER METHODS ====================

    public boolean consultationExists(String consultationId) {
        return consultationMap.containsKey(consultationId);
    }

    public Consultation getConsultation(String consultationId) {
        return consultationMap.get(consultationId);
    }

    // --- Create consultation manually from queue entry ---
    public String createConsultationFromQueue(String queueId) {
        // First try to get from local map
        PatientQueueEntry entry = queueMap.get(queueId);
        
        // If not found locally, refresh from file and try again
        if (entry == null) {
            this.queueMap = PatientQueueDAO.loadPatientQueue();
            entry = queueMap.get(queueId);
        }
        
        if (entry != null && entry.isAssigned() && entry.getAssignedDoctorId() != null) {
            String consultationId = ConsultationDAO.generateConsultationId();
            Consultation consultation = new Consultation(consultationId, entry.getPatientId(), entry.getSpecialty(), queueId);
            
            // Assign doctor and set consultation time
            consultation.assignDoctor(entry.getAssignedDoctorId(), null, LocalDateTime.now());
            
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            // Update queue status to in consultation
            entry.startConsultation();
            queueMap.put(queueId, entry);
            PatientQueueDAO.savePatientQueue(queueMap);
            
            return consultationId;
        }
        return null;
    }

    // --- Get queue entries ready for consultation creation ---
    public ListInterface<PatientQueueEntry> getQueueEntriesReadyForConsultation() {
        // Refresh queue data from file to get latest changes
        this.queueMap = PatientQueueDAO.loadPatientQueue();

        ListInterface<PatientQueueEntry> readyEntries = new ArrayList<PatientQueueEntry>();
        for (int i = 0; i < queueMap.keySet().size(); i++) {
            String key = queueMap.keySet().get(i);
            PatientQueueEntry entry = queueMap.get(key);
            if (entry != null && entry.isAssigned() && entry.getAssignedDoctorId() != null) {
                readyEntries.add(entry);
            }
        }
        return readyEntries;
    }

    // --- Get a specific queue entry ---
    public PatientQueueEntry getQueueEntry(String queueId) {
        // Refresh queue data from file to get latest changes
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        return queueMap.get(queueId);
    }

    // --- Get consultations ready for treatment ---
    public ListInterface<Consultation> getConsultationsReadyForTreatment() {
        // Refresh consultation data from file to get latest status updates
        this.consultationMap = ConsultationDAO.loadConsultations();

        ListInterface<Consultation> readyConsultations = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation consultation = consultationMap.get(key);
            if (consultation != null && consultation.getStatus().equals("IN_PROGRESS")) {
                readyConsultations.add(consultation);
            }
        }
        return readyConsultations;
    }

    // --- Complete consultation with treatment ---
    public void completeConsultation(String consultationId, String diagnosis, double treatmentFee, 
                                   ListInterface<entity.MedicinePrescribed> medicines) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return;
        }

        // Create treatment
        control.TreatmentController treatmentController = new control.TreatmentController();
        String treatmentId = treatmentController.createTreatment(
            consultation.getDoctorId(), 
            consultation.getPatientId(), 
            consultationId, 
            diagnosis, 
            treatmentFee, 
            medicines
        );

        if (treatmentId != null) {
            // Mark consultation as completed
            consultation.completeConsultation(treatmentId);
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            // Update queue status to completed
            if (consultation.getQueueId() != null) {
                // Refresh queue data to get latest changes
                this.queueMap = PatientQueueDAO.loadPatientQueue();

                PatientQueueEntry entry = queueMap.get(consultation.getQueueId());
                if (entry != null) {
                    entry.complete();
                    queueMap.put(consultation.getQueueId(), entry);
                    PatientQueueDAO.savePatientQueue(queueMap);
                }
            }

            System.out.println("Treatment created: " + treatmentId);
            System.out.println("Consultation completed successfully.");
        } else {
            System.out.println("Failed to create treatment record.");
        }
    }

    // --- Get consultations by doctor ---
    public ListInterface<Consultation> getConsultationsByDoctor(String doctorId) {
        ListInterface<Consultation> doctorConsultations = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getDoctorId() != null && c.getDoctorId().equals(doctorId)) {
                doctorConsultations.add(c);
            }
        }
        return doctorConsultations;
    }

    // --- Get pending consultations for a specific doctor ---
    public ListInterface<Consultation> getPendingConsultationsForDoctor(String doctorId) {
        ListInterface<Consultation> pendingConsultations = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            
            if (c != null && c.getDoctorId() != null && c.getDoctorId().equals(doctorId) && 
                c.getStatus().equals("SCHEDULED")) {
                pendingConsultations.add(c);
            }
        }
        
        return pendingConsultations;
    }

    // --- Get all consultations for a specific doctor ---
    public ListInterface<Consultation> getAllConsultationsForDoctor(String doctorId) {
        ListInterface<Consultation> allConsultations = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getDoctorId() != null && c.getDoctorId().equals(doctorId)) {
                allConsultations.add(c);
            }
        }
        return allConsultations;
    }

    // --- Complete consultation for a specific doctor ---
    public boolean completeConsultationForDoctor(String consultationId, String doctorId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null && 
            consultation.getDoctorId() != null && 
            consultation.getDoctorId().equals(doctorId)) {
            
            // Mark consultation as completed
            consultation.setStatus("COMPLETED");
            ConsultationDAO.saveConsultations(consultationMap);
            
            return true;
        }
        return false;
    }
    
    // --- Complete consultation with treatment details ---
    public boolean completeConsultationWithTreatment(String consultationId, String doctorId,
                                                    String diagnosis, double treatmentFee,
                                                    ListInterface<entity.MedicinePrescribed> medicines) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null && 
            consultation.getDoctorId() != null && 
            consultation.getDoctorId().equals(doctorId)) {
            
            // Create treatment record first
            control.TreatmentController treatmentController = new control.TreatmentController();
            String treatmentId = treatmentController.createTreatment(
                doctorId, 
                consultation.getPatientId(), 
                consultationId,
                diagnosis, 
                treatmentFee, 
                medicines
            );
            
            if (treatmentId != null) {
                // Mark consultation as completed
                consultation.completeConsultation(treatmentId);
                consultationMap.put(consultationId, consultation);
                ConsultationDAO.saveConsultations(consultationMap);
                
                // Update queue status to completed
                if (consultation.getQueueId() != null) {
                    // Refresh queue data to get latest changes
                    this.queueMap = PatientQueueDAO.loadPatientQueue();

                    PatientQueueEntry entry = queueMap.get(consultation.getQueueId());
                    if (entry != null) {
                        entry.complete();
                        queueMap.put(consultation.getQueueId(), entry);
                        PatientQueueDAO.savePatientQueue(queueMap);
                    }
                }
                
                return true;
            }
        }
        return false;
    }
    


    // --- View consultations ready for treatment ---
    public void viewConsultationsReadyForTreatment() {
        ListInterface<Consultation> readyConsultations = getConsultationsReadyForTreatment();
        
        if (readyConsultations.isEmpty()) {
            System.out.println("No consultations ready for treatment.");
            return;
        }
        
        System.out.println("\n=== CONSULTATIONS READY FOR TREATMENT ===");
        System.out.printf("%-15s %-10s %-10s %-20s %-15s%n", 
            "Consultation ID", "Patient ID", "Doctor ID", "Specialty", "Status");
        System.out.println("-".repeat(80));

        for (Consultation consultation : readyConsultations) {
            System.out.printf("%-15s %-10s %-10s %-20s %-15s%n",
                consultation.getConsultationId(),
                consultation.getPatientId(),
                consultation.getDoctorId(),
                consultation.getSpecialty(),
                consultation.getStatus());
        }
    }



    // --- Create consultation from queue entry with details ---
    public boolean createConsultationFromQueueEntry(String queueEntryId, String doctorId, String dateStr, String timeStr, String specialty) {
        // First try to get from local map
        PatientQueueEntry entry = queueMap.get(queueEntryId);
        
        // If not found locally, refresh from file and try again
        if (entry == null) {
            this.queueMap = PatientQueueDAO.loadPatientQueue();
            entry = queueMap.get(queueEntryId);
        }
        
        if (entry == null) {
            System.out.println("Queue entry not found: " + queueEntryId);
            return false;
        }

        if (!entry.isAssigned()) {
            System.out.println("Queue entry is not assigned to a doctor: " + queueEntryId);
            return false;
        }

        if (!entry.getAssignedDoctorId().equals(doctorId)) {
            System.out.println("Queue entry is assigned to different doctor: " + entry.getAssignedDoctorId());
            return false;
        }

        try {
            // Parse date and time
            java.time.LocalDateTime consultationTime = java.time.LocalDateTime.parse(
                dateStr + " " + timeStr, 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            String consultationId = ConsultationDAO.generateConsultationId();
            Consultation consultation = new Consultation(consultationId, entry.getPatientId(), specialty, queueEntryId);
            
            // Assign doctor and set consultation time
            consultation.assignDoctor(doctorId, null, consultationTime);
            
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            // Update queue status to in consultation
            entry.startConsultation();
            queueMap.put(queueEntryId, entry);
                PatientQueueDAO.savePatientQueue(queueMap);
            
            System.out.println("Consultation created successfully: " + consultationId);
            return true;
        } catch (Exception e) {
            System.out.println("Error creating consultation: " + e.getMessage());
            return false;
        }
    }

    // --- Get consultations by patient ---
    public ListInterface<Consultation> getConsultationsByPatient(String patientId) {
        ListInterface<Consultation> patientConsultations = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getPatientId().equals(patientId)) {
                patientConsultations.add(c);
            }
        }
        return patientConsultations;
    }

    // --- Get pending consultations ---
    public ListInterface<Consultation> getPendingConsultations() {
        ListInterface<Consultation> pending = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getStatus().equals("SCHEDULED")) {
                pending.add(c);
            }
        }
        return pending;
    }

    // --- Get completed consultations ---
    public ListInterface<Consultation> getCompletedConsultations() {
        ListInterface<Consultation> completed = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getStatus().equals("COMPLETED")) {
                completed.add(c);
            }
        }
        return completed;
    }
    
    // --- Get consultations by status ---
    public ListInterface<Consultation> getConsultationsByStatus(String status) {
        ListInterface<Consultation> consultations = new ArrayList<Consultation>();
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getStatus().equals(status)) {
                consultations.add(c);
            }
        }
        return consultations;
    }
    
    // --- View all consultations ---
    public void viewAllConsultations() {
        System.out.println("\n=== ALL CONSULTATIONS ===\n");
        if (consultationMap.isEmpty()) {
            System.out.println("No consultations found.");
            return;
        }

        System.out.printf("%-15s %-10s %-10s %-20s %-20s %-15s%n", 
            "Consultation ID", "Patient ID", "Doctor ID", "Specialty", "Consultation Time", "Status");
        System.out.println("-".repeat(100));

        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
        if (c != null) {
                String consultationTime = c.getConsultationTime() != null ? 
                    c.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                String doctorId = c.getDoctorId() != null ? c.getDoctorId() : "N/A";
                
                System.out.printf("%-15s %-10s %-10s %-20s %-20s %-15s%n",
                    c.getConsultationId(),
                    c.getPatientId(),
                    doctorId,
                    c.getSpecialty(),
                    consultationTime,
                    c.getStatus());
            }
        }
    }

    // --- View consultations by doctor ---
    public void viewConsultationsByDoctor() {
        System.out.println("\n=== CONSULTATIONS BY DOCTOR ===");
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        ListInterface<Consultation> doctorConsultations = getConsultationsByDoctor(doctorId);
        
        if (doctorConsultations.isEmpty()) {
            System.out.println("No consultations found for doctor: " + doctorId);
            return;
        }

        System.out.println("\nConsultations for Doctor " + doctorId + ":");
        System.out.printf("%-15s %-10s %-20s %-20s %-15s%n", 
            "Consultation ID", "Patient ID", "Specialty", "Consultation Time", "Status");
        System.out.println("-".repeat(90));

        for (int i = 0; i < doctorConsultations.size(); i++) {
            Consultation c = doctorConsultations.get(i);
            String consultationTime = c.getConsultationTime() != null ? 
                c.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
            
            System.out.printf("%-15s %-10s %-20s %-20s %-15s%n",
                    c.getConsultationId(),
                    c.getPatientId(),
                    c.getSpecialty(),
                consultationTime,
                    c.getStatus());
        }
    }

    // --- View consultations by patient ---
    public void viewConsultationsByPatient() {
        System.out.println("\n=== CONSULTATIONS BY PATIENT ===");
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        
        ListInterface<Consultation> patientConsultations = getConsultationsByPatient(patientId);
        
        if (patientConsultations.isEmpty()) {
            System.out.println("No consultations found for patient: " + patientId);
            return;
        }

        System.out.println("\nConsultations for Patient " + patientId + ":");
        System.out.printf("%-15s %-10s %-20s %-20s %-15s%n", 
            "Consultation ID", "Doctor ID", "Specialty", "Consultation Time", "Status");
        System.out.println("-".repeat(90));

        for (int i = 0; i < patientConsultations.size(); i++) {
            Consultation c = patientConsultations.get(i);
            String consultationTime = c.getConsultationTime() != null ? 
                c.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
            String doctorId = c.getDoctorId() != null ? c.getDoctorId() : "N/A";
            
            System.out.printf("%-15s %-10s %-20s %-20s %-15s%n",
                c.getConsultationId(),
                doctorId,
                c.getSpecialty(),
                consultationTime,
                c.getStatus());
        }
    }

    // --- Generate daily consultation summary ---
    public void generateDailyConsultationSummary() {
        System.out.println("\n=== DAILY CONSULTATION SUMMARY ===");
        
        LocalDate today = LocalDate.now();
            int totalConsultations = 0;
            int scheduledConsultations = 0;
            int completedConsultations = 0;
            int pendingConsultations = 0;
            
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
                Consultation c = consultationMap.get(key);
            if (c != null && c.getConsultationTime() != null && 
                c.getConsultationTime().toLocalDate().equals(today)) {
                    totalConsultations++;
                    
                    switch (c.getStatus()) {
                        case "SCHEDULED":
                            scheduledConsultations++;
                            break;
                        case "COMPLETED":
                            completedConsultations++;
                            break;
                        case "PENDING":
                            pendingConsultations++;
                            break;
                    }
                }
            }
            
        System.out.println("Date: " + today);
            System.out.println("Total Consultations: " + totalConsultations);
            System.out.println("Scheduled: " + scheduledConsultations);
            System.out.println("Completed: " + completedConsultations);
            System.out.println("Pending: " + pendingConsultations);
    }

    // --- Process payment for consultation ---
    public void processPayment() {
        System.out.println("\n=== PROCESS PAYMENT ===");
        System.out.print("Enter Consultation ID: ");
        String consultationId = sc.nextLine().trim();
        
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return;
        }

        if (consultation.getPaymentId() != null) {
            System.out.println("Payment already processed for this consultation.");
            return;
        }

        System.out.print("Enter Payment Amount: ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }

        // Generate invoice for payment
        PaymentController paymentController = new PaymentController();
        String invoiceId = paymentController.generateInvoice(consultationId, amount);
        
        if (invoiceId != null) {
            // For now, we'll use the invoice ID as payment ID
            // In a real system, you'd create a separate payment record
            consultation.setPayment(invoiceId);
        consultationMap.put(consultationId, consultation);
        ConsultationDAO.saveConsultations(consultationMap);
            System.out.println("Invoice generated successfully. Invoice ID: " + invoiceId);
            System.out.println("Payment amount: RM " + String.format("%.2f", amount));
        } else {
            System.out.println("Failed to generate invoice.");
        }
    }

    // --- View doctor schedules ---
    public void viewDoctorSchedules() {
        System.out.println("\n=== DOCTOR SCHEDULES ===");
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();

        // Validate doctor exists
        if (!doctorExists(doctorId)) {
            System.out.println("Doctor ID '" + doctorId + "' does not exist in the system.");
            return;
        }

        // Load doctor schedules
        HashMapInterface<String, DoctorSchedule> scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();

        // Filter schedules for this doctor
        ListInterface<DoctorSchedule> doctorSchedules = new ArrayList<>();
        for (int i = 0; i < scheduleMap.keySet().size(); i++) {
            String scheduleId = scheduleMap.keySet().get(i);
            DoctorSchedule schedule = scheduleMap.get(scheduleId);

            if (schedule != null && schedule.getDoctorId().equals(doctorId)) {
                doctorSchedules.add(schedule);
            }
        }

        if (doctorSchedules.isEmpty()) {
            String doctorName = getDoctorName(doctorId);
            System.out.println("No schedules found for " + doctorName);
            return;
        }

        String doctorName = getDoctorName(doctorId);
        System.out.println("\nSchedules for " + doctorName + ":");
        System.out.printf("%-15s %-12s %-15s %-15s %-20s%n",
            "Schedule ID", "Date", "Time Slot", "Status", "Patient ID");
        System.out.println("-".repeat(80));

        for (int i = 0; i < doctorSchedules.size(); i++) {
            DoctorSchedule schedule = doctorSchedules.get(i);

            String status = schedule.isBooked() ? "Booked" : "Available";
            String patientId = schedule.getPatientId() != null ? schedule.getPatientId() : "N/A";
            String timeSlot = schedule.getTimeSlotString();
            String date = schedule.getDateString();

            System.out.printf("%-15s %-12s %-15s %-15s %-20s%n",
                schedule.getScheduleId(),
                date,
                timeSlot,
                status,
                patientId);
        }
    }

    // --- Make appointment ---
    public void makeAppointment() {
        System.out.println("\n=== MAKE APPOINTMENT ===");
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        
        if (!patientMap.containsKey(patientId)) {
            System.out.println("Patient not found: " + patientId);
            return;
        }

        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        if (!doctorMap.containsKey(doctorId)) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        System.out.print("Enter Specialty: ");
        String specialty = sc.nextLine().trim();

        System.out.print("Enter Date (yyyy-MM-dd): ");
        String dateStr = sc.nextLine().trim();

        System.out.print("Enter Time (HH:mm): ");
        String timeStr = sc.nextLine().trim();

        try {
            LocalDateTime appointmentTime = LocalDateTime.parse(dateStr + " " + timeStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            if (appointmentTime.isBefore(LocalDateTime.now())) {
                System.out.println("Cannot schedule appointments in the past.");
                return;
            }
            
            // Check for scheduling conflicts
            if (hasSchedulingConflict(doctorId, appointmentTime)) {
                System.out.println("Time slot is not available. Please choose another time.");
                return;
            }

            String consultationId = ConsultationDAO.generateConsultationId();
            Consultation consultation = new Consultation(consultationId, patientId, specialty);
            consultation.assignDoctor(doctorId, null, appointmentTime);
            
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            System.out.println("Appointment scheduled successfully. Consultation ID: " + consultationId);
        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }

    // --- View available time slots ---
    public void viewAvailableTimeSlots() {
        System.out.println("\n=== AVAILABLE TIME SLOTS ===");
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        System.out.print("Enter Date (yyyy-MM-dd): ");
        String dateStr = sc.nextLine().trim();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            System.out.println("\nAvailable time slots for " + date + ":");
            
            // Show available slots (assuming 1-hour slots from 9 AM to 5 PM)
            String[] timeSlots = {"09:00", "10:00", "11:00", "14:00", "15:00", "16:00"};
            
            for (String slot : timeSlots) {
                LocalDateTime slotTime = LocalDateTime.parse(dateStr + " " + slot, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                if (!hasSchedulingConflict(doctorId, slotTime)) {
                    System.out.println(slot + " - Available");
            } else {
                    System.out.println(slot + " - Booked");
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }

    // --- Check for scheduling conflicts ---
    private boolean hasSchedulingConflict(String doctorId, LocalDateTime appointmentTime) {
        // Check if doctor already has an appointment at this time
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getDoctorId() != null && c.getDoctorId().equals(doctorId) && 
                c.getConsultationTime() != null && 
                c.getConsultationTime().toLocalDate().equals(appointmentTime.toLocalDate()) &&
                Math.abs(c.getConsultationTime().getHour() - appointmentTime.getHour()) < 1) {
                return true;
            }
        }
        return false;
    }

    // --- Get consultation statistics ---
    public void getConsultationStatistics() {
        System.out.println("\n=== CONSULTATION STATISTICS ===");
        
        int totalConsultations = 0;
        int scheduledConsultations = 0;
        int completedConsultations = 0;
        int pendingConsultations = 0;
        
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null) {
                totalConsultations++;
                
                switch (c.getStatus()) {
                    case "SCHEDULED":
                        scheduledConsultations++;
                        break;
                    case "COMPLETED":
                        completedConsultations++;
                        break;
                    case "PENDING":
                        pendingConsultations++;
                        break;
                }
            }
        }
        
        System.out.println("Total Consultations: " + totalConsultations);
        System.out.println("Scheduled: " + scheduledConsultations);
        System.out.println("Completed: " + completedConsultations);
        System.out.println("Pending: " + pendingConsultations);
    }

    // --- Get available consultation count ---
    public int getAvailableConsultationCount() {
        int count = 0;
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getStatus().equals("SCHEDULED") && c.getDoctorId() != null) {
                count++;
            }
        }
        return count;
    }

    // --- View consultation details ---
    public void viewConsultationDetails() {
        System.out.print("Enter Consultation ID: ");
        String id = sc.nextLine().trim();
        viewConsultationDetails(id);
    }

    // --- View consultation details with provided ID ---
    public void viewConsultationDetails(String consultationId) {
        Consultation c = consultationMap.get(consultationId);
        if (c == null) {
            System.out.println("Consultation not found.");
            return;
        }

        System.out.println("\n--- Consultation Details ---");
        System.out.println("Consultation ID: " + c.getConsultationId());
        System.out.println("Patient ID: " + c.getPatientId());
        System.out.println("Doctor ID: " + (c.getDoctorId() != null ? c.getDoctorId() : "Not assigned"));
        System.out.println("Specialty: " + c.getSpecialty());
        System.out.println("Status: " + c.getStatus());
        System.out.println("Queue ID: " + (c.getQueueId() != null ? c.getQueueId() : "N/A"));

        if (c.getConsultationTime() != null) {
            System.out.println("Consultation Time: " + c.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        if (c.getTreatmentId() != null) {
            System.out.println("Treatment ID: " + c.getTreatmentId());
        }

        if (c.getPaymentId() != null) {
            System.out.println("Payment ID: " + c.getPaymentId());
        }
    }

    // --- View pending consultations ---
    public void viewPendingConsultations() {
        ListInterface<Consultation> pending = getPendingConsultations();
        if (pending.isEmpty()) {
            System.out.println("No pending consultations found.");
            return;
        }
        
        System.out.println("\n--- Pending Consultations ---");
        System.out.printf("%-15s %-10s %-10s %-20s %-15s%n", 
            "Consultation ID", "Patient ID", "Doctor ID", "Specialty", "Status");
        System.out.println("-".repeat(80));
        
        for (int i = 0; i < pending.size(); i++) {
            Consultation c = pending.get(i);
            String doctorId = c.getDoctorId() != null ? c.getDoctorId() : "N/A";
            System.out.printf("%-15s %-10s %-10s %-20s %-15s%n",
                c.getConsultationId(),
                c.getPatientId(),
                doctorId,
                c.getSpecialty(),
                c.getStatus());
        }
    }

    // --- View completed consultations ---
    public void viewCompletedConsultations() {
        ListInterface<Consultation> completed = getCompletedConsultations();
        if (completed.isEmpty()) {
            System.out.println("No completed consultations found.");
            return;
        }
        
        System.out.println("\n--- Completed Consultations ---");
        System.out.printf("%-15s %-10s %-10s %-20s %-15s%n", 
            "Consultation ID", "Patient ID", "Doctor ID", "Specialty", "Status");
        System.out.println("-".repeat(80));
        
        for (int i = 0; i < completed.size(); i++) {
            Consultation c = completed.get(i);
            String doctorId = c.getDoctorId() != null ? c.getDoctorId() : "N/A";
            System.out.printf("%-15s %-10s %-10s %-20s %-15s%n",
                c.getConsultationId(),
                c.getPatientId(),
                doctorId,
                c.getSpecialty(),
                c.getStatus());
        }
    }

    // --- View all consultations including completed ---
    public void viewAllConsultationsIncludingCompleted() {
        viewAllConsultations();
    }

    // --- Sort consultations by date (newest first) ---
    public void sortConsultationsByDateNewestFirst() {
        System.out.println("Sorting consultations by date (newest first) is not implemented in this version.");
        System.out.println("Please use the view options to see consultations.");
    }

    // --- Sort consultations by date (oldest first) ---
    public void sortConsultationsByDateOldestFirst() {
        System.out.println("Sorting consultations by date (oldest first) is not implemented in this version.");
        System.out.println("Please use the view options to see consultations.");
    }

    // --- Sort consultations by doctor ID ---
    public void sortConsultationsByDoctorId() {
        System.out.println("Sorting consultations by doctor ID is not implemented in this version.");
        System.out.println("Please use the view options to see consultations.");
    }

    // --- Create appointment ---
    public boolean createAppointment(String patientId, String doctorId, String dateStr, String timeStr, String specialty) {
        try {
            LocalDateTime appointmentTime = LocalDateTime.parse(dateStr + " " + timeStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (appointmentTime.isBefore(LocalDateTime.now())) {
                System.out.println("Cannot schedule appointments in the past.");
                return false;
            }

            // Check for scheduling conflicts
            if (hasSchedulingConflict(doctorId, appointmentTime)) {
                System.out.println("Time slot is not available. Please choose another time.");
                return false;
            }

            // Create doctor schedule entry (appointments are just scheduled slots)
            createDoctorSchedule(doctorId, patientId, specialty, appointmentTime);

            System.out.println("Appointment created and booked successfully for " + dateStr + " at " + timeStr);
            return true;
            } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
            return false;
        }
    }

    // --- Create doctor schedule entry ---
    private void createDoctorSchedule(String doctorId, String patientId, String specialty, LocalDateTime appointmentTime) {
        try {
            // Generate schedule ID
            String scheduleId = "SCH" + (scheduleCounter++);

            // Create schedule entry
            DoctorSchedule schedule = new DoctorSchedule(
                scheduleId,
                doctorId,
                specialty,
                appointmentTime.toLocalDate(),
                appointmentTime.toLocalTime(),
                appointmentTime.toLocalTime().plusHours(1) // 1-hour appointment
            );

            // Mark schedule as booked with patient info
            schedule.bookSlot(patientId);

            // Save to schedule map and file
            scheduleMap.put(scheduleId, schedule);
            DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);

            System.out.println("Schedule created for " + getDoctorName(doctorId) + " on " +
                              appointmentTime.toLocalDate() + " at " + appointmentTime.toLocalTime());

        } catch (Exception e) {
            System.out.println("Error creating doctor schedule: " + e.getMessage());
        }
    }



    // --- Get doctor name by ID ---
    private String getDoctorName(String doctorId) {
        Doctor doctor = doctorMap.get(doctorId);
        return doctor != null ? doctor.getName() : doctorId;
    }

    // --- Validation methods ---
    public boolean patientExists(String patientId) {
        return patientMap.containsKey(patientId);
    }

    public boolean doctorExists(String doctorId) {
        return doctorMap.containsKey(doctorId);
    }

    // --- View available time slots with parameters ---
    public void viewAvailableTimeSlots(String doctorId, String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            String doctorName = getDoctorName(doctorId);
            System.out.println("\n--- Available Time Slots for " + doctorName + " on " + date + " ---");
            
            // Show available slots (assuming 1-hour slots from 9 AM to 5 PM)
            String[] timeSlots = {"09:00", "10:00", "11:00", "14:00", "15:00", "16:00"};
            
            for (String slot : timeSlots) {
                LocalDateTime slotTime = LocalDateTime.parse(dateStr + " " + slot, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                if (!hasSchedulingConflict(doctorId, slotTime)) {
                    System.out.println(slot + " - Available");
                } else {
                    System.out.println(slot + " - Booked");
                    }
                }
            } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }

    // --- Generate daily consultation summary with date parameter ---
    public void generateDailyConsultationSummary(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            System.out.println("\n=== DAILY CONSULTATION SUMMARY FOR " + date + " ===");
            
            int totalConsultations = 0;
            int scheduledConsultations = 0;
            int completedConsultations = 0;
            int pendingConsultations = 0;
            
            for (int i = 0; i < consultationMap.keySet().size(); i++) {
                String key = consultationMap.keySet().get(i);
                Consultation c = consultationMap.get(key);
                if (c != null && c.getConsultationTime() != null && 
                    c.getConsultationTime().toLocalDate().equals(date)) {
                    totalConsultations++;
                    
                    switch (c.getStatus()) {
                        case "SCHEDULED":
                            scheduledConsultations++;
                            break;
                        case "COMPLETED":
                            completedConsultations++;
                            break;
                        case "PENDING":
                            pendingConsultations++;
                            break;
                    }
                }
            }
            
            System.out.println("Date: " + date);
            System.out.println("Total Consultations: " + totalConsultations);
            System.out.println("Scheduled: " + scheduledConsultations);
            System.out.println("Completed: " + completedConsultations);
            System.out.println("Pending: " + pendingConsultations);
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }

    // --- View consultations for payment ---
    public void viewConsultationsForPayment() {
        // Refresh consultation data to get latest status updates
        this.consultationMap = ConsultationDAO.loadConsultations();

        System.out.println("\n=== CONSULTATIONS FOR PAYMENT ===");
        ListInterface<Consultation> consultationsForPayment = new ArrayList<>();

        // Collect consultations that need payment
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation consultation = consultationMap.get(key);
            if (consultation != null && consultation.getStatus().equals("COMPLETED") &&
                consultation.getPaymentId() == null) {
                consultationsForPayment.add(consultation);
            }
        }

        if (consultationsForPayment.isEmpty()) {
            System.out.println("No consultations require payment.");
            return;
        }

        System.out.printf("%-15s %-10s %-10s %-20s %-15s%n",
            "Consultation ID", "Patient ID", "Doctor ID", "Specialty", "Status");
        System.out.println("-".repeat(80));

        // Display consultations for payment
        for (int i = 0; i < consultationsForPayment.size(); i++) {
            Consultation consultation = consultationsForPayment.get(i);
            String doctorId = (consultation.getDoctorId() != null) ? consultation.getDoctorId() : "N/A";
            System.out.printf("%-15s %-10s %-10s %-20s %-15s%n",
                consultation.getConsultationId(),
                consultation.getPatientId(),
                doctorId,
                consultation.getSpecialty(),
                consultation.getStatus());
        }
    }

    // --- Get count of consultations for payment ---
    public int getConsultationsForPaymentCount() {
        // Refresh consultation data to get latest status updates
        this.consultationMap = ConsultationDAO.loadConsultations();

        int count = 0;
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getStatus().equals("COMPLETED") && c.getPaymentId() == null) {
                count++;
            }
        }
        return count;
    }
    
    // --- Check if consultation is eligible for payment ---
    public boolean isConsultationEligibleForPayment(String consultationId) {
        // Refresh consultation data to get latest status updates
        this.consultationMap = ConsultationDAO.loadConsultations();

        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            return false;
        }
        return consultation.getStatus().equals("COMPLETED") && consultation.getPaymentId() == null;
    }
    
    // --- Calculate consultation total ---
    public double calculateConsultationTotal(String consultationId) {
        double[] breakdown = calculateConsultationTotalWithBreakdown(consultationId);
        return breakdown[0] + breakdown[1] + breakdown[2]; // Total = consultation + treatment + medicines
    }

    public double[] calculateConsultationTotalWithBreakdown(String consultationId) {
        double[] breakdown = new double[3]; // [consultationFee, treatmentFee, medicineCost]

        try {
            // First, try to get the breakdown from existing invoice
            HashMapInterface<String, entity.Invoice> invoiceMap = dao.InvoiceDAO.loadInvoices();

            // Find invoice for this consultation
            for (int i = 0; i < invoiceMap.keySet().size(); i++) {
                String invoiceId = invoiceMap.keySet().get(i);
                entity.Invoice invoice = invoiceMap.get(invoiceId);

                if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                    // Use the breakdown from the invoice
                    breakdown[0] = invoice.getConsultationFee(); // Consultation fee
                    breakdown[1] = invoice.getTreatmentFee();   // Treatment fee
                    breakdown[2] = invoice.getMedicineFee();    // Medicine cost

                    System.out.println("Using invoice breakdown for consultation: " + consultationId);
                    return breakdown;
                }
            }

            // If no invoice found, fall back to calculating from data
            System.out.println("No invoice found, calculating breakdown from data for consultation: " + consultationId);

            Consultation consultation = consultationMap.get(consultationId);
            if (consultation == null) {
                return new double[]{-1.0, -1.0, -1.0};
            }

            // Get doctor's consultation fee
            Doctor doctor = doctorMap.get(consultation.getDoctorId());
            if (doctor != null) {
                breakdown[0] = doctor.getConsultationFee(); // Consultation fee
            }

            // Add treatment fee and medicine costs if treatment exists
            if (consultation.getTreatmentId() != null) {
                control.TreatmentController treatmentController = new control.TreatmentController();
                entity.Treatment treatment = treatmentController.getTreatmentById(consultation.getTreatmentId());
                if (treatment != null) {
                    breakdown[1] = treatment.getTreatmentFee(); // Treatment fee

                    // Calculate medicine costs
                    for (int i = 0; i < treatment.getPrescribedMedicines().size(); i++) {
                        entity.MedicinePrescribed prescribed = treatment.getPrescribedMedicines().get(i);
                        String medicineId = prescribed.getMedicineId();
                        int quantity = prescribed.getQuantity();

                        // Get medicine price from medicine map
                        Medicine medicine = medicineMap.get(medicineId);
                        if (medicine != null) {
                            breakdown[2] += medicine.getPrice() * quantity; // Medicine cost
                        }
                    }
                }
            }

            return breakdown;

        } catch (Exception e) {
            System.out.println("Error calculating consultation total: " + e.getMessage());
            return new double[]{-1.0, -1.0, -1.0};
        }
    }
    
    // --- Process consultation payment ---
    public boolean processConsultationPayment(String consultationId,
                                           entity.Payment.PaymentMethod paymentMethod,
                                           String remarks) {
        try {
            Consultation consultation = consultationMap.get(consultationId);
            if (consultation == null) {
                System.out.println("Consultation not found: " + consultationId);
                return false;
            }

            if (!isConsultationEligibleForPayment(consultationId)) {
                System.out.println("Consultation is not eligible for payment: " + consultationId);
                return false;
            }

            // Get the existing invoice for this consultation
            HashMapInterface<String, entity.Invoice> invoiceMap = dao.InvoiceDAO.loadInvoices();
            entity.Invoice existingInvoice = null;
            String invoiceId = null;

            // Find the invoice for this consultation
            for (int i = 0; i < invoiceMap.keySet().size(); i++) {
                String key = invoiceMap.keySet().get(i);
                entity.Invoice invoice = invoiceMap.get(key);
                if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                    existingInvoice = invoice;
                    invoiceId = key;
                    break;
                }
            }

            // If no existing invoice, create one
            if (existingInvoice == null) {
                PaymentController paymentController = new PaymentController();
                double[] costBreakdown = calculateConsultationTotalWithBreakdown(consultationId);
                double totalAmount = costBreakdown[0] + costBreakdown[1] + costBreakdown[2];
                invoiceId = paymentController.generateInvoice(consultationId, totalAmount);

                // Reload invoice map to get the newly created invoice
                invoiceMap = dao.InvoiceDAO.loadInvoices();
                existingInvoice = invoiceMap.get(invoiceId);
            }

            if (invoiceId != null && existingInvoice != null) {
                // Mark invoice as paid
                existingInvoice.markPaid();
                invoiceMap.put(invoiceId, existingInvoice);
                dao.InvoiceDAO.saveInvoices(invoiceMap);

                // Create payment record
                String paymentId = dao.PaymentDAO.generatePaymentId();
                entity.Payment payment = new entity.Payment(
                    paymentId,
                    invoiceId,
                    consultationId,
                    consultation.getPatientId(),
                    paymentMethod
                );
                payment.setRemarks(remarks);
                payment.markPaid(); // Mark as PAID since we're processing payment

                // Save payment
                HashMapInterface<String, entity.Payment> paymentMap = dao.PaymentDAO.loadPayments();
                paymentMap.put(paymentId, payment);
                dao.PaymentDAO.savePayments(paymentMap);

                // Update consultation with payment ID
                consultation.setPayment(paymentId);
                consultationMap.put(consultationId, consultation);
                ConsultationDAO.saveConsultations(consultationMap);

                // Update queue status to COMPLETED if consultation has a queue entry
                if (consultation.getQueueId() != null) {
                    HashMapInterface<String, entity.PatientQueueEntry> queueMap = PatientQueueDAO.loadPatientQueue();
                    entity.PatientQueueEntry queueEntry = queueMap.get(consultation.getQueueId());
                    if (queueEntry != null) {
                        queueEntry.complete(); // Mark queue entry as completed
                        queueMap.put(consultation.getQueueId(), queueEntry);
                        PatientQueueDAO.savePatientQueue(queueMap);
                        System.out.println("Queue entry " + consultation.getQueueId() + " status updated to COMPLETED");
                    }
                }

                System.out.println("Payment processed successfully for consultation: " + consultationId);
                System.out.println("Invoice " + invoiceId + " marked as PAID");
                return true;
            } else {
                System.out.println("Failed to process payment for consultation: " + consultationId);
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error processing payment: " + e.getMessage());
            return false;
        }
    }

    // --- View available consultations ---
    public void viewAvailableConsultations() {
        System.out.println("\n--- Available Consultations for Completion ---");
        if (consultationMap.isEmpty()) {
            System.out.println("No consultations available.");
            return;
        }
        
        System.out.printf("%-15s %-10s %-20s %-15s%n", 
            "Consultation ID", "Patient ID", "Specialty", "Status");
        System.out.println("-".repeat(70));

        int availableCount = 0;
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation c = consultationMap.get(key);
            if (c != null && c.getStatus().equals("SCHEDULED") && c.getDoctorId() != null) {
                System.out.printf("%-15s %-10s %-20s %-15s%n",
                    c.getConsultationId(),
                    c.getPatientId(),
                    c.getSpecialty(),
                    c.getStatus());
                availableCount++;
            }
        }
        
        if (availableCount == 0) {
            System.out.println("No consultations available for completion.");
        } else {
            System.out.println("Total available consultations: " + availableCount);
        }
    }

    // --- Create consultation for queue (for backward compatibility) ---
    public void createConsultationForQueue(String queueId) {
        String consultationId = createConsultationFromQueue(queueId);
        if (consultationId != null) {
            System.out.println("Consultation created successfully: " + consultationId);
        } else {
            System.out.println("Failed to create consultation from queue.");
        }
    }

    // --- Generate consultation frequency report ---
    public void generateConsultationFrequencyReport() {
        System.out.println("=".repeat(90));
        System.out.println("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY");
        System.out.println("CLINIC MANAGEMENT SYSTEM");
        System.out.println("CONSULTATION FREQUENCY REPORT");
        System.out.println("=".repeat(90));
        System.out.println();
        
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy, hh:mm a");
        System.out.println("Generated at: " + now.format(formatter));
        System.out.println();
        
        // Count consultations per specialty
        HashMapInterface<String, Integer> specialtyCount = new adt.HashMapADT<>();
        int totalConsultations = 0;
        
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation consultation = consultationMap.get(key);
            if (consultation != null) {
            totalConsultations++;
            String specialty = consultation.getSpecialty();
            Integer currentCount = specialtyCount.get(specialty);
            specialtyCount.put(specialty, currentCount != null ? currentCount + 1 : 1);
            }
        }
        
        // Display specialty frequency
        System.out.println("Consultation Frequency by Specialty:");
        System.out.println("-".repeat(70));
        System.out.printf("| %-25s | %-20s | %-20s |%n", "Specialty", "Consultation Count", "Percentage");
        System.out.println("-".repeat(70));
        
        String mostDemandedSpecialty = "";
        int maxConsultations = 0;
        
        for (int i = 0; i < specialtyCount.keySet().size(); i++) {
            String key = specialtyCount.keySet().get(i);
            int count = specialtyCount.get(key);
            double percentage = (double)count/totalConsultations*100;
            
            if (count > maxConsultations) {
                maxConsultations = count;
                mostDemandedSpecialty = key;
            }
            
            System.out.printf("| %-25s | %-20d | %-19.1f%% |%n", 
                key, 
                count,
                percentage);
        }
        System.out.println("-".repeat(70));
        System.out.printf("Total Consultations: %d%n", totalConsultations);
        System.out.println();
        
        // Summary
        System.out.println("Frequency Summary:");
        System.out.println("-".repeat(50));
        System.out.println("Most In-Demand Specialty: " + mostDemandedSpecialty + " (" + maxConsultations + " consultations)");
        System.out.println();
        
        System.out.println("=".repeat(90));
        System.out.println("END OF REPORT");
        System.out.println("=".repeat(90));
    }
    
    // --- Generate follow-up appointment report ---
    public void generateFollowUpAppointmentReport() {
        System.out.println("=".repeat(90));
        System.out.println("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY");
        System.out.println("CLINIC MANAGEMENT SYSTEM");
        System.out.println("FOLLOW-UP APPOINTMENT REPORT");
        System.out.println("=".repeat(90));
        System.out.println();
        
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy, hh:mm a");
        System.out.println("Generated at: " + now.format(formatter));
        System.out.println();
        
        // Count consultations by patient to identify follow-ups
        HashMapInterface<String, Integer> patientConsultationCount = new adt.HashMapADT<>();
        
        for (int i = 0; i < consultationMap.keySet().size(); i++) {
            String key = consultationMap.keySet().get(i);
            Consultation consultation = consultationMap.get(key);
            if (consultation != null) {
            String patientId = consultation.getPatientId();
            Integer currentCount = patientConsultationCount.get(patientId);
            patientConsultationCount.put(patientId, currentCount != null ? currentCount + 1 : 1);
            }
        }
        
        // Analyze follow-up patterns
        int oneTimeVisits = 0;
        int followUpVisits = 0;
        int totalPatients = patientConsultationCount.size();
        
        for (int i = 0; i < patientConsultationCount.keySet().size(); i++) {
            String key = patientConsultationCount.keySet().get(i);
            int count = patientConsultationCount.get(key);
            if (count == 1) {
                oneTimeVisits++;
            } else {
                followUpVisits++;
            }
        }
        
        // Display follow-up analysis
        System.out.println("Follow-up Appointment Analysis:");
        System.out.println("-".repeat(60));
        System.out.printf("| %-25s | %-15s | %-15s |%n", "Visit Type", "Patient Count", "Percentage");
        System.out.println("-".repeat(60));
        
        double oneTimePercentage = totalPatients > 0 ? (double)oneTimeVisits/totalPatients*100 : 0.0;
        double followUpPercentage = totalPatients > 0 ? (double)followUpVisits/totalPatients*100 : 0.0;
        
        System.out.printf("| %-25s | %-15d | %-14.1f%% |%n", "One-time Visits", oneTimeVisits, oneTimePercentage);
        System.out.printf("| %-25s | %-15d | %-14.1f%% |%n", "Follow-up Visits", followUpVisits, followUpPercentage);
        System.out.println("-".repeat(60));
        System.out.printf("Total Unique Patients: %d%n", totalPatients);
        System.out.println();
        
        // Summary
        System.out.println("Follow-up Summary:");
        System.out.println("-".repeat(50));
        System.out.printf("Follow-up Rate: %.1f%%%n", followUpPercentage);
        System.out.printf("One-time Visit Rate: %.1f%%%n", oneTimePercentage);
        System.out.println();
        
        System.out.println("=".repeat(90));
        System.out.println("END OF REPORT");
        System.out.println("=".repeat(90));
    }
}
