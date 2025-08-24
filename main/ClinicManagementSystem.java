package main;

import java.util.Scanner;
import boundary.StaffBoundary;
import control.StaffControl;
import boundary.DoctorBoundary;
import control.DoctorControl;

public class ClinicManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StaffBoundary staffBoundary = new StaffBoundary(new StaffControl());
        DoctorBoundary doctorBoundary = new DoctorBoundary(new DoctorControl());

        while (true) {
            System.out.println("====================================");
            System.out.println("   Welcome to the Clinic System");
            System.out.println("====================================");
            System.out.println("1. Staff");
            System.out.println("2. Doctor");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    staffBoundary.mainMenu();
                    break;

                case "2":
                    doctorBoundary.mainMenu();
                    break;

                case "0":
                    System.out.println("Exiting system. Goodbye!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }
}
