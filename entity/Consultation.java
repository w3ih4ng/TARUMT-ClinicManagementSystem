package entity;

import java.time.LocalDateTime;

/**
 * Consultation entity representing doctor-patient consultations
 * @author Your Name
 */
public class Consultation {
    private String consultationId;
    private String patientId;
    private String doctorId;
    private String specialty;
    private String scheduleId; // linked to DoctorSchedule
    private LocalDateTime consultationTime;
    private String treatmentId; // null until doctor creates treatment
    private String paymentId; // null until payment made
    private String status; // PENDING, SCHEDULED, IN_PROGRESS, TREATMENT_CREATED, MEDICINE_PRESCRIBED, MEDICINE_DISPENSED, COMPLETED
    private String queueId; // optional: back-reference to PatientQueueEntry if created from queue
    private boolean isDeleted; // soft delete flag

    public Consultation(String consultationId, String patientId, String specialty) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.specialty = specialty;
        this.status = "PENDING";
        this.isDeleted = false; // Initialize to false
    }
    
    // Constructor when queueId is known
    public Consultation(String consultationId, String patientId, String specialty, String queueId) {
        this(consultationId, patientId, specialty);
        this.queueId = queueId;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }

    public void assignDoctor(String doctorId, String scheduleId, LocalDateTime consultationTime) {
        this.doctorId = doctorId;
        this.scheduleId = scheduleId;
        this.consultationTime = consultationTime;
        this.status = "SCHEDULED";
    }

    public void startConsultation() {
        this.status = "IN_PROGRESS";
    }
    
    public void completeConsultation(String treatmentId) {
        this.treatmentId = treatmentId;
        this.status = "COMPLETED";
    }

    // Method to set status directly (used when loading from file)
    public void setStatus(String status) {
        this.status = status;
    }

    public void setPayment(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    // Method to update status based on medicine prescription
    public void updateStatusBasedOnMedicinePrescription(boolean hasMedicines) {
        if (this.treatmentId != null) {
            if (hasMedicines) {
                this.status = "MEDICINE_PRESCRIBED";
            } else {
                this.status = "TREATMENT_CREATED";
            }
        }
    }

    // Soft delete methods
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }
}
