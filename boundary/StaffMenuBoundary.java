package boundary;

import java.util.Scanner;
import control.StaffControl;

/**
 * Boundary class for staff menu interface
 * @author Your Name
 */
public class StaffMenuBoundary {
    private Scanner sc;
    private StaffControl staffControl;

    public StaffMenuBoundary(StaffControl staffControl) {
        this.sc = new Scanner(System.in);
        this.staffControl = staffControl;
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("    STAFF DASHBOARD");
            System.out.println("=".repeat(50));
            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Doctor Schedule Management");
            System.out.println("4. Consultation Management");
            System.out.println("5. Medical Treatment Management");
            System.out.println("6. Pharmacy Management");
            System.out.println("7. Payment Management");
            System.out.println("0. Back to Main Menu");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    System.out.println("\n--- Patient Management ---");
                    staffControl.openPatientModule(); 
                    break;
                case "2": 
                    System.out.println("\n--- Doctor Management ---");
                    staffControl.openDoctorManagementModule(); 
                    break;
                case "3": 
                    System.out.println("\n--- Doctor Schedule Management ---");
                    staffControl.openDoctorScheduleModule(); 
                    break;
                case "4": 
                    System.out.println("\n--- Consultation Management ---");
                    staffControl.openConsultationModule(); 
                    break;
                case "5": 
                    System.out.println("\n--- Medical Treatment Management ---");
                    staffControl.openTreatmentModule(); 
                    break;
                case "6": 
                    System.out.println("\n--- Pharmacy Management ---");
                    staffControl.openPharmacyModule(); 
                    break;
                case "7": 
                    System.out.println("\n--- Payment Management ---");
                    staffControl.openPaymentModule(); 
                    break;
                case "0": 
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("    RETURNING TO MAIN MENU");
                    System.out.println("=".repeat(40));
                    return;
                default: 
                    System.out.println("❌ Invalid choice, try again.");
            }
        }
    }
}
