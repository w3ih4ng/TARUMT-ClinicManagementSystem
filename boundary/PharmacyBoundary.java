package boundary;

import control.PharmacyControl;
import java.util.Scanner;

public class PharmacyBoundary {
    private Scanner sc;
    private PharmacyControl pharmacyControl;

    public PharmacyBoundary(PharmacyControl pharmacyControl) {
        this.sc = new Scanner(System.in);
        this.pharmacyControl = pharmacyControl;
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("      Pharmacy Module         ");
            System.out.println("==============================");
            System.out.println("1. Medicine Management");
            System.out.println("2. Dispensing Management");
            System.out.println("3. Stock Management");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    pharmacyControl.openMedicineModule(); 
                    break;
                case "2": 
                    System.out.println("[Dispensing Module - placeholder]");
                    break;
                case "3":
                    pharmacyControl.openStockModule();
                    break;
                case "0": 
                    return; // back to Staff Menu
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}
