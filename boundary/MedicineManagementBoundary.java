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
            utility.SystemUtil.showMenuHeader("Medicine Management Module");
            
            System.out.println("1. Add new medicine");
            System.out.println("2. View all medicines");
            System.out.println("3. Update medicine information");
            System.out.println("4. Delete medicine");
            System.out.println("5. Restore medicine");
            System.out.println("0. Back to Pharmacy Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Add New Medicine");
                    medicineControl.addMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("View All Medicines");
                    viewAllMedicineBoundary.show(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Update Medicine Information");
                    medicineControl.updateMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Delete Medicine");
                    medicineControl.deleteMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Restore Medicine");
                    medicineControl.restoreMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": return; // back
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
