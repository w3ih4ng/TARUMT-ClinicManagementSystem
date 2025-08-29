package boundary;

import control.PatientRecordControl;
import control.ViewPatientControl;
import entity.*;
import adt.*;
import utility.*;

import java.util.Scanner;

/**
 * Boundary class for viewing all patient data interface
 * @author Your Name
 */
public class ViewAllPatientBoundary {
    private final ViewPatientControl viewPatientControl;

    public ViewAllPatientBoundary(PatientRecordControl patientRecordControl) {
        this.viewPatientControl = new ViewPatientControl(patientRecordControl);
    }

    public void show() {
        HashMapInterface<String, Patient> baseView = viewPatientControl.getPatientMap();
        HashMapInterface<String, Patient> currentMap = baseView;
        ListInterface<Patient> currentList = viewPatientControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            utility.SystemUtil.pushNavigation("View All Patients");
            utility.SystemUtil.showMenuHeader("Patient List");
            
            viewPatientControl.printPatientsFromList(currentList);

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
                    utility.SystemUtil.showSectionHeader("Filter Patients");
                    currentMap = handleFilter(sc, currentMap);
                    currentList = viewPatientControl.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Search Patients");
                    currentMap = handleSearch(sc, currentMap);
                    currentList = viewPatientControl.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Sort Patients");
                    handleSort(sc, currentList);
                    utility.SystemUtil.popNavigation();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Reset View");
                    currentMap = baseView; // reset
                    currentList = viewPatientControl.toList(currentMap);
                    viewPatientControl.clearCriteria();
                    System.out.println("View reset to show all patients.");
                    utility.SystemUtil.pauseForUser();
                    utility.SystemUtil.popNavigation();
                    break;
                case "0":
                    viewPatientControl.clearCriteria();
                    utility.SystemUtil.popNavigation();
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
        System.out.println("3. Show Deleted");
        System.out.println("4. Do Not Show Deleted");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\nSelect a Role\n1. Student \n2. Tutor \n3. Staff");
                String roleChoice = sc.nextLine().trim();
                return viewPatientControl.filterByRole(map, roleChoice);
            case "2":
                System.out.print("\nEnter Gender (M/F): ");
                String gender = sc.nextLine().trim().toUpperCase();
                return viewPatientControl.filterByGender(map, gender);
            case "3":
                return viewPatientControl.filterShowDeleted(map);
            case "4":
                return viewPatientControl.filterNotDeleted(map);
            case "0":
                return map;
            default:
                System.out.println("\nInvalid filter option.");
                return map;
        }
    }

    private HashMapInterface<String, Patient> handleSearch(Scanner sc, HashMapInterface<String, Patient> map) {
        System.out.print("\nEnter keyword to search: ");
        String keyword = sc.nextLine().trim();
        HashMapInterface<String, Patient> results = viewPatientControl.searchPatients(map, keyword);
        return results;
    }

    private void handleSort(Scanner sc, ListInterface<Patient> list) {
        System.out.println("\nSort Options:");
        System.out.println("1. Patient ID");
        System.out.println("2. Name");
        System.out.println("3. Gender");
        System.out.println("4. Birthdate");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        if (choice == "0"){
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
                viewPatientControl.sortPatients(list, choice); // ascending
                break;
            case "2":
                viewPatientControl.reverseSortPatients(list, choice); // descending
                break;
            default:
                System.out.println("Invalid order. Defaulting to ascending.");
                viewPatientControl.sortPatients(list, choice);
                break;
        }
    }

}
