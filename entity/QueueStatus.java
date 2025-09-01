package entity;

/**
 * Enum defining patient queue status
 * @author Your Name
 */
public enum QueueStatus {
    WAITING,           // Patient waiting for doctor assignment
    ASSIGNED,          // Doctor assigned, ready for consultation creation
    IN_CONSULTATION,   // Consultation in progress
    CANCELLED,
    COMPLETED;         // Queue entry completed

    @Override
    public String toString() {
        // Pretty print with spaces
        return name().replace("_", " ");
    }
}
