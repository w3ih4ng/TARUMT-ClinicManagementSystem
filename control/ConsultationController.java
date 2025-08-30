package control;

import entity.*;
import adt.*;
import dao.ConsultationDAO;
import dao.PatientQueueDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import dao.PaymentDAO;

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
    private Scanner sc;

    public ConsultationController() {
        this.consultationMap = ConsultationDAO.loadConsultations();
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.patientMap = PatientDAO.loadPatients();
        this.sc = new Scanner(System.in);
        
        // Create consultations for existing assigned patients
        createConsultationsForExistingAssignments();
    }

    // --- Create consultation when doctor is assigned ---
    public void createConsultationForQueue(String queueId) {
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
        }
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

        // Complete consultation
        consultation.completeConsultation(treatmentId);
        consultationMap.put(consultationId, consultation);
        ConsultationDAO.saveConsultations(consultationMap);

        // Remove from active queue (history system removed)
        String queueId = consultation.getQueueId();
        if (queueId != null) {
            queueMap.remove(queueId);
            PatientQueueDAO.savePatientQueue(queueMap);
        }

        // Optionally generate invoice via Payment module (amount to be finalized elsewhere)
        generateInvoiceForConsultation(consultationId);

        System.out.println("Consultation completed with treatment: " + treatmentId);
    }

    // --- Auto-assign appointment patients to their booked doctors ---
    public void autoAssignAppointmentPatients() {
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            
            // Check if this is an appointment patient without a doctor assignment
            if (entry.getQueueType() == QueueType.APPOINTMENT && !entry.isAssigned()) {
                // Find the booked appointment for this patient
                String doctorId = findBookedAppointmentForPatient(entry.getPatientId());
                if (doctorId != null) {
                    entry.assignToDoctor(doctorId);
                    queueMap.put(key, entry);
                    System.out.println("Auto-assigned appointment patient " + entry.getPatientId() + " to Dr. " + doctorId);
                }
            }
        }
        
        // Save the updated queue
        PatientQueueDAO.savePatientQueue(queueMap);
    }

    // --- Find booked appointment for a patient ---
    private String findBookedAppointmentForPatient(String patientId) {
        // This would need to be implemented based on how appointments are linked to patients
        // For now, we'll return null and handle this in the appointment booking system
        return null;
    }

    // --- Get consultations by doctor ---
    public ListInterface<Consultation> getConsultationsByDoctor(String doctorId) {
        ListInterface<Consultation> doctorConsultations = new ArrayList<>();
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getDoctorId() != null && c.getDoctorId().equals(doctorId)) {
                doctorConsultations.add(c);
            }
        }
        return doctorConsultations;
    }

    // --- Get pending consultations for a specific doctor ---
    public ListInterface<Consultation> getPendingConsultationsForDoctor(String doctorId) {
        ListInterface<Consultation> pendingConsultations = new ArrayList<>();
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            
            if (c.getDoctorId() != null && c.getDoctorId().equals(doctorId) && 
                c.getStatus().equals("SCHEDULED")) {
                pendingConsultations.add(c);
            }
        }
        
        return pendingConsultations;
    }

    // --- Get all consultations for a specific doctor ---
    public ListInterface<Consultation> getAllConsultationsForDoctor(String doctorId) {
        ListInterface<Consultation> allConsultations = new ArrayList<>();
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getDoctorId() != null && c.getDoctorId().equals(doctorId)) {
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
            
            // Store completed consultation in history
            storeCompletedConsultationInHistory(consultationId);
            
            // Automatically generate invoice for payment
            generateInvoiceForConsultation(consultationId);
            
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
            control.TreatmentController treatmentControl = new control.TreatmentController();
            String treatmentId = treatmentControl.createTreatment(
                doctorId, 
                consultation.getPatientId(), 
                consultationId,
                diagnosis, 
                treatmentFee, 
                medicines
            );
            
            if (treatmentId != null) {
                // Mark consultation as completed with treatment
                consultation.completeConsultation(treatmentId);
                ConsultationDAO.saveConsultations(consultationMap);
                
                // Update the consultation map to ensure latest data
                consultationMap.put(consultationId, consultation);
                
                // Store completed consultation in history
                storeCompletedConsultationInHistory(consultationId);
                
                // Delete existing incorrect invoice if it exists
                deleteExistingInvoice(consultationId);
                
                // Generate new invoice with all fees included
                generateInvoiceForConsultation(consultationId);
                
                System.out.println("Treatment created: " + treatmentId);
                System.out.println("Consultation completed with proper treatment details!");
                
                return true;
            } else {
                System.out.println("Failed to create treatment record.");
            }
        }
        
        return false;
    }
    
    // --- Delete existing invoice for re-generation ---
    private void deleteExistingInvoice(String consultationId) {
        try {
            PaymentController paymentController = new PaymentController();
            paymentController.deleteInvoiceByConsultation(consultationId);
        } catch (Exception e) {
            // Ignore if invoice doesn't exist or deletion fails
        }
    }

    // --- Generate invoice for completed consultation ---
    private void generateInvoiceForConsultation(String consultationId) {
        try {
            PaymentController paymentController = new PaymentController();
            // For now, generate zero-amount invoice; Payment module will adjust upon processing
            paymentController.generateInvoice(consultationId, 0.0);
        } catch (Exception e) {
            System.out.println("Error generating invoice: " + e.getMessage());
        }
    }

    // --- Store completed consultation in history and remove from queue ---
    private void storeCompletedConsultationInHistory(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null && consultation.getQueueId() != null) {
            PatientQueueEntry queueEntry = queueMap.get(consultation.getQueueId());
            if (queueEntry != null) {
                // Store in history
                // QueueHistoryController historyControl = new QueueHistoryController(); // Removed
                // historyControl.storeCompletedQueueEntry(queueEntry, "CONSULTATION_COMPLETED"); // Removed
                
                // Remove from active queue
                queueMap.remove(consultation.getQueueId());
                PatientQueueDAO.savePatientQueue(queueMap);
                System.out.println("Patient " + consultation.getPatientId() + " moved to history after completion");
            }
        }
    }

    // --- Get consultations by patient ---
    public ListInterface<Consultation> getConsultationsByPatient(String patientId) {
        ListInterface<Consultation> patientConsultations = new ArrayList<>();
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getPatientId().equals(patientId)) {
                patientConsultations.add(c);
            }
        }
        return patientConsultations;
    }

    // --- Get consultation by ID ---
    public Consultation getConsultationById(String consultationId) {
        return consultationMap.get(consultationId);
    }

    // --- Update consultation status ---
    public void updateConsultationStatus(String consultationId, String status) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            // Update status based on the new status
            if (status.equals("COMPLETED") && consultation.getTreatmentId() != null) {
                consultation.completeConsultation(consultation.getTreatmentId());
            }
            ConsultationDAO.saveConsultations(consultationMap);
        }
    }

    // --- Get pending consultations ---
    public ListInterface<Consultation> getPendingConsultations() {
        ListInterface<Consultation> pending = new ArrayList<>();
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getStatus().equals("SCHEDULED")) {
                pending.add(c);
            }
        }
        return pending;
    }

    // --- Get completed consultations ---
    public ListInterface<Consultation> getCompletedConsultations() {
        ListInterface<Consultation> completed = new ArrayList<>();
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getStatus().equals("COMPLETED")) {
                completed.add(c);
            }
        }
        return completed;
    }

    // --- Display consultation details ---
    public void displayConsultationDetails(String consultationId) {
        Consultation c = consultationMap.get(consultationId);
        if (c != null) {
            Patient patient = patientMap.get(c.getPatientId());
            Doctor doctor = c.getDoctorId() != null ? doctorMap.get(c.getDoctorId()) : null;
            
            System.out.println("\n--- Consultation Details ---");
            System.out.println("Consultation ID: " + c.getConsultationId());
            System.out.println("Patient: " + (patient != null ? patient.getName() : c.getPatientId()));
            System.out.println("Doctor: " + (doctor != null ? doctor.getName() : "Not assigned"));
            System.out.println("Specialty: " + c.getSpecialty());
            System.out.println("Status: " + c.getStatus());
            System.out.println("Time: " + (c.getConsultationTime() != null ? c.getConsultationTime() : "Not scheduled"));
            System.out.println("Treatment ID: " + (c.getTreatmentId() != null ? c.getTreatmentId() : "Not created"));
            System.out.println("Payment ID: " + (c.getPaymentId() != null ? c.getPaymentId() : "Not paid"));
        } else {
            System.out.println("Consultation not found.");
        }
    }

    // --- Display consultations table ---
    public void displayConsultationsTable(ListInterface<Consultation> consultations, String title) {
        if (consultations.isEmpty()) {
            System.out.println("\nNo consultations found for: " + title);
            return;
        }

        System.out.println("\n--- " + title + " ---");
        String borderLine = "+----------------+------------+---------------------------+---------------------------+----------------------+----------------+";
        System.out.println(borderLine);
        System.out.printf("| %-14s | %-10s | %-25s | %-25s | %-20s | %-14s |%n",
                "ConsultationID", "PatientID", "Patient Name", "Doctor Name", "Specialty", "Status");
        System.out.println(borderLine);

        for (int i = 0; i < consultations.size(); i++) {
            Consultation c = consultations.get(i);
            Patient patient = patientMap.get(c.getPatientId());
            Doctor doctor = c.getDoctorId() != null ? doctorMap.get(c.getDoctorId()) : null;
            
            String patientName = patient != null ? patient.getName() : "Unknown";
            String doctorName = doctor != null ? doctor.getName() : "Not Assigned";
            
            System.out.printf("| %-14s | %-10s | %-25s | %-25s | %-20s | %-14s |%n",
                    c.getConsultationId(),
                    c.getPatientId(),
                    patientName,
                    doctorName,
                    c.getSpecialty(),
                    c.getStatus());
            System.out.println(borderLine);
        }
    }

    /**
     * Generate daily consultation summary (no arguments)
     */
    public void generateDailyConsultationSummary() {
        generateDailyConsultationSummary(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    /**
     * Generate daily consultation summary for specific date
     */
    public void generateDailyConsultationSummary(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            System.out.println("\n--- Daily Consultation Summary for " + date + " ---");
            
            int totalConsultations = 0;
            int scheduledConsultations = 0;
            int completedConsultations = 0;
            int pendingConsultations = 0;
            
            for (String key : consultationMap.keySet()) {
                Consultation c = consultationMap.get(key);
                if (c.getConsultationTime() != null && 
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
            
            System.out.println("Total Consultations: " + totalConsultations);
            System.out.println("Scheduled: " + scheduledConsultations);
            System.out.println("Completed: " + completedConsultations);
            System.out.println("Pending: " + pendingConsultations);
            
            if (totalConsultations > 0) {
                double completionRate = (double) completedConsultations / totalConsultations * 100;
                System.out.printf("Completion Rate: %.1f%%%n", completionRate);
            }
            
        } catch (Exception e) {
            System.out.println("Error generating daily summary: " + e.getMessage());
        }
    }

    // ==================== MISSING METHODS FOR UI ====================

    /**
     * Get count of available consultations for completion
     */
    public int getAvailableConsultationCount() {
        int count = 0;
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getStatus().equals("SCHEDULED") && c.getDoctorId() != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * View patient queue (waiting for doctor assignment)
     */
    public void viewPatientQueue() {
        System.out.println("\n--- Patient Queue (Waiting for Doctor) ---");
        if (queueMap.isEmpty()) {
            System.out.println("No patients in queue.");
            return;
        }

        String borderLine = "+------------+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-25s | %-25s | %-25s |%n", 
                         "Queue ID", "Patient ID", "Specialty", "Queue Type", "Arrival Time");
        System.out.println(borderLine);

        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.getQueueStatus() == entity.QueueStatus.WAITING) {
                System.out.printf("| %-10s | %-10s | %-25s | %-25s | %-25s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    entry.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        }
        System.out.println(borderLine);
    }

    /**
     * Get count of waiting patients
     */
    public int getWaitingPatientCount() {
        int count = 0;
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.getQueueStatus() == entity.QueueStatus.WAITING) {
                count++;
            }
        }
        return count;
    }

    /**
     * View available doctors for assignment
     */
    public void viewAvailableDoctors() {
        System.out.println("\n--- Available Doctors ---");
        if (doctorMap.isEmpty()) {
            System.out.println("No doctors available.");
            return;
        }

        String borderLine = "+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-25s | %-25s |%n", 
                         "Doctor ID", "Name", "Specialty", "Phone");
        System.out.println(borderLine);

        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-25s | %-25s |%n",
                    doctor.getDoctorId(),
                    doctor.getName(),
                    doctor.getSpecialty(),
                    doctor.getPhoneNumber());
            }
        }
        System.out.println(borderLine);
    }

    /**
     * Assign patient to doctor and create consultation
     */
    public boolean assignPatientToDoctor(String queueId, String doctorId) {
        PatientQueueEntry entry = queueMap.get(queueId);
        if (entry == null) {
            System.out.println("Queue entry not found: " + queueId);
            return false;
        }

        if (entry.getQueueStatus() != entity.QueueStatus.WAITING) {
            System.out.println("Patient is not waiting: " + entry.getQueueStatus());
            return false;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return false;
        }

        // Update queue entry
        entry.assignToDoctor(doctorId);
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);

        // Create consultation
        String consultationId = ConsultationDAO.generateConsultationId();
        Consultation consultation = new Consultation(consultationId, entry.getPatientId(), entry.getSpecialty(), queueId);
        
        // Assign doctor and set consultation time
        consultation.assignDoctor(doctorId, null, LocalDateTime.now());
        
        consultationMap.put(consultationId, consultation);
        ConsultationDAO.saveConsultations(consultationMap);
        
        System.out.println("Patient assigned to Dr. " + doctorId + " - Consultation created: " + consultationId);
        return true;
    }

    /**
     * View consultations by specific doctor (with doctorId parameter)
     */
    public void viewConsultationsByDoctor(String doctorId) {
        System.out.println("\n--- Consultations for Doctor: " + doctorId + " ---");
        
        ListInterface<Consultation> doctorConsultations = getConsultationsByDoctor(doctorId);
        if (doctorConsultations.isEmpty()) {
            System.out.println("No consultations found for this doctor.");
            return;
        }

        String borderLine = "+------------------+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-16s | %-10s | %-25s | %-25s | %-25s |%n", 
                         "Consultation ID", "Patient ID", "Specialty", "Status", "Consultation Time");
        System.out.println(borderLine);

        for (int i = 0; i < doctorConsultations.size(); i++) {
            Consultation c = doctorConsultations.get(i);
            String consultationTime = c.getConsultationTime() != null ? 
                c.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Not set";
            
            System.out.printf("| %-16s | %-10s | %-25s | %-25s | %-25s |%n",
                c.getConsultationId(),
                c.getPatientId(),
                c.getSpecialty(),
                c.getStatus(),
                consultationTime);
        }
        System.out.println(borderLine);
    }

    /**
     * View consultation details by ID (with consultationId parameter)
     */
    public void viewConsultationDetails(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return;
        }

        System.out.println("\n--- Consultation Details ---");
        System.out.println("Consultation ID: " + consultation.getConsultationId());
        System.out.println("Patient ID: " + consultation.getPatientId());
        System.out.println("Doctor ID: " + (consultation.getDoctorId() != null ? consultation.getDoctorId() : "Not assigned"));
        System.out.println("Specialty: " + consultation.getSpecialty());
        System.out.println("Status: " + consultation.getStatus());
        System.out.println("Queue ID: " + (consultation.getQueueId() != null ? consultation.getQueueId() : "N/A"));
        
        if (consultation.getConsultationTime() != null) {
            System.out.println("Consultation Time: " + consultation.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        
        if (consultation.getTreatmentId() != null) {
            System.out.println("Treatment ID: " + consultation.getTreatmentId());
        }
        
        if (consultation.getPaymentId() != null) {
            System.out.println("Payment ID: " + consultation.getPaymentId());
        }
    }

    /**
     * Create appointment (add to queue with appointment type)
     */
    public boolean createAppointment(String patientId, String doctorId, String dateStr, String timeStr, String specialty) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
            LocalDateTime appointmentTime = LocalDateTime.of(date, time);
            
            // Create queue entry for appointment
            String queueId = PatientQueueDAO.generateQueueId();
            PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, 
                                                         entity.QueueType.APPOINTMENT, LocalDateTime.now());
            entry.setScheduledStartTime(appointmentTime);
            entry.assignToDoctor(doctorId);
            
            queueMap.put(queueId, entry);
            PatientQueueDAO.savePatientQueue(queueMap);
            
            // Create consultation
            String consultationId = ConsultationDAO.generateConsultationId();
            Consultation consultation = new Consultation(consultationId, patientId, specialty, queueId);
            consultation.assignDoctor(doctorId, null, appointmentTime);
            
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            System.out.println("Appointment created successfully!");
            System.out.println("Queue ID: " + queueId);
            System.out.println("Consultation ID: " + consultationId);
            System.out.println("Scheduled for: " + appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            return true;
        } catch (Exception e) {
            System.out.println("Error creating appointment: " + e.getMessage());
            return false;
        }
    }

    /**
     * View available time slots for a doctor on a specific date
     */
    public void viewAvailableTimeSlots(String doctorId, String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            System.out.println("\n--- Available Time Slots for Dr. " + doctorId + " on " + date + " ---");
            
            // This would need to be implemented based on doctor schedule
            // For now, show a simple time slot structure
            System.out.println("Available time slots:");
            System.out.println("09:00 - 09:30");
            System.out.println("09:30 - 10:00");
            System.out.println("10:00 - 10:30");
            System.out.println("10:30 - 11:00");
            System.out.println("14:00 - 14:30");
            System.out.println("14:30 - 15:00");
            System.out.println("15:00 - 15:30");
            System.out.println("15:30 - 16:00");
            
            System.out.println("\nNote: Time slot availability should be integrated with doctor schedule system.");
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }

    /**
     * View available consultations for completion
     */
    public void viewAvailableConsultations() {
        System.out.println("\n--- Available Consultations for Completion ---");
        if (consultationMap.isEmpty()) {
            System.out.println("No consultations available.");
            return;
        }

        String borderLine = "+------------------+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-16s | %-10s | %-25s | %-25s | %-25s |%n", 
                         "Consultation ID", "Patient ID", "Specialty", "Status", "Doctor ID");
        System.out.println(borderLine);

        int availableCount = 0;
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            // Only show consultations that are SCHEDULED and have a doctor assigned
            if (c.getStatus().equals("SCHEDULED") && c.getDoctorId() != null) {
                System.out.printf("| %-16s | %-10s | %-25s | %-25s | %-25s |%n",
                    c.getConsultationId(),
                    c.getPatientId(),
                    c.getSpecialty(),
                    c.getStatus(),
                    c.getDoctorId());
                availableCount++;
            }
        }
        System.out.println(borderLine);
        
        if (availableCount == 0) {
            System.out.println("No consultations available for completion.");
        } else {
            System.out.println("Total available consultations: " + availableCount);
        }
    }



    // --- Get consultation map for other controls ---
    public HashMapInterface<String, Consultation> getConsultationMap() {
        return consultationMap;
    }

    // --- Create consultations for existing assigned patients ---
    private void createConsultationsForExistingAssignments() {
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            
            if (entry.isAssigned() && entry.getAssignedDoctorId() != null) {
                // Check if consultation already exists for this queue entry
                boolean consultationExists = false;
                for (String cKey : consultationMap.keySet()) {
                    Consultation c = consultationMap.get(cKey);
                    if (c.getQueueId() != null && c.getQueueId().equals(key)) {
                        consultationExists = true;
                        break;
                    }
                }
                
                if (!consultationExists) {
                    createConsultationForQueue(key);
                }
            }
        }
    }

    // --- Save consultations ---
    public void saveConsultations() {
        ConsultationDAO.saveConsultations(consultationMap);
    }
    
    // --- Refresh queue data from file ---
    public void refreshQueueData() {
        this.queueMap = PatientQueueDAO.loadPatientQueue();
    }

    // ==================== UI WRAPPERS FOR ConsultationUI ====================

    public void viewAllConsultations() {
        printConsultationsTable(consultationMap.toList(), "All Consultations");
    }

    public void viewPendingConsultations() {
        printConsultationsTable(getPendingConsultations(), "Pending Consultations");
    }

    public void viewCompletedConsultations() {
        printConsultationsTable(getCompletedConsultations(), "Completed Consultations");
    }

    public void viewConsultationDetails() {
        System.out.print("Enter Consultation ID: ");
        String id = sc.nextLine().trim();
        Consultation c = consultationMap.get(id);
        if (c == null) {
            System.out.println("Consultation not found.");
            return;
        }
        ListInterface<Consultation> one = new ArrayList<>();
        one.add(c);
        printConsultationsTable(one, "Consultation Details");
    }

    public void viewConsultationsByDoctor() {
        System.out.print("Enter Doctor ID: ");
        String id = sc.nextLine().trim();
        printConsultationsTable(getConsultationsByDoctor(id), "Consultations for Doctor " + id);
    }

    public void viewConsultationsByPatient() {
        System.out.print("Enter Patient ID: ");
        String id = sc.nextLine().trim();
        printConsultationsTable(getConsultationsByPatient(id), "Consultations for Patient " + id);
    }

    public void completeConsultationWithTreatment() {
        System.out.print("Enter Consultation ID to complete: ");
        String consultationId = sc.nextLine().trim();
        Consultation c = consultationMap.get(consultationId);
        if (c == null) {
            System.out.println("Consultation not found.");
            return;
        }
        System.out.print("Enter diagnosis: ");
        String diagnosis = sc.nextLine().trim();
        System.out.print("Enter treatment fee (e.g., 100.0): ");
        double fee = 0.0;
        try { fee = Double.parseDouble(sc.nextLine().trim()); } catch (Exception ignored) {}
        ListInterface<entity.MedicinePrescribed> meds = new ArrayList<>();
        completeConsultation(consultationId, diagnosis, fee, meds);
    }

    private void printConsultationsTable(ListInterface<Consultation> list, String title) {
        System.out.println("\n--- " + title + " ---");
        if (list.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        String border = "+--------------+------------+------------+-----------+";
        System.out.println(border);
        System.out.printf("| %-12s | %-10s | %-10s | %-9s |%n", "ConsultID", "PatientID", "DoctorID", "Status");
        System.out.println(border);
        for (int i = 0; i < list.size(); i++) {
            Consultation c = list.get(i);
            System.out.printf("| %-12s | %-10s | %-10s | %-9s |%n", c.getConsultationId(), c.getPatientId(),
                    c.getDoctorId() == null ? "N/A" : c.getDoctorId(), c.getStatus());
        }
        System.out.println(border);
    }

    // ==================== APPOINTMENT BOOKING SUPPORT METHODS ====================

    /**
     * Create consultation for appointment booking
     */
    public String createConsultation(String patientId, String specialty) {
        String consultationId = ConsultationDAO.generateConsultationId();
        Consultation consultation = new Consultation(consultationId, patientId, specialty);
        consultationMap.put(consultationId, consultation);
        ConsultationDAO.saveConsultations(consultationMap);
        return consultationId;
    }

    /**
     * Assign doctor to consultation for appointment booking
     */
    public void assignDoctor(String consultationId, String doctorId, String scheduleId, LocalDateTime consultationTime) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            consultation.assignDoctor(doctorId, scheduleId, consultationTime);
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
        }
    }
    
    // ==================== PAYMENT PROCESSING METHODS ====================
    
    /**
     * View consultations that require payment
     */
    public void viewConsultationsForPayment() {
        ListInterface<Consultation> consultationsForPayment = new ArrayList<>();
        
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            // Show consultations that are completed but don't have a payment ID
            if (c.getStatus().equals("COMPLETED") && c.getPaymentId() == null) {
                consultationsForPayment.add(c);
            }
        }
        
        if (consultationsForPayment.isEmpty()) {
            System.out.println("No consultations require payment.");
            return;
        }
        
        printConsultationsTable(consultationsForPayment, "Consultations Requiring Payment");
    }
    
    /**
     * Get count of consultations requiring payment
     */
    public int getConsultationsForPaymentCount() {
        int count = 0;
        for (String key : consultationMap.keySet()) {
            Consultation c = consultationMap.get(key);
            if (c.getStatus().equals("COMPLETED") && c.getPaymentId() == null) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Check if consultation is eligible for payment
     */
    public boolean isConsultationEligibleForPayment(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            return false;
        }
        // Consultation must be completed and not already paid
        return consultation.getStatus().equals("COMPLETED") && consultation.getPaymentId() == null;
    }
    
    /**
     * Process payment for a consultation
     */
    public boolean processConsultationPayment(String consultationId, double amount, 
                                           entity.Payment.PaymentMethod paymentMethod, 
                                           String referenceNumber, String notes) {
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
            
            // Create payment controller and process payment
            PaymentController paymentController = new PaymentController();
            
            // Get the invoice for this consultation
            Invoice invoice = paymentController.getInvoiceByConsultation(consultationId);
            if (invoice == null) {
                System.out.println("No invoice found for consultation: " + consultationId);
                return false;
            }
            
            // Create a new invoice with the correct amount if needed
            if (invoice.getAmount() != amount) {
                // Delete old invoice and create new one with correct amount
                paymentController.deleteInvoiceByConsultation(consultationId);
                paymentController.generateInvoice(consultationId, amount);
                invoice = paymentController.getInvoiceByConsultation(consultationId);
            }
            
            // Process the payment
            boolean paymentSuccess = paymentController.processPayment(invoice.getInvoiceId(), paymentMethod, 
                                                                   referenceNumber, notes);
            
            if (paymentSuccess) {
                // Update consultation with payment ID (get it from the payment map)
                String paymentId = getPaymentIdFromInvoice(invoice.getInvoiceId());
                consultation.setPayment(paymentId);
                consultationMap.put(consultationId, consultation);
                ConsultationDAO.saveConsultations(consultationMap);
                
                System.out.println("Payment processed successfully for consultation: " + consultationId);
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
    
    /**
     * Helper method to get payment ID from invoice ID
     */
    private String getPaymentIdFromInvoice(String invoiceId) {
        PaymentController paymentController = new PaymentController();
        HashMapInterface<String, Payment> payments = paymentController.getPaymentMap();
        
        for (String key : payments.keySet()) {
            Payment payment = payments.get(key);
            if (payment != null && payment.getInvoiceId().equals(invoiceId)) {
                return payment.getPaymentId();
            }
        }
        return null;
    }
}
