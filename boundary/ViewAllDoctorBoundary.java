package boundary;

import control.DoctorControl;
import control.ViewDoctorControl;
import entity.Doctor;
import adt.*;
import java.util.Scanner;

public class ViewAllDoctorBoundary {
    private final ViewDoctorControl viewDoctorControl;

    public ViewAllDoctorBoundary(DoctorControl doctorControl) {
        this.viewDoctorControl = new ViewDoctorControl(doctorControl);
    }

    public void show() {
        HashMapInterface<String, Doctor> baseMap = viewDoctorControl.getDoctorMap();
        HashMapInterface<String, Doctor> currentMap = baseMap;
        ListInterface<Doctor> currentList = viewDoctorControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println(
                    "\n---------------------------------------- Doctor List ----------------------------------------\n");
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
        System.out.println("Enter to cancel.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("\nEnter Specialty: ");
                String specialty = sc.nextLine().trim();
                return viewDoctorControl.filterBySpecialty(map, specialty);
            case "2":
                System.out.print("\nEnter Gender (M/F): ");
                String gender = sc.nextLine().trim();
                return viewDoctorControl.filterByGender(map, gender);
            case "3":
                return viewDoctorControl.filterShowDeleted(map);
            case "4":
                return viewDoctorControl.filterNotDeleted(map);
            case "":
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
        System.out.println("Enter to cancel.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        if (choice.isEmpty())
            return;

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
