package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Doctor extends Human {
    private String doctorId;  // system doctor ID
    private String specialty;

    public Doctor(String doctorId, String name, String gender, LocalDate birthdate, String phoneNumber, String specialty) {
        super(name, gender, birthdate, phoneNumber);
        this.doctorId = doctorId;
        this.specialty = specialty;
    }

    public String getDoctorId() { return doctorId; }
    public String getSpecialty() { return specialty; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String toString() {
        return String.format("DoctorID: %s, %s, Specialty: %s", doctorId, super.toString(), specialty);
    }
}
