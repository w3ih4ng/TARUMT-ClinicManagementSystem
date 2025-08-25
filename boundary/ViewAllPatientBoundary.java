package boundary;

import control.PatientControl;
import control.ViewPatientControl;
import entity.*;
import adt.*;

import java.util.Scanner;

public class ViewAllPatientBoundary {
    private final ViewPatientControl viewPatientControl;

    public ViewAllPatientBoundary(PatientControl patientControl) {
        this.viewPatientControl = new ViewPatientControl(patientControl);
    }

    public void show() {
        HashMapInterface<String, Patient> baseView = viewPatientControl.getPatientMap();
        HashMapInterface<String, Patient> currentView = baseView;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Patient List ---");
            viewPatientControl.printPatients(currentView);

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
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private HashMapInterface<String, Patient> handleFilter(Scanner sc, HashMapInterface<String, Patient> map) {
        System.out.println("\nFilter Options:");
        System.out.println("1. Role (Student/Tutor/Staff)");
        System.out.println("2. Gender (M/F)");
        System.out.println("3. Hide Deleted");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("1. Student \n2. Tutor \n3. Staff");
                String roleChoice = sc.nextLine().trim();
                return viewPatientControl.filterByRole(map, roleChoice);
            case "2":
                System.out.print("Enter Gender (M/F): ");
                String gender = sc.nextLine().trim().toUpperCase();
                return viewPatientControl.filterByGender(map, gender);
            case "3":
                return viewPatientControl.filterHideDeleted(map);
            default:
                System.out.println("Invalid filter option.");
                return map;
        }
    }

    private HashMapInterface<String, Patient> handleSearch(Scanner sc, HashMapInterface<String, Patient> map) {
        System.out.print("Enter keyword to search: ");
        String keyword = sc.nextLine().trim();
        HashMapInterface<String, Patient> results = viewPatientControl.searchPatients(map, keyword);
        return results;
    }
}
