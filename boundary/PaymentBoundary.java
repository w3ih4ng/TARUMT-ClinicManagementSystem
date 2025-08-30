package boundary;

import control.PaymentController;
import entity.*;
import adt.*;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Boundary class for payment management user interface
 * @author Your Name
 */
public class PaymentBoundary {
    private Scanner sc;
    private PaymentController paymentController;

    public PaymentBoundary(PaymentController paymentController) {
        this.sc = new Scanner(System.in);
        this.paymentController = paymentController;
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Payment Management");
            
            System.out.println("1. Process Payment");
            System.out.println("2. View Payment History");
            System.out.println("3. View Payment Statistics");
            System.out.println("4. Refund Payment");
            System.out.println("5. View Unpaid Invoices");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Process Payment");
                    processPayment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Payment History");
                    viewPaymentHistory(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Payment Statistics");
                    viewPaymentStatistics(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Refund Payment");
                    refundPayment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Unpaid Invoices");
                    viewUnpaidInvoices(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": return;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void processPayment() {
        System.out.println("\n--- Process Payment ---");
        
        // Show unpaid invoices
        ListInterface<Invoice> unpaidInvoices = paymentController.getUnpaidInvoices();
        if (unpaidInvoices.isEmpty()) {
            System.out.println("No unpaid invoices found.");
            return;
        }

        System.out.println("\nUnpaid Invoices:");
        for (int i = 0; i < unpaidInvoices.size(); i++) {
            Invoice invoice = unpaidInvoices.get(i);
            System.out.println((i + 1) + ". " + invoice.getInvoiceId() + 
                " - Consultation: " + invoice.getConsultationId() + 
                " - Amount: RM " + String.format("%.2f", invoice.getAmount()) +
                " - Date: " + invoice.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        // Select invoice
        System.out.print("Select invoice to pay (1-" + unpaidInvoices.size() + "): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice < 1 || choice > unpaidInvoices.size()) {
                System.out.println("Invalid invoice selection.");
                return;
            }

            Invoice selectedInvoice = unpaidInvoices.get(choice - 1);
            processPaymentForInvoice(selectedInvoice);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void processPaymentForInvoice(Invoice invoice) {
        System.out.println("\nProcessing payment for Invoice: " + invoice.getInvoiceId());
        System.out.println("Amount: RM " + String.format("%.2f", invoice.getAmount()));

        // Select payment method
        Payment.PaymentMethod paymentMethod = paymentController.selectPaymentMethod(sc);
        if (paymentMethod == null) return;

        // Get reference number
        System.out.print("Enter reference number (optional): ");
        String referenceNumber = sc.nextLine().trim();

        // Get notes
        System.out.print("Enter payment notes (optional): ");
        String notes = sc.nextLine().trim();

        // Confirm payment
        System.out.println("\nPayment Summary:");
        System.out.println("Invoice: " + invoice.getInvoiceId());
        System.out.println("Amount: RM " + String.format("%.2f", invoice.getAmount()));
        System.out.println("Method: " + paymentMethod);
        System.out.println("Reference: " + (referenceNumber.isEmpty() ? "N/A" : referenceNumber));
        System.out.println("Notes: " + (notes.isEmpty() ? "N/A" : notes));

        System.out.print("Confirm payment? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            boolean success = paymentController.processPaymentForInvoice(invoice.getInvoiceId(), paymentMethod, referenceNumber, notes);
            if (success) {
                System.out.println("Payment processed successfully!");
            } else {
                System.out.println("Payment processing failed.");
            }
        } else {
            System.out.println("Payment cancelled.");
        }
    }



    private void viewPaymentHistory() {
        System.out.println("\n--- Payment History ---");
        System.out.println("1. View All Payments");
        System.out.println("2. View Payments by Patient");
        System.out.println("3. View Payments by Status");
        System.out.print("Enter choice: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1": viewAllPayments(); break;
            case "2": viewPaymentsByPatient(); break;
            case "3": viewPaymentsByStatus(); break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void viewAllPayments() {
        ListInterface<Payment> payments = paymentController.getAllPayments();
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        paymentController.displayPaymentsTable(payments, "All Payments");
    }

    private void viewPaymentsByPatient() {
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        
        ListInterface<Payment> payments = paymentController.getPaymentsByPatient(patientId);
        if (payments.isEmpty()) {
            System.out.println("No payments found for patient: " + patientId);
            return;
        }

        paymentController.displayPaymentsTable(payments, "Payments for Patient: " + patientId);
    }

    private void viewPaymentsByStatus() {
        System.out.println("\nSelect Payment Status:");
        Payment.PaymentStatus[] statuses = Payment.PaymentStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }

        System.out.print("Enter choice (1-" + statuses.length + "): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= statuses.length) {
                Payment.PaymentStatus status = statuses[choice - 1];
                ListInterface<Payment> payments = paymentController.getPaymentsByStatus(status);
                
                if (payments.isEmpty()) {
                    System.out.println("No " + status + " payments found.");
                } else {
                    paymentController.displayPaymentsTable(payments, status + " Payments");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }



    private void viewPaymentStatistics() {
        paymentController.displayPaymentStatistics();
    }

    private void refundPayment() {
        System.out.println("\n--- Refund Payment ---");
        
        // Show completed payments
        ListInterface<Payment> completedPayments = paymentController.getCompletedPayments();
        if (completedPayments.isEmpty()) {
            System.out.println("No completed payments found for refund.");
            return;
        }

        System.out.println("\nCompleted Payments (Eligible for Refund):");
        for (int i = 0; i < completedPayments.size(); i++) {
            Payment payment = completedPayments.get(i);
            System.out.println((i + 1) + ". " + payment.getPaymentId() + 
                " - Invoice: " + payment.getInvoiceId() + 
                " - Amount: RM " + String.format("%.2f", payment.getAmount()) +
                " - Date: " + payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        System.out.print("Select payment to refund (1-" + completedPayments.size() + "): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice < 1 || choice > completedPayments.size()) {
                System.out.println("Invalid payment selection.");
                return;
            }

            Payment selectedPayment = completedPayments.get(choice - 1);
            
            System.out.print("Enter refund reason: ");
            String reason = sc.nextLine().trim();
            
            if (reason.isEmpty()) {
                System.out.println("Refund reason is required.");
                return;
            }

            System.out.print("Confirm refund? (y/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                boolean success = paymentController.refundPayment(selectedPayment.getPaymentId(), reason);
                if (success) {
                    System.out.println("Payment refunded successfully!");
                } else {
                    System.out.println("Payment refund failed.");
                }
            } else {
                System.out.println("Refund cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void viewUnpaidInvoices() {
        ListInterface<Invoice> unpaidInvoices = paymentController.getUnpaidInvoices();
        if (unpaidInvoices.isEmpty()) {
            System.out.println("No unpaid invoices found.");
            return;
        }

        System.out.println("\n--- Unpaid Invoices ---");
        String borderLine = "+--------+------------+------------+--------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-10s | %-6s | %-10s |%n",
                "InvID", "ConsultID", "PatientID", "Amount", "Date");
        System.out.println(borderLine);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < unpaidInvoices.size(); i++) {
            Invoice invoice = unpaidInvoices.get(i);
            System.out.printf("| %-6s | %-10s | %-10s | %-6.2f | %-10s |%n",
                    invoice.getInvoiceId(),
                    invoice.getConsultationId(),
                    paymentController.getPatientIdFromConsultation(invoice.getConsultationId()),
                    invoice.getAmount(),
                    invoice.getCreatedTime().format(dateFormatter));
        }
        System.out.println(borderLine);
    }


}
