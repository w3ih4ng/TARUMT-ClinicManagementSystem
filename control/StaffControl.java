package control;

import boundary.*;

public class StaffControl {
    private PatientManagementBoundary patientManagementBoundary;
    private DoctorManagementBoundary doctorManagementBoundary;
    private PharmacyBoundary pharmacyBoundary;

    public StaffControl() {
        // Initialize modules staff can access
        this.patientManagementBoundary = new PatientManagementBoundary(new PatientRecordControl(),new PatientQueueControl());
        this.doctorManagementBoundary = new DoctorManagementBoundary(new DoctorRecordControl());
        this.pharmacyBoundary = new PharmacyBoundary(new PharmacyControl());
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
        pharmacyBoundary.mainMenu();
    }
}
