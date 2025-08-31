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
    private String status; // PENDING, SCHEDULED, TREATMENT_CREATED, MEDICINES_DISPENSED, COMPLETED
    private String queueId; // new: back-reference to PatientQueueEntry

    public Consultation(String consultationId, String patientId, String specialty) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.specialty = specialty;
        this.status = "PENDING";
    }
    // new overloaded constructor when queueId known
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

    public void completeConsultation(String treatmentId) {
        this.treatmentId = treatmentId;
        this.status = "COMPLETED";
    }
    
    public void markTreatmentCreated(String treatmentId) {
        this.treatmentId = treatmentId;
        this.status = "TREATMENT_CREATED";
    }
    
    public void markMedicinesDispensed() {
        this.status = "MEDICINES_DISPENSED";
    }
    
    public void markFullyCompleted() {
        this.status = "COMPLETED";
    }

    public void setPayment(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
