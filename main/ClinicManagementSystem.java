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

        // Create shared consultation control for system-wide consistency
        ConsultationControl sharedConsultationControl = new ConsultationControl();
        
        StaffMenuBoundary staffMenuBoundary = new StaffMenuBoundary(new StaffControl(sharedConsultationControl));
        DoctorMenuBoundary doctorMenuBoundary = new DoctorMenuBoundary(new DoctorRecordControl(), sharedConsultationControl);

        while (true) {
            // Set navigation to home
            utility.SystemUtil.setNavigationPath("Home");
            utility.SystemUtil.showMenuHeader("TARUMT Clinic Management System");
            
            System.out.println("1. Staff Portal");
            System.out.println("2. Doctor Portal");
            System.out.println("0. Exit System");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.setNavigationPath("Home", "Staff Portal");
                    utility.SystemUtil.showMenuHeader("Staff Portal");
                    staffMenuBoundary.mainMenu();
                    break;

                case "2":
                    utility.SystemUtil.setNavigationPath("Home", "Doctor Portal");
                    utility.SystemUtil.showMenuHeader("Doctor Portal");
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
