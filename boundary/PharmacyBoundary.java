package boundary;

import control.PharmacyControl;
import java.util.Scanner;

/**
 * Boundary class for pharmacy operations interface
 * @author Your Name
 */
public class PharmacyBoundary {
    private Scanner sc;
    private PharmacyControl pharmacyControl;

    public PharmacyBoundary(PharmacyControl pharmacyControl) {
        this.sc = new Scanner(System.in);
        this.pharmacyControl = pharmacyControl;
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Pharmacy Module");
            
            System.out.println("1. Medicine Management");
            System.out.println("2. Dispensing Management");
            System.out.println("3. Stock Management");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("Medicine Management");
                    utility.SystemUtil.showMenuHeader("Medicine Management");
                    pharmacyControl.openMedicineModule(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("Dispensing Management");
                    utility.SystemUtil.showMenuHeader("Dispensing Management");
                    System.out.println("[Dispensing Module - placeholder]");
                    utility.SystemUtil.pauseForUser();
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.pushNavigation("Stock Management");
                    utility.SystemUtil.showMenuHeader("Stock Management");
                    pharmacyControl.openStockModule();
                    utility.SystemUtil.popNavigation();
                    break;
                case "0": 
                    return; // back to Staff Menu
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}
