package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Patient queue entry entity
 * @author Your Name
 */
public class PatientQueueEntry {
    private String queueId;
    private String patientId;
    private String specialty;
    private QueueType queueType;
    private LocalDateTime arrivalTime;
    private QueueStatus queueStatus;
    private LocalDateTime scheduledStartTime; // null for walk-ins or unknown
    private String assignedDoctorId; // set when doctor assigned

    public PatientQueueEntry(String queueId, String patientId, String specialty, QueueType queueType, LocalDateTime arrivalTime) {
        this.queueId = queueId;
        this.patientId = patientId;
        this.specialty = specialty;
        this.queueType = queueType;
        this.arrivalTime = arrivalTime;
        this.queueStatus = QueueStatus.WAITING;
    }

    // Getters
    public String getQueueId() { return queueId; }
    public String getPatientId() { return patientId; }
    public String getSpecialty() { return specialty; }
    public QueueType getQueueType() { return queueType; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public QueueStatus getQueueStatus() { return queueStatus; }
    public LocalDateTime getScheduledStartTime() { return scheduledStartTime; }
    public String getAssignedDoctorId() { return assignedDoctorId; }

    // Setters
    public void setQueueStatus(QueueStatus status) { this.queueStatus = status; }
    public void setAssignedDoctorId(String doctorId) { this.assignedDoctorId = doctorId; }
    public void setScheduledStartTime(LocalDateTime t) { this.scheduledStartTime = t; }

    // Business methods
    public void assignToDoctor(String doctorId) {
        this.assignedDoctorId = doctorId;
        this.queueStatus = QueueStatus.ASSIGNED;
    }
    
    public void startConsultation() {
        this.queueStatus = QueueStatus.IN_CONSULTATION;
    }
    
    public void complete() {
        this.queueStatus = QueueStatus.COMPLETED;
    }

    public boolean isWaiting() {
        return this.queueStatus == QueueStatus.WAITING;
    }

    public boolean isAssigned() {
        return this.queueStatus == QueueStatus.ASSIGNED;
    }

    public boolean isInConsultation() {
        return this.queueStatus == QueueStatus.IN_CONSULTATION;
    }

    public boolean isCompleted() {
        return this.queueStatus == QueueStatus.COMPLETED;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String doctorInfo = assignedDoctorId != null ? " | Dr: " + assignedDoctorId : "";
        
        return String.format("Queue: %s | Patient: %s | %s | %s | %s | %s%s", 
            queueId, patientId, specialty, queueType, 
            arrivalTime.format(formatter), queueStatus, doctorInfo);
    }
}
