package boundary;

import java.util.Scanner;
import control.StaffControl;

/**
 * Boundary class for staff menu interface
 * @author Your Name
 */
public class StaffMenuBoundary {
    private Scanner sc;
    private StaffControl staffControl;

    public StaffMenuBoundary(StaffControl staffControl) {
        this.sc = new Scanner(System.in);
        this.staffControl = staffControl;
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Module Selection");
            utility.SystemUtil.showMenuHeader("TARUMT Clinic Management Modules");
            System.out.println();
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
                    utility.SystemUtil.setNavigationPath("Home");
                    return;
                default: 
                    System.out.println("Invalid choice. Please select 1-6 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }
}
