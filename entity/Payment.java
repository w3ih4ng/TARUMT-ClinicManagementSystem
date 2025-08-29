package entity;

import java.time.LocalDateTime;

/**
 * Payment entity for tracking payment transactions
 * @author Your Name
 */
public class Payment {
    public enum PaymentMethod {
        CASH("Cash"),
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        BANK_TRANSFER("Bank Transfer"),
        INSURANCE("Insurance"),
        ONLINE_PAYMENT("Online Payment");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum PaymentStatus {
        PENDING("Pending"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        CANCELLED("Cancelled"),
        REFUNDED("Refunded");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private String paymentId;
    private String invoiceId;
    private String consultationId;
    private String patientId;
    private double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String referenceNumber;
    private String notes;
    private boolean isDeleted;

    public Payment(String paymentId, String invoiceId, String consultationId, String patientId, 
                   double amount, PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
        this.referenceNumber = "";
        this.notes = "";
        this.isDeleted = false;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getInvoiceId() { return invoiceId; }
    public String getConsultationId() { return consultationId; }
    public String getPatientId() { return patientId; }
    public double getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getNotes() { return notes; }
    public boolean isDeleted() { return isDeleted; }

    // Setters
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public void setConsultationId(String consultationId) { this.consultationId = consultationId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setNotes(String notes) { this.notes = notes; }

    // Business methods
    public void markCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    @Override
    public String toString() {
        return String.format("Payment[ID=%s, Invoice=%s, Amount=%.2f, Method=%s, Status=%s]",
                paymentId, invoiceId, amount, paymentMethod, status);
    }
}
