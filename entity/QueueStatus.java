package entity;

/**
 * Enum defining patient queue status
 * @author Your Name
 */
public enum QueueStatus {
    WAITING,
    ASSIGNED,
    COMPLETED;

    @Override
    public String toString() {
        // Pretty print with spaces
        return name().replace("_", " ");
    }
}
