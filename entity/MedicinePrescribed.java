package entity;

/**
 * Medicine prescribed entity linking medicines to treatments
 * @author Your Name
 */
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
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calculate the total cost for this prescribed medicine
     * @param medicine The medicine entity to get price from
     * @return Total cost (price * quantity)
     */
    public double calculateCost(Medicine medicine) {
        if (medicine != null) {
            return medicine.getPrice() * quantity;
        }
        return 0.0;
    }
}
