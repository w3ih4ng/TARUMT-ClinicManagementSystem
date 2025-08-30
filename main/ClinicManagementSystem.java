package main;

import java.util.Scanner;
import boundary.*;
import control.*;

/**
 * Main entry point for the Clinic Management System
 * Features 5 core modules: Patient Management, Doctor Management, 
 * Consultation Management, Medical Treatment Management, and Pharmacy Management
 * @author Your Name
 */
public class ClinicManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Create shared consultation control for system-wide consistency
        ConsultationController sharedConsultationControl = new ConsultationController();
        
        StaffControl staffControl = new StaffControl(sharedConsultationControl);
        StaffMenuBoundary staffMenuBoundary = new StaffMenuBoundary(staffControl);

        while (true) {
            // Set navigation to home
            utility.SystemUtil.setNavigationPath("Home");
            utility.SystemUtil.showMenuHeader("TARUMT Clinic Management System");
            
            System.out.println("🏥 Welcome to TARUMT Clinic Management System");
            System.out.println("   Complete healthcare management solution");
            System.out.println();
            System.out.println("1. Access Management System");
            System.out.println("0. Exit System");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.setNavigationPath("Home", "Clinic Management");
                    utility.SystemUtil.showMenuHeader("Clinic Management");
                    staffMenuBoundary.mainMenu();
                    break;

                case "0":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("    Thank you for using TARUMT Clinic System");
                    System.out.println("    Goodbye!");
                    System.out.println("=".repeat(80));
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }
}
