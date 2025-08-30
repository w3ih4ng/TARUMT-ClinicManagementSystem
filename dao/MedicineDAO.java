package dao;

import entity.Medicine;
import entity.Medicine.Unit;
import adt.*;

import java.io.*;

/**
 * Data Access Object for Medicine persistence
 * @author Your Name
 */
public class MedicineDAO {
    private static final String FILE_NAME = "data/medicines.txt";

    // Ensure file exists
    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring medicines file: " + e.getMessage());
        }
    }

    // Save all medicines
    public static void saveMedicines(HashMapInterface<String, Medicine> medicineMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Write header comment
            pw.println("# MedicineID|Name|Dosage|Unit|Price|isDeleted");
            
            for (int i = 0; i < medicineMap.keySet().size(); i++) {
                String key = medicineMap.keySet().get(i);
                Medicine m = medicineMap.get(key);
                if (m != null)
                    pw.println(toFileString(m));
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
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Medicine m = fromFileString(line);
                if (m != null)
                    map.put(m.getMedicineId(), m);
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines: " + e.getMessage());
        }

        return map;
    }

    // Convert Medicine to file line
    private static String toFileString(Medicine m) {
        return String.join("|",
                m.getMedicineId(),
                m.getName(),
                String.valueOf(m.getDosage()),
                m.getUnit().name(), // save enum name
                String.valueOf(m.getPrice()),
                Boolean.toString(m.isDeleted()));
    }

    // Convert file line to Medicine
    private static Medicine fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 6) {
                throw new IllegalArgumentException("Expected 6 columns, got " + parts.length);
            }
            String medicineId = parts[0];
            String name = parts[1];
            double dosage = Double.parseDouble(parts[2]);
            Unit unit = Unit.valueOf(parts[3]); // parse enum
            double price = Double.parseDouble(parts[4]);
            boolean deleted = Boolean.parseBoolean(parts[5]);

            Medicine m = new Medicine(medicineId, name, dosage, unit, price);
            if (deleted)
                m.delete();
            return m;
        } catch (Exception e) {
            System.out.println("Error parsing medicine line: " + line);
            return null;
        }
    }
}
