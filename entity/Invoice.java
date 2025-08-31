package entity;

import java.time.LocalDateTime;

/**
 * Invoice entity for billing and payments
 * @author Your Name
 */
public class Invoice {
    private String invoiceId;
    private String consultationId;
    private double consultationFee;
    private double treatmentFee;
    private double medicineFee;
    private double totalAmount;
    private LocalDateTime createdTime;
    private boolean isPaid;

    // Constructor for backward compatibility
    public Invoice(String invoiceId, String consultationId, double amount) {
        this.invoiceId = invoiceId;
        this.consultationId = consultationId;
        this.consultationFee = 0.0; // Will be calculated separately
        this.treatmentFee = 0.0;   // Will be set from treatment
        this.medicineFee = amount; // Assume amount is medicine fee for backward compatibility
        this.totalAmount = amount;
        this.createdTime = LocalDateTime.now();
        this.isPaid = false;
    }

    // New constructor with breakdown
    public Invoice(String invoiceId, String consultationId, double consultationFee,
                  double treatmentFee, double medicineFee) {
        this.invoiceId = invoiceId;
        this.consultationId = consultationId;
        this.consultationFee = consultationFee;
        this.treatmentFee = treatmentFee;
        this.medicineFee = medicineFee;
        this.totalAmount = consultationFee + treatmentFee + medicineFee;
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
        return totalAmount; // Return total for backward compatibility
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public double getTreatmentFee() {
        return treatmentFee;
    }

    public double getMedicineFee() {
        return medicineFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
        this.totalAmount = this.consultationFee + this.treatmentFee + this.medicineFee;
    }

    public void setTreatmentFee(double treatmentFee) {
        this.treatmentFee = treatmentFee;
        this.totalAmount = this.consultationFee + this.treatmentFee + this.medicineFee;
    }

    public void setMedicineFee(double medicineFee) {
        this.medicineFee = medicineFee;
        this.totalAmount = this.consultationFee + this.treatmentFee + this.medicineFee;
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
