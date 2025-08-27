package entity;

import java.util.ArrayList;
import java.util.List;

public class Treatment {
    private String treatmentId;
    private String doctorId;
    private String patientId;
    private String consultationId;
    private String description; // doctor's notes / diagnosis
    private double treatmentFee; // cost of the treatment itself
    private List<MedicinePrescribed> prescribedMedicines;

    public Treatment(String treatmentId, String doctorId, String patientId, String consultationId, String description,
            double treatmentFee) {
        this.treatmentId = treatmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.consultationId = consultationId;
        this.description = description;
        this.treatmentFee = treatmentFee;
        this.prescribedMedicines = new ArrayList<>();
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getDescription() {
        return description;
    }

    public double getTreatmentFee() {
        return treatmentFee;
    }

    public List<MedicinePrescribed> getPrescribedMedicines() {
        return prescribedMedicines;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTreatmentFee(double treatmentFee) {
        this.treatmentFee = treatmentFee;
    }

    public void addPrescribedMedicine(MedicinePrescribed medicine) {
        prescribedMedicines.add(medicine);
    }
}
