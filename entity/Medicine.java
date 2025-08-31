package entity;

/**
 * Medicine entity representing pharmaceutical products
 * @author Your Name
 */
public class Medicine {
    public enum Unit {
        MG, ML, TABLET, CAPSULE, UNIT
    }

    private String medicineId;
    private String name;
    private double dosage;
    private Unit unit;
    private double price;
    private boolean isDeleted;

    public Medicine(String medicineId, String name, double dosage, Unit unit, double price) {
        this.medicineId = medicineId;
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
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

    public double getPrice() {
        return price;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDosage(double dosage) {
        this.dosage = dosage;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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
                price + " " +
                (isDeleted ? "deleted" : "active");
    }
}
