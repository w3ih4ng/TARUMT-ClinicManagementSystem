package boundary;

import control.MedicineControl;
import control.ViewMedicineControl;
import entity.Medicine;
import adt.*;
import java.util.Scanner;

/**
 * Boundary class for viewing all medicine data interface
 * @author Your Name
 */
public class ViewAllMedicineBoundary {
    private final ViewMedicineControl viewMedicineControl;

    public ViewAllMedicineBoundary(MedicineControl medicineControl) {
        this.viewMedicineControl = new ViewMedicineControl(medicineControl);
    }

    public void show() {
        HashMapInterface<String, Medicine> baseMap = viewMedicineControl.getMedicineMap();
        HashMapInterface<String, Medicine> currentMap = baseMap;
        ListInterface<Medicine> currentList = viewMedicineControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            utility.SystemUtil.showMenuHeader("Medicine List");
            viewMedicineControl.printMedicinesFromList(currentList);

            System.out.println("\nOptions:");
            System.out.println("1. Filter");
            System.out.println("2. Search");
            System.out.println("3. Sort");
            System.out.println("4. Reset");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    currentMap = handleFilter(sc, currentMap);
                    currentList = viewMedicineControl.toList(currentMap);
                    break;
                case "2":
                    currentMap = handleSearch(sc, currentMap);
                    currentList = viewMedicineControl.toList(currentMap);
                    break;
                case "3":
                    handleSort(sc, currentList);
                    break;
                case "4":
                    currentMap = baseMap;
                    currentList = viewMedicineControl.toList(currentMap);
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
        System.out.println("1. By Dosage Value");
        System.out.println("2. By Unit");
        System.out.println("3. Show Active Medicines");
        System.out.println("4. Show Deleted Medicines");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                double dosage = 0;
                while (true) {
                    System.out.print("\nEnter Dosage value: ");
                    String input = sc.nextLine().trim();
                    try {
                        dosage = Double.parseDouble(input);
                        if (dosage >= 0)
                            break;
                        System.out.println("Dosage must be non-negative.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                }
                return viewMedicineControl.filterByDosageValue(map, dosage);

            case "2":
                System.out.println("Select Unit:");
                System.out.println("1. MG");
                System.out.println("2. ML");
                System.out.println("3. TABLET");
                System.out.println("4. CAPSULE");
                System.out.print("Choose: ");
                String unitChoice = sc.nextLine().trim();
                Medicine.Unit unit;
                switch (unitChoice) {
                    case "1":
                        unit = Medicine.Unit.MG;
                        break;
                    case "2":
                        unit = Medicine.Unit.ML;
                        break;
                    case "3":
                        unit = Medicine.Unit.TABLET;
                        break;
                    case "4":
                        unit = Medicine.Unit.CAPSULE;
                        break;
                    default:
                        System.out.println("Invalid unit. Returning without filtering.");
                        return map;
                }
                return viewMedicineControl.filterByUnit(map, unit);

            case "3":
                return viewMedicineControl.filterNotDeleted(map);

            case "4":
                return viewMedicineControl.filterShowDeleted(map);

            case "0":
                return map;

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

    private void handleSort(Scanner sc, ListInterface<Medicine> list) {
        System.out.println("\nSort Options:");
        System.out.println("1. Medicine ID");
        System.out.println("2. Name");
        System.out.println("3. Dosage");
        System.out.println("4. Unit");
        System.out.println("5. Stock Quantity");
        System.out.println("6. Price");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        if (choice == "0") {
            return;
        } else if (choice.isEmpty()){
            return;
        }   
        
        System.out.println("Sort Order:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Choose: ");
        String orderChoice = sc.nextLine().trim();

        switch (orderChoice) {
            case "1":
                viewMedicineControl.sortMedicines(list, choice); // ascending
                break;
            case "2":
                viewMedicineControl.reverseSortMedicines(list, choice); // descending
                break;
            default:
                System.out.println("Invalid order. Defaulting to ascending.");
                viewMedicineControl.sortMedicines(list, choice);
                break;
        }
    }

}
