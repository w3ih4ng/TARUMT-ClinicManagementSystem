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
            // Write header comment
            pw.println("# StockID|MedicineID|BatchNumber|Supplier|Quantity|ManufacturingDate|ExpiryDate|ReceivedDate|CostPerUnit|isDeleted");
            
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
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
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
            
            if (parts.length != 10) {
                throw new IllegalArgumentException("Expected 10 columns for stock data, got " + parts.length);
            }
            
            // Format: stockId|medicineId|batchNumber|supplier|quantity|mfgDate|expiryDate|receivedDate|costPerUnit|isDeleted
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
            
        } catch (Exception e) {
            System.out.println("Error parsing stock line: " + line + " - " + e.getMessage());
        }
        return null;
    }
    
    public static String generateStockId() {
        return "S" + (stockCounter++);
    }
}
