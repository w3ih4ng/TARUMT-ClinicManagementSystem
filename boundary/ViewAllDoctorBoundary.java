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
        HashMapInterface<String, Doctor> baseView = viewDoctorControl.getDoctorMap();
        HashMapInterface<String, Doctor> currentView = baseView;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n---------------------------------------- Doctor List ----------------------------------------\n");
            viewDoctorControl.printDoctors(currentView);

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
                    currentView = baseView;
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
        System.out.println("Enter to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("\nEnter Specialty: ");
                String specialty = sc.nextLine().trim();
                return viewDoctorControl.filterBySpecialty(map, specialty);
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
}
