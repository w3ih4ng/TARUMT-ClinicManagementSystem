package control;

import boundary.MedicineManagementBoundary;

public class PharmacyControl {
    private MedicineManagementBoundary medicineManagementBoundary;

    public PharmacyControl() {
        // Initialize medicine management with medicine + view logic
        this.medicineManagementBoundary = new MedicineManagementBoundary(new MedicineControl());
    }

    public void openMedicineModule() {
        medicineManagementBoundary.mainMenu();
    }
}
