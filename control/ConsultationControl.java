package control;

import entity.*;
import adt.*;
import dao.ConsultationDAO;
import dao.PatientQueueDAO;
import dao.DoctorDAO;
import dao.PatientDAO;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Control class for consultation management
 * @author Your Name
 */
public class ConsultationControl {
    private HashMapInterface<String, Consultation> consultationMap;
    private HashMapInterface<String, PatientQueueEntry> queueMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private HashMapInterface<String, Patient> patientMap;
    private Scanner sc;

    public ConsultationControl() {
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
        PatientQueueEntry entry = queueMap.get(queueId);
        if (entry != null && entry.isAssigned() && entry.getAssignedDoctorId() != null) {
            String consultationId = ConsultationDAO.generateConsultationId();
            Consultation consultation = new Consultation(consultationId, entry.getPatientId(), entry.getSpecialty(), queueId);
            
            // Assign doctor and set consultation time
            consultation.assignDoctor(entry.getAssignedDoctorId(), null, LocalDateTime.now());
            
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            System.out.println("Consultation created: " + consultationId);
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

        // Create treatment using TreatmentControl
        control.TreatmentControl treatmentControl = new control.TreatmentControl();
        String treatmentId = treatmentControl.createTreatment(
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

        // Store completed queue entry in history instead of removing
        String queueId = consultation.getQueueId();
        if (queueId != null) {
            PatientQueueEntry queueEntry = queueMap.get(queueId);
            if (queueEntry != null) {
                // Store in history
                control.QueueHistoryControl historyControl = new control.QueueHistoryControl();
                historyControl.storeCompletedQueueEntry(queueEntry, "CONSULTATION_COMPLETED");
                
                // Remove from active queue
                queueMap.remove(queueId);
                PatientQueueDAO.savePatientQueue(queueMap);
                System.out.println("Patient " + consultation.getPatientId() + " moved to history after completion");
            }
        }

        // Automatically generate invoice for payment
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
            control.TreatmentControl treatmentControl = new control.TreatmentControl();
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
            control.InvoiceControl invoiceControl = new control.InvoiceControl();
            invoiceControl.deleteInvoiceByConsultation(consultationId);
        } catch (Exception e) {
            // Ignore if invoice doesn't exist or deletion fails
        }
    }

    // --- Generate invoice for completed consultation ---
    private void generateInvoiceForConsultation(String consultationId) {
        try {
            // Create InvoiceControl instance to generate invoice
            control.InvoiceControl invoiceControl = new control.InvoiceControl();
            entity.Invoice invoice = invoiceControl.generateInvoice(consultationId);
            
            if (invoice != null) {
                System.out.println("Invoice generated automatically for consultation: " + consultationId);
                System.out.println("   Invoice ID: " + invoice.getInvoiceId());
                System.out.println("   Amount: RM " + String.format("%.2f", invoice.getAmount()));
            } else {
                System.out.println("Failed to generate invoice for consultation: " + consultationId);
            }
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
                control.QueueHistoryControl historyControl = new control.QueueHistoryControl();
                historyControl.storeCompletedQueueEntry(queueEntry, "CONSULTATION_COMPLETED");
                
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
                    if (c.getPatientId().equals(entry.getPatientId()) && 
                        c.getDoctorId() != null && 
                        c.getDoctorId().equals(entry.getAssignedDoctorId())) {
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
}
