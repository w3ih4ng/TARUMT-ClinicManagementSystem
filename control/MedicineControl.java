package control;

import entity.Medicine;
import adt.*;
import dao.MedicineDAO;

import java.util.Scanner;

public class MedicineControl {
    private HashMapInterface<String, Medicine> medicineMap; // key = medicineId
    private Scanner sc;
    private int medicineCounter = 1; // start from MED001

    public MedicineControl() {
        this.medicineMap = MedicineDAO.loadMedicines(); // load from DAO
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    // Initialize counter by scanning existing medicine IDs
    private void initCounterFromMap() {
        int max = 0;
        ListInterface<String> keys = medicineMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i); // e.g., "MED010"
            try {
                int num = Integer.parseInt(key.substring(3));
                if (num > max)
                    max = num;
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        medicineCounter = max + 1;
    }

    // Generate new Medicine ID
    private String generateMedicineId() {
        String id;
        do {
            id = String.format("MED%03d", medicineCounter++);
        } while (medicineMap.containsKey(id));
        return id;
    }

    // --- CRUD ---
    public void addMedicine() {
        System.out.println("\n--- Add New Medicine ---");

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Dosage (e.g., 500mg, 10ml): ");
        String dosage = sc.nextLine().trim();

        int quantity = 0;
        while (true) {
            System.out.print("Quantity: ");
            String input = sc.nextLine().trim();
            try {
                quantity = Integer.parseInt(input);
                if (quantity >= 0)
                    break;
                System.out.println("Quantity must be non-negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }

        double price = 0.0;
        while (true) {
            System.out.print("Price per unit: ");
            String input = sc.nextLine().trim();
            try {
                price = Double.parseDouble(input);
                if (price >= 0)
                    break;
                System.out.println("Price must be non-negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
            }
        }

        String medicineId = generateMedicineId();
        Medicine med = new Medicine(medicineId, name, dosage, quantity, price);

        medicineMap.put(medicineId, med);
        MedicineDAO.saveMedicines(medicineMap); // persist
        System.out.println("\nMedicine added successfully! Medicine ID: " + medicineId);
    }

    public HashMapInterface<String, Medicine> getMedicineMap() {
        return this.medicineMap;
    }

    public void printMedicinesTable(ListInterface<Medicine> medicines, String criteriaSummary) {
        if (medicines.isEmpty()) {
            System.out.println(
                    "--------------------------------------------- No medicines found. ---------------------------------------------");
            return;
        }

        if (!criteriaSummary.isEmpty()) {
            System.out.println(criteriaSummary);
        } else {
            System.out.println(
                    "--------------------------------------------- No active filter ---------------------------------------------");
        }
        System.out.println();

        // Format
        String leftAlignFormat = "| %-8s | %-20s | %-12s | %-8d | $%-9.2f |%n";
        String borderLine = "+----------+----------------------+--------------+----------+------------+";

        // Header
        System.out.println(borderLine);
        System.out.printf("| %-8s | %-20s | %-12s | %-8s | %-10s |%n",
                "Med ID", "Name", "Dosage", "Quantity", "Price");
        System.out.println(borderLine);

        // Rows
        for (int i = 0; i < medicines.size(); i++) {
            Medicine m = medicines.get(i);
            System.out.printf("| %-8s | %-20s | %-12s | %-8d | $%-9.2f |%n",
                    m.getMedicineId(),
                    m.getName(),
                    m.getDosage(),
                    m.getQuantity(),
                    m.getPrice());
            System.out.println(borderLine);
        }
    }

    // --- Update ---
    public void updateMedicine() {
        System.out.print("\nEnter Medicine ID to update: ");
        String id = sc.nextLine().trim();
        Medicine m = medicineMap.get(id);

        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }

        System.out.println("\nUpdating medicine: " + m.getName());

        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty())
            m.setName(name);

        System.out.print("New dosage (leave blank to keep): ");
        String dosage = sc.nextLine().trim();
        if (!dosage.isEmpty())
            m.setDosage(dosage);

        System.out.print("New quantity (leave blank to keep): ");
        String quantityStr = sc.nextLine().trim();
        if (!quantityStr.isEmpty()) {
            try {
                int qty = Integer.parseInt(quantityStr);
                if (qty >= 0)
                    m.setQuantity(qty);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Quantity not updated.");
            }
        }

        System.out.print("New price (leave blank to keep): ");
        String priceStr = sc.nextLine().trim();
        if (!priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                if (price >= 0)
                    m.setPrice(price);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Price not updated.");
            }
        }

        MedicineDAO.saveMedicines(medicineMap);
        System.out.println("Medicine updated successfully.");
    }

    // --- Delete ---
    public void deleteMedicine() {
        System.out.print("\nEnter Medicine ID to delete: ");
        String id = sc.nextLine().trim();
        Medicine m = medicineMap.get(id);

        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }

        medicineMap.remove(id);
        MedicineDAO.saveMedicines(medicineMap);
        System.out.println("Medicine deleted successfully.");
    }

    // --- Reports ---
    public void reportLowStock(int threshold) {
        System.out.println("\n--- Medicines Below Stock Threshold (" + threshold + ") ---");

        ListInterface<String> keys = medicineMap.keySet();
        boolean found = false;

        System.out.printf("%-8s %-20s %-12s %-10s %-10s%n",
                "Med ID", "Name", "Dosage", "Quantity", "Price");

        for (int i = 0; i < keys.size(); i++) {
            Medicine m = medicineMap.get(keys.get(i));
            if (m.getQuantity() < threshold) {
                found = true;
                System.out.printf("%-8s %-20s %-12s %-10d $%-10.2f%n",
                        m.getMedicineId(),
                        m.getName(),
                        m.getDosage(),
                        m.getQuantity(),
                        m.getPrice());
            }
        }

        if (!found) {
            System.out.println("No medicines below threshold.");
        }
    }
}
