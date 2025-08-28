package boundary;

import java.util.Scanner;
import control.DoctorRecordControl;
import boundary.ConsultationMenuBoundary;

/**
 * Boundary class for doctor menu interface
 * @author Your Name
 */
public class DoctorMenuBoundary {
    private Scanner sc;
    private DoctorRecordControl doctorRecordControl;
    private ConsultationMenuBoundary consultationBoundary;

    public DoctorMenuBoundary(DoctorRecordControl doctorRecordControl, ConsultationMenuBoundary consultationBoundary) {
        this.sc = new Scanner(System.in);
        this.doctorRecordControl = doctorRecordControl;
        this.consultationBoundary = consultationBoundary;
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("         Doctor Menu          ");
            System.out.println("==============================");
            System.out.println("1. My Consultations");
            System.out.println("2. Complete Consultation");
            System.out.println("3. View All Consultations");
            System.out.println("0. Back to Main");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    consultationBoundary.viewPendingConsultations(); 
                    break;
                case "2": 
                    consultationBoundary.completeConsultation(); 
                    break;
                case "3": 
                    consultationBoundary.viewAllConsultations(); 
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}
