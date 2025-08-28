package boundary;

import java.util.Scanner;
import control.StaffControl;

public class StaffMenuBoundary {
    private Scanner sc;
    private StaffControl staffControl;

    public StaffMenuBoundary(StaffControl staffControl) {
        this.sc = new Scanner(System.in);
        this.staffControl = staffControl;
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("        Staff Menu            ");
            System.out.println("==============================");
            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Doctor Schedule Management");
            System.out.println("4. Consultation Management");
            System.out.println("5. Medical Treatment Management");
            System.out.println("6. Pharmacy Management");
            System.out.println("0. Back to Main");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": staffControl.openPatientModule(); break;
                case "2": staffControl.openDoctorManagementModule(); break;
                case "3": staffControl.openDoctorScheduleModule(); break;
                case "4": staffControl.openConsultationModule(); break;
                case "5": staffControl.openTreatmentModule(); break;
                case "6": staffControl.openPharmacyModule(); break;
                case "0": return; // back to Main
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
