package entity;

public enum QueueType {
    WALK_IN,
    APPOINTMENT;

    @Override
    public String toString() {
        // Pretty print with spaces
        return name().replace("_", " ");
    }
}
