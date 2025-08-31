package boundary;

import control.ConsultationController;
import control.DoctorController;
import entity.*;
import adt.*;
import java.util.Scanner;

/**
 * Consolidated Consultation UI - focuses on consultation workflow
 * Handles patient assignment, consultation management, and doctor scheduling
 * @author Your Name
 */
public class ConsultationUI {
    private Scanner sc;
    private ConsultationController consultationController;
    private DoctorController doctorController;

    public ConsultationUI(ConsultationController consultationController, DoctorController doctorController) {
        this.sc = new Scanner(System.in);
        this.consultationController = consultationController;
        this.doctorController = doctorController;
    }

    // ==================== MAIN CONSULTATION MENU ====================

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Consultation Management");
            
            System.out.println("=== CONSULTATION MANAGEMENT ===");
            System.out.println("  1. View All Consultations");
            System.out.println("  2. Conduct Consultation");
            System.out.println("  3. Process Payment");
            System.out.println();
            
            System.out.println("=== DOCTOR SCHEDULE MANAGEMENT ===");
            System.out.println("  4. View Doctor Schedules");
            System.out.println("  5. Make Appointment");
            System.out.println("  6. View Available Time Slots");
            System.out.println();
            
            System.out.println("=== CONSULTATION REPORTS ===");
            System.out.println("  7. Consultations by Doctor Report");
            System.out.println("  8. Consultations by Patient Report");
            System.out.println("  9. Daily Consultation Summary");
            System.out.println();
            
