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
    private MedicineControl medicineControl; // Shared instance

    public PharmacyControl() {
        // Initialize shared medicine control instance
        this.medicineControl = new MedicineControl();
        
        // Use the same instance for both modules
        this.medicineManagementBoundary = new MedicineManagementBoundary(medicineControl);
        this.stockManagementBoundary = new StockManagementBoundary(new StockControl(medicineControl));
    }

    public void openMedicineModule() {
        medicineManagementBoundary.mainMenu();
    }

    public void openStockModule() {
        stockManagementBoundary.mainMenu();
    }
}
