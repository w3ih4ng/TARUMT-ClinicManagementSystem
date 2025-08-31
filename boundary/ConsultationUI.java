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

            System.out.println("=== MAIN ===");
            System.out.println("  1. Consultations");
            System.out.println("  2. Appointments");
            System.out.println("  3. Payments");
            System.out.println("  0. Back to Staff Menu");
            System.out.println();
            System.out.println("Note: Patient queue management is in Patient Management Module");
            System.out.println("      Treatment creation is available after consultation is created");
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("\n\nEnter your choice (1-3, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.pushNavigation("Consultations");
                    consultationsMenu();
                    utility.SystemUtil.popNavigation();
                    break;
                case "2":
                    utility.SystemUtil.pushNavigation("Appointments");
                    appointmentsMenu();
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.pushNavigation("Payments");
                    paymentsMenu();
                    utility.SystemUtil.popNavigation();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-3 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    // ==================== CONSULTATIONS SUB-MENU ====================

    private void consultationsMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Consultations");

            System.out.println("  1. Create Consultation");
            System.out.println("  2. View All Consultations");
            System.out.println("  3. Update Consultation");
            System.out.println("  4. Delete Consultation");
            System.out.println("  0. Back to Consultation Management");
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("\n\nEnter your choice (1-4, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Create Consultation from Queue");
                    createConsultationFromQueue();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    utility.SystemUtil.pushNavigation("View All Consultations");
                    viewAllConsultations();
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Update Consultation");
                    updateConsultation();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Delete Consultation");
                    deleteConsultation();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-4 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    // ==================== APPOINTMENTS SUB-MENU ====================

    private void appointmentsMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Appointments");

            System.out.println("  1. Create Appointment");
            System.out.println("  2. View All Appointments");
            System.out.println("  3. Update Appointment");
            System.out.println("  4. Delete Appointment");
            System.out.println("  5. View Doctor Schedules");
            System.out.println("  6. View Available Time Slots");
            System.out.println("  0. Back to Consultation Management");
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("\n\nEnter your choice (1-6, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Create Appointment");
                    makeAppointment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("View All Appointments");
                    viewDoctorSchedules();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Update Appointment");
                    updateAppointment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Delete Appointment");
                    deleteAppointment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5":
                    utility.SystemUtil.showSectionHeader("View Doctor Schedules");
                    viewDoctorSchedules();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "6":
                    utility.SystemUtil.showSectionHeader("View Available Time Slots");
                    viewAvailableTimeSlots();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-6 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    // ==================== PAYMENTS SUB-MENU ====================

    private void paymentsMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Payments");

            System.out.println("  1. Process Consultation Payment");
            System.out.println("  2. View Payment History");
            System.out.println("  0. Back to Consultation Management");
            System.out.println();
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("\n\nEnter your choice (1-2, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Process Consultation Payment");
                    processPayment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("View Payment History");
                    viewPaymentHistory();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-2 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    // ==================== CONSULTATION CRUD METHODS ====================

    private void updateConsultation() {
        System.out.println("\n--- Update Consultation Status ---");
        System.out.print("Enter Consultation ID: ");
        String consultationId = sc.nextLine().trim();

        // Find consultation
        if (!consultationController.consultationExists(consultationId)) {
            System.out.println("Consultation not found.");
            return;
        }

        Consultation consultation = consultationController.getConsultation(consultationId);
        System.out.println("Current Status: " + consultation.getStatus());

        System.out.println("\nAvailable Status Options:");
        System.out.println("1. PENDING");
        System.out.println("2. SCHEDULED");
        System.out.println("3. IN_PROGRESS");
        System.out.println("4. TREATMENT_CREATED");
        System.out.println("5. COMPLETED");
        System.out.print("Select new status (1-5): ");

        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            String newStatus;
            switch (choice) {
                case 1: newStatus = "PENDING"; break;
                case 2: newStatus = "SCHEDULED"; break;
                case 3: newStatus = "IN_PROGRESS"; break;
                case 4: newStatus = "TREATMENT_CREATED"; break;
                case 5: newStatus = "COMPLETED"; break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            consultationController.updateConsultationStatus(consultationId, newStatus);
            System.out.println("Consultation status updated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void deleteConsultation() {
        System.out.println("\n--- Delete Consultation ---");
        System.out.print("Enter Consultation ID: ");
        String consultationId = sc.nextLine().trim();

        if (!consultationController.consultationExists(consultationId)) {
            System.out.println("Consultation not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this consultation? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            consultationController.deleteConsultation(consultationId);
            System.out.println("Consultation deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void updateAppointment() {
        System.out.println("\n--- Update Appointment ---");
        System.out.println("Appointment update functionality not implemented yet.");
        System.out.println("This would allow changing appointment time, doctor, etc.");
    }

    private void deleteAppointment() {
        System.out.println("\n--- Delete Appointment ---");
        System.out.println("Appointment deletion functionality not implemented yet.");
        System.out.println("This would allow cancelling scheduled appointments.");
    }

    private void viewPaymentHistory() {
        System.out.println("\n--- Payment History ---");

        try {
            // Load payment data
            HashMapInterface<String, entity.Payment> paymentMap = dao.PaymentDAO.loadPayments();

            // Load invoice data to get amounts
            HashMapInterface<String, entity.Invoice> invoiceMap = dao.InvoiceDAO.loadInvoices();

            if (paymentMap.isEmpty()) {
                System.out.println("No payment history found.");
                return;
            }

            // Display payment history table
            System.out.println("=".repeat(120));
            System.out.println("PAYMENT HISTORY");
            System.out.println("=".repeat(120));
            System.out.println(String.format("%-15s %-15s %-15s %-12s %-20s %-20s",
                "Payment ID", "Consultation ID", "Patient ID", "Amount (RM)", "Method", "Date"));
            System.out.println("-".repeat(120));

            // Sort payments by date (most recent first)
            ListInterface<entity.Payment> paymentList = new adt.ArrayList<>();
            for (int i = 0; i < paymentMap.keySet().size(); i++) {
                String key = paymentMap.keySet().get(i);
                entity.Payment payment = paymentMap.get(key);
                if (payment != null) {
                    paymentList.add(payment);
                }
            }

            // Display only PAID payments (in reverse order to show most recent first)
            for (int i = paymentList.size() - 1; i >= 0; i--) {
                entity.Payment payment = paymentList.get(i);

                // Only show PAID payments as per user requirement
                if (!payment.isPaid()) {
                    continue;
                }

                // Get amount from linked invoice
                String amountStr = "N/A";
                if (payment.getInvoiceId() != null) {
                    entity.Invoice invoice = invoiceMap.get(payment.getInvoiceId());
                    if (invoice != null) {
                        amountStr = String.format("%.2f", invoice.getTotalAmount());
                    }
                }

                String method = payment.getPaymentMethod().toString().replace("_", " ");
                String date = payment.getPaymentDate() != null ?
                    payment.getPaymentDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) :
                    "N/A";
                String remarks = payment.getRemarks().isEmpty() ? "-" : payment.getRemarks();

                System.out.println(String.format("%-15s %-15s %-15s %-12s %-20s %-20s",
                    payment.getPaymentId(),
                    payment.getConsultationId() != null ? payment.getConsultationId() : "N/A",
                    payment.getPatientId(),
                    amountStr,
                    method,
                    date));
                if (!remarks.equals("-")) {
                    System.out.println(String.format("Remarks: %s", remarks));
                }
            }

            System.out.println("=".repeat(120));

            // Count only PAID payments (since NOT_PAID are not displayed)
            int totalPaidPayments = 0;
            double totalAmountPaid = 0.0;

            for (int i = 0; i < paymentList.size(); i++) {
                entity.Payment payment = paymentList.get(i);
                if (payment.isPaid()) {
                    totalPaidPayments++;

                    // Add amount to total if invoice exists
                    if (payment.getInvoiceId() != null) {
                        entity.Invoice invoice = invoiceMap.get(payment.getInvoiceId());
                        if (invoice != null) {
                            totalAmountPaid += invoice.getTotalAmount();
                        }
                    }
                }
            }

            System.out.println("SUMMARY:");
            System.out.println("Total Paid Payments: " + totalPaidPayments);
            System.out.println("Total Amount Paid: RM " + String.format("%.2f", totalAmountPaid));
            System.out.println("=".repeat(120));

        } catch (Exception e) {
            System.out.println("Error loading payment history: " + e.getMessage());
            System.out.println("Please try again later.");
        }
    }

    // ==================== CONSULTATION MANAGEMENT ====================

    private void viewAllConsultations() {
        while (true) {
            // Show consultations first
            System.out.println("\n" + "=".repeat(80));
            System.out.println("ACTIVE CONSULTATIONS");
            System.out.println("=".repeat(80));
            consultationController.viewAllConsultations();
            
            // Show options
            System.out.println("\n" + "-".repeat(80));
            System.out.println("OPTIONS");
            System.out.println("-".repeat(80));
            System.out.println("1. Conduct Consultation");
            System.out.println("2. Sort Consultations");
            System.out.println("3. Search Consultation");
            System.out.println("4. View Consultation Details");
            System.out.println("5. Filter Consultations");
            System.out.println("6. Refresh List");
            System.out.println("0. Back to Consultation Management");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Conduct Consultation");
                    conductConsultation(); 
                    utility.SystemUtil.pauseForUser();
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
                    utility.SystemUtil.showSectionHeader("Filter Consultations");
                    filterConsultations(); 
                    break;
                case "6": 
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
        System.out.println("4. View All Consultations (Including Completed)");
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
                consultationController.viewConsultationsByDoctor();
                break;
            case "3": 
                consultationController.viewConsultationsByPatient();
                break;
            case "4": 
                consultationController.viewAllConsultationsIncludingCompleted();
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
                consultationController.sortConsultationsByDateNewestFirst();
                break;
            case "2": 
                consultationController.sortConsultationsByDateOldestFirst();
                break;
            case "3": 
                consultationController.sortConsultationsByDoctorId();
                break;
            case "4": 
                System.out.println("Sorting by Patient ID is not implemented in this version.");
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

        // Set consultation status to IN_PROGRESS
        boolean statusUpdated = consultationController.updateConsultationStatus(consultationId, "IN_PROGRESS");

        if (statusUpdated) {
            System.out.println("Consultation status updated to IN_PROGRESS.");
        } else {
            System.out.println("Failed to update consultation status. Consultation may not exist.");
            return;
        }

        // Show consultation details using the entered ID
        consultationController.viewConsultationDetails(consultationId);

        System.out.println("\nNote: To complete consultation with treatment,");
        System.out.println("      use the Treatment Module.");
    }

    // ==================== DOCTOR SCHEDULE MANAGEMENT ====================

    private void viewDoctorSchedules() {
        System.out.println("\n--- Doctor Schedules ---");
        consultationController.viewDoctorSchedules();
    }

    private void makeAppointment() {
        System.out.println("\n--- Make Appointment ---");
        
        // 1. Select Patient
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();

        if (patientId.isEmpty()) {
            System.out.println("Patient ID cannot be empty.");
            return;
        }

        // Validate patient exists
        if (!consultationController.patientExists(patientId)) {
            System.out.println("Patient ID '" + patientId + "' does not exist in the system.");
            System.out.println("Please check the Patient ID and try again.");
            return;
        }

        // 2. Select Doctor
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();

        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty.");
            return;
        }

        // Validate doctor exists
        if (!consultationController.doctorExists(doctorId)) {
            System.out.println("Doctor ID '" + doctorId + "' does not exist in the system.");
            System.out.println("Please check the Doctor ID and try again.");
            return;
        }
        
        // 3. Enter Date (today or future only)
        System.out.println("\n--- Enter Appointment Date ---");
        String dateStr;

        while (true) {
            System.out.print("Enter date (yyyy-MM-dd): ");
            dateStr = sc.nextLine().trim();

            try {
                java.time.LocalDate selectedDate = java.time.LocalDate.parse(dateStr);
                java.time.LocalDate today = java.time.LocalDate.now();

                if (selectedDate.isBefore(today)) {
                    System.out.println("Cannot select past dates. Please choose today or a future date.");
                    continue;
                }

                System.out.println("Selected date: " + dateStr);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd format.");
                continue;
            }
        }
        
        // 4. Select Time Slot
        System.out.println("\n--- Select Time Slot ---");
        System.out.println("Available time slots (1-hour slots):");
        System.out.println("1. 09:00 - 10:00");
        System.out.println("2. 10:00 - 11:00");
        System.out.println("3. 11:00 - 12:00");
        System.out.println("4. 14:00 - 15:00");
        System.out.println("5. 15:00 - 16:00");
        System.out.println("6. 16:00 - 17:00");
        System.out.println("7. Custom time");
        System.out.print("Choose time slot (1-7): ");
        
        String timeChoice = sc.nextLine().trim();
        String timeStr;
        
        switch (timeChoice) {
            case "1": timeStr = "09:00"; break;
            case "2": timeStr = "10:00"; break;
            case "3": timeStr = "11:00"; break;
            case "4": timeStr = "14:00"; break;
            case "5": timeStr = "15:00"; break;
            case "6": timeStr = "16:00"; break;
            case "7":
                System.out.print("Enter custom time (HH:mm): ");
                timeStr = sc.nextLine().trim();
                break;
            default:
                System.out.println("Invalid choice. Using 09:00.");
                timeStr = "09:00";
        }
        
        // 5. Select Specialty (auto-detect from doctor, but allow override)
        System.out.println("\n--- Select Specialty ---");
        String specialty;
        
        // Try to get specialty from doctor
        try {
            Doctor doctor = doctorController.getDoctorById(doctorId);
            if (doctor != null) {
                System.out.println("Doctor's specialty: " + doctor.getSpecialty());
                System.out.println("1. Use doctor's specialty (" + doctor.getSpecialty() + ")");
                System.out.println("2. Choose different specialty");
                System.out.print("Choose option (1-2): ");
                
                String specialtyChoice = sc.nextLine().trim();
                if (specialtyChoice.equals("1")) {
                    specialty = doctor.getSpecialty().toString();
                } else {
                    specialty = selectSpecialty();
                }
            } else {
                specialty = selectSpecialty();
            }
        } catch (Exception e) {
            specialty = selectSpecialty();
        }
        
        // Create appointment
        boolean success = consultationController.createAppointment(patientId, doctorId, dateStr, timeStr, specialty);
        if (success) {
            System.out.println("Appointment created successfully!");
        } else {
            System.out.println("Failed to create appointment.");
        }
    }
    
    private String selectSpecialty() {
        System.out.println("\nAvailable specialties:");
        System.out.println("1. GENERAL_MEDICINE");
        System.out.println("2. PEDIATRICS");
        System.out.println("3. DENTISTRY");
        System.out.println("4. CARDIOLOGY");
        System.out.println("5. DERMATOLOGY");
        System.out.println("6. ORTHOPEDICS");
        System.out.println("7. NEUROLOGY");
        System.out.println("8. PSYCHIATRY");
        System.out.println("9. CUSTOM");
        System.out.print("Choose specialty (1-9): ");
        
        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1": return "GENERAL_MEDICINE";
            case "2": return "PEDIATRICS";
            case "3": return "DENTISTRY";
            case "4": return "CARDIOLOGY";
            case "5": return "DERMATOLOGY";
            case "6": return "ORTHOPEDICS";
            case "7": return "NEUROLOGY";
            case "8": return "PSYCHIATRY";
            case "9":
                System.out.print("Enter custom specialty: ");
                return sc.nextLine().trim();
            default:
                System.out.println("Invalid choice. Using GENERAL_MEDICINE.");
                return "GENERAL_MEDICINE";
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
        
        // Validate doctor exists
        if (!consultationController.doctorExists(doctorId)) {
            System.out.println("Doctor ID '" + doctorId + "' does not exist in the system.");
            System.out.println("Please check the Doctor ID and try again.");
            return;
        }

        // Enter Date (today or future only)
        System.out.println("\n--- Enter Date ---");
        String dateStr;

        while (true) {
            System.out.print("Enter date (yyyy-MM-dd) - today or future only: ");
            dateStr = sc.nextLine().trim();

            try {
                java.time.LocalDate selectedDate = java.time.LocalDate.parse(dateStr);
                java.time.LocalDate today = java.time.LocalDate.now();

                if (selectedDate.isBefore(today)) {
                    System.out.println("Cannot select past dates. Please choose today or a future date.");
                    continue;
                }

                System.out.println("Selected date: " + dateStr);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd format.");
                continue;
            }
        }
        
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
        
        // Calculate payment amount with detailed breakdown
        double[] costBreakdown = consultationController.calculateConsultationTotalWithBreakdown(consultationId);
        if (costBreakdown[0] < 0) {
            System.out.println("Error: Could not calculate payment amount for consultation: " + consultationId);
            return;
        }

        double consultationFee = costBreakdown[0];
        double treatmentFee = costBreakdown[1];
        double medicineCost = costBreakdown[2];
        double totalAmount = consultationFee + treatmentFee + medicineCost;

        // Check if this consultation has an invoice
        boolean hasInvoice = false;
        try {
            HashMapInterface<String, entity.Invoice> invoiceMap = dao.InvoiceDAO.loadInvoices();
            for (int i = 0; i < invoiceMap.keySet().size(); i++) {
                String invoiceId = invoiceMap.keySet().get(i);
                entity.Invoice invoice = invoiceMap.get(invoiceId);
                if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                    hasInvoice = true;
                    break;
                }
            }
        } catch (Exception e) {
            // Ignore errors when checking for invoice
        }

        // Display detailed cost breakdown table
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PAYMENT BREAKDOWN");
        System.out.println("=".repeat(60));
        if (hasInvoice) {
            System.out.println("(Based on invoice)");
        } else {
            System.out.println("(Calculated from consultation data)");
        }
        System.out.println(String.format("%-25s %10s", "Item", "Amount (RM)"));
        System.out.println("-".repeat(60));
        System.out.println(String.format("%-25s %10.2f", "Consultation Fee", consultationFee));
        if (treatmentFee > 0) {
            System.out.println(String.format("%-25s %10.2f", "Treatment Fee", treatmentFee));
        }
        if (medicineCost > 0) {
            System.out.println(String.format("%-25s %10.2f", "Medicine Cost", medicineCost));
        }
        System.out.println("-".repeat(60));
        System.out.println(String.format("%-25s %10.2f", "TOTAL AMOUNT", totalAmount));
        System.out.println("=".repeat(60));
        
        // Show payment methods
        System.out.println("\n--- Available Payment Methods ---");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Debit Card");
        System.out.println("4. Bank Transfer");
        System.out.println("5. Insurance");
        System.out.println("6. Online Payment");
        System.out.println("0. Cancel/Go Back");

        // Get payment method selection
        int paymentMethodChoice = 0;
        while (true) {
            System.out.print("Choose payment method (0-6): ");
            String choiceStr = sc.nextLine().trim();
            try {
                paymentMethodChoice = Integer.parseInt(choiceStr);
                if (paymentMethodChoice == 0) {
                    System.out.println("Payment cancelled. Returning to consultation menu.");
                    return;
                } else if (paymentMethodChoice >= 1 && paymentMethodChoice <= 6) {
                    break;
                } else {
                    System.out.println("Please choose 0-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        // Convert choice to PaymentMethod enum
        entity.Payment.PaymentMethod paymentMethod = getPaymentMethodFromChoice(paymentMethodChoice);
        

        
                // Confirm payment
        System.out.print("\nConfirm payment of RM " + String.format("%.2f", totalAmount) + "? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Payment cancelled.");
            return;
        }

        // Get remarks (optional)
        System.out.print("Enter payment remarks (optional): ");
        String remarks = sc.nextLine().trim();

        // Process payment
        System.out.println("\nProcessing payment...");
        boolean success = consultationController.processConsultationPayment(consultationId, paymentMethod, remarks);

        if (success) {
            System.out.println("Payment processed successfully!");
            System.out.println("=".repeat(40));
            System.out.println("Consultation ID: " + consultationId);
            System.out.println("Total Amount Paid: RM " + String.format("%.2f", totalAmount));
            System.out.println("Payment Method: " + paymentMethod);
            if (!remarks.isEmpty()) {
                System.out.println("Remarks: " + remarks);
            }
            System.out.println("=".repeat(40));
            System.out.println("Thank you for your payment!");
        } else {
            System.out.println("Failed to process payment for consultation: " + consultationId);
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

    private void createConsultationFromQueue() {
        System.out.println("\n--- Create Consultation from Queue Entry ---");
        
        // 1. Select Queue Entry
        System.out.println("Available queue entries:");
        ListInterface<entity.PatientQueueEntry> readyEntries = consultationController.getQueueEntriesReadyForConsultation();
        if (readyEntries.isEmpty()) {
            System.out.println("No queue entries ready for consultation at this time.");
            return;
        }

        System.out.println("\n=== QUEUE ENTRIES READY FOR CONSULTATION ===");
        System.out.printf("%-10s %-10s %-20s %-15s %-15s%n",
            "Queue ID", "Patient ID", "Specialty", "Queue Type", "Doctor ID");
        System.out.println("-".repeat(80));

        for (entity.PatientQueueEntry entry : readyEntries) {
            String doctorId = entry.getAssignedDoctorId() != null ? entry.getAssignedDoctorId() : "N/A";

            System.out.printf("%-10s %-10s %-20s %-15s %-15s%n",
                entry.getQueueId(),
                entry.getPatientId(),
                entry.getSpecialty(),
                entry.getQueueType(),
                doctorId);
        }
        
        System.out.print("\nEnter Queue ID to create consultation: ");
        String queueEntryId = sc.nextLine().trim();
        
        if (queueEntryId.isEmpty()) {
            System.out.println("Queue ID cannot be empty.");
            return;
        }
        
        // Get queue entry details
        entity.PatientQueueEntry entry = consultationController.getQueueEntry(queueEntryId);
        if (entry == null) {
            System.out.println("Queue entry not found.");
            return;
        }

        if (!entry.isAssigned()) {
            System.out.println("Queue entry is not assigned to a doctor.");
            return;
        }

        if (entry.getAssignedDoctorId() == null) {
            System.out.println("No doctor assigned to this queue entry.");
            return;
        }

        // Use information from queue entry
        String doctorId = entry.getAssignedDoctorId();
        String specialty = entry.getSpecialty();
        String patientId = entry.getPatientId();

        // Use current date and time for consultation
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        System.out.println("Creating consultation for:");
        System.out.println("- Patient: " + patientId);
        System.out.println("- Doctor: " + doctorId);
        System.out.println("- Specialty: " + specialty);
        System.out.println("- Date/Time: " + dateStr + " " + timeStr);

        // Create consultation
        boolean success = consultationController.createConsultationFromQueueEntry(queueEntryId, doctorId, dateStr, timeStr, specialty);
        if (success) {
            System.out.println("Consultation created successfully from queue entry: " + queueEntryId);
        } else {
            System.out.println("Failed to create consultation from queue entry: " + queueEntryId);
        }
    }


}
