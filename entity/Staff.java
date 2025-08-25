package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Staff extends Patient {
    private String staffId; // staff identity
    private String department;

    public Staff(String patientId, String staffId, String name, String gender, LocalDate birthdate, String phoneNumber,
            String department) {
        super(patientId, name, gender, birthdate, phoneNumber);
        this.staffId = staffId;
        this.department = department;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getDepartment() {
        return department;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return super.toString() + " " +
                staffId + " " +
                department;
    }

}
