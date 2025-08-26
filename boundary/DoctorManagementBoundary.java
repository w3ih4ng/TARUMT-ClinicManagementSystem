package boundary;

import control.DoctorControl;
import entity.Doctor;
import java.util.Scanner;
import java.time.LocalDate;

public class DoctorManagementBoundary {
    private Scanner sc;
    private DoctorControl doctorControl;
    private ViewAllDoctorBoundary viewAllDoctorBoundary;

    public DoctorManagementBoundary(DoctorControl doctorControl) {
        this.sc = new Scanner(System.in);
        this.doctorControl = doctorControl;
        this.viewAllDoctorBoundary = new ViewAllDoctorBoundary(doctorControl);
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n--- Doctor Management ---");
            System.out.println("1. Add Doctor");
            System.out.println("2. View Doctors");
            System.out.println("3. Update Doctor");
            System.out.println("4. Delete Doctor");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": doctorControl.registerDoctor(); break;
                case "2": viewAllDoctorBoundary.show(); break;
                case "3": doctorControl.updateDoctor(); break;
                case "4": doctorControl.deleteDoctor(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    
}
