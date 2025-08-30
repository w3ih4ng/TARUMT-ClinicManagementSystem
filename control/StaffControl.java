package control;

import boundary.*;

/**
 * Control class for staff management and coordination
 * @author Your Name
 */
public class StaffControl {
    // Core 5 Module Boundaries
    private PatientUI patientManagementBoundary;          // Module 1
    private DoctorUI doctorManagementBoundary;            // Module 2  
    private ConsultationUI consultationManagementBoundary; // Module 3
    private TreatmentUI treatmentManagementBoundary;      // Module 4
    private PharmacyUI pharmacyBoundary;                  // Module 5
    
    private ConsultationController sharedConsultationControl; // Shared instance
    public DoctorController sharedDoctorController; // Shared doctor instance

    public StaffControl(ConsultationController consultationControl) {
        // Use the shared consultation control from main system
        this.sharedConsultationControl = consultationControl;
        
        // Create shared doctor controller instance
        this.sharedDoctorController = new DoctorController();
        
        // Initialize the 5 core modules with shared instances
        this.patientManagementBoundary = new PatientUI(new PatientController(sharedConsultationControl), sharedDoctorController);
        this.doctorManagementBoundary = new DoctorUI(sharedDoctorController);
        this.consultationManagementBoundary = new ConsultationUI(sharedConsultationControl, sharedDoctorController);
        this.treatmentManagementBoundary = new TreatmentUI(new TreatmentController(), new PatientController(sharedConsultationControl), sharedDoctorController, sharedConsultationControl);
        this.pharmacyBoundary = new PharmacyUI(new PharmacyController());
    }

    // ==================== MODULE 1: PATIENT MANAGEMENT ====================
    public void openPatientModule() {
        patientManagementBoundary.mainMenu();
    }

    // ==================== MODULE 2: DOCTOR MANAGEMENT ====================
    public void openDoctorManagementModule() {
        doctorManagementBoundary.mainMenu();
    }

    // ==================== MODULE 3: CONSULTATION MANAGEMENT ====================
    public void openConsultationModule() {
        consultationManagementBoundary.mainMenu();
    }

    // ==================== MODULE 4: MEDICAL TREATMENT MANAGEMENT ====================
    public void openTreatmentModule() {
        treatmentManagementBoundary.mainMenu();
    }

    // ==================== MODULE 5: PHARMACY MANAGEMENT ====================
    public void openPharmacyModule() {
        pharmacyBoundary.mainMenu();
    }
}
