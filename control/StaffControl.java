package control;

import boundary.PatientManagementBoundary;
import boundary.DoctorManagementBoundary;

public class StaffControl {
    private PatientManagementBoundary patientManagementBoundary;
    private DoctorManagementBoundary doctorManagementBoundary;

    public StaffControl() {
        // Initialize modules staff can access
        this.patientManagementBoundary = new PatientManagementBoundary(new PatientControl(),new PatientQueueControl());
        this.doctorManagementBoundary = new DoctorManagementBoundary(new DoctorControl());
    }

    public void openPatientModule() {
        patientManagementBoundary.mainMenu();
    }

    public void openDoctorManagementModule() {
        doctorManagementBoundary.mainMenu();
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
