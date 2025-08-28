package boundary;

import control.DoctorRecordControl;
import java.util.Scanner;

/**
 * Boundary class for doctor management interface
 * @author Your Name
 */
public class DoctorManagementBoundary {
    private Scanner sc;
    private DoctorRecordControl doctorRecordControl;
    private ViewAllDoctorBoundary viewAllDoctorBoundary;

    public DoctorManagementBoundary(DoctorRecordControl doctorRecordControl) {
        this.sc = new Scanner(System.in);
        this.doctorRecordControl = doctorRecordControl;
        this.viewAllDoctorBoundary = new ViewAllDoctorBoundary(doctorRecordControl);
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("      Doctor Management");
            System.out.println("==============================");
            System.out.println("1. Add Doctor");
            System.out.println("2. View Doctors");
            System.out.println("3. Update Doctor");
            System.out.println("4. Delete Doctor");
            System.out.println("5. Restore Doctor");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": doctorRecordControl.registerDoctor(); break;
                case "2": viewAllDoctorBoundary.show(); break;
                case "3": doctorRecordControl.updateDoctor(); break;
                case "4": doctorRecordControl.deleteDoctor(); break;
                case "5": doctorRecordControl.restoreDoctor(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    
}
