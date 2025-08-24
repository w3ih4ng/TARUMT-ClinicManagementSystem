package entity;

public class Staff extends Patient {
    private String staffId; // staff identity

    public Staff(String patientId, String staffId, String name, String gender, String birthdate, String phoneNumber) {
        super(patientId, name, gender, birthdate, phoneNumber);
        this.staffId = staffId;
    }

    public String getStaffId() { return staffId; }

    @Override
    public String toString() {
        return super.toString() + String.format(" [StaffID: %s] [Staff]", staffId);
    }
}
