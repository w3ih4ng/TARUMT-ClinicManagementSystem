package control;

import entity.*;
import adt.*;
import java.util.Scanner;

/**
 * Control class for view all doctor menu business logic
 * @author Your Name
 */
public class ViewAllDoctorMenuControl {
    private ViewDoctorControl viewDoctorControl;
    private Scanner sc;

    public ViewAllDoctorMenuControl(ViewDoctorControl viewDoctorControl) {
        this.viewDoctorControl = viewDoctorControl;
        this.sc = new Scanner(System.in);
    }

    /**
     * Get doctor map
     */
    public HashMapInterface<String, Doctor> getDoctorMap() {
        return viewDoctorControl.getDoctorMap();
    }

    /**
     * Convert map to list
     */
    public ListInterface<Doctor> toList(HashMapInterface<String, Doctor> map) {
        return viewDoctorControl.toList(map);
    }

    /**
     * Print doctors from list
     */
    public void printDoctorsFromList(ListInterface<Doctor> list) {
        viewDoctorControl.printDoctorsFromList(list);
    }

    /**
     * Clear criteria
     */
    public void clearCriteria() {
        viewDoctorControl.clearCriteria();
    }

    /**
     * Handle filter selection
     */
    public HashMapInterface<String, Doctor> handleFilter(Scanner sc, HashMapInterface<String, Doctor> map) {
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
                return handleSpecialtyFilter(sc, map);
            case "2":
                return handleGenderFilter(sc, map);
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

    /**
     * Handle specialty filter
     */
    private HashMapInterface<String, Doctor> handleSpecialtyFilter(Scanner sc, HashMapInterface<String, Doctor> map) {
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
    }

    /**
     * Handle gender filter
     */
    private HashMapInterface<String, Doctor> handleGenderFilter(Scanner sc, HashMapInterface<String, Doctor> map) {
        System.out.print("\nEnter Gender (M/F): ");
        String gender = sc.nextLine().trim();
        return viewDoctorControl.filterByGender(map, gender);
    }

    /**
     * Handle search
     */
    public HashMapInterface<String, Doctor> handleSearch(Scanner sc, HashMapInterface<String, Doctor> map) {
        System.out.print("\nEnter keyword to search: ");
        String keyword = sc.nextLine().trim();
        return viewDoctorControl.searchDoctors(map, keyword);
    }

    /**
     * Handle sort
     */
    public void handleSort(Scanner sc, ListInterface<Doctor> list) {
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

        if (choice == "0" || choice.isEmpty()) {
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
