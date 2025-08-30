package control;

import entity.*;
import adt.*;
import dao.PaymentDAO;
import dao.InvoiceDAO;
import dao.ConsultationDAO;
import dao.PatientDAO;
import dao.DoctorDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Consolidated Payment Controller - combines payment and invoice functionality
 * Handles payment processing, invoice generation, and financial management
 * @author Your Name
 */
public class PaymentController {
    private HashMapInterface<String, Payment> paymentMap;
    private HashMapInterface<String, Invoice> invoiceMap;
    private HashMapInterface<String, Consultation> consultationMap;
    private HashMapInterface<String, Patient> patientMap;
    private HashMapInterface<String, Doctor> doctorMap;

    public PaymentController() {
        this.paymentMap = PaymentDAO.loadPayments();
        this.invoiceMap = InvoiceDAO.loadInvoices();
        this.consultationMap = ConsultationDAO.loadConsultations();
        this.patientMap = PatientDAO.loadPatients();
        this.doctorMap = DoctorDAO.loadDoctors();
    }

    // ==================== PAYMENT OPERATIONS ====================

    /**
     * Process a new payment for an invoice
     */
    public boolean processPayment(String invoiceId, Payment.PaymentMethod paymentMethod, 
                                 String referenceNumber, String notes) {
        Invoice invoice = invoiceMap.get(invoiceId);
        if (invoice == null) {
            System.out.println("Invoice not found: " + invoiceId);
            return false;
        }

        if (invoice.isPaid()) {
            System.out.println("Invoice is already paid: " + invoiceId);
            return false;
        }

        // Create payment record
        String paymentId = PaymentDAO.generatePaymentId();
        Payment payment = new Payment(paymentId, invoiceId, invoice.getConsultationId(), 
                                    getPatientIdFromConsultation(invoice.getConsultationId()),
                                    invoice.getAmount(), paymentMethod);
        
        payment.setReferenceNumber(referenceNumber);
        payment.setNotes(notes);
        payment.markCompleted();

        // Save payment
        paymentMap.put(paymentId, payment);
        PaymentDAO.savePayments(paymentMap);

        // Mark invoice as paid
        invoice.markPaid();
        invoiceMap.put(invoiceId, invoice);
        InvoiceDAO.saveInvoices(invoiceMap);

        // Update consultation with payment ID
        updateConsultationPayment(invoice.getConsultationId(), paymentId);

        System.out.println("Payment processed successfully: " + paymentId);
        System.out.println("Amount: RM " + String.format("%.2f", invoice.getAmount()));
        System.out.println("Method: " + paymentMethod);
        return true;
    }

    // Convenience wrapper used by UI
    public boolean processPaymentForInvoice(String invoiceId, Payment.PaymentMethod paymentMethod,
                                            String referenceNumber, String notes) {
        return processPayment(invoiceId, paymentMethod, referenceNumber, notes);
    }

