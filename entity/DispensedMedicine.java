package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entity class for tracking dispensed medicines
 * @author Your Name
 */
public class DispensedMedicine {
    private String dispenseId;
    private String treatmentId;
    private String consultationId;
    private String patientId;
    private String medicineId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private LocalDateTime dispenseDate;
    private String stockId; // Stock ID used for dispensing
    private boolean isDeleted;

    // Constructor
    public DispensedMedicine(String dispenseId, String treatmentId, String consultationId, 
                           String patientId, String medicineId,
                           int quantity, double unitPrice, String stockId) {
        this.dispenseId = dispenseId;
        this.treatmentId = treatmentId;
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.dispenseDate = LocalDateTime.now();
        this.stockId = stockId;
        this.isDeleted = false;
    }

    // Constructor for loading from file
    public DispensedMedicine(String dispenseId, String treatmentId, String consultationId,
                           String patientId, String medicineId,
                           int quantity, double unitPrice, double totalPrice,
                           LocalDateTime dispenseDate, String stockId, boolean isDeleted) {
        this.dispenseId = dispenseId;
        this.treatmentId = treatmentId;
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.dispenseDate = dispenseDate;
        this.stockId = stockId;
        this.isDeleted = isDeleted;
    }

    // Getters
    public String getDispenseId() { return dispenseId; }
    public String getTreatmentId() { return treatmentId; }
    public String getConsultationId() { return consultationId; }
    public String getPatientId() { return patientId; }
    public String getMedicineId() { return medicineId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getDispenseDate() { return dispenseDate; }
    public String getStockId() { return stockId; }
    public boolean isDeleted() { return isDeleted; }

    // Setters
    public void setDispenseId(String dispenseId) { this.dispenseId = dispenseId; }
    public void setTreatmentId(String treatmentId) { this.treatmentId = treatmentId; }
    public void setConsultationId(String consultationId) { this.consultationId = consultationId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
        this.totalPrice = quantity * unitPrice;
    }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice; 
        this.totalPrice = quantity * unitPrice;
    }
    public void setDispenseDate(LocalDateTime dispenseDate) { this.dispenseDate = dispenseDate; }
    public void setStockId(String stockId) { this.stockId = stockId; }
    public void setIsDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    // Business methods
    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    // Utility methods
    public String getDispenseDateString() {
        return dispenseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Dispense[%s] %s x%d @RM%.2f = RM%.2f", 
            dispenseId, medicineId, quantity, unitPrice, totalPrice);
    }
}
