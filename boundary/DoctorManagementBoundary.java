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
            utility.SystemUtil.showMenuHeader("Doctor Management");
            
            System.out.println("1. Add Doctor");
            System.out.println("2. View Doctors");
            System.out.println("3. Update Doctor");
            System.out.println("4. Delete Doctor");
            System.out.println("5. Restore Doctor");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Add New Doctor");
                    doctorRecordControl.registerDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("View All Doctors");
                    viewAllDoctorBoundary.show(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Update Doctor");
                    doctorRecordControl.updateDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Delete Doctor");
                    doctorRecordControl.deleteDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Restore Doctor");
                    doctorRecordControl.restoreDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    
}
