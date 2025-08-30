package boundary;

import control.ConsultationMenuControl;
import entity.Consultation;
import java.util.Scanner;

/**
 * Boundary class for consultation management user interface
 * Follows ECB pattern - contains only UI logic
 * @author Your Name
 */
public class ConsultationMenuBoundary {
    private Scanner sc;
    private ConsultationMenuControl consultationMenuControl;

    public ConsultationMenuBoundary(ConsultationMenuControl consultationMenuControl) {
        this.sc = new Scanner(System.in);
        this.consultationMenuControl = consultationMenuControl;
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Consultation Management");
            
            System.out.println("=== CONSULTATION OPERATIONS ===");
            System.out.println("  1. View All Consultations");
            System.out.println("  2. View Pending Consultations");
            System.out.println("  3. View Completed Consultations");
            System.out.println("  4. View Consultation Details");
            System.out.println("  5. Complete Consultation (Staff)");
            System.out.println();
            
            System.out.println("=== SUMMARY REPORTS ===");
            System.out.println("  6. Consultations by Doctor Report");
            System.out.println("  7. Consultations by Patient Report");
            System.out.println("  8. Consultation Statistics Report");
            System.out.println();
            
            System.out.println("=== NAVIGATION ===");
            System.out.println("  0. Back to Staff Menu");
            System.out.println();
            System.out.println("Note: Appointment scheduling is available through Patient Queue Management");
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("Enter your choice (1-8, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("View All Consultations");
                    consultationMenuControl.viewAllConsultations(); 
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View Pending Consultations");
                    consultationMenuControl.viewPendingConsultations(); 
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("View Completed Consultations");
                    consultationMenuControl.viewCompletedConsultations(); 
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("View Consultation Details");
                    consultationMenuControl.viewConsultationDetails(); 
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Complete Consultation (Staff)");
                    consultationMenuControl.completeConsultationWithTreatment(); 
                    break;
                case "6": 
                    utility.SystemUtil.showSectionHeader("Consultations by Doctor Report");
                    consultationMenuControl.viewConsultationsByDoctor(); 
                    break;
                case "7": 
                    utility.SystemUtil.showSectionHeader("Consultations by Patient Report");
                    consultationMenuControl.viewConsultationsByPatient(); 
                    break;
                case "8": 
                    utility.SystemUtil.showSectionHeader("Consultation Statistics Report");
                    generateConsultationStatisticsReport();
                    break;
                case "0": 
                    utility.SystemUtil.popNavigation();
                    return;
                default: 
                    System.out.println();
                    System.out.println("Invalid choice! Please enter 1-8 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }
    
    private void generateConsultationStatisticsReport() {
        System.out.println("=== CONSULTATION STATISTICS REPORT ===");
        System.out.println();
        
        // Get consultation statistics
        var allConsultations = consultationMenuControl.getConsultationControl().getConsultationMap();
        int totalConsultations = 0;
        int pendingCount = 0;
        int scheduledCount = 0;
        int completedCount = 0;
        
        for (String key : allConsultations.keySet()) {
            Consultation consultation = allConsultations.get(key);
            totalConsultations++;
            
            String status = consultation.getStatus();
            switch (status) {
                case "PENDING":
                    pendingCount++;
                    break;
                case "SCHEDULED":
                    scheduledCount++;
                    break;
                case "COMPLETED":
                    completedCount++;
                    break;
            }
        }
        
        System.out.println("Total Consultations: " + totalConsultations);
        System.out.println("  - Pending: " + pendingCount);
        System.out.println("  - Scheduled: " + scheduledCount);
        System.out.println("  - Completed: " + completedCount);
        System.out.println();
        
        utility.SystemUtil.pauseForUser();
    }
}
