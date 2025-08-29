package boundary;

import java.util.Scanner;
import control.StockControl;
import control.ViewStockControl;

/**
 * Boundary class for stock management interface
 * @author Your Name
 */
public class StockManagementBoundary {
    private Scanner sc;
    private StockControl stockControl;
    private ViewAllStockBoundary viewAllStockBoundary;

    public StockManagementBoundary(StockControl stockControl) {
        this.sc = new Scanner(System.in);
        this.stockControl = stockControl;
        this.viewAllStockBoundary = new ViewAllStockBoundary(stockControl);
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Stock Management Module");
            
            System.out.println("1. Add new stock batch");
            System.out.println("2. View all stock batches");
            System.out.println("3. View medicine stock summary");
            System.out.println("4. Update stock batch");
            System.out.println("5. Delete stock batch");
            System.out.println("6. Restore stock batch");
            System.out.println("0. Back to Pharmacy Menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Add New Stock Batch");
                    stockControl.addStockBatch(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("View All Stock Batches");
                    viewAllStockBoundary.show(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Medicine Stock Summary");
                    stockControl.viewMedicineStockSummary(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Update Stock Batch");
                    stockControl.updateStockBatch(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Delete Stock Batch");
                    stockControl.deleteStockBatch(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "6": 
                    utility.SystemUtil.showSectionHeader("Restore Stock Batch");
                    stockControl.restoreStockBatch(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return; // back
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}
