package boundary;

import control.ConsultationControl;
import adt.ListInterface;
import adt.ArrayList;
import entity.Consultation;
import entity.MedicinePrescribed;
import java.util.Scanner;

/**
 * Boundary class for consultation management user interface
 * @author Your Name
 */
public class ConsultationMenuBoundary {
    private Scanner sc;
    private ConsultationControl consultationControl;

    public ConsultationMenuBoundary(ConsultationControl consultationControl) {
        this.sc = new Scanner(System.in);
        this.consultationControl = consultationControl;
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("    Consultation Management    ");
            System.out.println("==============================");
            System.out.println("1. View All Consultations");
            System.out.println("2. View Pending Consultations");
            System.out.println("3. View Completed Consultations");
            System.out.println("4. View Consultation Details");
            System.out.println("5. Complete Consultation (Doctor)");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": viewAllConsultations(); break;
                case "2": viewPendingConsultations(); break;
                case "3": viewCompletedConsultations(); break;
                case "4": viewConsultationDetails(); break;
                case "5": completeConsultation(); break;
                case "0": return;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    public void viewAllConsultations() {
        ListInterface<Consultation> allConsultations = consultationControl.getConsultationMap().toList();
        if (allConsultations.isEmpty()) {
            System.out.println("\nNo consultations found in the system.");
            return;
        }
        consultationControl.displayConsultationsTable(allConsultations, "All Consultations");
    }

    public void viewPendingConsultations() {
        ListInterface<Consultation> pending = consultationControl.getPendingConsultations();
        consultationControl.displayConsultationsTable(pending, "Pending Consultations");
    }

    private void viewCompletedConsultations() {
        ListInterface<Consultation> completed = consultationControl.getCompletedConsultations();
        consultationControl.displayConsultationsTable(completed, "Completed Consultations");
    }

    private void viewConsultationDetails() {
        System.out.print("\nEnter Consultation ID: ");
        String consultationId = sc.nextLine().trim();
        
        if (consultationId.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }
        
        consultationControl.displayConsultationDetails(consultationId);
    }

    private void startConsultation() {
        System.out.println("\n--- Start Consultation ---");
        System.out.println("Note: Consultations are automatically created when doctors are assigned to patients.");
        System.out.println("To start a consultation, first assign a doctor to a patient in the queue.");
        
        // Show pending consultations
        ListInterface<Consultation> pending = consultationControl.getPendingConsultations();
        if (pending.isEmpty()) {
            System.out.println("\nNo pending consultations found.");
            System.out.println("Please assign doctors to patients in the queue first.");
            return;
        }
        
        System.out.println("\nPending consultations ready to start:");
        consultationControl.displayConsultationsTable(pending, "Ready to Start");
        
        System.out.println("\nTo start a consultation:");
        System.out.println("1. Go to Patient Queue Management");
        System.out.println("2. Assign a doctor to a waiting patient");
        System.out.println("3. The consultation will be automatically created");
        System.out.println("4. Return here to view the new consultation");
    }

    /**
     * Complete consultation with treatment
     */
    public void completeConsultation() {
        System.out.println("\n--- Complete Consultation ---");
        
        // Show scheduled consultations
        ListInterface<Consultation> scheduled = consultationControl.getPendingConsultations();
        if (scheduled.isEmpty()) {
            System.out.println("📋 No scheduled consultations to complete");
            return;
        }
        
        System.out.println("\nScheduled Consultations:");
        for (int i = 0; i < scheduled.size(); i++) {
            Consultation consultation = scheduled.get(i);
            System.out.println((i + 1) + ". " + consultation.getConsultationId() + 
                " - Patient: " + consultation.getPatientId() + 
                " - Doctor: " + consultation.getDoctorId());
        }
        
        // Select consultation
        System.out.print("Select consultation to complete (1-" + scheduled.size() + "): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice < 1 || choice > scheduled.size()) {
                System.out.println("❌ Invalid consultation selection");
                return;
            }
            
            Consultation selectedConsultation = scheduled.get(choice - 1);
            
            // Get diagnosis
            System.out.print("Enter diagnosis/description: ");
            String diagnosis = sc.nextLine().trim();
            if (diagnosis.isEmpty()) {
                System.out.println("❌ Diagnosis cannot be empty");
                return;
            }
            
            // Get treatment fee
            System.out.print("Enter treatment fee: $");
            double treatmentFee;
            try {
                treatmentFee = Double.parseDouble(sc.nextLine().trim());
                if (treatmentFee < 0) {
                    System.out.println("❌ Treatment fee cannot be negative");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid fee amount");
                return;
            }
            
            // Get prescribed medicines
            ListInterface<MedicinePrescribed> medicines = new ArrayList<>();
            System.out.println("\nAdd prescribed medicines (type 'done' when finished):");
            
            while (true) {
                System.out.print("Medicine ID (or 'done'): ");
                String medicineId = sc.nextLine().trim();
                
                if (medicineId.equalsIgnoreCase("done")) {
                    break;
                }
                
                if (medicineId.isEmpty()) {
                    System.out.println("❌ Medicine ID cannot be empty");
                    continue;
                }
                
                System.out.print("Quantity: ");
                try {
                    int quantity = Integer.parseInt(sc.nextLine().trim());
                    if (quantity <= 0) {
                        System.out.println("❌ Quantity must be positive");
                        continue;
                    }
                    
                    medicines.add(new MedicinePrescribed(medicineId, quantity));
                    System.out.println("✅ Added: " + medicineId + " x" + quantity);
                    
                } catch (NumberFormatException e) {
                    System.out.println("❌ Please enter a valid quantity");
                }
            }
            
            // Complete consultation
            consultationControl.completeConsultation(
                selectedConsultation.getConsultationId(), 
                diagnosis, 
                treatmentFee, 
                medicines
            );
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number");
        }
    }
}
