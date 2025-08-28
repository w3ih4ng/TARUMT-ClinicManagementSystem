package control;

import entity.*;
import adt.*;
import dao.StockDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Control class for stock management
 * @author Your Name
 */
public class StockControl {
    private HashMapInterface<String, Stock> stockMap; // key = stockId
    private Scanner sc;
    private MedicineControl medicineControl;

    public StockControl(MedicineControl medicineControl) {
        this.medicineControl = medicineControl;
        this.stockMap = StockDAO.loadStocks();
        this.sc = new Scanner(System.in);
    }

    public HashMapInterface<String, Stock> getStockMap() {
        return stockMap;
    }

    // --- Add new stock batch ---
    public void addStockBatch() {
        System.out.println("\n--- Add New Stock Batch ---");
        System.out.println("Type 'exit' at any point to cancel.\n");

        // Show available medicines
        ListInterface<Medicine> medicines = medicineControl.getMedicineMap().toList();
        medicines.sort((m1, m2) -> m1.getMedicineId().compareTo(m2.getMedicineId()));

        System.out.println("\nAvailable Medicines:");
        System.out.println("+------------+---------------------------+");
        System.out.printf("| %-10s | %-25s |%n", "Med ID", "Name");
        System.out.println("+------------+---------------------------+");
        for (int i = 0; i < medicines.size(); i++) {
            Medicine m = medicines.get(i);
            if (!m.isDeleted()) {
                System.out.printf("| %-10s | %-25s |%n", m.getMedicineId(), m.getName());
            }
        }
        System.out.println("+------------+---------------------------+");

        // --- Select Medicine ID ---
        String medId;
        while (true) {
            System.out.print("Enter Medicine ID to add stock: ");
            medId = sc.nextLine().trim();

            if (medId.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            } else if (medicineControl.getMedicineMap().containsKey(medId)
                    && !medicineControl.getMedicineMap().get(medId).isDeleted()) {
                break;
            }

            System.out.println("Invalid Medicine ID. Please choose from the list above.");
        }

        // --- Quantity input ---
        int quantity = getQuantityInput();
        if (quantity == -1) return;

        // --- Expiry date input ---
        LocalDate expiryDate = getExpiryDateInput();
        if (expiryDate == null) return;

        // --- Create new stock batch ---
        String stockId = StockDAO.generateStockId();
        LocalDate receivedDate = LocalDate.now();
        
        Stock s = new Stock(stockId, medId, quantity, expiryDate, receivedDate);
        stockMap.put(stockId, s);

        StockDAO.saveStocks(stockMap);
        System.out.println("\nStock batch added successfully!");
        System.out.println("Stock ID: " + stockId);
        System.out.println("Medicine: " + medId);
        System.out.println("Quantity: " + quantity);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("Received Date: " + receivedDate);
    }

    // --- View all stock batches ---
    public void viewAllStockBatches() {
        if (stockMap.isEmpty()) {
            System.out.println("\nNo stock batches found.");
            return;
        }

        System.out.println("\n--- All Stock Batches ---");
        
        // Beautiful table header
        String borderLine = "+--------+------------+---------------------------+----------+-------------+----------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-8s | %-11s | %-14s |%n",
                "StockID", "MedicineID", "Medicine Name", "Quantity", "Expiry Date", "Received Date");
        System.out.println(borderLine);

        // Get all stocks and sort by stock ID
        ListInterface<Stock> stocks = stockMap.toList();
        stocks.sort((s1, s2) -> s1.getStockId().compareTo(s2.getStockId()));

        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            Medicine m = medicineControl.getMedicineMap().get(s.getMedicineId());
            String medicineName = (m != null) ? m.getName() : "Unknown";
            
