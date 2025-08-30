package boundary;

import control.ConsultationController;
import entity.Consultation;
import java.util.Scanner;

/**
 * Consolidated Consultation UI - combines all consultation-related boundary functionality
 * Handles consultation management, viewing, and reporting operations
 * @author Your Name
 */
public class ConsultationUI {
    private Scanner sc;
    private ConsultationController consultationController;

    public ConsultationUI(ConsultationController consultationController) {
        this.sc = new Scanner(System.in);
        this.consultationController = consultationController;
    }

    // ==================== MAIN CONSULTATION MENU ====================

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
            System.out.print("\n\nEnter your choice (1-8, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("View All Consultations");
                    consultationController.viewAllConsultations(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View Pending Consultations");
                    consultationController.viewPendingConsultations(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("View Completed Consultations");
                    consultationController.viewCompletedConsultations(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("View Consultation Details");
                    consultationController.viewConsultationDetails(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Complete Consultation (Staff)");
                    consultationController.completeConsultationWithTreatment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "6": 
                    utility.SystemUtil.showSectionHeader("Consultations by Doctor Report");
                    consultationController.viewConsultationsByDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "7": 
                    utility.SystemUtil.showSectionHeader("Consultations by Patient Report");
                    consultationController.viewConsultationsByPatient(); 
                    utility.SystemUtil.pauseForUser();
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
        var allConsultations = consultationController.getConsultationMap();
        
        if (allConsultations.isEmpty()) {
            System.out.println("No consultations found in the system.");
            utility.SystemUtil.pauseForUser();
            return;
        }
        
        // Calculate statistics
        int totalConsultations = 0;
        int pendingConsultations = 0;
        int completedConsultations = 0;
        
        for (String key : allConsultations.keySet()) {
            Consultation consultation = allConsultations.get(key);
            totalConsultations++;
            
            if (consultation.getStatus().equals("PENDING")) {
                pendingConsultations++;
            } else if (consultation.getStatus().equals("COMPLETED")) {
                completedConsultations++;
            }
        }
        
        // Display report
        System.out.println("CONSULTATION STATISTICS SUMMARY");
        System.out.println("=".repeat(40));
        System.out.printf("Total Consultations: %d%n", totalConsultations);
        System.out.printf("Pending Consultations: %d%n", pendingConsultations);
        System.out.printf("Completed Consultations: %d%n", completedConsultations);
        System.out.printf("Completion Rate: %.1f%%%n", 
            totalConsultations > 0 ? (double) completedConsultations / totalConsultations * 100 : 0.0);
        System.out.println("=".repeat(40));
        
        utility.SystemUtil.pauseForUser();
    }
}
