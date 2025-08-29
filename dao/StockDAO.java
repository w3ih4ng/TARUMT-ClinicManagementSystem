package dao;

import entity.Stock;
import entity.Medicine;
import adt.HashMapADT;
import adt.HashMapInterface;
import adt.ListInterface;
import java.time.*;

import java.io.*;

/**
 * Data Access Object for Stock persistence
 * @author Your Name
 */
public class StockDAO {
    private static final String FILE_NAME = "data/stocks.txt";
    private static int stockCounter = 1001; // Start from S1001

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring stocks file: " + e.getMessage());
        }
    }

    public static void saveStocks(HashMapInterface<String, Stock> stockMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < stockMap.keySet().size(); i++) {
                String key = stockMap.keySet().get(i);
                Stock s = stockMap.get(key);
                if (s != null)
                    pw.println(toFileString(s));
            }
        } catch (IOException e) {
            System.out.println("Error saving stocks: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, Stock> loadStocks() {
        ensureFile();
        HashMapInterface<String, Stock> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Stock s = fromFileString(line);
                if (s != null)
                    map.put(s.getStockId(), s);
            }
        } catch (IOException e) {
            System.out.println("Error loading stocks: " + e.getMessage());
        }

        // Cross-validate with medicines: skip stocks for missing/deleted medicines
        HashMapInterface<String, Medicine> medicines = MedicineDAO.loadMedicines();
        ListInterface<String> keys = map.keySet();
        HashMapInterface<String, Stock> validated = new HashMapADT<>();
        for (int i = 0; i < keys.size(); i++) {
            String stockId = keys.get(i);
            Stock s = map.get(stockId);
            Medicine med = medicines.get(s.getMedicineId());
            if (med != null && !med.isDeleted()) {
                validated.put(stockId, s);
            }
        }
        return validated;
    }

    private static String toFileString(Stock s) {
        return s.getStockId() + "|" + s.getMedicineId() + "|" + s.getBatchNumber() + "|" + 
               s.getSupplier().name() + "|" + s.getQuantity() + "|" + s.getManufacturingDate() + "|" + 
               s.getExpiryDate() + "|" + s.getReceivedDate() + "|" + s.getCostPerUnit() + "|" + s.isDeleted();
    }

    private static Stock fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            
            // Handle old format (backward compatibility)
            if (parts.length == 4) {
                // Old format: medicineId|quantity|expiryDate|isDeleted
                String medId = parts[0];
                int qty = Integer.parseInt(parts[1]);
                LocalDate expiry = LocalDate.parse(parts[2]);
                boolean deleted = Boolean.parseBoolean(parts[3]);
                
                // Generate defaults for missing fields
                String stockId = "S" + (stockCounter++);
                String batchNumber = Stock.generateBatchNumber();
                Stock.Supplier supplier = Stock.Supplier.PHARMACORP; // default
                LocalDate mfgDate = expiry.minusYears(2); // 2 years before expiry
                LocalDate receivedDate = generateRandomPastDate();
                double costPerUnit = 1.0; // default cost
                
                Stock s = new Stock(stockId, medId, batchNumber, supplier, qty, mfgDate, expiry, receivedDate, costPerUnit);
                if (deleted) s.delete();
                return s;
            } else if (parts.length == 6) {
                // Medium format: stockId|medicineId|quantity|expiryDate|receivedDate|isDeleted
                String stockId = parts[0];
                String medId = parts[1];
                int qty = Integer.parseInt(parts[2]);
                LocalDate expiry = LocalDate.parse(parts[3]);
                LocalDate received = LocalDate.parse(parts[4]);
                boolean deleted = Boolean.parseBoolean(parts[5]);
                
                // Generate defaults for missing fields
                String batchNumber = Stock.generateBatchNumber();
                Stock.Supplier supplier = Stock.Supplier.PHARMACORP; // default
                LocalDate mfgDate = expiry.minusYears(2); // 2 years before expiry
                double costPerUnit = 1.0; // default cost
                
                // Update counter if needed
                try {
                    int num = Integer.parseInt(stockId.substring(1));
                    if (num >= stockCounter) stockCounter = num + 1;
                } catch (NumberFormatException e) {
                    // ignore
                }
                
                Stock s = new Stock(stockId, medId, batchNumber, supplier, qty, mfgDate, expiry, received, costPerUnit);
                if (deleted) s.delete();
                return s;
            } else if (parts.length == 10) {
                // New format: stockId|medicineId|batchNumber|supplier|quantity|mfgDate|expiryDate|receivedDate|costPerUnit|isDeleted
                String stockId = parts[0];
                String medId = parts[1];
                String batchNumber = parts[2];
                Stock.Supplier supplier = Stock.Supplier.valueOf(parts[3]);
                int qty = Integer.parseInt(parts[4]);
                LocalDate mfgDate = LocalDate.parse(parts[5]);
                LocalDate expiry = LocalDate.parse(parts[6]);
                LocalDate received = LocalDate.parse(parts[7]);
                double costPerUnit = Double.parseDouble(parts[8]);
                boolean deleted = Boolean.parseBoolean(parts[9]);
                
                // Update counter if needed
                try {
                    int num = Integer.parseInt(stockId.substring(1));
                    if (num >= stockCounter) stockCounter = num + 1;
                } catch (NumberFormatException e) {
                    // ignore
                }
                
                Stock s = new Stock(stockId, medId, batchNumber, supplier, qty, mfgDate, expiry, received, costPerUnit);
                if (deleted) s.delete();
                return s;
            }
        } catch (Exception e) {
            System.out.println("Error parsing stock line: " + line + " - " + e.getMessage());
        }
        return null;
    }
    
    private static LocalDate generateRandomPastDate() {
        // Simple pseudo-random using current time instead of java.util.Random
        long seed = System.currentTimeMillis();
        int year = 2023 + (int)(seed % 2); // 2023 or 2024
        int month = 1 + (int)(seed % 12);  // 1-12
        int day = 1 + (int)(seed % 28);    // 1-28 (safe for all months)
        return LocalDate.of(year, month, day);
    }
    
    public static String generateStockId() {
        return "S" + (stockCounter++);
    }
}
