package entity;

public class PatientQueueEntry {
    private int queueNo;
    private String patientId;

    public PatientQueueEntry(int queueNo, String patientId) {
        this.queueNo = queueNo;
        this.patientId = patientId;
    }

    public int getQueueNo() {
        return queueNo;
    }

    public String getPatientId() {
        return patientId;
    }

    @Override
    public String toString() {
        return String.format("Queue No: %d | Patient ID: %s", queueNo, patientId);
    }
}
