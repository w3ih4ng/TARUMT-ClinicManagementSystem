package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Stock entity representing medicine inventory
 * @author Your Name
 */
public class Stock {
    
    /**
     * Enum for medicine suppliers
     */
    public enum Supplier {
        PHARMACORP("PharmaCorp"),
        MEDISUPPLY("MediSupply"),
        COUGHCURE_LTD("CoughCure Ltd"),
        HEALTHPLUS("HealthPlus"),
        MEDTECH("MedTech"),
        BIOSUPPLY("BioSupply");
        
        private final String displayName;
        
        Supplier(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    private String stockId;        // Unique batch ID (S1001, S1002...)
    private String medicineId;     // links to Medicine
    private String batchNumber;    // auto-generated batch number (B-YYYYMMDD format)
    private Supplier supplier;     // supplier enum
    private int quantity;          // batch quantity
    private LocalDate manufacturingDate; // when the medicine was manufactured
    private LocalDate expiryDate;  // batch expiry
    private LocalDate receivedDate; // when batch was received
    private double costPerUnit;    // cost price per unit
    private boolean isDeleted;

    public Stock(String stockId, String medicineId, String batchNumber, Supplier supplier, 
                 int quantity, LocalDate manufacturingDate, LocalDate expiryDate, 
                 LocalDate receivedDate, double costPerUnit) {
        this.stockId = stockId;
        this.medicineId = medicineId;
        this.batchNumber = batchNumber;
        this.supplier = supplier;
        this.quantity = quantity;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
        this.costPerUnit = costPerUnit;
        this.isDeleted = false;
    }
    
    // Helper method to generate batch number based on today's date
    public static String generateBatchNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "B-" + LocalDate.now().format(formatter);
    }

    // Getters
    public String getStockId() {
        return stockId;
    }

    public String getMedicineId() {
        return medicineId;
    }
    
    public String getBatchNumber() {
        return batchNumber;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }
    
    public double getCostPerUnit() {
        return costPerUnit;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    // Setters
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }
    
    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    // Soft delete / restore
    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return stockId + " " + medicineId + " " + batchNumber + " " + supplier + " " + 
               quantity + " " + manufacturingDate.format(fmt) + " " + expiryDate.format(fmt) + " " + 
               receivedDate.format(fmt) + " " + String.format("%.2f", costPerUnit) + " " + 
               (isDeleted ? "deleted" : "active");
    }
}
