package dao;

import entity.Medicine;
import adt.*;

import java.io.*;
import java.util.*;

public class PharmacyDAO {
    private static final String FILE_NAME = "data/medicines.txt";

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring medicines file: " + e.getMessage());
        }
    }

    // Save all medicines
    public static void saveMedicines(HashMapInterface<String, Medicine> map) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < map.keySet().size(); i++) {
                String key = map.keySet().get(i);
                Medicine m = map.get(key);
                if (m != null) {
                    pw.println(toFileString(m));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving medicines: " + e.getMessage());
        }
    }

    // Load all medicines
    public static HashMapInterface<String, Medicine> loadMedicines() {
        ensureFile();
        HashMapInterface<String, Medicine> map = new HashMapADT<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Medicine m = fromFileString(line);
                if (m != null) map.put(m.getMedicineId(), m);
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines: " + e.getMessage());
        }
        return map;
    }

    private static String toFileString(Medicine m) {
        return String.join("|",
                m.getMedicineId(),
                m.getName(),
                m.getDosage(),
                Integer.toString(m.getQuantity()),
                Double.toString(m.getPrice())
        );
    }

    private static Medicine fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            String id = parts[0];
            String name = parts[1];
            String dosage = parts[2];
            int qty = Integer.parseInt(parts[3]);
            double price = Double.parseDouble(parts[4]);
            return new Medicine(id, name, dosage, qty, price);
        } catch (Exception e) {
            System.out.println("Error parsing medicine line: " + line);
            return null;
        }
    }
}
