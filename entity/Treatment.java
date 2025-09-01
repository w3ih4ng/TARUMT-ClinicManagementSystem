package entity;

import adt.ListInterface;
import adt.ArrayList;

/**
 * Treatment entity class representing medical treatment records
 * @author Your Name
 */
public class Treatment {
    private String treatmentId;
    private String doctorId;
    private String patientId;
    private String consultationId;
    private String description; // doctor's notes / diagnosis
    private double treatmentFee; // cost of the treatment itself
    private ListInterface<MedicinePrescribed> prescribedMedicines;
    private boolean isDeleted; // soft delete flag

    public Treatment(String treatmentId, String doctorId, String patientId, String consultationId, String description,
            double treatmentFee) {
        this.treatmentId = treatmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.consultationId = consultationId;
        this.description = description;
        this.treatmentFee = treatmentFee;
        this.prescribedMedicines = new ArrayList<>();
        this.isDeleted = false;
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

    public ListInterface<MedicinePrescribed> getPrescribedMedicines() {
        return prescribedMedicines;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTreatmentFee(double treatmentFee) {
        this.treatmentFee = treatmentFee;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getDiagnosis() {
        return description;
    }

    public void setDiagnosis(String diagnosis) {
        this.description = diagnosis;
    }

    public void addPrescribedMedicine(MedicinePrescribed medicine) {
        prescribedMedicines.add(medicine);
    }

    // Business methods for soft delete
    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }
}
