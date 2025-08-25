package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Patient extends Human {
    protected String patientId; // system ID for patient

    public Patient(String patientId, String name, String gender, LocalDate birthdate, String phoneNumber) {
        super(name, gender, birthdate, phoneNumber);
        this.patientId = patientId;
    }

    public String getPatientId() { return patientId; }

    @Override
    public String toString() {
        return String.format("PatientID: %s, %s", patientId, super.toString());
    }
}
