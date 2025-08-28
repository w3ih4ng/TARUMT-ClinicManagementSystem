package entity;

/**
 * Enum defining patient queue types
 * @author Your Name
 */
public enum QueueType {
    WALK_IN,
    APPOINTMENT;

    @Override
    public String toString() {
        // Pretty print with spaces
        return name().replace("_", " ");
    }
}
