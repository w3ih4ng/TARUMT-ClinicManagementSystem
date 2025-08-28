package control;

import entity.Medicine;
import entity.Medicine.Unit;
import adt.*;
import dao.MedicineDAO;

import java.util.Scanner;

/**
 * Control class for medicine management
 * @author Your Name
 */
public class MedicineControl {
    private HashMapInterface<String, Medicine> medicineMap; // key = medicineId
    private Scanner sc;
    private int medicineCounter = 1; // start from MED001

    public MedicineControl() {
        this.medicineMap = MedicineDAO.loadMedicines(); // load from DAO
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

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

    // --- CRUD ---
    public void addMedicine() {
        System.out.println("\n--- Add New Medicine ---");

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        double dosage = 0;
        while (true) {
            System.out.print("Dosage (numeric): ");
            String input = sc.nextLine().trim();
            try {
                dosage = Double.parseDouble(input);
                if (dosage > 0)
                    break;
                System.out.println("Dosage must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }

        Unit unit = null;
        while (unit == null) {
            System.out.println("Select Unit:");
            System.out.println("1. MG");
            System.out.println("2. ML");
            System.out.println("3. TABLET");
            System.out.println("4. CAPSULE");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    unit = Unit.MG;
                    break;
                case "2":
                    unit = Unit.ML;
                    break;
                case "3":
                    unit = Unit.TABLET;
                    break;
                case "4":
                    unit = Unit.CAPSULE;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
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
        Medicine med = new Medicine(medicineId, name, dosage, unit, price);
        medicineMap.put(medicineId, med);
        MedicineDAO.saveMedicines(medicineMap);
        System.out.println("\nMedicine added successfully! Medicine ID: " + medicineId);
    }

    public HashMapInterface<String, Medicine> getMedicineMap() {
        return medicineMap;
    }

    public void printMedicinesTable(ListInterface<Medicine> medicines, String criteriaSummary) {
        if (medicines.isEmpty()) {
            System.out.println(
                    "--------------------------------------------- No medicines found. ---------------------------------------------");
            return;
        }

        System.out.println(criteriaSummary.isEmpty()
                ? "--------------------------------------------- No active filter ---------------------------------------------"
                : criteriaSummary);
        System.out.println();

        // Updated column widths
        String borderLine = "+------------+---------------------------+------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-10s |%n",
                "Med ID", "Name", "Dosage", "Unit", "Price (RM)");
        System.out.println(borderLine);

        for (int i = 0; i < medicines.size(); i++) {
            Medicine m = medicines.get(i);
            int dosageInt = (int) m.getDosage(); // cast to int to remove decimal
            System.out.printf("| %-10s | %-25s | %10d | %-10s | %-10.2f |%n",
                    m.getMedicineId(),
                    m.getName(),
                    dosageInt,
                    m.getUnit(),
                    m.getPrice());
            System.out.println(borderLine);
        }
    }

    public void updateMedicine() {
        System.out.print("\nEnter Medicine ID to update: ");
        String id = sc.nextLine().trim();
        Medicine m = medicineMap.get(id);

        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }

        System.out.println("\nUpdating medicine:");

       medicineDetailsTable(m);
       
        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty())
            m.setName(name);

        System.out.print("New dosage (leave blank to keep): ");
        String dosageStr = sc.nextLine().trim();
        if (!dosageStr.isEmpty()) {
            try {
                double d = Double.parseDouble(dosageStr);
                if (d > 0)
                    m.setDosage(d);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Dosage not updated.");
            }
        }

        System.out.println("Select new Unit (leave blank to keep):");
        System.out.println("1. MG  2. ML  3. TABLET  4. CAPSULE");
        String unitChoice = sc.nextLine().trim();
        switch (unitChoice) {
            case "1":
                m.setUnit(Unit.MG);
                break;
            case "2":
                m.setUnit(Unit.ML);
                break;
            case "3":
                m.setUnit(Unit.TABLET);
                break;
            case "4":
                m.setUnit(Unit.CAPSULE);
                break;
            case "":
                break; // keep current
            default:
                System.out.println("Invalid choice. Unit not updated.");
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

    public void deleteMedicine() {
        System.out.print("\nEnter Medicine ID to delete: ");
        String id = sc.nextLine().trim();
        Medicine m = medicineMap.get(id);

        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }

        medicineDetailsTable(m);

        // Confirm deletion
        System.out.print("Are you sure you want to delete this medicine? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            m.delete();
            MedicineDAO.saveMedicines(medicineMap);
            System.out.println("Medicine soft-deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public void restoreMedicine() {
        System.out.print("\nEnter Medicine ID to restore: ");
        String id = sc.nextLine().trim();
        Medicine m = medicineMap.get(id);

        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }

        if (!m.isDeleted()) {
            System.out.println("Medicine is already active.");
            return;
        }

        m.restore();
        MedicineDAO.saveMedicines(medicineMap);
        System.out.println("Medicine restored successfully.");
    }

    public void medicineDetailsTable(Medicine m) {
        // Display medicine details in table
        System.out.println("\nMedicine details:");
        System.out.println("+------------+----------------+--------+-------+---------+");
        System.out.printf("| %-10s | %-14s | %-6s | %-5s | %-7s |%n",
                "Medicine ID", "Name", "Dosage", "Unit", "Price");
        System.out.println("+------------+----------------+--------+-------+---------+");
        System.out.printf("| %-10s | %-14s | %-6.2f | %-5s | %-7.2f |%n",
                m.getMedicineId(),
                m.getName(),
                m.getDosage(),
                m.getUnit().name(),
                m.getPrice());
        System.out.println("+------------+----------------+--------+-------+---------+");
    }
}
