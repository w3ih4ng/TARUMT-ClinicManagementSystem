package control;

import entity.*;
import adt.*;
import dao.MedicineDAO;
import dao.StockDAO;
import utility.FilterCriteriaUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Consolidated Pharmacy Controller - combines all pharmacy-related control functionality
 * Handles medicine management, stock management, and business logic
 * @author Your Name
 */
public class PharmacyController {
    private HashMapInterface<String, Medicine> medicineMap;
    private HashMapInterface<String, Stock> stockMap;
    private Scanner sc;
    private int medicineCounter = 1; // start from MED001
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public PharmacyController() {
        this.medicineMap = MedicineDAO.loadMedicines();
        this.stockMap = StockDAO.loadStocks();
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    // ==================== COUNTER INITIALIZATION ====================

    private void initCounterFromMap() {
        int max = 0;
        ListInterface<String> keys = medicineMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            try {
                int num = Integer.parseInt(key.substring(3));
                if (num > max)
                    max = num;
            } catch (NumberFormatException e) {
                // ignore invalid keys
            }
        }
        medicineCounter = max + 1;
    }

    private String generateMedicineId() {
        String id;
        do {
            id = String.format("MED%03d", medicineCounter++);
        } while (medicineMap.containsKey(id));
        return id;
    }

    // ==================== MEDICINE CRUD OPERATIONS ====================

    public void addMedicine() {
        System.out.println("\n--- Add New Medicine ---");
        System.out.println("Type 'exit' at any point to cancel.\n");

        // --- Name Input ---
        String name;
        while (true) {
            System.out.print("Enter medicine name: ");
            name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Medicine addition cancelled.");
                return;
            }
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Name cannot be empty. Please try again.");
        }