            System.out.printf("| %-6s | %-10s | %-25s | %8d | %-11s | %-14s |%n",
                    s.getStockId(),
                    s.getMedicineId(),
                    medicineName,
                    s.getQuantity(),
                    s.getExpiryDate(),
                    s.getReceivedDate());
            System.out.println(borderLine);
        }
    }

    // --- View medicine stock summary ---
    public void viewMedicineStockSummary() {
        System.out.println("\n--- Medicine Stock Summary ---");
        
        // Group stocks by medicine
        HashMapInterface<String, Integer> totalStock = new HashMapADT<>();
        HashMapInterface<String, Integer> activeStock = new HashMapADT<>();
        
        ListInterface<Stock> stocks = stockMap.toList();
        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            if (!s.isDeleted()) {
                String medId = s.getMedicineId();
                int currentQty = totalStock.containsKey(medId) ? totalStock.get(medId) : 0;
                totalStock.put(medId, currentQty + s.getQuantity());
                
                // Check if not expired
                if (s.getExpiryDate().isAfter(LocalDate.now())) {
                    int activeQty = activeStock.containsKey(medId) ? activeStock.get(medId) : 0;
                    activeStock.put(medId, activeQty + s.getQuantity());
                }
            }
        }

        // Display summary table
        String borderLine = "+------------+---------------------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-10s | %-10s |%n",
                "MedicineID", "Medicine Name", "Total Stock", "Active Stock");
        System.out.println(borderLine);

        ListInterface<String> medIds = totalStock.keySet();
        for (int i = 0; i < medIds.size(); i++) {
            String medId = medIds.get(i);
            Medicine m = medicineControl.getMedicineMap().get(medId);
            String medicineName = (m != null) ? m.getName() : "Unknown";
            int total = totalStock.get(medId);
            int active = activeStock.containsKey(medId) ? activeStock.get(medId) : 0;
            
            System.out.printf("| %-10s | %-25s | %10d | %10d |%n",
                    medId, medicineName, total, active);
            System.out.println(borderLine);
        }
    }

    // --- Update stock batch ---
    public void updateStockBatch() {
        System.out.print("\nEnter Stock ID to update: ");
        String stockId = sc.nextLine().trim();

        if (!stockMap.containsKey(stockId)) {
            System.out.println("Stock batch not found.");
            return;
        }

        Stock s = stockMap.get(stockId);
        stockDetailsTable(s);

        System.out.print("New quantity (leave blank to keep): ");
        String qtyStr = sc.nextLine().trim();
        if (!qtyStr.isEmpty()) {
            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty >= 0)
                    s.setQuantity(qty);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Not updated.");
            }
        }

        System.out.print("New expiry date (yyyy-MM-dd, leave blank to keep): ");
        String dateStr = sc.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateStr);
                s.setExpiryDate(date);
            } catch (Exception e) {
                System.out.println("Invalid date. Not updated.");
            }
        }

        StockDAO.saveStocks(stockMap);
        System.out.println("Stock batch updated successfully.");
    }

    // --- Delete stock batch ---
    public void deleteStockBatch() {
        System.out.print("\nEnter Stock ID to delete: ");
        String stockId = sc.nextLine().trim();

        if (!stockMap.containsKey(stockId)) {
            System.out.println("Stock batch not found.");
            return;
        }

        Stock s = stockMap.get(stockId);
        stockDetailsTable(s);

        System.out.print("Are you sure you want to delete this stock batch? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            s.delete();
            StockDAO.saveStocks(stockMap);
            System.out.println("Stock batch soft-deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // --- Restore stock batch ---
    public void restoreStockBatch() {
        System.out.print("\nEnter Stock ID to restore: ");
        String stockId = sc.nextLine().trim();

        if (!stockMap.containsKey(stockId)) {
            System.out.println("Stock batch not found.");
            return;
        }

        Stock s = stockMap.get(stockId);
        if (!s.isDeleted()) {
            System.out.println("Stock batch is already active.");
            return;
        }

        s.restore();
        StockDAO.saveStocks(stockMap);
        System.out.println("Stock batch restored successfully.");
    }

    // --- Display stock in table (for ViewStockControl compatibility) ---
    public void printStockTable(ListInterface<Stock> stocks, String criteriaSummary) {
        if (stocks.isEmpty()) {
            System.out.println("--------------------------------------------- No stock batches found. ---------------------------------------------");
            return;
        }

        System.out.println(criteriaSummary.isEmpty()
                ? "--------------------------------------------- No active filter ---------------------------------------------"
                : criteriaSummary);
        System.out.println();

        // Beautiful table with new format
        String borderLine = "+--------+------------+---------------------------+----------+-------------+----------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-8s | %-11s | %-14s |%n",
                "StockID", "MedicineID", "Medicine Name", "Quantity", "Expiry Date", "Received Date");
        System.out.println(borderLine);

        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            Medicine m = medicineControl.getMedicineMap().get(s.getMedicineId());
            String medicineName = (m != null) ? m.getName() : "Unknown";
            
            System.out.printf("| %-6s | %-10s | %-25s | %8d | %-11s | %-14s |%n",
                    s.getStockId(),
                    s.getMedicineId(),
                    medicineName,
                    s.getQuantity(),
                    s.getExpiryDate(),
                    s.getReceivedDate());
            System.out.println(borderLine);
        }
    }

    // --- Helper for displaying a single stock batch ---
    private void stockDetailsTable(Stock s) {
        Medicine m = medicineControl.getMedicineMap().get(s.getMedicineId());
        String medicineName = (m != null) ? m.getName() : "Unknown";
        
        String borderLine = "+--------+------------+---------------------------+----------+-------------+----------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-8s | %-11s | %-14s |%n",
                "StockID", "MedicineID", "Medicine Name", "Quantity", "Expiry Date", "Received Date");
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %8d | %-11s | %-14s |%n",
                s.getStockId(),
                s.getMedicineId(),
                medicineName,
                s.getQuantity(),
                s.getExpiryDate(),
                s.getReceivedDate());
        System.out.println(borderLine);
    }

    // --- Input helpers ---
    private int getQuantityInput() {
        while (true) {
            System.out.print("Enter quantity: ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return -1;
            }
            try {
                int qty = Integer.parseInt(input);
                if (qty >= 0) return qty;
                System.out.println("Quantity must be non-negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }
    
    private LocalDate getExpiryDateInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Enter expiry date (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return null;
            }
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                if (date.isAfter(LocalDate.now())) return date;
                System.out.println("Expiry date must be in the future.");
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }
    }
}
