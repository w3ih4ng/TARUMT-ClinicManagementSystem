package boundary;

import java.util.Scanner;
import control.PatientControl;

public class PatientBoundary {
    private Scanner sc;
    private PatientControl patientControl;

    public PatientBoundary(PatientControl patientControl) {
        this.sc = new Scanner(System.in);
        this.patientControl = patientControl;
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
                case "2": patientControl.viewAllPatients(); break;
                case "3": patientControl.updatePatient(); break;
                case "4": patientControl.deletePatient(); break;
                case "5": patientControl.manageQueue(); break;
                case "0": return; // back to Staff menu
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
