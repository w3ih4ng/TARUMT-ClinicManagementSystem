package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatientQueueEntry {
    private String queueId;
    private String patientId;
    private String specialty;
    private QueueType queueType;
    private LocalDateTime arrivalTime;
    private QueueStatus queueStatus;
    private String assignedDoctorId; // null if not assigned yet

    public PatientQueueEntry(String queueId, String patientId, String specialty, QueueType queueType) {
        this.queueId = queueId;
        this.patientId = patientId;
        this.specialty = specialty;
        this.queueType = queueType;
        this.arrivalTime = LocalDateTime.now();
        this.queueStatus = QueueStatus.WAITING;
        this.assignedDoctorId = null;
    }

    // Getters
    public String getQueueId() { return queueId; }
    public String getPatientId() { return patientId; }
    public String getSpecialty() { return specialty; }
    public QueueType getQueueType() { return queueType; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public QueueStatus getQueueStatus() { return queueStatus; }
    public String getAssignedDoctorId() { return assignedDoctorId; }

    // Setters
    public void setQueueStatus(QueueStatus status) { this.queueStatus = status; }
    public void setAssignedDoctorId(String doctorId) { this.assignedDoctorId = doctorId; }

    // Business methods
    public void assignToDoctor(String doctorId) {
        this.assignedDoctorId = doctorId;
        this.queueStatus = QueueStatus.ASSIGNED;
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
