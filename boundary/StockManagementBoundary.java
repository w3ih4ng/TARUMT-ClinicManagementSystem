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
            System.out.println("\n=================================");
            System.out.println("   Stock Management Module ");
            System.out.println("=================================");
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
                    stockControl.addStockBatch(); 
                    break;
                case "2": 
                    viewAllStockBoundary.show(); 
                    break;
                case "3": 
                    stockControl.viewMedicineStockSummary(); 
                    break;
                case "4": 
                    stockControl.updateStockBatch(); 
                    break;
                case "5": 
                    stockControl.deleteStockBatch(); 
                    break;
                case "6": 
                    stockControl.restoreStockBatch(); 
                    break;
                case "0": 
                    return; // back
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}