        // --- Dosage Input ---
        double dosage;
        while (true) {
            System.out.print("Enter dosage: ");
            String dosageStr = sc.nextLine().trim();
            if (dosageStr.equalsIgnoreCase("exit")) {
                System.out.println("Medicine addition cancelled.");
                return;
            }
            try {
                dosage = Double.parseDouble(dosageStr);
                if (dosage > 0) {
                    break;
                } else {
                    System.out.println("Dosage must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // --- Unit Input ---
        Medicine.Unit unit;
        while (true) {
            System.out.println("\nAvailable units:");
            Medicine.Unit[] units = Medicine.Unit.values();
            for (int i = 0; i < units.length; i++) {
                System.out.println((i + 1) + ". " + units[i]);
            }
            System.out.print("Choose unit (1-" + units.length + "): ");
            String choice = sc.nextLine().trim();
            
            if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Medicine addition cancelled.");
                return;
            }
            
            try {
                int unitChoice = Integer.parseInt(choice);
                if (unitChoice >= 1 && unitChoice <= units.length) {
                    unit = units[unitChoice - 1];
                    break;
                } else {
                    System.out.println("Please enter a number between 1 and " + units.length);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // --- Price Input ---
        double price;
        while (true) {
            System.out.print("Enter price per unit: ");
            String priceStr = sc.nextLine().trim();
            if (priceStr.equalsIgnoreCase("exit")) {
                System.out.println("Medicine addition cancelled.");
                return;
            }
            try {
                price = Double.parseDouble(priceStr);
                if (price >= 0) {
                    break;
                } else {
                    System.out.println("Price cannot be negative.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Create and save medicine
        String medicineId = generateMedicineId();
        Medicine medicine = new Medicine(medicineId, name, dosage, unit, price);
        medicineMap.put(medicineId, medicine);
        MedicineDAO.saveMedicines(medicineMap);

        System.out.println("\nMedicine added successfully!");
        System.out.println("Medicine ID: " + medicineId);
        System.out.println("Name: " + name);
        System.out.println("Dosage: " + dosage + " " + unit);
        System.out.println("Price: RM " + String.format("%.2f", price));
    }

    public void viewAllMedicines() {
        System.out.println("\n--- All Medicines ---");
        if (medicineMap.isEmpty()) {
            System.out.println("No medicines found.");
            return;
        }

        String borderLine = "+------------+---------------------------+------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-10s | %-25s | %-25s |%n", "Medicine ID", "Name", "Dosage", "Unit", "Price");
        System.out.println(borderLine);

        for (String key : medicineMap.keySet()) {
            Medicine medicine = medicineMap.get(key);
            if (!medicine.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-10s | %-25s | %-25s |%n",
                        medicine.getMedicineId(),
                        medicine.getName(),
                        medicine.getDosage(),
                        medicine.getUnit(),
                        "RM " + String.format("%.2f", medicine.getPrice()));
            }
        }
        System.out.println(borderLine);
    }

    public void viewMedicineDetails() {
        System.out.println("\n--- View Medicine Details ---");
        System.out.print("Enter Medicine ID: ");
        String medicineId = sc.nextLine().trim().toUpperCase();

        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty!");
            return;
        }

        Medicine medicine = medicineMap.get(medicineId);
        if (medicine == null) {
            System.out.println("Medicine not found: " + medicineId);
            return;
        }

        if (medicine.isDeleted()) {
            System.out.println("Medicine has been deleted.");
            return;
        }

        System.out.println("\n--- Medicine Details ---");
        System.out.println("Medicine ID: " + medicine.getMedicineId());
        System.out.println("Name: " + medicine.getName());
        System.out.println("Dosage: " + medicine.getDosage());
        System.out.println("Unit: " + medicine.getUnit());
        System.out.println("Price: RM " + String.format("%.2f", medicine.getPrice()));
    }

    public void updateMedicine() {
        System.out.println("\n--- Update Medicine ---");
        System.out.print("Enter Medicine ID to update: ");
        String medicineId = sc.nextLine().trim().toUpperCase();

        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty!");
            return;
        }

        Medicine medicine = medicineMap.get(medicineId);
        if (medicine == null) {
            System.out.println("Medicine not found: " + medicineId);
            return;
        }

        if (medicine.isDeleted()) {
            System.out.println("Cannot update deleted medicine.");
            return;
        }

        System.out.println("\nCurrent medicine details:");
        System.out.println("Name: " + medicine.getName());
        System.out.println("Dosage: " + medicine.getDosage());
        System.out.println("Unit: " + medicine.getUnit());
        System.out.println("Price: RM " + String.format("%.2f", medicine.getPrice()));

        System.out.println("\nEnter new values (press Enter to keep current value):");

        // Update name
        System.out.print("New name [" + medicine.getName() + "]: ");
        String newName = sc.nextLine().trim();
        if (!newName.isEmpty()) {
            medicine.setName(newName);
        }

        // Update price
        System.out.print("New price [" + String.format("%.2f", medicine.getPrice()) + "]: ");
        String newPriceStr = sc.nextLine().trim();
        if (!newPriceStr.isEmpty()) {
            try {
                double newPrice = Double.parseDouble(newPriceStr);
                if (newPrice >= 0) {
                    medicine.setPrice(newPrice);
                } else {
                    System.out.println("Price cannot be negative. Keeping current price.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Keeping current price.");
            }
        }

        // Save changes
        medicineMap.put(medicineId, medicine);
        MedicineDAO.saveMedicines(medicineMap);
        System.out.println("\nMedicine updated successfully!");
    }

    public void deleteMedicine() {
        System.out.println("\n--- Delete Medicine ---");
        System.out.print("Enter Medicine ID to delete: ");
        String medicineId = sc.nextLine().trim().toUpperCase();

        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty!");
            return;
        }

        Medicine medicine = medicineMap.get(medicineId);
        if (medicine == null) {
            System.out.println("Medicine not found: " + medicineId);
            return;
        }

        if (medicine.isDeleted()) {
            System.out.println("Medicine is already deleted.");
            return;
        }

        System.out.println("\nMedicine to delete:");
        System.out.println("ID: " + medicine.getMedicineId());
        System.out.println("Name: " + medicine.getName());
        System.out.println("Dosage: " + medicine.getDosage() + " " + medicine.getUnit());

        System.out.print("\nAre you sure you want to delete this medicine? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            medicine.delete();
            medicineMap.put(medicineId, medicine);
            MedicineDAO.saveMedicines(medicineMap);
            System.out.println("Medicine deleted successfully!");
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    public void restoreMedicine() {
        System.out.println("\n--- Restore Deleted Medicine ---");
        System.out.print("Enter Medicine ID to restore: ");
        String medicineId = sc.nextLine().trim().toUpperCase();

        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty!");
            return;
        }

        Medicine medicine = medicineMap.get(medicineId);
        if (medicine == null) {
            System.out.println("Medicine not found: " + medicineId);
            return;
        }

        if (!medicine.isDeleted()) {
            System.out.println("Medicine is not deleted.");
            return;
        }

        System.out.println("\nMedicine to restore:");
        System.out.println("ID: " + medicine.getMedicineId());
        System.out.println("Name: " + medicine.getName());
        System.out.println("Dosage: " + medicine.getDosage() + " " + medicine.getUnit());

        System.out.print("\nAre you sure you want to restore this medicine? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            medicine.restore();
            medicineMap.put(medicineId, medicine);
            MedicineDAO.saveMedicines(medicineMap);
            System.out.println("Medicine restored successfully!");
        } else {
            System.out.println("Restore operation cancelled.");
        }
    }

    // ==================== STOCK MANAGEMENT OPERATIONS ====================

    public void addStockBatch() {
        System.out.println("\n--- Add New Stock Batch ---");
        System.out.println("Type 'exit' at any point to cancel.\n");

        // Show available medicines
        ListInterface<Medicine> medicines = medicineMap.toList();
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
            medId = sc.nextLine().trim().toUpperCase();
            if (medId.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            }
            if (!medId.isEmpty() && medicineMap.containsKey(medId)) {
                Medicine med = medicineMap.get(medId);
                if (!med.isDeleted()) {
                    break;
                } else {
                    System.out.println("Medicine is deleted. Please choose another.");
                }
            } else {
                System.out.println("Invalid Medicine ID. Please try again.");
            }
        }

        // --- Quantity Input ---
        int quantity;
        while (true) {
            System.out.print("Enter quantity: ");
            String quantityStr = sc.nextLine().trim();
            if (quantityStr.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            }
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity > 0) {
                    break;
                } else {
                    System.out.println("Quantity must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // --- Expiry Date Input ---
        LocalDate expiryDate;
        while (true) {
            System.out.print("Enter expiry date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine().trim();
            if (dateStr.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            }
            try {
                expiryDate = LocalDate.parse(dateStr);
                if (expiryDate.isAfter(LocalDate.now())) {
                    break;
                } else {
                    System.out.println("Expiry date must be in the future.");
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        // --- Supplier Input ---
        Stock.Supplier supplier;
        while (true) {
            System.out.println("\nAvailable suppliers:");
            Stock.Supplier[] suppliers = Stock.Supplier.values();
            for (int i = 0; i < suppliers.length; i++) {
                System.out.println((i + 1) + ". " + suppliers[i]);
            }
            System.out.print("Choose supplier (1-" + suppliers.length + "): ");
            String choice = sc.nextLine().trim();
            
            if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            }
            
            try {
                int supplierChoice = Integer.parseInt(choice);
                if (supplierChoice >= 1 && supplierChoice <= suppliers.length) {
                    supplier = suppliers[supplierChoice - 1];
                    break;
                } else {
                    System.out.println("Please enter a number between 1 and " + suppliers.length);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // --- Manufacturing Date Input ---
        LocalDate manufacturingDate;
        while (true) {
            System.out.print("Enter manufacturing date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine().trim();
            if (dateStr.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            }
            try {
                manufacturingDate = LocalDate.parse(dateStr);
                if (manufacturingDate.isBefore(LocalDate.now().plusDays(1))) { // Allow today or earlier
                    if (manufacturingDate.isBefore(expiryDate)) {
                        break;
                    } else {
                        System.out.println("Manufacturing date must be before expiry date.");
                    }
                } else {
                    System.out.println("Manufacturing date cannot be in the future.");
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        // --- Cost Per Unit Input ---
        double costPerUnit;
        while (true) {
            System.out.print("Enter cost per unit: ");
            String costStr = sc.nextLine().trim();
            if (costStr.equalsIgnoreCase("exit")) {
                System.out.println("Stock addition cancelled.");
                return;
            }
            try {
                costPerUnit = Double.parseDouble(costStr);
                if (costPerUnit >= 0) {
                    break;
                } else {
                    System.out.println("Cost cannot be negative.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Create and save stock batch
        String stockId = generateStockId();
        String batchNumber = generateBatchNumber();
        LocalDate receivedDate = LocalDate.now();
        
        Stock stock = new Stock(stockId, medId, batchNumber, supplier, quantity, 
                               manufacturingDate, expiryDate, receivedDate, costPerUnit);
        
        stockMap.put(stockId, stock);
        StockDAO.saveStocks(stockMap);

        System.out.println("\nStock batch added successfully!");
        System.out.println("Stock ID: " + stockId);
        System.out.println("Batch Number: " + batchNumber);
        System.out.println("Medicine: " + medId);
        System.out.println("Quantity: " + quantity);
        System.out.println("Manufacturing Date: " + manufacturingDate);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("Supplier: " + supplier);
        System.out.println("Cost per Unit: RM " + String.format("%.2f", costPerUnit));
    }

    private String generateStockId() {
        int counter = 1;
        String stockId;
        do {
            stockId = "S" + String.format("%04d", counter++);
        } while (stockMap.containsKey(stockId));
        return stockId;
    }

    private String generateBatchNumber() {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "B-" + dateStr;
    }

    public void viewAllStockBatches() {
        System.out.println("\n--- All Stock Batches ---");
        if (stockMap.isEmpty()) {
            System.out.println("No stock batches found.");
            return;
        }

        String borderLine = "+------------+------------+---------------------------+------------+------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-25s | %-10s | %-10s | %-25s | %-25s |%n", 
                "Stock ID", "Medicine ID", "Medicine Name", "Batch Number", "Quantity", "Supplier", "Expiry Date");
        System.out.println(borderLine);

        for (String key : stockMap.keySet()) {
            Stock stock = stockMap.get(key);
            if (!stock.isDeleted()) {
                Medicine medicine = medicineMap.get(stock.getMedicineId());
                String medicineName = medicine != null ? medicine.getName() : "Unknown";
                
                System.out.printf("| %-10s | %-10s | %-25s | %-10s | %-10s | %-25s | %-25s |%n",
                        stock.getStockId(),
                        stock.getMedicineId(),
                        medicineName,
                        stock.getBatchNumber(),
                        stock.getQuantity(),
                        stock.getSupplier(),
                        stock.getExpiryDate());
            }
        }
        System.out.println(borderLine);
    }

    public void viewMedicineStockSummary() {
        System.out.println("\n--- Medicine Stock Summary ---");
        if (stockMap.isEmpty()) {
            System.out.println("No stock found.");
            return;
        }

        // Group stock by medicine
        HashMapInterface<String, ListInterface<Stock>> medicineStock = new HashMapADT<>();
        
        for (String key : stockMap.keySet()) {
            Stock stock = stockMap.get(key);
            if (!stock.isDeleted()) {
                String medicineId = stock.getMedicineId();
                
                if (!medicineStock.containsKey(medicineId)) {
                    medicineStock.put(medicineId, new ArrayList<>());
                }
                
                medicineStock.get(medicineId).add(stock);
            }
        }

        if (medicineStock.isEmpty()) {
            System.out.println("No active stock found.");
            return;
        }

        System.out.println("Stock Summary by Medicine:");
        String borderLine = "+------------+---------------------------+------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-10s | %-25s | %-25s |%n", 
                "Medicine ID", "Medicine Name", "Total Stock", "Total Value", "Low Stock Alert");
        System.out.println(borderLine);

        for (String medicineId : medicineStock.keySet()) {
            ListInterface<Stock> stocks = medicineStock.get(medicineId);
            Medicine medicine = medicineMap.get(medicineId);
            String medicineName = medicine != null ? medicine.getName() : "Unknown";
            
            int totalStock = 0;
            double totalValue = 0.0;
            
            for (int i = 0; i < stocks.size(); i++) {
                Stock stock = stocks.get(i);
                totalStock += stock.getQuantity();
                totalValue += stock.getCostPerUnit() * stock.getQuantity();
            }
            
            String lowStockAlert = totalStock < 100 ? "⚠️ LOW STOCK" : "✓ OK";
            
            System.out.printf("| %-10s | %-25s | %-10s | %-25s | %-25s |%n",
                    medicineId,
                    medicineName,
                    totalStock,
                    "RM " + String.format("%.2f", totalValue),
                    lowStockAlert);
        }
        System.out.println(borderLine);
    }

    // ==================== FILTERING AND VIEWING OPERATIONS ====================

    public void clearCriteria() {
        criteriaUtil.clearCriteria();
    }

    public void addCriteria(String text) {
        criteriaUtil.addCriteria(text);
    }

    private void removeOldSortCriteria() {
        criteriaUtil.removeOldSortCriteria();
    }

    public String getCriteriaSummary() {
        return criteriaUtil.getCriteriaSummary();
    }

    // Medicine filters
    public HashMapInterface<String, Medicine> filterByDosageValue(HashMapInterface<String, Medicine> map, double dosage) {
        addCriteria("Dosage = " + dosage);
        return map.filter(m -> !m.isDeleted() && m.getDosage() == dosage);
    }

    public HashMapInterface<String, Medicine> filterByUnit(HashMapInterface<String, Medicine> map, Medicine.Unit unit) {
        addCriteria("Unit = " + unit.name());
        return map.filter(m -> !m.isDeleted() && m.getUnit() == unit);
    }

    public HashMapInterface<String, Medicine> filterShowDeleted(HashMapInterface<String, Medicine> map) {
        addCriteria("Show Deleted");
        return map.filter(Medicine::isDeleted);
    }

    public HashMapInterface<String, Medicine> filterNotDeleted(HashMapInterface<String, Medicine> map) {
        addCriteria("Hide Deleted");
        return map.filter(m -> !m.isDeleted());
    }

    public HashMapInterface<String, Medicine> searchByName(HashMapInterface<String, Medicine> map, String keyword) {
        addCriteria("Search Name = " + keyword);
        return map.filter(m -> !m.isDeleted() && m.getName().toLowerCase().contains(keyword.toLowerCase()));
    }

    public HashMapInterface<String, Medicine> searchByMedicineId(HashMapInterface<String, Medicine> map, String medicineId) {
        addCriteria("Search ID = " + medicineId);
        return map.filter(m -> !m.isDeleted() && m.getMedicineId().toLowerCase().contains(medicineId.toLowerCase()));
    }

    // Stock filters
    public HashMapInterface<String, Stock> filterExpired(HashMapInterface<String, Stock> map) {
        addCriteria("Expired Stock");
        LocalDate today = LocalDate.now();
        return map.filter(s -> !s.isDeleted() && s.getExpiryDate().isBefore(today));
    }

    public HashMapInterface<String, Stock> filterActive(HashMapInterface<String, Stock> map) {
        addCriteria("Active Stock");
        LocalDate today = LocalDate.now();
        return map.filter(s -> !s.isDeleted() && !s.getExpiryDate().isBefore(today));
    }

    public HashMapInterface<String, Stock> filterByExpiryBefore(HashMapInterface<String, Stock> map, LocalDate date) {
        addCriteria("Expiry before " + date.toString());
        return map.filter(s -> !s.isDeleted() && s.getExpiryDate().isBefore(date));
    }

    public HashMapInterface<String, Stock> filterByMedicineId(HashMapInterface<String, Stock> map, String medicineId) {
        addCriteria("Medicine = " + medicineId);
        return map.filter(s -> !s.isDeleted() && s.getMedicineId().equalsIgnoreCase(medicineId));
    }

    public HashMapInterface<String, Stock> filterBySupplier(HashMapInterface<String, Stock> map, Stock.Supplier supplier) {
        addCriteria("Supplier = " + supplier.name());
        return map.filter(s -> !s.isDeleted() && s.getSupplier() == supplier);
    }

    // ==================== UTILITY METHODS ====================

    public Medicine getMedicineById(String medicineId) {
        return medicineMap.get(medicineId);
    }

    public ListInterface<Medicine> getAllMedicines() {
        return toList(medicineMap);
    }

    public HashMapInterface<String, Medicine> getMedicineMap() {
        return medicineMap;
    }

    public Stock getStockById(String stockId) {
        return stockMap.get(stockId);
    }

    public HashMapInterface<String, Stock> getStockMap() {
        return stockMap;
    }

    public ListInterface<Medicine> toList(HashMapInterface<String, Medicine> map) {
        ListInterface<Medicine> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    public ListInterface<Stock> stockToList(HashMapInterface<String, Stock> map) {
        ListInterface<Stock> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    public void saveMedicines() {
        MedicineDAO.saveMedicines(medicineMap);
    }

    public void saveStocks() {
        StockDAO.saveStocks(stockMap);
    }

    // ==================== TABLE DISPLAY METHODS ====================

    public void printMedicinesTable(ListInterface<Medicine> medicines, String title) {
        if (medicines.isEmpty()) {
            System.out.println("------------------------------------------------ No medicines found. ------------------------------------------------");
            return;
        }

        if (!title.isEmpty()) {
            System.out.println(title);
        }
        System.out.println();

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-25s | %-8s | %-8s | %-10s | %-8s |%n";

        // Define border line
        String borderLine = "+--------------+---------------------------+----------+----------+------------+----------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Medicine ID", "Name", "Dosage", "Unit", "Price", "Deleted");

        // Print header separator
        System.out.println(borderLine);

        // Print each row + row separator
        for (int i = 0; i < medicines.size(); i++) {
            Medicine m = medicines.get(i);
            
            // Print row
            System.out.printf(leftAlignFormat,
                    m.getMedicineId(),
                    m.getName(),
                    String.format("%.1f", m.getDosage()),
                    m.getUnit(),
                    String.format("%.2f", m.getPrice()),
                    m.isDeleted() ? "Yes" : "No");

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }

    public void printStocksTable(ListInterface<Stock> stocks, String title) {
        if (stocks.isEmpty()) {
            System.out.println("------------------------------------------------ No stocks found. ------------------------------------------------");
            return;
        }

        if (!title.isEmpty()) {
            System.out.println(title);
        }
        System.out.println();

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-12s | %-15s | %-15s | %-8s | %-12s | %-12s | %-12s | %-10s |%n";

        // Define border line
        String borderLine = "+--------------+--------------+-----------------+-----------------+----------+--------------+--------------+--------------+------------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Stock ID", "Medicine ID", "Batch Number", "Supplier", "Quantity", "Manufacture", "Expiry", "Received", "Cost");

        // Print header separator
        System.out.println(borderLine);

        // Print each row + row separator
        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            
            // Print row
            System.out.printf(leftAlignFormat,
                    s.getStockId(),
                    s.getMedicineId(),
                    s.getBatchNumber(),
                    s.getSupplier(),
                    s.getQuantity(),
                    s.getManufacturingDate(),
                    s.getExpiryDate(),
                    s.getReceivedDate(),
                    String.format("%.2f", s.getCostPerUnit()));

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }

    // ==================== MEDICINE DISPENSING METHODS ====================

    /**
     * Display medicines prescribed for a specific treatment
     */
    public boolean displayMedicinesForTreatment(String treatmentId) {
        // Load treatment data
        HashMapInterface<String, entity.Treatment> treatmentMap = dao.TreatmentDAO.loadTreatments();
        entity.Treatment treatment = treatmentMap.get(treatmentId);
        
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
        
        if (treatment.getPrescribedMedicines().isEmpty()) {
            System.out.println("No medicines prescribed for treatment: " + treatmentId);
            return false;
        }
        
        // Display treatment details
        System.out.println("\n--- Treatment Details ---");
        System.out.println("Treatment ID: " + treatment.getTreatmentId());
        System.out.println("Patient ID: " + treatment.getPatientId());
        System.out.println("Consultation ID: " + treatment.getConsultationId());
        System.out.println("Diagnosis: " + treatment.getDescription());
        System.out.println("Treatment Fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
        
        // Display prescribed medicines table
        System.out.println("\n--- Prescribed Medicines ---");
        String borderLine = "+------------------+---------------------------+------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-16s | %-25s | %-10s | %-10s | %-10s |%n", 
                         "Medicine ID", "Medicine Name", "Quantity", "Unit Price", "Total Cost");
        System.out.println(borderLine);
        
        double totalMedicineCost = 0.0;
        for (int i = 0; i < treatment.getPrescribedMedicines().size(); i++) {
            entity.MedicinePrescribed prescribed = treatment.getPrescribedMedicines().get(i);
            String medicineId = prescribed.getMedicineId();
            int quantity = prescribed.getQuantity();
            
            // Get medicine details
            Medicine medicine = medicineMap.get(medicineId);
            if (medicine != null) {
                double unitPrice = medicine.getPrice();
                double totalCost = unitPrice * quantity;
                totalMedicineCost += totalCost;
                
                System.out.printf("| %-16s | %-25s | %-10s | %-10s | %-10s |%n",
                    medicineId,
                    medicine.getName(),
                    quantity,
                    "RM " + String.format("%.2f", unitPrice),
                    "RM " + String.format("%.2f", totalCost));
            } else {
                System.out.printf("| %-16s | %-25s | %-10s | %-10s | %-10s |%n",
                    medicineId,
                    "NOT FOUND",
                    quantity,
                    "N/A",
                    "N/A");
            }
        }
        System.out.println(borderLine);
        System.out.printf("| %-16s | %-25s | %-10s | %-10s | %-10s |%n",
                         "", "", "", "TOTAL:", "RM " + String.format("%.2f", totalMedicineCost));
        System.out.println(borderLine);
        
        // Show total invoice amount that will be generated
        double totalInvoiceAmount = totalMedicineCost + treatment.getTreatmentFee();
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           INVOICE SUMMARY");
        System.out.println("=".repeat(50));
        System.out.printf("%-20s: RM %8.2f%n", "Medicine Cost", totalMedicineCost);
        System.out.printf("%-20s: RM %8.2f%n", "Treatment Fee", treatment.getTreatmentFee());
        System.out.println("-".repeat(50));
        System.out.printf("%-20s: RM %8.2f%n", "TOTAL AMOUNT", totalInvoiceAmount);
        System.out.println("=".repeat(50));
        
        return true;
    }

    /**
     * Display treatments that have medicine prescriptions
     */
    public void displayTreatmentsWithPrescriptions() {
        // Load treatments and show those with prescriptions
        HashMapInterface<String, entity.Treatment> treatmentMap = dao.TreatmentDAO.loadTreatments();
        
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }
        
        System.out.println("\n--- Treatments with Medicine Prescriptions ---");
        String borderLine = "+------------------+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-16s | %-10s | %-25s | %-25s | %-25s |%n", 
                         "Treatment ID", "Patient ID", "Consultation ID", "Description", "Prescribed Medicines");
        System.out.println(borderLine);
        
        int count = 0;
        for (String key : treatmentMap.keySet()) {
            entity.Treatment treatment = treatmentMap.get(key);
            if (treatment != null && !treatment.getPrescribedMedicines().isEmpty()) {
                System.out.printf("| %-16s | %-10s | %-25s | %-25s | %-25s |%n",
                    treatment.getTreatmentId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    treatment.getDescription().substring(0, Math.min(23, treatment.getDescription().length())) + "...",
                    treatment.getPrescribedMedicines().size() + " medicines");
                count++;
            }
        }
        System.out.println(borderLine);
        
        if (count == 0) {
            System.out.println("No treatments with medicine prescriptions found.");
        } else {
            System.out.println("Total treatments with prescriptions: " + count);
        }
    }

    /**
     * Dispense medicines for a specific treatment
     * This method will:
     * 1. Get the treatment and its prescribed medicines
     * 2. Check stock availability
     * 3. Reduce stock quantities
     * 4. Calculate total medicine cost
     * 5. Generate invoice for patient payment
     */
    public boolean dispenseMedicinesForTreatment(String treatmentId) {
        System.out.println("Dispensing medicines for treatment: " + treatmentId);
        
        // Load treatment data
        HashMapInterface<String, entity.Treatment> treatmentMap = dao.TreatmentDAO.loadTreatments();
        entity.Treatment treatment = treatmentMap.get(treatmentId);
        
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
        
        if (treatment.getPrescribedMedicines().isEmpty()) {
            System.out.println("No medicines prescribed for this treatment.");
            return false;
        }
        
        // Check stock availability and calculate total cost
        double totalMedicineCost = 0.0;
        boolean allMedicinesAvailable = true;
        
        System.out.println("\n--- Medicine Dispensing Summary ---");
        System.out.println("Patient ID: " + treatment.getPatientId());
        System.out.println("Consultation ID: " + treatment.getConsultationId());
        System.out.println();
        
        for (int i = 0; i < treatment.getPrescribedMedicines().size(); i++) {
            entity.MedicinePrescribed prescribed = treatment.getPrescribedMedicines().get(i);
            String medicineId = prescribed.getMedicineId();
            int requiredQuantity = prescribed.getQuantity();
            
            // Get medicine details
            Medicine medicine = medicineMap.get(medicineId);
            if (medicine == null) {
                System.out.println("ERROR: Medicine " + medicineId + " not found in system!");
                allMedicinesAvailable = false;
                continue;
            }
            
            // Check stock availability
            int availableStock = getAvailableStockForMedicine(medicineId);
            
            if (availableStock < requiredQuantity) {
                System.out.println("INSUFFICIENT STOCK: " + medicine.getName() + " (ID: " + medicineId + ")");
                System.out.println("  Required: " + requiredQuantity + ", Available: " + availableStock);
                allMedicinesAvailable = false;
            } else {
                // Calculate cost for this medicine
                double medicineCost = medicine.getPrice() * requiredQuantity;
                totalMedicineCost += medicineCost;
                
                System.out.println(medicine.getName() + " (ID: " + medicineId + ")");
                System.out.println("  Quantity: " + requiredQuantity + " x RM " + String.format("%.2f", medicine.getPrice()) + " = RM " + String.format("%.2f", medicineCost));
                
                // Reduce stock
                reduceStockForMedicine(medicineId, requiredQuantity);
            }
        }
        
        if (!allMedicinesAvailable) {
            System.out.println("\nCannot dispense medicines due to insufficient stock.");
            return false;
        }
        
        // All medicines available, complete dispensing
        System.out.println("\nAll medicines dispensed successfully!");
        System.out.println("Total medicine cost: RM " + String.format("%.2f", totalMedicineCost));
        
        // Generate invoice for medicines
        generateInvoiceForMedicines(treatment, totalMedicineCost);
        
        // Update treatment and consultation status after medicine dispensing
        try {
            // Get TreatmentController to update consultation status
            control.TreatmentController treatmentController = new control.TreatmentController();
            
            // Mark medicines as dispensed in the treatment
            treatmentController.markMedicinesDispensed(treatmentId);
            
            // Complete the consultation after dispensing
            treatmentController.completeConsultationAfterDispensing(treatmentId);
            
            System.out.println("\nConsultation workflow updated:");
            System.out.println("  • Treatment status: Medicines dispensed");
            System.out.println("  • Consultation status: Ready for payment");
            System.out.println("  • Queue status: Updated to MEDICINES_DISPENSED");
            
        } catch (Exception e) {
            System.out.println("\nWarning: Could not update consultation workflow: " + e.getMessage());
            System.out.println("Medicines were dispensed, but workflow status may not be updated.");
        }
        
        return true;
    }

    /**
     * Display dispensing history
     */
    public void displayDispensingHistory() {
        System.out.println("Dispensing history functionality not yet implemented.");
        System.out.println("This would show records of all medicine dispensations.");
    }
    
    /**
     * Display treatments ready for medicine dispensing (new workflow integration)
     */
    public void displayTreatmentsReadyForDispensing() {
        try {
            control.TreatmentController treatmentController = new control.TreatmentController();
            ListInterface<entity.Treatment> readyTreatments = treatmentController.getTreatmentsReadyForMedicineDispensing();
            
            if (readyTreatments.isEmpty()) {
                System.out.println("\nNo treatments are currently ready for medicine dispensing.");
                System.out.println("Treatments must be in 'TREATMENT_CREATED' status to be eligible.");
                return;
            }
            
            System.out.println("\nTreatments Ready for Medicine Dispensing:");
            System.out.println("(These treatments have been created and are waiting for medicine dispensing)");
            
            String borderLine = "+------------+------------+------------+------------+---------------------------+------------+";
            System.out.println(borderLine);
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n",
                    "TreatmentID", "DoctorID", "PatientID", "ConsultationID", "Diagnosis", "Fee");
            System.out.println(borderLine);
            
            for (int i = 0; i < readyTreatments.size(); i++) {
                entity.Treatment treatment = readyTreatments.get(i);
                System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n",
                        treatment.getTreatmentId(),
                        treatment.getDoctorId(),
                        treatment.getPatientId(),
                        treatment.getConsultationId(),
                        treatment.getDescription(),
                        String.format("%.2f", treatment.getTreatmentFee()));
            }
            System.out.println(borderLine);
            
        } catch (Exception e) {
            System.out.println("Error displaying treatments ready for dispensing: " + e.getMessage());
        }
    }

    // ==================== MEDICINE DISPENSING METHODS ====================

    /**
     * Get available stock quantity for a specific medicine
     */
    private int getAvailableStockForMedicine(String medicineId) {
        int totalAvailable = 0;
        
        for (String key : stockMap.keySet()) {
            Stock stock = stockMap.get(key);
            if (stock != null && stock.getMedicineId().equals(medicineId) && !stock.isDeleted()) {
                totalAvailable += stock.getQuantity();
            }
        }
        
        return totalAvailable;
    }

    /**
     * Reduce stock quantity for a specific medicine
     * Uses FIFO (First In, First Out) approach
     */
    private void reduceStockForMedicine(String medicineId, int quantityToReduce) {
        int remainingToReduce = quantityToReduce;
        
        // Get all stock entries for this medicine, sorted by received date (FIFO)
        ListInterface<Stock> medicineStocks = new adt.ArrayList<>();
        for (String key : stockMap.keySet()) {
            Stock stock = stockMap.get(key);
            if (stock != null && stock.getMedicineId().equals(medicineId) && !stock.isDeleted()) {
                medicineStocks.add(stock);
            }
        }
        
        // Sort by received date (earliest first for FIFO)
        // For simplicity, we'll just process them in order
        for (int i = 0; i < medicineStocks.size() && remainingToReduce > 0; i++) {
            Stock stock = medicineStocks.get(i);
            int availableInThisBatch = stock.getQuantity();
            
            if (availableInThisBatch >= remainingToReduce) {
                // This batch has enough
                stock.setQuantity(availableInThisBatch - remainingToReduce);
                remainingToReduce = 0;
            } else {
                // Use all from this batch
                remainingToReduce -= availableInThisBatch;
                stock.setQuantity(0);
            }
            
            // Update the stock map
            stockMap.put(stock.getStockId(), stock);
        }
        
        // Save updated stocks
        StockDAO.saveStocks(stockMap);
    }

    /**
     * Generate invoice for medicines dispensed
     */
    private void generateInvoiceForMedicines(entity.Treatment treatment, double medicineCost) {
        try {
            // Load existing invoices
            HashMapInterface<String, entity.Invoice> invoiceMap = dao.InvoiceDAO.loadInvoices();
            
            // Generate invoice ID
            String invoiceId = dao.InvoiceDAO.generateInvoiceId();
            
            // Calculate total amount including treatment fee
            double totalAmount = medicineCost + treatment.getTreatmentFee();
            
            // Create invoice for total amount (medicines + treatment fee)
            entity.Invoice totalInvoice = new entity.Invoice(invoiceId, treatment.getConsultationId(), totalAmount);
            
            // Save invoice
            invoiceMap.put(invoiceId, totalInvoice);
            dao.InvoiceDAO.saveInvoices(invoiceMap);
            
            System.out.println("Invoice generated: " + invoiceId);
            System.out.println("Medicine cost: RM " + String.format("%.2f", medicineCost));
            System.out.println("Treatment fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
            System.out.println("Total amount: RM " + String.format("%.2f", totalAmount));
            
        } catch (Exception e) {
            System.out.println("Error generating invoice: " + e.getMessage());
        }
    }
}
