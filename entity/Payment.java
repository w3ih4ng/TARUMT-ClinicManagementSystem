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
        PAID("Paid"),
        NOT_PAID("Not Paid");

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
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String remarks;
    private boolean isDeleted;

    public Payment(String paymentId, String invoiceId, String consultationId, String patientId,
                   PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.NOT_PAID;
        this.paymentDate = null; // Will be set when payment is made
        this.remarks = "";
        this.isDeleted = false;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getInvoiceId() { return invoiceId; }
    public String getConsultationId() { return consultationId; }
    public String getPatientId() { return patientId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getRemarks() { return remarks; }
    public boolean isDeleted() { return isDeleted; }

    // Setters
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public void setConsultationId(String consultationId) { this.consultationId = consultationId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    // Business methods
    public void markPaid() {
        this.status = PaymentStatus.PAID;
        this.paymentDate = LocalDateTime.now();
    }

    public void markNotPaid() {
        this.status = PaymentStatus.NOT_PAID;
        this.paymentDate = null;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isPaid() {
        return this.status == PaymentStatus.PAID;
    }

    public boolean isNotPaid() {
        return this.status == PaymentStatus.NOT_PAID;
    }

    @Override
    public String toString() {
        return String.format("Payment[ID=%s, Invoice=%s, Method=%s, Status=%s, Date=%s]",
                paymentId, invoiceId, paymentMethod, status,
                paymentDate != null ? paymentDate.toString() : "Not Set");
    }
}
