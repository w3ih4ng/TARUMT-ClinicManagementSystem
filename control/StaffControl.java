package control;

import boundary.*;

public class StaffControl {
    private PatientManagementBoundary patientManagementBoundary;
    private DoctorManagementBoundary doctorManagementBoundary;
    private PharmacyBoundary pharmacyBoundary;
    private DoctorScheduleBoundary doctorScheduleBoundary;

    public StaffControl() {
        // Initialize modules staff can access
        ConsultationControl consultationControl = new ConsultationControl();
        this.patientManagementBoundary = new PatientManagementBoundary(new PatientRecordControl(), new PatientQueueControl(consultationControl));
        this.doctorManagementBoundary = new DoctorManagementBoundary(new DoctorRecordControl());
        this.pharmacyBoundary = new PharmacyBoundary(new PharmacyControl());
        this.doctorScheduleBoundary = new DoctorScheduleBoundary(new DoctorScheduleControl(), new DoctorRecordControl());
    }

    public void openPatientModule() {
        patientManagementBoundary.mainMenu();
    }

    public void openDoctorManagementModule() {
        doctorManagementBoundary.mainMenu();
    }

    public void openConsultationModule() {
        new ConsultationMenuBoundary(new ConsultationControl()).mainMenu();
    }

    public void openDoctorScheduleModule() {
        doctorScheduleBoundary.mainMenu();
    }

    public void openTreatmentModule() {
        System.out.println("[Medical Treatment Module - placeholder]");
        // later: new TreatmentBoundary(new TreatmentControl()).mainMenu();
    }

    public void openPharmacyModule() {
        pharmacyBoundary.mainMenu();
    }
}
