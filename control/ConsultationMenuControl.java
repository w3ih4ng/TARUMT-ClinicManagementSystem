package control;

import entity.*;
import adt.*;
import java.util.Scanner;
import java.time.LocalDateTime;

/**
 * Control class for consultation menu business logic
 * @author Your Name
 */
public class ConsultationMenuControl {
    private ConsultationControl consultationControl;
    private TreatmentControl treatmentControl;
    private PatientQueueControl queueControl;
    private Scanner sc;

    public ConsultationMenuControl(ConsultationControl consultationControl, 
                                 TreatmentControl treatmentControl,
                                 PatientQueueControl queueControl) {
        this.consultationControl = consultationControl;
        this.treatmentControl = treatmentControl;
        this.queueControl = queueControl;
        this.sc = new Scanner(System.in);
    }

    /**
     * Complete consultation with treatment details
     */
    public void completeConsultationWithTreatment() {
        System.out.println("\n--- Complete Consultation with Treatment ---");
        
        // Show pending consultations
        adt.ListInterface<entity.Consultation> pendingConsultations = consultationControl.getPendingConsultations();
        if (pendingConsultations.isEmpty()) {
            System.out.println("No pending consultations found.");
            return;
        }

        // Display consultations
        consultationControl.displayConsultationsTable(pendingConsultations, "Pending Consultations");

        // Get consultation ID
        System.out.print("\nEnter Consultation ID to complete: ");
        String consultationId = sc.nextLine().trim();
        
        entity.Consultation consultation = consultationControl.getConsultationById(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return;
        }

        if (!consultation.getStatus().equals("PENDING")) {
            System.out.println("Consultation is not pending. Current status: " + consultation.getStatus());
            return;
        }

        // Get treatment details
        System.out.print("Enter diagnosis: ");
        String diagnosis = sc.nextLine().trim();
        
        System.out.print("Enter treatment fee: RM ");
        double treatmentFee;
        try {
            treatmentFee = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid fee amount. Please enter a valid number.");
            return;
        }

        // Get prescribed medicines
        adt.ListInterface<entity.MedicinePrescribed> medicines = getPrescribedMedicines();
        if (medicines == null) {
            System.out.println("Medicine prescription cancelled.");
            return;
        }

        // Complete consultation
        consultationControl.completeConsultation(consultationId, diagnosis, treatmentFee, medicines);
    }

    /**
     * Get prescribed medicines from user
     */
    private adt.ListInterface<entity.MedicinePrescribed> getPrescribedMedicines() {
        adt.ListInterface<entity.MedicinePrescribed> medicines = new adt.ArrayList<>();
        
        System.out.println("\n--- Prescribe Medicines ---");
        System.out.println("Enter medicine details (type 'done' when finished):");
        
        while (true) {
            System.out.print("\nMedicine ID (or 'done' to finish): ");
            String medicineId = sc.nextLine().trim();
            
            if (medicineId.equalsIgnoreCase("done")) {
                break;
            }
            
            System.out.print("Quantity: ");
            int quantity;
            try {
                quantity = Integer.parseInt(sc.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Please enter a valid number.");
                continue;
            }
            
            // Create MedicinePrescribed object
            entity.MedicinePrescribed medicine = new entity.MedicinePrescribed(medicineId, quantity);
            medicines.add(medicine);
            
            System.out.println("✅ Medicine " + medicineId + " x" + quantity + " added");
        }
        
        return medicines;
    }

    /**
     * View consultation details
     */
    public void viewConsultationDetails() {
        System.out.println("\n--- View Consultation Details ---");
        
        // Show all consultations
        adt.ListInterface<entity.Consultation> allConsultations = consultationControl.getConsultationMap().toList();
        if (allConsultations.isEmpty()) {
            System.out.println("No consultations found.");
            return;
        }

        consultationControl.displayConsultationsTable(allConsultations, "All Consultations");

        // Get consultation ID
        System.out.print("\nEnter Consultation ID to view details: ");
        String consultationId = sc.nextLine().trim();
        
        consultationControl.displayConsultationDetails(consultationId);
        
        // Show treatment details if exists
        entity.Consultation consultation = consultationControl.getConsultationById(consultationId);
        if (consultation != null && consultation.getTreatmentId() != null) {
            System.out.println("\n--- Treatment Details ---");
            treatmentControl.displayTreatmentDetails(consultation.getTreatmentId());
        }
    }

    /**
     * View all consultations
     */
    public void viewAllConsultations() {
        adt.ListInterface<entity.Consultation> allConsultations = consultationControl.getConsultationMap().toList();
        if (allConsultations.isEmpty()) {
            System.out.println("\nNo consultations found in the system.");
        } else {
            consultationControl.displayConsultationsTable(allConsultations, "All Consultations");
        }
    }

    /**
     * View pending consultations
     */
    public void viewPendingConsultations() {
        adt.ListInterface<entity.Consultation> pendingConsultations = consultationControl.getPendingConsultations();
        if (pendingConsultations.isEmpty()) {
            System.out.println("\nNo pending consultations found.");
        } else {
            consultationControl.displayConsultationsTable(pendingConsultations, "Pending Consultations");
        }
    }

    /**
     * View completed consultations
     */
    public void viewCompletedConsultations() {
        adt.ListInterface<entity.Consultation> completedConsultations = consultationControl.getCompletedConsultations();
        if (completedConsultations.isEmpty()) {
            System.out.println("\nNo completed consultations found.");
        } else {
            consultationControl.displayConsultationsTable(completedConsultations, "Completed Consultations");
        }
    }

    /**
     * View consultations by doctor
     */
    public void viewConsultationsByDoctor() {
        System.out.println("\n--- View Consultations by Doctor ---");
        
        // Show available doctors
        System.out.println("Available Doctors:");
        // This would need to be implemented to show doctor list
        
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        adt.ListInterface<entity.Consultation> doctorConsultations = consultationControl.getConsultationsByDoctor(doctorId);
        if (doctorConsultations.isEmpty()) {
            System.out.println("No consultations found for doctor: " + doctorId);
        } else {
            consultationControl.displayConsultationsTable(doctorConsultations, "Consultations for Dr. " + doctorId);
        }
    }

    /**
     * View consultations by patient
     */
    public void viewConsultationsByPatient() {
        System.out.println("\n--- View Consultations by Patient ---");
        
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        
        adt.ListInterface<entity.Consultation> patientConsultations = consultationControl.getConsultationsByPatient(patientId);
        if (patientConsultations.isEmpty()) {
            System.out.println("No consultations found for patient: " + patientId);
        } else {
            consultationControl.displayConsultationsTable(patientConsultations, "Consultations for Patient " + patientId);
        }
    }

    /**
     * Get treatment control for other uses
     */
    public TreatmentControl getTreatmentControl() {
        return treatmentControl;
    }
    
    /**
     * Get consultation control for other uses
     */
    public ConsultationControl getConsultationControl() {
        return consultationControl;
    }
}
