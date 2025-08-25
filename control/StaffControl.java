package control;

import boundary.PatientManagementBoundary;
import boundary.DoctorMenuBoundary;

public class StaffControl {
    private PatientManagementBoundary patientManagementBoundary;
    private DoctorMenuBoundary doctorMenuBoundary;

    public StaffControl() {
        // Initialize modules staff can access
        this.patientManagementBoundary = new PatientManagementBoundary(new PatientControl());
        this.doctorMenuBoundary = new DoctorMenuBoundary(new DoctorControl());
    }

    public void openPatientModule() {
        patientManagementBoundary.mainMenu();
    }

    public void openDoctorModule() {
        doctorMenuBoundary.mainMenu();
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
