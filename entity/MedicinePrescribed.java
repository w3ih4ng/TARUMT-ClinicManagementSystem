package entity;

public class MedicinePrescribed {
    private String medicineId;
    private int quantity;

    public MedicinePrescribed(String medicineId, int quantity) {
        this.medicineId = medicineId;
        this.quantity = quantity;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public int getQuantity() {
        return quantity;
    }
}
