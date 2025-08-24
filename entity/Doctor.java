package entity;

public class Doctor extends Human {
    private String doctorId;  // system doctor ID
    private String specialty;

    public Doctor(String doctorId, String name, String gender, String birthdate, String phoneNumber, String specialty) {
        super(name, gender, birthdate, phoneNumber);
        this.doctorId = doctorId;
        this.specialty = specialty;
    }

    public String getDoctorId() { return doctorId; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String toString() {
        return String.format("DoctorID: %s, %s, Specialty: %s", doctorId, super.toString(), specialty);
    }
}
