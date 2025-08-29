package boundary;

import control.ConsultationMenuControl;
import java.util.Scanner;

/**
 * Boundary class for consultation management user interface
 * Follows ECB pattern - contains only UI logic
 * @author Your Name
 */
public class ConsultationMenuBoundary {
    private Scanner sc;
    private ConsultationMenuControl consultationMenuControl;

    public ConsultationMenuBoundary(ConsultationMenuControl consultationMenuControl) {
        this.sc = new Scanner(System.in);
        this.consultationMenuControl = consultationMenuControl;
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
            System.out.println("6. View Consultations by Doctor");
            System.out.println("7. View Consultations by Patient");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    System.out.println("\n--- View All Consultations ---");
                    consultationMenuControl.viewAllConsultations(); 
                    break;
                case "2": 
                    System.out.println("\n--- View Pending Consultations ---");
                    consultationMenuControl.viewPendingConsultations(); 
                    break;
                case "3": 
                    System.out.println("\n--- View Completed Consultations ---");
                    consultationMenuControl.viewCompletedConsultations(); 
                    break;
                case "4": 
                    consultationMenuControl.viewConsultationDetails(); 
                    break;
                case "5": 
                    consultationMenuControl.completeConsultationWithTreatment(); 
                    break;
                case "6": 
                    consultationMenuControl.viewConsultationsByDoctor(); 
                    break;
                case "7": 
                    consultationMenuControl.viewConsultationsByPatient(); 
                    break;
                case "0": 
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("    RETURNING TO STAFF MENU");
                    System.out.println("=".repeat(40));
                    return;
                default: 
                    System.out.println("❌ Invalid choice, try again.");
            }
            
            // Wait for user to continue
            System.out.println("\nPress Enter to continue...");
            sc.nextLine();
        }
    }
}
