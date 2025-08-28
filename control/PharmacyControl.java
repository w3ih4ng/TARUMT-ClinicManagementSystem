package control;

import boundary.MedicineManagementBoundary;
import boundary.StockManagementBoundary;

/**
 * Control class for pharmacy operations and medicine dispensing
 * @author Your Name
 */
public class PharmacyControl {
    private MedicineManagementBoundary medicineManagementBoundary;
    private StockManagementBoundary stockManagementBoundary;

    public PharmacyControl() {
        // Initialize medicine management with medicine + view logic
        this.medicineManagementBoundary = new MedicineManagementBoundary(new MedicineControl());
        this.stockManagementBoundary = new StockManagementBoundary(new StockControl(new MedicineControl()));
    }

    public void openMedicineModule() {
        medicineManagementBoundary.mainMenu();
    }

    public void openStockModule() {
        stockManagementBoundary.mainMenu();
    }
}
