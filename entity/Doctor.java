package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Doctor extends Human {
    private String doctorId;
    private Specialty specialty; // changed to enum
    private double consultationFee;

    public Doctor(String doctorId, String name, String gender, LocalDate birthdate, String phoneNumber,
            Specialty specialty, double consultationFee) {
        super(name, gender, birthdate, phoneNumber);
        this.doctorId = doctorId;
        this.specialty = specialty;
        this.consultationFee = consultationFee;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return doctorId + " " +
                name + " " +
                gender + " " +
                phoneNumber + " " +
                birthdate.format(fmt) + " " +
                specialty + " " +
                String.format("%.2f", consultationFee);
    }

}
