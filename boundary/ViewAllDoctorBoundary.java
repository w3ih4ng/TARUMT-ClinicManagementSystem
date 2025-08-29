package boundary;

import control.DoctorRecordControl;
import control.ViewDoctorControl;
import control.ViewAllDoctorMenuControl;
import entity.Doctor;
import entity.Specialty;
import adt.*;
import java.util.Scanner;

/**
 * Boundary class for viewing all doctor data interface
 * @author Your Name
 */
public class ViewAllDoctorBoundary {
    private final ViewAllDoctorMenuControl viewDoctorMenuControl;

    public ViewAllDoctorBoundary(DoctorRecordControl doctorRecordControl) {
        this.viewDoctorMenuControl = new ViewAllDoctorMenuControl(new ViewDoctorControl(doctorRecordControl));
    }

    public void show() {
        HashMapInterface<String, Doctor> baseMap = viewDoctorMenuControl.getDoctorMap();
        HashMapInterface<String, Doctor> currentMap = baseMap;
        ListInterface<Doctor> currentList = viewDoctorMenuControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            utility.SystemUtil.showMenuHeader("Doctor List");
            viewDoctorMenuControl.printDoctorsFromList(currentList);

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
                    utility.SystemUtil.showSectionHeader("Filter Doctors");
                    currentMap = viewDoctorMenuControl.handleFilter(sc, currentMap);
                    currentList = viewDoctorMenuControl.toList(currentMap);
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Search Doctors");
                    currentMap = viewDoctorMenuControl.handleSearch(sc, currentMap);
                    currentList = viewDoctorMenuControl.toList(currentMap);
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Sort Doctors");
                    viewDoctorMenuControl.handleSort(sc, currentList);
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Reset View");
                    currentMap = baseMap;
                    currentList = viewDoctorMenuControl.toList(currentMap);
                    viewDoctorMenuControl.clearCriteria();
                    System.out.println("View reset to show all doctors.");
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    viewDoctorMenuControl.clearCriteria();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }


}
