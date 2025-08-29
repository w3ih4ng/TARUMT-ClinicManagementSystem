package main;

import java.util.Scanner;
import boundary.*;
import control.*;

/**
 * Main entry point for the Clinic Management System
 * @author Your Name
 */
public class ClinicManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StaffMenuBoundary staffMenuBoundary = new StaffMenuBoundary(new StaffControl());
        DoctorMenuBoundary doctorMenuBoundary = new DoctorMenuBoundary(new DoctorRecordControl(), null);

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("    TARUMT CLINIC MANAGEMENT SYSTEM");
            System.out.println("=".repeat(50));
            System.out.println("1. Staff Portal");
            System.out.println("2. Doctor Portal");
            System.out.println("0. Exit System");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("    STAFF PORTAL");
                    System.out.println("=".repeat(40));
                    staffMenuBoundary.mainMenu();
                    break;

                case "2":
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("    DOCTOR PORTAL");
                    System.out.println("=".repeat(40));
                    doctorMenuBoundary.mainMenu();
                    break;

                case "0":
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("    Thank you for using TARUMT Clinic System");
                    System.out.println("    Goodbye!");
                    System.out.println("=".repeat(50));
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }
}
