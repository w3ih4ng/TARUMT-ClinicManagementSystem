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

/**
 * Control class for payment processing and management
 * @author Your Name
 */
public class PaymentControl {
    private HashMapInterface<String, Payment> paymentMap;
    private HashMapInterface<String, Invoice> invoiceMap;
    private HashMapInterface<String, Consultation> consultationMap;
    private HashMapInterface<String, Patient> patientMap;
    private HashMapInterface<String, Doctor> doctorMap;

    public PaymentControl() {
        this.paymentMap = PaymentDAO.loadPayments();
        this.invoiceMap = InvoiceDAO.loadInvoices();
        this.consultationMap = ConsultationDAO.loadConsultations();
        this.patientMap = PatientDAO.loadPatients();
        this.doctorMap = DoctorDAO.loadDoctors();
    }

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

    /**
     * Get payment history for a consultation
     */
    public ListInterface<Payment> getConsultationPaymentHistory(String consultationId) {
        ListInterface<Payment> consultationPayments = new ArrayList<>();
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && payment.getConsultationId().equals(consultationId) && !payment.isDeleted()) {
                consultationPayments.add(payment);
            }
        }
        
        return consultationPayments;
    }

    /**
     * Get all payments with optional filtering
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

    /**
     * Get payments by status
     */
    public ListInterface<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        ListInterface<Payment> filteredPayments = new ArrayList<>();
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && payment.getStatus() == status && !payment.isDeleted()) {
                filteredPayments.add(payment);
            }
        }
        
        return filteredPayments;
    }

    /**
     * Get payments by date range
     */
    public ListInterface<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        ListInterface<Payment> filteredPayments = new ArrayList<>();
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && !payment.isDeleted()) {
                LocalDateTime paymentDate = payment.getPaymentDate();
                if (paymentDate.isAfter(startDate) && paymentDate.isBefore(endDate)) {
                    filteredPayments.add(payment);
                }
            }
        }
        
        return filteredPayments;
    }

    /**
     * Calculate total revenue for a date range
     */
    public double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        double totalRevenue = 0.0;
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && payment.isCompleted() && !payment.isDeleted()) {
                LocalDateTime paymentDate = payment.getPaymentDate();
                if (paymentDate.isAfter(startDate) && paymentDate.isBefore(endDate)) {
                    totalRevenue += payment.getAmount();
                }
            }
        }
        
        return totalRevenue;
    }

    /**
     * Get payment statistics
     */
    public void displayPaymentStatistics() {
        int totalPayments = 0;
        int completedPayments = 0;
        int pendingPayments = 0;
        int failedPayments = 0;
        double totalRevenue = 0.0;
        
        for (int i = 0; i < paymentMap.keySet().size(); i++) {
            String key = paymentMap.keySet().get(i);
            Payment payment = paymentMap.get(key);
            if (payment != null && !payment.isDeleted()) {
                totalPayments++;
                
                switch (payment.getStatus()) {
                    case COMPLETED:
                        completedPayments++;
                        totalRevenue += payment.getAmount();
                        break;
                    case PENDING:
                        pendingPayments++;
                        break;
                    case FAILED:
                        failedPayments++;
                        break;
                    default:
                        break;
                }
            }
        }
        
        System.out.println("\n=== Payment Statistics ===");
        System.out.println("Total Payments: " + totalPayments);
        System.out.println("Completed: " + completedPayments);
        System.out.println("Pending: " + pendingPayments);
        System.out.println("Failed: " + failedPayments);
        System.out.println("Total Revenue: RM " + String.format("%.2f", totalRevenue));
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
            // Create a new refund invoice or mark as refunded
            invoice.markPaid(); // This needs to be updated in Invoice class
        }

        paymentMap.put(paymentId, payment);
        PaymentDAO.savePayments(paymentMap);

        System.out.println("Payment refunded successfully: " + paymentId);
        return true;
    }

    // Helper methods
    private String getPatientIdFromConsultation(String consultationId) {
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

    public HashMapInterface<String, Payment> getPaymentMap() {
        return paymentMap;
    }

    public void savePayments() {
        PaymentDAO.savePayments(paymentMap);
    }
}
