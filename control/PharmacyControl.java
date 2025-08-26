package control;

import entity.Medicine;
import adt.*;
import dao.PharmacyDAO;

import java.util.Scanner;

public class PharmacyControl {
    private HashMapInterface<String, Medicine> medicineMap;
    private Scanner sc;
    private int medicineCounter = 100; // start ID from M101

    public PharmacyControl() {
        this.medicineMap = PharmacyDAO.loadMedicines();
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    private void initCounterFromMap() {
        int max = 100;
        ListInterface<String> keys = medicineMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i); // e.g., "M105"
            try {
                int num = Integer.parseInt(key.substring(1));
                if (num > max) max = num;
            } catch (NumberFormatException e) {}
        }
        medicineCounter = max + 1;
    }

    private String generateMedicineId() {
        String id;
        do {
            id = "M" + (medicineCounter++);
        } while (medicineMap.containsKey(id));
        return id;
    }

    // --- CRUD ---
    public void addMedicine() {
        System.out.println("\n--- Add New Medicine ---");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Dosage (e.g., 500mg): ");
        String dosage = sc.nextLine().trim();
        int qty = readInt("Quantity: ");
        double price = readDouble("Price per unit: ");

        String id = generateMedicineId();
        Medicine m = new Medicine(id, name, dosage, qty, price);
        medicineMap.put(id, m);
        PharmacyDAO.saveMedicines(medicineMap);
        System.out.println("Medicine added successfully! ID: " + id);
    }

    public void updateMedicine() {
        System.out.print("\nEnter Medicine ID to update: ");
        String id = sc.nextLine().trim();
        Medicine m = medicineMap.get(id);
        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }

        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) m.setName(name);

        System.out.print("New dosage (leave blank to keep): ");
        String dosage = sc.nextLine().trim();
        if (!dosage.isEmpty()) m.setDosage(dosage);

        String qtyStr = readOptional("New quantity (leave blank to keep): ");
        if (!qtyStr.isEmpty()) m.setQuantity(Integer.parseInt(qtyStr));

        String priceStr = readOptional("New price (leave blank to keep): ");
        if (!priceStr.isEmpty()) m.setPrice(Double.parseDouble(priceStr));

        PharmacyDAO.saveMedicines(medicineMap);
        System.out.println("Medicine updated successfully.");
    }

    public void deleteMedicine() {
        System.out.print("\nEnter Medicine ID to delete: ");
        String id = sc.nextLine().trim();
        if (medicineMap.remove(id) != null) {
            PharmacyDAO.saveMedicines(medicineMap);
            System.out.println("Medicine deleted successfully.");
        } else {
            System.out.println("Medicine not found.");
        }
    }

    public void listMedicines() {
        System.out.println("\n--- Medicine List ---");
        if (medicineMap.keySet().size() == 0) {
            System.out.println("No medicines found.");
            return;
        }

        System.out.printf("%-10s %-20s %-10s %-8s %-8s%n",
                "ID", "Name", "Dosage", "Qty", "Price");
        ListInterface<String> keys = medicineMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            Medicine m = medicineMap.get(keys.get(i));
            System.out.printf("%-10s %-20s %-10s %-8d $%-8.2f%n",
                    m.getMedicineId(), m.getName(), m.getDosage(),
                    m.getQuantity(), m.getPrice());
        }
    }

    // --- Helpers ---
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try { return Integer.parseInt(input); }
            catch (NumberFormatException e) { System.out.println("Invalid integer. Try again."); }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try { return Double.parseDouble(input); }
            catch (NumberFormatException e) { System.out.println("Invalid number. Try again."); }
        }
    }

    private String readOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public HashMapInterface<String, Medicine> getMedicineMap() {
        return medicineMap;
    }
}
