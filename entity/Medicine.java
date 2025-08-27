package entity;

public class Medicine {
    public enum Unit {
        MG, ML, TABLET, CAPSULE
    }

    private String medicineId; // unique ID
    private String name; // medicine name
    private double dosage; // numeric dosage
    private Unit unit; // dosage unit
    private int quantity; // units in stock
    private double price; // per unit
    private boolean isDeleted;

    public Medicine(String medicineId, String name, double dosage, Unit unit, int quantity, double price) {
        this.medicineId = medicineId;
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
        this.isDeleted = false;
    }

    // Getters
    public String getMedicineId() {
        return medicineId;
    }

    public String getName() {
        return name;
    }

    public double getDosage() {
        return dosage;
    }

    public Unit getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    // Setters
    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDosage(double dosage) {
        this.dosage = dosage;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
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
        return medicineId + " " +
                name + " " +
                dosage + unit + " " +
                quantity + " " +
                price + " " +
                (isDeleted ? "deleted" : "active");
    }

}
