package boundary;

import control.DoctorRecordControl;
import control.ViewDoctorControl;
import entity.Doctor;
import entity.Specialty;
import adt.*;
import java.util.Scanner;

public class ViewAllDoctorBoundary {
    private final ViewDoctorControl viewDoctorControl;

    public ViewAllDoctorBoundary(DoctorRecordControl doctorRecordControl) {
        this.viewDoctorControl = new ViewDoctorControl(doctorRecordControl);
    }

    public void show() {
        HashMapInterface<String, Doctor> baseMap = viewDoctorControl.getDoctorMap();
        HashMapInterface<String, Doctor> currentMap = baseMap;
        ListInterface<Doctor> currentList = viewDoctorControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n---------------------------------------- Doctor List ----------------------------------------\n");
            viewDoctorControl.printDoctorsFromList(currentList);

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
                    currentList = viewDoctorControl.toList(currentMap);
                    break;
                case "2":
                    currentMap = handleSearch(sc, currentMap);
                    currentList = viewDoctorControl.toList(currentMap);
                    break;
                case "3":
                    handleSort(sc, currentList);
                    break;
                case "4":
                    currentMap = baseMap;
                    currentList = viewDoctorControl.toList(currentMap);
                    viewDoctorControl.clearCriteria();
                    break;
                case "0":
                    viewDoctorControl.clearCriteria();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private HashMapInterface<String, Doctor> handleFilter(Scanner sc, HashMapInterface<String, Doctor> map) {
        System.out.println("\nFilter Options:");
        System.out.println("1. Specialty");
        System.out.println("2. Gender");
        System.out.println("3. Show Deleted Only");
        System.out.println("4. Hide Deleted");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\nSelect Specialty:");
                Specialty[] specialties = Specialty.values();
                for (int i = 0; i < specialties.length; i++) {
                    System.out.printf("%d. %s%n", i + 1, specialties[i].name());
                }
                System.out.print("Choose: ");
                String specChoice = sc.nextLine().trim();
                try {
                    int idx = Integer.parseInt(specChoice);
                    if (idx >= 1 && idx <= specialties.length) {
                        return viewDoctorControl.filterBySpecialty(map, specialties[idx - 1]);
                    } else {
                        System.out.println("Invalid choice. Filter cancelled.");
                        return map;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Filter cancelled.");
                    return map;
                }
            case "2":
                System.out.print("\nEnter Gender (M/F): ");
                String gender = sc.nextLine().trim();
                return viewDoctorControl.filterByGender(map, gender);
            case "3":
                return viewDoctorControl.filterShowDeleted(map);
            case "4":
                return viewDoctorControl.filterNotDeleted(map);
            case "0":
                return map;
            default:
                System.out.println("\nInvalid filter option.");
                return map;
        }
    }

    private HashMapInterface<String, Doctor> handleSearch(Scanner sc, HashMapInterface<String, Doctor> map) {
        System.out.print("\nEnter keyword to search: ");
        String keyword = sc.nextLine().trim();
        return viewDoctorControl.searchDoctors(map, keyword);
    }

    private void handleSort(Scanner sc, ListInterface<Doctor> list) {
        System.out.println("\nSort Options:");
        System.out.println("1. Doctor ID");
        System.out.println("2. Name");
        System.out.println("3. Gender");
        System.out.println("4. Specialty");
        System.out.println("5. Consultation Fee");
        System.out.println("6. Birthdate");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        if (choice == "0"){
            return;
        } else if (choice.isEmpty()) {
            return;
        }

        System.out.println("Sort Order:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Choose: ");
        String orderChoice = sc.nextLine().trim();

        switch (orderChoice) {
            case "1":
                viewDoctorControl.sortDoctors(list, choice); // ascending
                break;
            case "2":
                viewDoctorControl.reverseSortDoctors(list, choice); // descending
                break;
            default:
                System.out.println("Invalid order. Defaulting to ascending.");
                viewDoctorControl.sortDoctors(list, choice);
                break;
        }
    }
}
