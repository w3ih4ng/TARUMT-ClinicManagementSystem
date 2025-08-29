package boundary;

import java.util.Scanner;
import control.*;

/**
 * Boundary class for patient management interface
 * @author Your Name
 */
public class PatientManagementBoundary {
    private Scanner sc;
    private PatientRecordControl patientRecordControl;
    private PatientQueueControl queueControl;
    private ViewAllPatientBoundary viewAllPatientBoundary;

    public PatientManagementBoundary(PatientRecordControl patientRecordControl, PatientQueueControl queueControl) {
        this.sc = new Scanner(System.in);
        this.patientRecordControl = patientRecordControl;
        this.queueControl = queueControl;
        this.viewAllPatientBoundary = new ViewAllPatientBoundary(patientRecordControl);
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Patient Management Module");
            
            System.out.println("1. Patient Details Management");
            System.out.println("2. Manage Patient Queue");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("Patient Details");
                    patientDetailsManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("Patient Queue");
                    queueManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "0": 
                    return; // back to Staff menu
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void patientDetailsManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Patient Details Management");
            
            System.out.println("1. Register new patient");
            System.out.println("2. View all patients");
            System.out.println("3. Update patient information");
            System.out.println("4. Delete patient");
            System.out.println("5. Restore patient");
            System.out.println("0. Back to Patient Management");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Register New Patient");
                    patientRecordControl.registerPatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View All Patients");
                    viewAllPatientBoundary.show(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Update Patient Information");
                    patientRecordControl.updatePatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Delete Patient");
                    patientRecordControl.deletePatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Restore Patient");
                    patientRecordControl.restorePatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return; // back to Patient Management
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void queueManagement() {
        PatientQueueBoundary queueBoundary = new PatientQueueBoundary(queueControl, new DoctorRecordControl());
        queueBoundary.menu();
    }
}
