package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract patient entity extending Human
 * @author Your Name
 */
public abstract class Patient extends Human {
    protected String patientId; // system ID for patient

    public Patient(String patientId, String name, String gender, LocalDate birthdate, String phoneNumber) {
        super(name, gender, birthdate, phoneNumber);
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return patientId + " " +
                name + " " +
                gender + " " +
                phoneNumber + " " +
                birthdate.format(fmt);
    }

}
