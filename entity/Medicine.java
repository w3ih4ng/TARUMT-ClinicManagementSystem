package entity;

public class Medicine {
    private String medicineId;    // unique ID
    private String name;          // medicine name
    private String dosage;        // e.g., "500mg"
    private int quantity;         // units in stock
    private double price;         // per unit

    public Medicine(String medicineId, String name, String dosage, int quantity, double price) {
        this.medicineId = medicineId;
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // Setters
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public void setName(String name) { this.name = name; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | Qty: %d | $%.2f",
                medicineId, name, dosage, quantity, price);
    }
}
