package entity;

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
