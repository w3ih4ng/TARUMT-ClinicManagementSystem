package control;

import boundary.PatientBoundary;
import boundary.DoctorBoundary;

public class StaffControl {
    private PatientBoundary patientBoundary;
    private DoctorBoundary doctorBoundary;

    public StaffControl() {
        // Initialize modules staff can access
        this.patientBoundary = new PatientBoundary(new PatientControl());
        this.doctorBoundary = new DoctorBoundary(new DoctorControl());
    }

    public void openPatientModule() {
        patientBoundary.mainMenu();
    }

    public void openDoctorModule() {
        doctorBoundary.mainMenu();
    }

    public void openConsultationModule() {
        System.out.println("[Consultation Module - placeholder]");
        // later: new ConsultationBoundary(new ConsultationControl()).mainMenu();
    }

    public void openTreatmentModule() {
        System.out.println("[Medical Treatment Module - placeholder]");
        // later: new TreatmentBoundary(new TreatmentControl()).mainMenu();
    }

    public void openPharmacyModule() {
        System.out.println("[Pharmacy Module - placeholder]");
        // later: new PharmacyBoundary(new PharmacyControl()).mainMenu();
    }
}
