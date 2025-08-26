package boundary;

import java.util.Scanner;
import control.DoctorControl;

public class DoctorMenuBoundary {
    private Scanner sc;
    private DoctorControl doctorControl;

    public DoctorMenuBoundary(DoctorControl doctorControl) {
        this.sc = new Scanner(System.in);
        this.doctorControl = doctorControl;
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("         Doctor Menu          ");
            System.out.println("==============================");
            System.out.println("1. View Patient Queue");
            System.out.println("2. Start Consultation");
            System.out.println("3. Record Diagnosis & Treatment");
            System.out.println("4. View Patient Treatment History");
            System.out.println("5. Manage My Schedule");
            System.out.println("0. Back to Main");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                //case "1": doctorControl.viewQueue(); break;
                //case "2": doctorControl.startConsultation(); break;
                //case "3": doctorControl.recordTreatment(); break;
                //case "4": doctorControl.viewTreatmentHistory(); break;
                //case "5": doctorControl.manageSchedule(); break;
                //case "0": return;
                //default: System.out.println("Invalid choice, try again.");
            }
        }
    }
}
