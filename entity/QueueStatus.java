package entity;

/**
 * Enum defining patient queue status
 * @author Your Name
 */
public enum QueueStatus {
    WAITING,           // Patient waiting for doctor assignment
    ASSIGNED,          // Doctor assigned, consultation created
    IN_CONSULTATION,   // Consultation in progress
    TREATMENT_CREATED, // Treatment created, medicines prescribed
            MEDICINES_DISPENSED, // Medicines dispensed, ready for payment
            COMPLETED;         // Ready for payment

    @Override
    public String toString() {
        // Pretty print with spaces
        return name().replace("_", " ");
    }
}
