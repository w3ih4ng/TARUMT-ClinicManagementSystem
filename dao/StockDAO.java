package dao;

import entity.Stock;
import entity.Medicine;
import adt.HashMapADT;
import adt.HashMapInterface;
import adt.ListInterface;
import java.time.*;
import java.util.Random;

import java.io.*;

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
        return s.getStockId() + "|" + s.getMedicineId() + "|" + s.getQuantity() + "|" + s.getExpiryDate() + "|" + s.getReceivedDate() + "|" + s.isDeleted();
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
                
                // Generate new stock ID and random past received date
                String stockId = "S" + (stockCounter++);
                LocalDate receivedDate = generateRandomPastDate();
                
                Stock s = new Stock(stockId, medId, qty, expiry, receivedDate);
                if (deleted) s.delete();
                return s;
            } else if (parts.length == 6) {
                // New format: stockId|medicineId|quantity|expiryDate|receivedDate|isDeleted
                String stockId = parts[0];
                String medId = parts[1];
                int qty = Integer.parseInt(parts[2]);
                LocalDate expiry = LocalDate.parse(parts[3]);
                LocalDate received = LocalDate.parse(parts[4]);
                boolean deleted = Boolean.parseBoolean(parts[5]);
                
                // Update counter if needed
                try {
                    int num = Integer.parseInt(stockId.substring(1));
                    if (num >= stockCounter) stockCounter = num + 1;
                } catch (NumberFormatException e) {
                    // ignore
                }
                
                Stock s = new Stock(stockId, medId, qty, expiry, received);
                if (deleted) s.delete();
                return s;
            }
        } catch (Exception e) {
            System.out.println("Error parsing stock line: " + line);
        }
        return null;
    }
    
    private static LocalDate generateRandomPastDate() {
        Random random = new Random();
        int year = 2023 + random.nextInt(2); // 2023 or 2024
        int month = 1 + random.nextInt(12);  // 1-12
        int day = 1 + random.nextInt(28);    // 1-28 (safe for all months)
        return LocalDate.of(year, month, day);
    }
    
    public static String generateStockId() {
        return "S" + (stockCounter++);
    }
}
