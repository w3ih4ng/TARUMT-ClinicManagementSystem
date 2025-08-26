package boundary;

import control.MedicineControl;
import control.ViewMedicineControl;
import entity.Medicine;
import adt.HashMapInterface;

import java.util.Scanner;

public class ViewAllMedicineBoundary {
    private final ViewMedicineControl viewMedicineControl;

    public ViewAllMedicineBoundary(MedicineControl medicineControl) {
        this.viewMedicineControl = new ViewMedicineControl(medicineControl);
    }

    public void show() {
        HashMapInterface<String, Medicine> baseView = viewMedicineControl.getMedicineMap();
        HashMapInterface<String, Medicine> currentView = baseView;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n---------------------------------------- Medicine List ----------------------------------------\n");
            viewMedicineControl.printMedicines(currentView);

            System.out.println("\nOptions:");
            System.out.println("1. Filter");
            System.out.println("2. Search");
            System.out.println("3. Reset");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    currentView = handleFilter(sc, currentView);
                    break;
                case "2":
                    currentView = handleSearch(sc, currentView);
                    break;
                case "3":
                    currentView = baseView; // reset
                    viewMedicineControl.clearCriteria();
                    break;
                case "0":
                    viewMedicineControl.clearCriteria();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private HashMapInterface<String, Medicine> handleFilter(Scanner sc, HashMapInterface<String, Medicine> map) {
        System.out.println("\nFilter Options:");
        System.out.println("1. By Dosage");
        System.out.println("2. By Stock Less Than");
        System.out.println("3. Show Active Medicines");
        System.out.println("4. Show Deleted Medicines");
        System.out.println("Enter to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("\nEnter Dosage (e.g., 500mg): ");
                String dosage = sc.nextLine().trim();
                return viewMedicineControl.filterByDosage(map, dosage);
            case "2":
                System.out.print("\nEnter maximum stock quantity: ");
                try {
                    int qty = Integer.parseInt(sc.nextLine().trim());
                    return viewMedicineControl.filterByStockLessThan(map, qty);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Returning without filtering.");
                    return map;
                }
            case "3":
                return viewMedicineControl.filterNotDeleted(map);
            case "4":
                return viewMedicineControl.filterShowDeleted(map);
            default:
                System.out.println("\nInvalid filter option.");
                return map;
        }
    }

    private HashMapInterface<String, Medicine> handleSearch(Scanner sc, HashMapInterface<String, Medicine> map) {
        System.out.print("\nEnter keyword to search: ");
        String keyword = sc.nextLine().trim();
        return viewMedicineControl.searchMedicines(map, keyword);
    }
}
