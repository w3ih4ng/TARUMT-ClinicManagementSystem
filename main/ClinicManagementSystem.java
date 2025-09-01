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
            // Set navigation to home (directly show modules)
            utility.SystemUtil.setNavigationPath("Home");
            utility.SystemUtil.showMenuHeader("TARUMT Clinic Management System");
            System.out.println("=================================================");
            System.out.println("1. Patient Management Module");
            System.out.println("2. Doctor Management Module");
            System.out.println("3. Consultation Management Module");
            System.out.println("4. Medical Treatment Management Module");
            System.out.println("5. Pharmacy Management Module");
            System.out.println("6. Comprehensive Reports Module");
            System.out.println("0. Exit System");
            System.out.println("=================================================");
            System.out.print("Select Module (1-6, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.pushNavigation("Patient Management");
                    staffControl.openPatientModule();
                    break;
                case "2":
                    utility.SystemUtil.pushNavigation("Doctor Management");
                    staffControl.openDoctorManagementModule();
                    break;
                case "3":
                    utility.SystemUtil.pushNavigation("Consultation Management");
                    staffControl.openConsultationModule();
                    break;
                case "4":
                    utility.SystemUtil.pushNavigation("Medical Treatment Management");
                    staffControl.openTreatmentModule();
                    break;
                case "5":
                    utility.SystemUtil.pushNavigation("Pharmacy Management");
                    staffControl.openPharmacyModule();
                    break;
                case "6":
                    utility.SystemUtil.pushNavigation("Comprehensive Reports");
                    staffControl.openReportsModule();
                    break;

                case "0":
                    System.out.println("\n\n\n\n" + "=".repeat(80));
                    System.out.println("    Thank you for using TARUMT Clinic System");
                    System.out.println("    Goodbye!");
                    System.out.println("=".repeat(80)+"\n\n\n\n");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please select 1-6 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }
}
