package boundary;

import control.PatientQueueControl;
import java.util.Scanner;

public class PatientQueueBoundary {
    private Scanner sc;
    private PatientQueueControl queueControl;

    public PatientQueueBoundary(PatientQueueControl queueControl) {
        this.sc = new Scanner(System.in);
        this.queueControl = queueControl;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   Patient Queue Management ");
            System.out.println("=================================");
            System.out.println("1. Add patient to queue");
            System.out.println("2. View current queue");
            System.out.println("3. Assign doctor to patient");
            System.out.println("4. Complete patient consultation");
            System.out.println("0. Back to Patient Management");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    queueControl.addPatientToQueue();
                    break;
                case "2":
                    queueControl.viewCurrentQueue();
                    break;
                case "3":
                    queueControl.assignDoctorToPatient();
                    break;
                case "4":
                    queueControl.completePatientConsultation();
                    break;
                case "0":
                    return; // back to Patient Management
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}
