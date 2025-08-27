package main;

import java.util.Scanner;
import boundary.*;
import control.*;

public class ClinicManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StaffMenuBoundary staffMenuBoundary = new StaffMenuBoundary(new StaffControl());
        DoctorMenuBoundary doctorMenuBoundary = new DoctorMenuBoundary(new DoctorRecordControl());

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
                    staffMenuBoundary.mainMenu();
                    break;

                case "2":
                    doctorMenuBoundary.mainMenu();
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
