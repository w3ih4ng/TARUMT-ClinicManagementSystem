package dao;

import entity.Medicine;
import adt.*;

import java.io.*;

public class MedicineDAO {
    private static final String FILE_NAME = "data/medicines.txt";

    // Ensure file exists
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
    public static void saveMedicines(HashMapInterface<String, Medicine> medicineMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < medicineMap.keySet().size(); i++) {
                String key = medicineMap.keySet().get(i);
                Medicine m = medicineMap.get(key);
                if (m != null) pw.println(toFileString(m));
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

    // Convert Medicine to file line
    private static String toFileString(Medicine m) {
        return String.join("|",
                m.getMedicineId(),
                m.getName(),
                m.getDosage(),
                String.valueOf(m.getQuantity()),
                String.valueOf(m.getPrice()),
                Boolean.toString(m.isDeleted())   // <-- Added deleted flag
        );
    }

    // Convert file line to Medicine
    private static Medicine fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            String medicineId = parts[0];
            String name = parts[1];
            String dosage = parts[2];
            int quantity = Integer.parseInt(parts[3]);
            double price = Double.parseDouble(parts[4]);
            boolean deleted = parts.length > 5 && Boolean.parseBoolean(parts[5]);

            Medicine m = new Medicine(medicineId, name, dosage, quantity, price);
            if (deleted) m.delete();  // mark as deleted
            return m;
        } catch (Exception e) {
            System.out.println("Error parsing medicine line: " + line);
            return null;
        }
    }
}
