package entity;

import java.time.LocalDateTime;

/**
 * Invoice entity for billing and payments
 * @author Your Name
 */
public class Invoice {
    private String invoiceId;
    private String consultationId;
    private double amount;
    private LocalDateTime createdTime;
    private boolean isPaid;

    public Invoice(String invoiceId, String consultationId, double amount) {
        this.invoiceId = invoiceId;
        this.consultationId = consultationId;
        this.amount = amount;
        this.createdTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void markPaid() {
        this.isPaid = true;
    }

    public void markUnpaid() {
        this.isPaid = false;
    }
}