            System.out.println("=== NAVIGATION ===");
            System.out.println("  0. Back to Staff Menu");
            System.out.println();
            System.out.println("Note: Patient queue management and doctor assignment is in Patient Module");
            System.out.println("      Treatment completion is in Treatment Module");
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("\n\nEnter your choice (1-9, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("View All Consultations");
                    viewAllConsultations(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Conduct Consultation");
                    conductConsultation(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Process Payment");
                    processPayment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("View Doctor Schedules");
                    viewDoctorSchedules(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Make Appointment");
                    makeAppointment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "6": 
                    utility.SystemUtil.showSectionHeader("Available Time Slots");
                    viewAvailableTimeSlots(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "7": 
                    utility.SystemUtil.showSectionHeader("Consultations by Doctor Report");
                    consultationController.viewConsultationsByDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "8": 
                    utility.SystemUtil.showSectionHeader("Consultations by Patient Report");
                    consultationController.viewConsultationsByPatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "9": 
                    utility.SystemUtil.showSectionHeader("Daily Consultation Summary");
                    generateDailyConsultationSummary();
                    break;
                case "0": 
                    utility.SystemUtil.popNavigation();
                    return;
                default: 
                    System.out.println();
                    System.out.println("Invalid choice! Please enter 1-9 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    // ==================== CONSULTATION MANAGEMENT ====================

    private void viewAllConsultations() {
        while (true) {
            // Show consultations first
            System.out.println("\n" + "=".repeat(80));
            System.out.println("CONSULTATION LIST");
            System.out.println("=".repeat(80));
            consultationController.viewAllConsultations();
            
            // Show options
            System.out.println("\n" + "-".repeat(80));
            System.out.println("OPTIONS");
            System.out.println("-".repeat(80));
            System.out.println("1. Filter Consultations");
            System.out.println("2. Sort Consultations");
            System.out.println("3. Search Consultation");
            System.out.println("4. View Consultation Details");
            System.out.println("5. Refresh List");
            System.out.println("0. Back to Consultation Management");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Filter Consultations");
                    filterConsultations(); 
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Sort Consultations");
                    sortConsultations(); 
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Search Consultation");
                    searchConsultations(); 
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("View Consultation Details");
                    consultationController.viewConsultationDetails(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    // Refresh - just continue the loop to show consultations again
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    private void filterConsultations() {
        System.out.println("\n--- Filter Consultations ---");
        System.out.println("1. Filter by Status (Pending/Completed)");
        System.out.println("2. Filter by Doctor");
        System.out.println("3. Filter by Patient");
        System.out.println("0. Back to View All Consultations");
        System.out.print("\n\nChoose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1": 
                System.out.println("\n--- Filter by Status ---");
                System.out.println("1. Pending Consultations");
                System.out.println("2. Completed Consultations");
                System.out.print("Choose: ");
                String statusChoice = sc.nextLine().trim();
                if (statusChoice.equals("1")) {
                    consultationController.viewPendingConsultations();
                } else if (statusChoice.equals("2")) {
                    consultationController.viewCompletedConsultations();
                }
                break;
            case "2": 
                System.out.print("Enter Doctor ID: ");
                String doctorId = sc.nextLine().trim();
                consultationController.viewConsultationsByDoctor(doctorId);
                break;
            case "3": 
                consultationController.viewConsultationsByPatient();
                break;
            case "0": 
                return;
            default: 
                System.out.println("Invalid choice, try again.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void sortConsultations() {
        System.out.println("\n--- Sort Consultations ---");
        System.out.println("1. Sort by Date (Newest First)");
        System.out.println("2. Sort by Date (Oldest First)");
        System.out.println("3. Sort by Doctor ID");
        System.out.println("4. Sort by Patient ID");
        System.out.println("0. Back to View All Consultations");
        System.out.print("\n\nChoose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1": 
                System.out.println("Sorted by Date (Newest First)");
                // Implementation would go here
                break;
            case "2": 
                System.out.println("Sorted by Date (Oldest First)");
                // Implementation would go here
                break;
            case "3": 
                System.out.println("Sorted by Doctor ID");
                // Implementation would go here
                break;
            case "4": 
                System.out.println("Sorted by Patient ID");
                // Implementation would go here
                break;
            case "0": 
                return;
            default: 
                System.out.println("Invalid choice, try again.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void searchConsultations() {
        System.out.println("\n--- Search Consultations ---");
        System.out.print("Enter Consultation ID: ");
        String consultationId = sc.nextLine().trim();
        
        if (consultationId.isEmpty()) {
            System.out.println("Consultation ID cannot be empty!");
            return;
        }
        
        consultationController.viewConsultationDetails(consultationId);
        utility.SystemUtil.pauseForUser();
    }

    private void conductConsultation() {
        System.out.println("\n--- Conduct Consultation ---");
        
        System.out.print("Enter Consultation ID: ");
        String consultationId = sc.nextLine().trim();
        
        if (consultationId.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }
        
        // Show consultation details
        consultationController.viewConsultationDetails(consultationId);
        
        System.out.println("\nNote: To complete consultation with treatment,");
        System.out.println("      use the Treatment Module.");
    }

    // ==================== DOCTOR SCHEDULE MANAGEMENT ====================

    private void viewDoctorSchedules() {
        System.out.println("\n--- Doctor Schedules ---");
        doctorController.viewDoctorSchedules();
    }

    private void makeAppointment() {
        System.out.println("\n--- Make Appointment ---");
        
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        
        if (patientId.isEmpty()) {
            System.out.println("Patient ID cannot be empty.");
            return;
        }
        
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty.");
            return;
        }
        
        System.out.print("Enter Date (yyyy-MM-dd): ");
        String dateStr = sc.nextLine().trim();
        
        System.out.print("Enter Time (HH:mm): ");
        String timeStr = sc.nextLine().trim();
        
        System.out.print("Enter Specialty: ");
        String specialty = sc.nextLine().trim();
        
        // Create appointment
        boolean success = consultationController.createAppointment(patientId, doctorId, dateStr, timeStr, specialty);
        if (success) {
            System.out.println("Appointment created successfully!");
        } else {
            System.out.println("Failed to create appointment.");
        }
    }

    private void viewAvailableTimeSlots() {
        System.out.println("\n--- Available Time Slots ---");
        
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty.");
            return;
        }
        
        System.out.print("Enter Date (yyyy-MM-dd): ");
        String dateStr = sc.nextLine().trim();
        
        consultationController.viewAvailableTimeSlots(doctorId, dateStr);
    }

    // ==================== REPORTING ====================

    private void generateDailyConsultationSummary() {
        System.out.println("\n--- Daily Consultation Summary ---");
        
        System.out.print("Enter Date (yyyy-MM-dd) or 'today': ");
        String dateStr = sc.nextLine().trim();
        
        if (dateStr.equalsIgnoreCase("today")) {
            consultationController.generateDailyConsultationSummary();
        } else {
            consultationController.generateDailyConsultationSummary(dateStr);
        }
    }
    
    // ==================== PAYMENT PROCESSING ====================
    
    private void processPayment() {
        System.out.println("\n--- Process Payment for Consultation ---");
        
        // Show consultations that need payment
        System.out.println("Consultations requiring payment:");
        consultationController.viewConsultationsForPayment();
        
        if (consultationController.getConsultationsForPaymentCount() == 0) {
            System.out.println("No consultations require payment at this time.");
            return;
        }
        
        // Get consultation selection
        System.out.print("\nEnter Consultation ID to process payment: ");
        String consultationId = sc.nextLine().trim();
        
        if (consultationId.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }
        
        // Validate consultation exists and needs payment
        if (!consultationController.isConsultationEligibleForPayment(consultationId)) {
            System.out.println("Consultation ID '" + consultationId + "' is not eligible for payment.");
            return;
        }
        
        // Calculate payment amount automatically
        double paymentAmount = consultationController.calculateConsultationTotal(consultationId);
        if (paymentAmount <= 0) {
            System.out.println("Error: Could not calculate payment amount for consultation: " + consultationId);
            return;
        }
        
        System.out.println("Calculated Payment Amount: RM " + String.format("%.2f", paymentAmount));
        System.out.println("(Consultation fee + Medicine costs if prescribed)");
        
        // Show payment methods
        System.out.println("\n--- Available Payment Methods ---");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Debit Card");
        System.out.println("4. Bank Transfer");
        System.out.println("5. Insurance");
        System.out.println("6. Online Payment");
        
        // Get payment method selection
        int paymentMethodChoice = 0;
        while (true) {
            System.out.print("Choose payment method (1-6): ");
            String choiceStr = sc.nextLine().trim();
            try {
                paymentMethodChoice = Integer.parseInt(choiceStr);
                if (paymentMethodChoice >= 1 && paymentMethodChoice <= 6) {
                    break;
                } else {
                    System.out.println("Please choose 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        // Convert choice to PaymentMethod enum
        entity.Payment.PaymentMethod paymentMethod = getPaymentMethodFromChoice(paymentMethodChoice);
        
        // Get reference number (optional)
        System.out.print("Enter reference number (optional): ");
        String referenceNumber = sc.nextLine().trim();
        
        // Get notes (optional)
        System.out.print("Enter payment notes (optional): ");
        String notes = sc.nextLine().trim();
        
        // Process payment
        System.out.println("\nProcessing payment...");
        boolean success = consultationController.processConsultationPayment(consultationId, paymentAmount, 
                                                                        paymentMethod, referenceNumber, notes);
        
        if (success) {
            System.out.println("✓ Payment processed successfully!");
            System.out.println("Consultation ID: " + consultationId);
            System.out.println("Amount: RM " + String.format("%.2f", paymentAmount));
            System.out.println("Method: " + paymentMethod);
        } else {
            System.out.println("✗ Failed to process payment for consultation: " + consultationId);
        }
    }
    
    private entity.Payment.PaymentMethod getPaymentMethodFromChoice(int choice) {
        switch (choice) {
            case 1: return entity.Payment.PaymentMethod.CASH;
            case 2: return entity.Payment.PaymentMethod.CREDIT_CARD;
            case 3: return entity.Payment.PaymentMethod.DEBIT_CARD;
            case 4: return entity.Payment.PaymentMethod.BANK_TRANSFER;
            case 5: return entity.Payment.PaymentMethod.INSURANCE;
            case 6: return entity.Payment.PaymentMethod.ONLINE_PAYMENT;
            default: return entity.Payment.PaymentMethod.CASH;
        }
    }
}
