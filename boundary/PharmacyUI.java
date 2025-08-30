package boundary;

import control.PharmacyController;
import entity.*;
import adt.*;
import utility.*;
import java.util.Scanner;
import java.util.HashMap;

/**
 * Consolidated Pharmacy UI - combines all pharmacy-related boundary functionality
 * Handles medicine management, stock management, and dispensing operations
 * @author Your Name
 */
public class PharmacyUI {
    private Scanner sc;
    private PharmacyController pharmacyController;

    public PharmacyUI(PharmacyController pharmacyController) {
        this.sc = new Scanner(System.in);
        this.pharmacyController = pharmacyController;
    }

    // ==================== MAIN PHARMACY MENU ====================

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Pharmacy Module");
            
            System.out.println("1. Medicine Management");
            System.out.println("2. Stock Management");
            System.out.println("3. Medicine Dispensing");
            System.out.println("0. Back to Staff Menu");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("Medicine Management");
                    medicineManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "2":
                    utility.SystemUtil.pushNavigation("Stock Management");
                    stockManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.pushNavigation("Medicine Dispensing");
                    medicineDispensing(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "0": 
                    return; // back to Staff Menu
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== MEDICINE MANAGEMENT ====================

    private void medicineManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Medicine Management Module");
            
            System.out.println("1. Add new medicine");
            System.out.println("2. View all medicines");
            System.out.println("3. Update medicine information");
            System.out.println("4. Delete medicine");
            System.out.println("5. Restore medicine");
            System.out.println("0. Back to Pharmacy Menu");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Add New Medicine");
                    pharmacyController.addMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("View All Medicines");
                    viewAllMedicines(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Update Medicine Information");
                    pharmacyController.updateMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Delete Medicine");
                    pharmacyController.deleteMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Restore Medicine");
                    pharmacyController.restoreMedicine(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== VIEW ALL MEDICINES ====================

    private void viewAllMedicines() {
        utility.SystemUtil.showSectionHeader("All Medicines");
        pharmacyController.viewAllMedicines(); 
        
        // Show filter/sort/search options after displaying medicines
        showMedicineOptions();
    }
    
    private void showMedicineOptions() {
        while (true) {
            System.out.println("\n--- Medicine Options ---");
            System.out.println("1. Filter Medicines");
            System.out.println("2. Search Medicine");
            System.out.println("3. View Medicine Details");
            System.out.println("0. Back to Medicine Management");
            System.out.print("\nChoose option: ");
            
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Filter Medicines");
                    filterMedicines();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Search Medicine");
                    searchMedicine();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("View Medicine Details");
                    pharmacyController.viewMedicineDetails();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    private void filterMedicines() {
        while (true) {
            System.out.println("\n--- Filter Medicines ---");
            System.out.println("1. Filter by Unit");
            System.out.println("2. Filter by Active Status");
            System.out.println("0. Back to Medicine Options");
            System.out.print("\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Filter by Unit");
                    filterByUnit();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Filter by Active Status");
                    filterByStatus();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    private void filterByUnit() {
        System.out.println("\n--- Filter by Unit ---");
        System.out.println("Available units:");
        Medicine.Unit[] units = Medicine.Unit.values();
        for (int i = 0; i < units.length; i++) {
            System.out.println((i + 1) + ". " + units[i]);
        }
        
        System.out.print("Select unit number: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= units.length) {
                Medicine.Unit selectedUnit = units[choice - 1];
                HashMapInterface<String, Medicine> filtered = pharmacyController.filterByUnit(pharmacyController.getMedicineMap(), selectedUnit);
                System.out.println("\nFiltered Results:");
                displayMedicinesFromMap(filtered);
            } else {
                System.out.println("Invalid unit selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }
    
    private void filterByStatus() {
        System.out.println("\n--- Filter by Status ---");
        System.out.println("1. Active Medicines");
        System.out.println("2. Deleted Medicines");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            HashMapInterface<String, Medicine> filtered;
            if (choice == 1) {
                filtered = pharmacyController.filterNotDeleted(pharmacyController.getMedicineMap());
                System.out.println("\n--- Active Medicines ---");
            } else if (choice == 2) {
                filtered = pharmacyController.filterShowDeleted(pharmacyController.getMedicineMap());
                System.out.println("\n--- Deleted Medicines ---");
            } else {
                System.out.println("Invalid choice.");
                return;
            }
            displayMedicinesFromMap(filtered);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }
    
    private void searchMedicine() {
        System.out.println("\n--- Search Medicine ---");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Medicine ID");
        System.out.print("Choose search type: ");
        
        String choice = sc.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("Enter medicine name keywords: ");
                String keywords = sc.nextLine().trim();
                
                if (keywords.isEmpty()) {
                    System.out.println("Keywords cannot be empty!");
                    return;
                }
                
                HashMapInterface<String, Medicine> searchResults = pharmacyController.searchByName(pharmacyController.getMedicineMap(), keywords);
                System.out.println("\nSearch Results:");
                displayMedicinesFromMap(searchResults);
                break;
                
            case "2":
                System.out.print("Enter Medicine ID: ");
                String medicineId = sc.nextLine().trim().toUpperCase();
                
                if (medicineId.isEmpty()) {
                    System.out.println("Medicine ID cannot be empty!");
                    return;
                }
                
                HashMapInterface<String, Medicine> searchResults2 = pharmacyController.searchByMedicineId(pharmacyController.getMedicineMap(), medicineId);
                System.out.println("\nSearch Results:");
                displayMedicinesFromMap(searchResults2);
                break;
                
            default:
                System.out.println("Invalid choice.");
                return;
        }
        utility.SystemUtil.pauseForUser();
    }
    
    private void displayMedicinesFromMap(HashMapInterface<String, Medicine> medicineMap) {
        if (medicineMap.isEmpty()) {
            System.out.println("No medicines found.");
            return;
        }

        String borderLine = "+------------+---------------------------+------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-10s |%n", 
            "Medicine ID", "Name", "Dosage", "Unit", "Price");
        System.out.println(borderLine);

        for (String key : medicineMap.keySet()) {
            Medicine medicine = medicineMap.get(key);
            if (!medicine.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-10s | %-10s | %-10s |%n",
                        medicine.getMedicineId(),
                        medicine.getName(),
                        String.format("%.1f", medicine.getDosage()),
                        medicine.getUnit(),
                        String.format("%.2f", medicine.getPrice()));
            }
        }
        System.out.println(borderLine);
    }



    // ==================== STOCK MANAGEMENT ====================

    private void stockManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Stock Management");
            
            System.out.println("1. Add Stock Batch");
            System.out.println("2. View All Stock Batches");
            System.out.println("3. View Medicine Stock Summary");
            System.out.println("0. Back to Pharmacy Menu");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Add Stock Batch");
                    pharmacyController.addStockBatch(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View All Stock Batches");
                    pharmacyController.viewAllStockBatches(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("View Medicine Stock Summary");
                    pharmacyController.viewMedicineStockSummary(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== MEDICINE DISPENSING ====================

    private void medicineDispensing() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Medicine Dispensing");
            
            System.out.println("1. Dispense Medicine for Treatment");
            System.out.println("2. View Dispensing History");
            System.out.println("0. Back to Pharmacy Menu");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Dispense Medicine for Treatment");
                    dispenseMedicineForTreatment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Dispensing History");
                    viewDispensingHistory(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void dispenseMedicineForTreatment() {
        System.out.println("\n--- Dispense Medicine for Treatment ---");
        
        // Show available treatments with medicine prescriptions
        System.out.println("Available treatments with medicine prescriptions:");
        pharmacyController.displayTreatmentsWithPrescriptions();
        
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
        // Show the medicines that will be dispensed for this treatment
        boolean medicinesShown = pharmacyController.displayMedicinesForTreatment(treatmentId);
        if (!medicinesShown) {
            System.out.println("No medicines found for treatment: " + treatmentId);
            return;
        }
        
        // Ask for confirmation to dispense
        System.out.print("\nDispense these medicines? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Medicine dispensing cancelled.");
            return;
        }
        
        // Dispense medicines for the treatment
        boolean success = pharmacyController.dispenseMedicinesForTreatment(treatmentId);
        if (success) {
            System.out.println("Medicines dispensed successfully for treatment: " + treatmentId);
            System.out.println("Stock quantities have been updated and invoice generated.");
        } else {
            System.out.println("Failed to dispense medicines for treatment: " + treatmentId);
        }
    }

    private void viewDispensingHistory() {
        System.out.println("\n--- Dispensing History ---");
        // This would need to be implemented in PharmacyController
        pharmacyController.displayDispensingHistory();
    }








}
