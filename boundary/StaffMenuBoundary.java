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
            utility.SystemUtil.setNavigationPath("Home", "Staff Portal", "Staff Menu");
            utility.SystemUtil.showMenuHeader("Staff Dashboard");
            
            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Consultation Management");
            System.out.println("4. Medical Treatment Management");
            System.out.println("5. Pharmacy Management");
            System.out.println("6. Payment Management");
            System.out.println("0. Back to Main Menu");
            System.out.println();
            System.out.println("=".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("Patient Management");
                    staffControl.openPatientModule(); 
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("Doctor Management");
                    staffControl.openDoctorManagementModule(); 
                    break;
                case "3": 
                    utility.SystemUtil.pushNavigation("Consultation Management");
                    staffControl.openConsultationModule(); 
                    break;
                case "4": 
                    utility.SystemUtil.pushNavigation("Medical Treatment Management");
                    staffControl.openTreatmentModule(); 
                    break;
                case "5": 
                    utility.SystemUtil.pushNavigation("Pharmacy Management");
                    staffControl.openPharmacyModule(); 
                    break;
                case "6": 
                    utility.SystemUtil.pushNavigation("Payment Management");
                    staffControl.openPaymentModule(); 
                    break;
                case "0": 
                    utility.SystemUtil.setNavigationPath("Home");
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }
}
