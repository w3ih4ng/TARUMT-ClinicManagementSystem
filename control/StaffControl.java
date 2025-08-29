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
    private PaymentBoundary paymentBoundary;

    public StaffControl() {
        // Initialize modules staff can access
        ConsultationControl consultationControl = new ConsultationControl();
        this.patientManagementBoundary = new PatientManagementBoundary(new PatientRecordControl(), new PatientQueueControl(consultationControl));
        this.doctorManagementBoundary = new DoctorManagementBoundary(new DoctorRecordControl());
        this.pharmacyBoundary = new PharmacyBoundary(new PharmacyControl());
        this.doctorScheduleBoundary = new DoctorScheduleBoundary(new DoctorScheduleControl(), new DoctorRecordControl(), new PatientQueueControl(consultationControl));
        this.paymentBoundary = new PaymentBoundary(new PaymentControl(), new InvoiceControl());
    }

    public void openPatientModule() {
        patientManagementBoundary.mainMenu();
    }

    public void openDoctorManagementModule() {
        doctorManagementBoundary.mainMenu();
    }

    public void openConsultationModule() {
        control.ConsultationControl consultationControl = new control.ConsultationControl();
        control.TreatmentControl treatmentControl = new control.TreatmentControl();
        control.PatientQueueControl queueControl = new control.PatientQueueControl(consultationControl);
        control.ConsultationMenuControl consultationMenuControl = new control.ConsultationMenuControl(consultationControl, treatmentControl, queueControl);
        new boundary.ConsultationMenuBoundary(consultationMenuControl).mainMenu();
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

    public void openPaymentModule() {
        paymentBoundary.mainMenu();
    }
}
