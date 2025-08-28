package boundary;

import java.util.Scanner;
import control.MedicineControl;

/**
 * Boundary class for medicine management interface
 * @author Your Name
 */
public class MedicineManagementBoundary {
    private Scanner sc;
    private MedicineControl medicineControl;
    private ViewAllMedicineBoundary viewAllMedicineBoundary;

    public MedicineManagementBoundary(MedicineControl medicineControl) {
        this.sc = new Scanner(System.in);
        this.medicineControl = medicineControl;
        this.viewAllMedicineBoundary = new ViewAllMedicineBoundary(medicineControl);
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   Medicine Management Module ");
            System.out.println("=================================");
            System.out.println("1. Add new medicine");
            System.out.println("2. View all medicines");
            System.out.println("3. Update medicine information");
            System.out.println("4. Delete medicine");
            System.out.println("5. Restore medicine");
            System.out.println("0. Back to Pharmacy Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": medicineControl.addMedicine(); break;
                case "2": viewAllMedicineBoundary.show(); break;
                case "3": medicineControl.updateMedicine(); break;
                case "4": medicineControl.deleteMedicine(); break;
                case "5": medicineControl.restoreMedicine(); break;
                case "0": return; // back
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
