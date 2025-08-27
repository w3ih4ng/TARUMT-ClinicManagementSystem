package boundary;

import java.util.Scanner;
import control.*;

public class PatientManagementBoundary {
    private Scanner sc;
    private PatientRecordControl patientRecordControl;
    private ViewAllPatientBoundary viewAllPatientBoundary;
    private PatientQueueBoundary queueBoundary;

    public PatientManagementBoundary(PatientRecordControl patientRecordControl, PatientQueueControl queueControl) {
        this.sc = new Scanner(System.in);
        this.patientRecordControl = patientRecordControl;
        this.viewAllPatientBoundary = new ViewAllPatientBoundary(patientRecordControl);
        this.queueBoundary = new PatientQueueBoundary(queueControl);
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("   Patient Management Module  ");
            System.out.println("==============================");
            System.out.println("1. Register new patient");
            System.out.println("2. View all patients");
            System.out.println("3. Update patient information");
            System.out.println("4. Delete patient");
            System.out.println("5. Restore patient");
            System.out.println("6. Manage patient queue");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": patientRecordControl.registerPatient(); break;
                case "2": viewAllPatientBoundary.show(); break;
                case "3": patientRecordControl.updatePatient(); break;
                case "4": patientRecordControl.deletePatient(); break;
                case "5": patientRecordControl.restorePatient(); break;
                case "6": queueBoundary.menu(); break;

                case "0": return; // back to Staff menu
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
