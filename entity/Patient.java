package entity;

import java.time.LocalDate;

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
        return patientId + " " +
                name + " " +
                gender + " " +
                phoneNumber + " " +
                birthdate;
    }

}
