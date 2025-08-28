package boundary;

import control.ConsultationControl;
import adt.ListInterface;
import entity.Consultation;
import java.util.Scanner;

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
            System.out.println("5. Start Consultation");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": viewAllConsultations(); break;
                case "2": viewPendingConsultations(); break;
                case "3": viewCompletedConsultations(); break;
                case "4": viewConsultationDetails(); break;
                case "5": startConsultation(); break;
                case "0": return;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void viewAllConsultations() {
        ListInterface<Consultation> allConsultations = consultationControl.getConsultationMap().toList();
        if (allConsultations.isEmpty()) {
            System.out.println("\nNo consultations found in the system.");
            return;
        }
        consultationControl.displayConsultationsTable(allConsultations, "All Consultations");
    }

    private void viewPendingConsultations() {
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
}
