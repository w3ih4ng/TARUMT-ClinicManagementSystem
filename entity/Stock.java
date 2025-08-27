package entity;

import java.time.LocalDate;

public class Stock {
    private String stockId;        // Unique batch ID (S1001, S1002...)
    private String medicineId;     // links to Medicine
    private int quantity;          // batch quantity
    private LocalDate expiryDate;  // batch expiry
    private LocalDate receivedDate; // when batch was received
    private boolean isDeleted;

    public Stock(String stockId, String medicineId, int quantity, LocalDate expiryDate, LocalDate receivedDate) {
        this.stockId = stockId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
        this.isDeleted = false;
    }

    // Getters
    public String getStockId() {
        return stockId;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
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
        return stockId + " " + medicineId + " " + quantity + " " + expiryDate + " " + receivedDate + " " + (isDeleted ? "deleted" : "active");
    }
}
