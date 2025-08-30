package control;

import boundary.*;
import control.*;
import control.ConsultationMenuControl;

/**
 * Control class for staff management and coordination
 * @author Your Name
 */
public class StaffControl {
    private PatientManagementBoundary patientManagementBoundary;
    private DoctorManagementBoundary doctorManagementBoundary;
    private PharmacyBoundary pharmacyBoundary;
    private DoctorScheduleBoundary doctorScheduleBoundary;
    private TreatmentManagementBoundary treatmentManagementBoundary;
    private PaymentBoundary paymentBoundary;
    private ConsultationControl sharedConsultationControl; // Shared instance

    public StaffControl(ConsultationControl consultationControl) {
        // Use the shared consultation control from main system
        this.sharedConsultationControl = consultationControl;
        
        // Initialize modules staff can access with shared instance
        this.patientManagementBoundary = new PatientManagementBoundary(new PatientRecordControl(), new PatientQueueControl(sharedConsultationControl));
        this.doctorManagementBoundary = new DoctorManagementBoundary(new DoctorRecordControl());
        this.treatmentManagementBoundary = new TreatmentManagementBoundary(new TreatmentControl(), new PatientRecordControl(), new DoctorRecordControl());
        this.pharmacyBoundary = new PharmacyBoundary(new PharmacyControl());
        this.doctorScheduleBoundary = new DoctorScheduleBoundary(new DoctorScheduleControl(), new DoctorRecordControl(), new PatientQueueControl(sharedConsultationControl));
        this.paymentBoundary = new PaymentBoundary(new PaymentControl(), new InvoiceControl());
    }

    public void openPatientModule() {
        patientManagementBoundary.mainMenu();
    }

    public void openDoctorManagementModule() {
        doctorManagementBoundary.mainMenu();
    }

    public void openConsultationModule() {
        // Use shared consultation control instance
        control.TreatmentControl treatmentControl = new control.TreatmentControl();
        control.PatientQueueControl queueControl = new control.PatientQueueControl(sharedConsultationControl);
        control.ConsultationMenuControl consultationMenuControl = new control.ConsultationMenuControl(sharedConsultationControl, treatmentControl, queueControl);
        new boundary.ConsultationMenuBoundary(consultationMenuControl).mainMenu();
    }

    public void openDoctorScheduleModule() {
        doctorScheduleBoundary.mainMenu();
    }

    public void openTreatmentModule() {
        treatmentManagementBoundary.mainMenu();
    }

    public void openPharmacyModule() {
        pharmacyBoundary.mainMenu();
    }

    public void openPaymentModule() {
        paymentBoundary.mainMenu();
    }
}
