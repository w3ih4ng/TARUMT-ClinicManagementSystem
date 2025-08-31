package entity;

/**
 * Enum representing the status of a doctor
 * @author Your Name
 */
public enum DoctorStatus {
    ACTIVE("Active"),
    VACATION("Vacation"),
    INACTIVE("Inactive");

    private final String displayName;

    DoctorStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
