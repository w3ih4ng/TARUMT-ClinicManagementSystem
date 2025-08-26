package boundary;

import control.PatientQueueControl;
import java.util.Scanner;

public class PatientQueueBoundary {
    private PatientQueueControl queueControl;
    private Scanner sc;

    public PatientQueueBoundary(PatientQueueControl queueControl) {
        this.queueControl = queueControl;
        this.sc = new Scanner(System.in);
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- Patient Queue ---");
            queueControl.printQueue();
            System.out.println("\nOptions:");
            System.out.println("1. Add patient to queue");
            System.out.println("2. Call next patient");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter Patient ID: ");
                    String pid = sc.nextLine().trim();
                    queueControl.addToQueue(pid);
                    break;
                case "2":
                    var next = queueControl.callNext();
                    if (next != null) {
                        System.out.println("Next patient: " + next);
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