    /**
     * Get payment history for a patient
     */
    public ListInterface<Payment> getPatientPaymentHistory(String patientId) {
        ListInterface<Payment> patientPayments = new ArrayList<>();
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && payment.getPatientId().equals(patientId) && !payment.isDeleted()) {
                patientPayments.add(payment);
            }
        }
        
        return patientPayments;
    }

    // Used by PaymentBoundary
    public ListInterface<Payment> getPaymentsByPatient(String patientId) {
        return getPatientPaymentHistory(patientId);
    }

    /**
     * Get all payments
     */
    public ListInterface<Payment> getAllPayments() {
        ListInterface<Payment> allPayments = new ArrayList<>();
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && !payment.isDeleted()) {
                allPayments.add(payment);
            }
        }
        
        return allPayments;
    }

    public ListInterface<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        ListInterface<Payment> filteredPayments = new ArrayList<>();
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment p = paymentMap.get(key);
            if (p != null && p.getStatus() == status && !p.isDeleted()) {
                filteredPayments.add(p);
            }
        }
        return filteredPayments;
    }

    public ListInterface<Payment> getCompletedPayments() {
        return getPaymentsByStatus(Payment.PaymentStatus.COMPLETED);
    }

    public void displayPaymentsTable(ListInterface<Payment> payments, String title) {
        System.out.println("\n--- " + title + " ---");
        String borderLine = "+------------+------------+------------+----------+---------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-8s | %-19s |%n",
                "PayID", "InvID", "PatientID", "Amount", "Date");
        System.out.println(borderLine);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            System.out.printf("| %-10s | %-10s | %-10s | %-8.2f | %-19s |%n",
                    p.getPaymentId(),
                    p.getInvoiceId(),
                    p.getPatientId(),
                    p.getAmount(),
                    p.getPaymentDate().format(fmt));
        }
        System.out.println(borderLine);
    }

    /**
     * Refund a payment
     */
    public boolean refundPayment(String paymentId, String reason) {
        Payment payment = paymentMap.get(paymentId);
        if (payment == null) {
            System.out.println("Payment not found: " + paymentId);
            return false;
        }

        if (!payment.isCompleted()) {
            System.out.println("Payment is not completed, cannot refund: " + paymentId);
            return false;
        }

        payment.markRefunded();
        payment.setNotes(payment.getNotes() + " | Refunded: " + reason);
        
        // Mark invoice as unpaid
        String invoiceId = payment.getInvoiceId();
        Invoice invoice = invoiceMap.get(invoiceId);
        if (invoice != null) {
            invoice.markUnpaid();
            invoiceMap.put(invoiceId, invoice);
            InvoiceDAO.saveInvoices(invoiceMap);
        }

        paymentMap.put(paymentId, payment);
        PaymentDAO.savePayments(paymentMap);

        System.out.println("Payment refunded successfully: " + paymentId);
        return true;
    }

    // ==================== INVOICE OPERATIONS ====================

    /**
     * Generate invoice for a consultation
     */
    public String generateInvoice(String consultationId, double totalAmount) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return null;
        }

        String invoiceId = InvoiceDAO.generateInvoiceId();
        Invoice invoice = new Invoice(invoiceId, consultationId, totalAmount);

        invoiceMap.put(invoiceId, invoice);
        InvoiceDAO.saveInvoices(invoiceMap);

        System.out.println("Invoice generated successfully: " + invoiceId);
        return invoiceId;
    }

    /**
     * Get all unpaid invoices
     */
    public ListInterface<Invoice> getUnpaidInvoices() {
        ListInterface<Invoice> unpaidInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && !invoice.isPaid()) {
                unpaidInvoices.add(invoice);
            }
        }
        
        return unpaidInvoices;
    }

    /**
     * Get unpaid invoices for a specific patient
     */
    public ListInterface<Invoice> getUnpaidInvoicesForPatient(String patientId) {
        ListInterface<Invoice> unpaidInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && !invoice.isPaid() && 
                getPatientIdFromConsultation(invoice.getConsultationId()).equals(patientId)) {
                unpaidInvoices.add(invoice);
            }
        }
        
        return unpaidInvoices;
    }

    /**
     * Get invoice by ID
     */
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceMap.get(invoiceId);
    }

    /**
     * Get all invoices
     */
    public ListInterface<Invoice> getAllInvoices() {
        ListInterface<Invoice> allInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null) {
                allInvoices.add(invoice);
            }
        }
        
        return allInvoices;
    }

    /**
     * Display invoice details
     */
    public void displayInvoiceDetails(String invoiceId) {
        Invoice invoice = invoiceMap.get(invoiceId);
        if (invoice == null) {
            System.out.println("Invoice not found: " + invoiceId);
            return;
        }

        Consultation consultation = consultationMap.get(invoice.getConsultationId());
        Patient patient = (consultation != null) ? patientMap.get(consultation.getPatientId()) : null;
        Doctor doctor = (consultation != null) ? doctorMap.get(consultation.getDoctorId()) : null;

        System.out.println("\n=== INVOICE DETAILS ===");
        System.out.println("Invoice ID: " + invoice.getInvoiceId());
        System.out.println("Consultation ID: " + invoice.getConsultationId());
        System.out.println("Patient: " + (patient != null ? patient.getName() : "Unknown"));
        System.out.println("Doctor: " + (doctor != null ? doctor.getName() : "Unknown"));
        System.out.println("Generated Date: " + invoice.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Status: " + (invoice.isPaid() ? "PAID" : "UNPAID"));
        System.out.println("Total Amount: RM " + String.format("%.2f", invoice.getAmount()));
    }

    // ==================== REPORTING OPERATIONS ====================

    /**
     * Generate financial summary report
     */
    public void generateFinancialSummaryReport() {
        System.out.println("\n=== FINANCIAL SUMMARY REPORT ===");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println();

        // Payment statistics
        int totalPayments = 0;
        int completedPayments = 0;
        double totalRevenue = 0.0;
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && !payment.isDeleted()) {
                totalPayments++;
                if (payment.isCompleted()) {
                    completedPayments++;
                    totalRevenue += payment.getAmount();
                }
            }
        }

        // Invoice statistics
        int totalInvoices = 0;
        int paidInvoices = 0;
        int unpaidInvoices = 0;
        double totalInvoiceAmount = 0.0;
        double totalUnpaidAmount = 0.0;
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null) {
                totalInvoices++;
                totalInvoiceAmount += invoice.getAmount();
                
                if (invoice.isPaid()) {
                    paidInvoices++;
                } else {
                    unpaidInvoices++;
                    totalUnpaidAmount += invoice.getAmount();
                }
            }
        }

        System.out.println("--- PAYMENT SUMMARY ---");
        System.out.println("Total Payments: " + totalPayments);
        System.out.println("Completed Payments: " + completedPayments);
        System.out.println("Total Revenue: RM " + String.format("%.2f", totalRevenue));
        System.out.println();
        
        System.out.println("--- INVOICE SUMMARY ---");
        System.out.println("Total Invoices: " + totalInvoices);
        System.out.println("Paid Invoices: " + paidInvoices);
        System.out.println("Unpaid Invoices: " + unpaidInvoices);
        System.out.println("Total Invoice Amount: RM " + String.format("%.2f", totalInvoiceAmount));
        System.out.println("Total Unpaid Amount: RM " + String.format("%.2f", totalUnpaidAmount));
        System.out.println();
        
        double collectionRate = totalInvoices > 0 ? (double) paidInvoices / totalInvoices * 100 : 0;
        System.out.println("Collection Rate: " + String.format("%.1f%%", collectionRate));
    }

    // ==================== HELPER METHODS ====================

    public String getPatientIdFromConsultation(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        return consultation != null ? consultation.getPatientId() : null;
    }

    private void updateConsultationPayment(String consultationId, String paymentId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            consultation.setPayment(paymentId);
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
        }
    }

    // Allow UI to choose method
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
            }
        } catch (NumberFormatException ignored) {}
        System.out.println("Invalid choice.");
        return null;
    }

    // ==================== GETTERS ====================

    public HashMapInterface<String, Payment> getPaymentMap() {
        return paymentMap;
    }

    public HashMapInterface<String, Invoice> getInvoiceMap() {
        return invoiceMap;
    }

    public void deleteInvoiceByConsultation(String consultationId) {
        ListInterface<String> toRemove = new ArrayList<>();
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                toRemove.add(key);
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            invoiceMap.remove(toRemove.get(i));
        }
        InvoiceDAO.saveInvoices(invoiceMap);
    }

    public void displayPaymentStatistics() {
        generateFinancialSummaryReport();
    }

    public Invoice getInvoiceByConsultation(String consultationId) {
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                return invoice;
            }
        }
        return null;
    }

    public void saveData() {
        PaymentDAO.savePayments(paymentMap);
        InvoiceDAO.saveInvoices(invoiceMap);
    }
}
