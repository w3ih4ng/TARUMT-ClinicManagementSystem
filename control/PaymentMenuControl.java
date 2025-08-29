package control;

import entity.*;
import adt.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Control class for payment menu business logic
 * @author Your Name
 */
public class PaymentMenuControl {
    private PaymentControl paymentControl;
    private InvoiceControl invoiceControl;
    private Scanner sc;

    public PaymentMenuControl(PaymentControl paymentControl, InvoiceControl invoiceControl) {
        this.paymentControl = paymentControl;
        this.invoiceControl = invoiceControl;
        this.sc = new Scanner(System.in);
    }

    /**
     * Get unpaid invoices for display
     */
    public ListInterface<Invoice> getUnpaidInvoices() {
        return invoiceControl.getUnpaidInvoices();
    }

    /**
     * Get patient ID from consultation (placeholder for future enhancement)
     */
    public String getPatientIdFromConsultation(String consultationId) {
        // This would need to be implemented to get patient ID from consultation
        // For now, return consultation ID - can be enhanced later
        return consultationId;
    }

    /**
     * Get completed payments for refund
     */
    public ListInterface<Payment> getCompletedPayments() {
        return paymentControl.getPaymentsByStatus(Payment.PaymentStatus.COMPLETED);
    }

    /**
     * Get all payments
     */
    public ListInterface<Payment> getAllPayments() {
        return paymentControl.getAllPayments();
    }

    /**
     * Get payments by patient
     */
    public ListInterface<Payment> getPaymentsByPatient(String patientId) {
        return paymentControl.getPatientPaymentHistory(patientId);
    }

    /**
     * Get payments by status
     */
    public ListInterface<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentControl.getPaymentsByStatus(status);
    }

    /**
     * Process payment for an invoice
     */
    public boolean processPaymentForInvoice(String invoiceId, Payment.PaymentMethod paymentMethod, 
                                          String referenceNumber, String notes) {
        return paymentControl.processPayment(invoiceId, paymentMethod, referenceNumber, notes);
    }

    /**
     * Refund a payment
     */
    public boolean refundPayment(String paymentId, String reason) {
        return paymentControl.refundPayment(paymentId, reason);
    }

    /**
     * Display payment statistics
     */
    public void displayPaymentStatistics() {
        paymentControl.displayPaymentStatistics();
    }

    /**
     * Get payment method from user selection
     */
    public Payment.PaymentMethod selectPaymentMethod(Scanner sc) {
        System.out.println("\nSelect Payment Method:");
        Payment.PaymentMethod[] methods = Payment.PaymentMethod.values();
        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i]);
        }

        System.out.print("Enter choice (1-" + methods.length + "): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= methods.length) {
                return methods[choice - 1];
            } else {
                System.out.println("Invalid choice.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    /**
     * Get payment status from user selection
     */
    public Payment.PaymentStatus selectPaymentStatus(Scanner sc) {
        System.out.println("\nSelect Payment Status:");
        Payment.PaymentStatus[] statuses = Payment.PaymentStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }

        System.out.print("Enter choice (1-" + statuses.length + "): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= statuses.length) {
                return statuses[choice - 1];
            } else {
                System.out.println("Invalid choice.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    /**
     * Display payments in a formatted table
     */
    public void displayPaymentsTable(ListInterface<Payment> payments, String title) {
        System.out.println("\n--- " + title + " ---");
        String borderLine = "+--------+--------+------------+------------+--------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-6s | %-10s | %-10s | %-6s | %-10s | %-10s |%n",
                "PayID", "InvID", "PatientID", "Amount", "Method", "Status", "Date");
        System.out.println(borderLine);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < payments.size(); i++) {
            Payment payment = payments.get(i);
            System.out.printf("| %-6s | %-6s | %-10s | %-8.2f | %-6s | %-10s | %-10s |%n",
                    payment.getPaymentId(),
                    payment.getInvoiceId(),
                    payment.getPatientId(),
                    payment.getAmount(),
                    payment.getPaymentMethod().toString().substring(0, Math.min(6, payment.getPaymentMethod().toString().length())),
                    payment.getStatus().toString().substring(0, Math.min(10, payment.getStatus().toString().length())),
                    payment.getPaymentDate().format(dateFormatter));
        }
        System.out.println(borderLine);
    }
}
