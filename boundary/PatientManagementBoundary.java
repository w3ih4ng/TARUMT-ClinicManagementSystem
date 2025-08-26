package boundary;

import java.util.Scanner;
import control.*;

public class PatientManagementBoundary {
    private Scanner sc;
    private PatientControl patientControl;
    private ViewAllPatientBoundary viewAllPatientBoundary;
    private PatientQueueBoundary queueBoundary;
    private PatientQueueControl queueControl;

    public PatientManagementBoundary(PatientControl patientControl, PatientQueueControl queueControl) {
        this.sc = new Scanner(System.in);
        this.patientControl = patientControl;
        this.viewAllPatientBoundary = new ViewAllPatientBoundary(patientControl);
        this.queueControl = queueControl;
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
            System.out.println("4. Delete patient (soft delete)");
            System.out.println("5. Manage patient queue");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": patientControl.registerPatient(); break;
                case "2": viewAllPatientBoundary.show(); break;
                case "3": patientControl.updatePatient(); break;
                case "4": patientControl.deletePatient(); break;
                case "5": queueBoundary.menu(); break;

                case "0": return; // back to Staff menu
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
