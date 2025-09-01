package boundary;

import control.TreatmentController;
import control.PaymentController;
import control.PatientController;
import control.DoctorController;
import control.ConsultationController;
import entity.*;
import adt.*;
import utility.*;
import java.util.Scanner;

/**
 * Consolidated Treatment UI - combines all treatment-related boundary functionality
 * Handles treatment management, viewing, editing, and reporting operations
 * @author Your Name
 */
public class TreatmentUI {
    private Scanner sc;
    private TreatmentController treatmentController;
    private PaymentController paymentController;
    private PatientController patientController;
    private DoctorController doctorController;
    private ConsultationController consultationController;

    public TreatmentUI(TreatmentController treatmentController, 
                      PatientController patientController,
                      DoctorController doctorController,
                      ConsultationController consultationController) {
        this.sc = new Scanner(System.in);
        this.treatmentController = treatmentController;
        this.paymentController = new PaymentController();
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.consultationController = consultationController;
    }

    // ==================== MAIN TREATMENT MANAGEMENT MENU ====================

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Medical Treatment Management");

            System.out.println("=== TREATMENT OPERATIONS ===");
            System.out.println("  1. Create Treatment for Consultation");
            System.out.println("  2. Edit Treatment");
            System.out.println("  3. View All Treatments");
            System.out.println("  4. View Treatment Details");
            System.out.println("  0. Back to Staff Menu");
            System.out.println();
            System.out.println("Note: Create treatment first, then use Pharmacy Module to dispense medicines");
            System.out.println("      After dispensing, invoice will be generated for payment processing");
            System.out.println();
            System.out.println("=".repeat(80));
            System.out.print("\n\nEnter your choice (1-4, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Create Treatment for Consultation");
                    createTreatmentForConsultation();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Edit Treatment");
                    editTreatment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3":
                    utility.SystemUtil.pushNavigation("View All Treatments");
                    viewAllTreatments();
                    utility.SystemUtil.popNavigation();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("View Treatment Details");
                    viewTreatmentDetails();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    utility.SystemUtil.popNavigation();
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    // ==================== VIEW ALL TREATMENTS FUNCTIONALITY ====================

    private void viewAllTreatments() {
        utility.SystemUtil.showSectionHeader("All Treatments");
        treatmentController.displayAllTreatments();
        
        // Show simplified options
        showTreatmentOptions();
    }
    
    private void showTreatmentOptions() {
        while (true) {
            System.out.println("\n--- Treatment Options ---");
            System.out.println("1. Sort Treatments");
            System.out.println("2. Search Treatments");
            System.out.println("3. Show Active Treatments Only");
            System.out.println("4. Show Deleted Treatments Only");
            System.out.println("5. Restore Deleted Treatment");
            System.out.println("0. Back to Treatment Management");
            System.out.print("\nChoose option: ");
            
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Sort Treatments");
                    sortTreatments();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Search Treatments");
                    searchTreatments();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Active Treatments Only");
                    treatmentController.displayActiveTreatmentsOnly();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Deleted Treatments Only");
                    displayDeletedTreatmentsOnly();
                    break;
                case "5":
                    utility.SystemUtil.showSectionHeader("Restore Deleted Treatment");
                    restoreDeletedTreatment();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    private void sortTreatments() {
        System.out.println("\n--- Sort Options ---");
        System.out.println("1. Sort by Treatment ID (A-Z)");
        System.out.println("2. Sort by Treatment ID (Z-A)");
        System.out.println("3. Sort by Fee (Low to High)");
        System.out.println("4. Sort by Fee (High to Low)");
        System.out.println("0. Back to Treatment Options");
        System.out.print("\nChoose: ");
        
        String choice = sc.nextLine().trim();
        
        switch (choice) {
            case "1":
                ListInterface<Treatment> sortedAsc = treatmentController.sortByTreatmentId(treatmentController.getTreatmentMap(), true);
                System.out.println("\nSorted Results (A-Z):");
                displayTreatmentsFromList(sortedAsc);
                break;
            case "2":
                ListInterface<Treatment> sortedDesc = treatmentController.sortByTreatmentId(treatmentController.getTreatmentMap(), false);
                System.out.println("\nSorted Results (Z-A):");
                displayTreatmentsFromList(sortedDesc);
                break;
            case "3":
                ListInterface<Treatment> feeAsc = treatmentController.sortByFee(treatmentController.getTreatmentMap(), true);
                System.out.println("\nSorted Results (Low to High):");
                displayTreatmentsFromList(feeAsc);
                break;
            case "4":
                ListInterface<Treatment> feeDesc = treatmentController.sortByFee(treatmentController.getTreatmentMap(), false);
                System.out.println("\nSorted Results (High to Low):");
                displayTreatmentsFromList(feeDesc);
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid choice.");
        }
        utility.SystemUtil.pauseForUser();
    }
    
    private void searchTreatments() {
        System.out.println("\n--- Search Treatments ---");
        System.out.println("Enter search term to find treatments by:");
        System.out.println("- Treatment ID");
        System.out.println("- Diagnosis");
        System.out.println("- Doctor ID");
        System.out.println("- Patient ID");
        System.out.println("- Consultation ID");
        System.out.println();
        System.out.print("Search term: ");
        
        String searchTerm = sc.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty!");
            return;
        }
        
        // Search across all fields
        HashMapInterface<String, Treatment> searchResults = searchAcrossAllFields(searchTerm);
        
        if (searchResults.isEmpty()) {
            System.out.println("No treatments found matching: " + searchTerm);
        } else {
            System.out.println("\nSearch Results for: " + searchTerm);
            displayTreatmentsFromMap(searchResults);
        }
        
        utility.SystemUtil.pauseForUser();
    }
    
    private HashMapInterface<String, Treatment> searchAcrossAllFields(String searchTerm) {
        HashMapInterface<String, Treatment> results = new HashMapADT<>();
        String upperSearchTerm = searchTerm.toUpperCase();
        
        for (String key : treatmentController.getTreatmentMap().keySet()) {
            Treatment treatment = treatmentController.getTreatmentMap().get(key);
            
            // Search in Treatment ID
            if (treatment.getTreatmentId().toUpperCase().contains(upperSearchTerm)) {
                results.put(treatment.getTreatmentId(), treatment);
                continue;
            }
            
            // Search in Doctor ID
            if (treatment.getDoctorId().toUpperCase().contains(upperSearchTerm)) {
                results.put(treatment.getTreatmentId(), treatment);
                continue;
            }
            
            // Search in Patient ID
            if (treatment.getPatientId().toUpperCase().contains(upperSearchTerm)) {
                results.put(treatment.getTreatmentId(), treatment);
                continue;
            }
            
            // Search in Consultation ID
            if (treatment.getConsultationId().toUpperCase().contains(upperSearchTerm)) {
                results.put(treatment.getTreatmentId(), treatment);
                continue;
            }
            
            // Search in Diagnosis (case-insensitive)
            if (treatment.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.put(treatment.getTreatmentId(), treatment);
                continue;
            }
        }
        
        return results;
    }
    




    // ==================== VIEW TREATMENT DETAILS ====================

    private void viewTreatmentDetails() {
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim().toUpperCase();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty!");
            return;
        }
        
        // Display treatment details
        treatmentController.displayTreatmentDetails(treatmentId);
        
        // Show treatment management options
        showTreatmentManagementOptions(treatmentId);
    }
    
    private void showTreatmentManagementOptions(String treatmentId) {
        while (true) {
            // Check if treatment still exists (not soft-deleted)
            Treatment treatment = treatmentController.getTreatmentById(treatmentId);
            if (treatment == null) {
                System.out.println("\nTreatment " + treatmentId + " has been deleted.");
                System.out.println("Returning to Treatment Management Menu...");
                return;
            }
            
            System.out.println("\n=== Treatment Management Options ===");
            System.out.println("1. Edit Prescribed Medicines");
            System.out.println("2. Update Treatment Diagnosis");
            System.out.println("3. Update Treatment Fee");
            System.out.println("4. Delete Treatment");
            System.out.println("0. Back to Treatment Menu");
            System.out.print("\nChoose option: ");
            
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Edit Prescribed Medicines");
                    editPrescribedMedicinesForTreatment(treatmentId);
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Update Treatment Diagnosis");
                    updateTreatmentDiagnosis(treatmentId);
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Update Treatment Fee");
                    updateTreatmentFee(treatmentId);
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Delete Treatment");
                    deleteTreatment(treatmentId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== CONSULTATION COMPLETION ====================

    private void completeConsultationWithTreatmentAndMedicines() {
        viewAvailableConsultations();
        
        if (consultationController.getAvailableConsultationCount() == 0) {
            System.out.println("No consultations available for completion.");
            return;
        }
        
        // Get consultation selection
        System.out.print("Enter Consultation ID to complete: ");
        String consultationId = sc.nextLine().trim();
        
        if (consultationId.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }
        
        // Get diagnosis
        System.out.print("Enter diagnosis: ");
        String diagnosis = sc.nextLine().trim();
        
        if (diagnosis.isEmpty()) {
            System.out.println("Diagnosis cannot be empty.");
            return;
        }
        
        // Get treatment fee
        double treatmentFee = 0.0;
        while (true) {
            System.out.print("Enter treatment fee: ");
            String feeStr = sc.nextLine().trim();
            try {
                treatmentFee = Double.parseDouble(feeStr);
                if (treatmentFee >= 0) {
                    break;
                } else {
                    System.out.println("Treatment fee must be non-negative.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        // Create treatment with medicines in one unified process
        System.out.println("\n=== Medicine Prescription ===");
        System.out.println("Now add medicines to the treatment. Type 'done' when finished.");
        
        ListInterface<MedicinePrescribed> medicines = new ArrayList<>();
        
        while (true) {
            System.out.println("\n--- Add Medicine ---");
            
            // Show available medicines
            treatmentController.displayAvailableMedicines();
            
            // Get medicine selection
            System.out.print("Enter Medicine ID (or 'done' to finish): ");
            String medicineId = sc.nextLine().trim();
            
            if (medicineId.equalsIgnoreCase("done")) {
                break;
            }
            
            if (medicineId.isEmpty()) {
                System.out.println("Medicine ID cannot be empty.");
                continue;
            }
            
            // Get quantity
            int quantity = 0;
            while (true) {
                System.out.print("Enter quantity for " + medicineId + ": ");
                String quantityStr = sc.nextLine().trim();
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity > 0) {
                        break;
                    } else {
                        System.out.println("Quantity must be positive.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            
            // Create medicine prescription
            MedicinePrescribed prescribed = new MedicinePrescribed(medicineId, quantity);
            medicines.add(prescribed);
            
            System.out.println("Medicine " + medicineId + " added with quantity " + quantity);
        }
        
        // Complete consultation with treatment and medicines
        boolean success = treatmentController.completeConsultationWithTreatmentAndMedicines(
            consultationId, diagnosis, treatmentFee, medicines);
            
        if (success) {
            System.out.println("\nConsultation completed successfully with treatment and medicines!");
            System.out.println("\nNext steps:");
            System.out.println("  - Treatment is now ready for medicine dispensing");
            System.out.println("  - Use Pharmacy Module to dispense medicines");
            System.out.println("  - After dispensing, consultation will be marked complete");
        } else {
            System.out.println("Failed to complete consultation with treatment and medicines.");
        }
    }

    private void editPrescribedMedicines() {
        
        // Show treatments
        System.out.println("Available treatments:");
        treatmentController.displayAllTreatments();
        
        // Get treatment selection
        System.out.print("\nEnter Treatment ID: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
        // Validate treatment exists
        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment ID '" + treatmentId + "' does not exist.");
            return;
        }
        
        // Show current medicines first
        System.out.println("\n=== Current Medicines in Treatment ===");
        ListInterface<MedicinePrescribed> currentMedicines = treatment.getPrescribedMedicines();
        if (currentMedicines.isEmpty()) {
            System.out.println("No medicines currently prescribed.");
        } else {
            displayTreatmentMedicines(currentMedicines);
        }
        
        // Medicine editing menu
        while (true) {
            System.out.println("\n=== Edit Prescribed Medicines ===");
            System.out.println("1. Add Medicine");
            System.out.println("2. Remove Medicine");
            System.out.println("3. Update Medicine Quantity");
            System.out.println("4. Refresh Medicines Display");
            System.out.println("0. Back to Treatment Menu");
            System.out.print("\nChoose option: ");
            
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addMedicineToTreatment(treatmentId);
                    break;
                case "2":
                    removeMedicineFromTreatment(treatmentId);
                    break;
                case "3":
                    updateMedicineQuantity(treatmentId);
                    break;
                case "4":
                    refreshMedicinesDisplay(treatmentId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    private void editPrescribedMedicinesForTreatment(String treatmentId) {
        
        // Validate treatment exists
        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment ID '" + treatmentId + "' does not exist.");
            return;
        }
        
        // Show current medicines first
        System.out.println("\n=== Current Medicines in Treatment ===");
        ListInterface<MedicinePrescribed> currentMedicines = treatment.getPrescribedMedicines();
        if (currentMedicines.isEmpty()) {
            System.out.println("No medicines currently prescribed.");
        } else {
            displayTreatmentMedicines(currentMedicines);
        }
        
        // Medicine editing menu
        while (true) {
            System.out.println("\n=== Edit Prescribed Medicines ===");
            System.out.println("1. Add Medicine");
            System.out.println("2. Remove Medicine");
            System.out.println("3. Update Medicine Quantity");
            System.out.println("4. Refresh Medicines Display");
            System.out.println("0. Back to Treatment Management Options");
            System.out.print("\nChoose option: ");
            
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addMedicineToTreatment(treatmentId);
                    break;
                case "2":
                    removeMedicineFromTreatment(treatmentId);
                    break;
                case "3":
                    updateMedicineQuantity(treatmentId);
                    break;
                case "4":
                    refreshMedicinesDisplay(treatmentId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    private void addMedicineToTreatment(String treatmentId) {
        System.out.println("\n--- Add Medicine to Treatment ---");
        
        // Show available medicines
        treatmentController.displayAvailableMedicines();
        
        // Get medicine selection
        System.out.print("Enter Medicine ID: ");
        String medicineId = sc.nextLine().trim();
        
        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty.");
            return;
            }
            
            // Get quantity
            int quantity = 0;
            while (true) {
            System.out.print("Enter quantity for " + medicineId + ": ");
            String quantityStr = sc.nextLine().trim();
                try {
                quantity = Integer.parseInt(quantityStr);
                    if (quantity > 0) {
                        break;
                    } else {
                        System.out.println("Quantity must be positive.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            
        // Add medicine to treatment
        boolean success = treatmentController.addMedicineToTreatment(treatmentId, medicineId, quantity);
        if (success) {
            System.out.println("Medicine " + medicineId + " added to treatment " + treatmentId);
            System.out.println("Medicine added successfully.");
        } else {
            System.out.println("Failed to add medicine " + medicineId + " to treatment " + treatmentId);
        }
    }
    
    private void removeMedicineFromTreatment(String treatmentId) {
        System.out.println("\n--- Remove Medicine from Treatment ---");
        
        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        
        if (medicines.isEmpty()) {
            System.out.println("No medicines to remove.");
            return;
        }
        
        displayTreatmentMedicines(medicines);
        
        System.out.print("Enter Medicine ID to remove: ");
        String medicineId = sc.nextLine().trim();
        
        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty.");
            return;
        }
        
        // Remove medicine from treatment
        boolean success = treatmentController.removeMedicineFromTreatment(treatmentId, medicineId);
        if (success) {
            System.out.println("Medicine " + medicineId + " removed from treatment " + treatmentId);
            System.out.println("Medicine removed successfully.");
        } else {
            System.out.println("Failed to remove medicine " + medicineId + " from treatment " + treatmentId);
        }
    }
    
    private void updateMedicineQuantity(String treatmentId) {
        System.out.println("\n--- Update Medicine Quantity ---");
        
        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        
        if (medicines.isEmpty()) {
            System.out.println("No medicines to update.");
            return;
        }
        
        displayTreatmentMedicines(medicines);
        
        System.out.print("Enter Medicine ID to update: ");
        String medicineId = sc.nextLine().trim();
        
        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty.");
            return;
        }
        
        // Get new quantity
        int newQuantity = 0;
        while (true) {
            System.out.print("Enter new quantity for " + medicineId + ": ");
            String quantityStr = sc.nextLine().trim();
            try {
                newQuantity = Integer.parseInt(quantityStr);
                if (newQuantity > 0) {
                    break;
                } else {
                    System.out.println("Quantity must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        // Update medicine quantity
        boolean success = treatmentController.updateMedicineQuantity(treatmentId, medicineId, newQuantity);
            if (success) {
            System.out.println("Medicine " + medicineId + " quantity updated to " + newQuantity);
            System.out.println("Quantity updated successfully.");
            } else {
            System.out.println("Failed to update medicine " + medicineId + " quantity");
        }
    }
    
    private void displayTreatmentMedicines(ListInterface<MedicinePrescribed> medicines) {
        if (medicines.isEmpty()) {
            System.out.println("No medicines prescribed.");
            return;
        }
        
        String borderLine = "+---------------+---------------------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-13s | %-25s | %-10s | %-10s |%n", 
            "Medicine ID", "Name", "Quantity", "Unit Price");
        System.out.println(borderLine);
        
        for (int i = 0; i < medicines.size(); i++) {
            MedicinePrescribed prescribed = medicines.get(i);
            // Get medicine details from controller
            Medicine medicine = treatmentController.getMedicineById(prescribed.getMedicineId());
            String medicineName = medicine != null ? medicine.getName() : "Unknown";
            double unitPrice = medicine != null ? medicine.getPrice() : 0.0;
            
            System.out.printf("| %-13s | %-25s | %-10s | %-10s |%n",
                    prescribed.getMedicineId(),
                    medicineName,
                    prescribed.getQuantity(),
                    String.format("%.2f", unitPrice));
        }
        System.out.println(borderLine);
    }
    
    private void refreshMedicinesDisplay(String treatmentId) {
        System.out.println("\n=== Refreshed Medicines Display ===");
        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        if (treatment != null) {
            ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
            if (medicines.isEmpty()) {
                System.out.println("No medicines currently prescribed.");
            } else {
                displayTreatmentMedicines(medicines);
            }
        } else {
            System.out.println("Treatment not found.");
        }
    }

    /**
     * Restore a deleted treatment
     */
    private void restoreDeletedTreatment() {
        System.out.println("\n--- Restore Deleted Treatment ---");
        
        // Show deleted treatments first
        System.out.println("Available deleted treatments:");
        displayDeletedTreatmentsOnly();
        
        System.out.print("\nEnter Treatment ID to restore: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
        // Check if treatment exists and is deleted
        Treatment treatment = treatmentController.getTreatmentMap().get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }
        
        if (!treatment.isDeleted()) {
            System.out.println("Treatment is not deleted. No need to restore.");
            return;
        }
        
        System.out.println("Warning: This will restore the treatment and update the consultation status based on medicine prescription!");
        System.out.print("Are you sure? (Y/N): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            boolean success = treatmentController.restoreTreatment(treatmentId);
            if (success) {
                System.out.println("Treatment restored successfully!");
                System.out.println("Consultation status has been updated based on medicine prescription.");
            } else {
                System.out.println("Failed to restore treatment.");
            }
        } else {
            System.out.println("Treatment restoration cancelled.");
        }
    }

    /**
     * Display only deleted treatments
     */
    private void displayDeletedTreatmentsOnly() {
        if (treatmentController.getTreatmentMap().isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+--------------+------------+------------+------------------+---------------------------+------------+---------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-12s | %-10s | %-10s | %-16s | %-25s | %-10s | %-19s | %-10s |%n",
                "TreatmentID", "DoctorID", "PatientID", "ConsultationID", "Diagnosis", "Fee", "Medicine Prescribed", "Status");
        System.out.println(borderLine);

        int deletedCount = 0;

        for (String key : treatmentController.getTreatmentMap().keySet()) {
            Treatment treatment = treatmentController.getTreatmentMap().get(key);
            
            // Only show deleted treatments
            if (!treatment.isDeleted()) {
                continue;
            }
            
            deletedCount++;
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            String status = "DELETED";
            
            System.out.printf("| %-12s | %-10s | %-10s | %-16s | %-25s | %-10s | %-19s | %-10s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    treatment.getDescription(),
                    "RM " + String.format("%.2f", treatment.getTreatmentFee()),
                    medicineStatus,
                    status);
        }
        System.out.println(borderLine);
        
        if (deletedCount == 0) {
            System.out.println("No deleted treatments found.");
        } else {
            System.out.println("Showing " + deletedCount + " deleted treatments");
        }
    }

    private void viewAvailableConsultations() {
        consultationController.viewAvailableConsultations();
    }



    // ==================== TREATMENT CRUD OPERATIONS ====================

    private void updateTreatmentDiagnosis(String treatmentId) {
        System.out.println("\n--- Update Treatment Diagnosis ---");
        
        System.out.print("Enter new diagnosis: ");
        String newDiagnosis = sc.nextLine().trim();
        
        if (newDiagnosis.isEmpty()) {
            System.out.println("Diagnosis cannot be empty.");
            return;
        }
        
        treatmentController.updateTreatmentDiagnosis(treatmentId, newDiagnosis);
        System.out.println("Treatment diagnosis updated successfully!");
    }

    private void updateTreatmentFee(String treatmentId) {
        System.out.println("\n--- Update Treatment Fee ---");
        
        double newFee = 0.0;
        while (true) {
            System.out.print("Enter new treatment fee: ");
            String feeStr = sc.nextLine().trim();
            try {
                newFee = Double.parseDouble(feeStr);
                if (newFee >= 0) {
                    break;
                } else {
                    System.out.println("Treatment fee must be non-negative.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        treatmentController.updateTreatmentFee(treatmentId, newFee);
        System.out.println("Treatment fee updated successfully!");
    }

    private void deleteTreatment(String treatmentId) {
        System.out.println("\n--- Delete Treatment ---");
        
        System.out.println("Warning: This will delete the treatment and set the consultation status back to IN_PROGRESS!");
        System.out.print("Are you sure? (Y/N): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            boolean success = treatmentController.deleteTreatment(treatmentId);
            if (success) {
                System.out.println("Treatment deleted successfully!");
                System.out.println("Consultation status has been set back to IN_PROGRESS.");
            } else {
                System.out.println("Failed to delete treatment.");
            }
        } else {
            System.out.println("Treatment deletion cancelled.");
        }
    }

    // ==================== REPORT GENERATION ====================

    private void generatePatientTreatmentHistoryReport() {
        treatmentController.generatePatientTreatmentHistoryReport();
        utility.SystemUtil.pauseForUser();
    }

    private void generateDoctorPerformanceReport() {
        treatmentController.generateDoctorPerformanceReport();
        utility.SystemUtil.pauseForUser();
    }

    private void generateMedicinePrescriptionReport() {
        treatmentController.generateMedicinePrescriptionReport();
        utility.SystemUtil.pauseForUser();
    }

    // ==================== DISPLAY METHODS ====================

    private void displayTreatmentsFromMap(HashMapInterface<String, Treatment> treatmentMap) {
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s | %-10s |%n", 
            "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee", "Medicine Prescribed", "Status");
        System.out.println(borderLine);

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String description = treatment.getDescription();
            if (description.length() > 23) {
                description = description.substring(0, 20) + "...";
            }
            
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            String status = treatment.isDeleted() ? "DELETED" : "ACTIVE";
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s | %-10s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    description,
                    String.format("%.2f", treatment.getTreatmentFee()),
                    medicineStatus,
                    status);
        }
        System.out.println(borderLine);
    }

    private void displayTreatmentsFromList(ListInterface<Treatment> treatmentList) {
        if (treatmentList.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s | %-10s |%n", 
            "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee", "Medicine Prescribed", "Status");
        System.out.println(borderLine);

        for (int i = 0; i < treatmentList.size(); i++) {
            Treatment treatment = treatmentList.get(i);
            String description = treatment.getDescription();
            if (description.length() > 23) {
                description = description.substring(0, 20) + "...";
            }
            
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            String status = treatment.isDeleted() ? "DELETED" : "ACTIVE";
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s | %-10s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    description,
                    String.format("%.2f", treatment.getTreatmentFee()),
                    medicineStatus,
                    status);
        }
        System.out.println(borderLine);
    }

    // ==================== TREATMENT CREATION AND EDITING ====================

    private void createTreatmentForConsultation() {
        System.out.println("Available consultations ready for treatment:");
        consultationController.viewConsultationsReadyForTreatment();

        System.out.print("\nEnter Consultation ID to create treatment for: ");
        String consultationId = sc.nextLine().trim();

        if (consultationId.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }

        // Check if consultation exists and is ready for treatment
        if (!consultationController.consultationExists(consultationId)) {
            System.out.println("Consultation not found.");
            return;
        }

        Consultation consultation = consultationController.getConsultation(consultationId);
        if (!consultation.getStatus().equals("IN_PROGRESS")) {
            System.out.println("Consultation must be in IN_PROGRESS status to create treatment.");
            return;
        }

        // Check if treatment already exists for this consultation
        ListInterface<Treatment> existingTreatments = treatmentController.getTreatmentsByConsultation(consultationId);
        if (!existingTreatments.isEmpty()) {
            System.out.println("Treatment already exists for this consultation. Use edit options instead.");
            return;
        }

        // Get diagnosis
        System.out.print("Enter diagnosis: ");
        String diagnosis = sc.nextLine().trim();

        if (diagnosis.isEmpty()) {
            System.out.println("Diagnosis cannot be empty.");
            return;
        }

        // Get treatment fee
        double treatmentFee = 0.0;
        while (true) {
            System.out.print("Enter treatment fee: ");
            String feeStr = sc.nextLine().trim();
            try {
                treatmentFee = Double.parseDouble(feeStr);
                if (treatmentFee >= 0) {
                    break;
                } else {
                    System.out.println("Treatment fee must be non-negative.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Create treatment without medicines first
        String doctorId = consultation.getDoctorId() != null ? consultation.getDoctorId() :
                          "D1000"; // fallback doctor ID

        String treatmentId = treatmentController.createTreatment(
            doctorId, consultation.getPatientId(), consultationId, diagnosis, treatmentFee, new adt.ArrayList<>());

        if (treatmentId != null) {
            // Refresh consultation data to get the latest status
            Consultation updatedConsultation = consultationController.getConsultation(consultationId);
            if (updatedConsultation != null) {
                System.out.println("Consultation status updated to " + updatedConsultation.getStatus() + ".");
            } else {
                System.out.println("Consultation status updated to TREATMENT_CREATED.");
            }

            System.out.println("\nTreatment created successfully! Treatment ID: " + treatmentId);

            // Now add medicine prescriptions
            addMedicinePrescriptionsToTreatment(treatmentId);

        } else {
            System.out.println("Failed to create treatment.");
        }
    }

    private void addMedicinePrescriptionsToTreatment(String treatmentId) {
        System.out.println("\n=== Add Medicine Prescriptions ===");
        System.out.println("Add medicines to the treatment. Type 'done' when finished.");

        while (true) {
            System.out.println("\n--- Add Medicine ---");

            // Show available medicines
            treatmentController.displayAvailableMedicines();

            // Get medicine selection
            System.out.print("Enter Medicine ID (or 'done' to finish): ");
            String medicineId = sc.nextLine().trim();

            if (medicineId.equalsIgnoreCase("done")) {
                break;
            }

            if (medicineId.isEmpty()) {
                System.out.println("Medicine ID cannot be empty.");
                continue;
            }

            // Get quantity
            int quantity = 0;
            while (true) {
                System.out.print("Enter quantity for " + medicineId + ": ");
                String quantityStr = sc.nextLine().trim();
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity > 0) {
                        break;
                    } else {
                        System.out.println("Quantity must be positive.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            // Add medicine to treatment
            boolean success = treatmentController.addMedicinePrescription(treatmentId, medicineId, quantity);
            if (success) {
                System.out.println("Medicine " + medicineId + " added with quantity " + quantity);
            } else {
                System.out.println("Failed to add medicine " + medicineId);
            }
        }
    }

    private void editTreatment() {
        // Show available treatments (only active ones)
        System.out.println("\nAvailable Treatments (Active Only):");
        treatmentController.displayActiveTreatmentsOnly();

        System.out.print("\nEnter Treatment ID to edit: ");
        String treatmentId = sc.nextLine().trim();

        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }

        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }

        if (treatment.isDeleted()) {
            System.out.println("Cannot edit deleted treatment.");
            return;
        }

        // Show current treatment details
        System.out.println("\n=== Current Treatment Details ===");
        System.out.println("Treatment ID: " + treatment.getTreatmentId());
        System.out.println("Diagnosis: " + treatment.getDescription());
        System.out.println("Fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
        
        // Show current medicines
        System.out.println("\n=== Current Medicines ===");
        ListInterface<MedicinePrescribed> currentMedicines = treatment.getPrescribedMedicines();
        if (currentMedicines.isEmpty()) {
            System.out.println("No medicines currently prescribed.");
        } else {
            displayTreatmentMedicines(currentMedicines);
        }

        // Edit menu
        while (true) {
            System.out.println("\n=== Edit Treatment Options ===");
            System.out.println("1. Update Diagnosis");
            System.out.println("2. Update Treatment Fee");
            System.out.println("3. Edit Medicine Prescriptions");
            System.out.println("0. Back to Treatment Menu");
            System.out.print("\nChoose option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    updateTreatmentDiagnosis(treatmentId);
                    break;
                case "2":
                    updateTreatmentFee(treatmentId);
                    break;
                case "3":
                    editPrescribedMedicinesForTreatment(treatmentId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void editTreatmentDiagnosis() {
        // Show available treatments (only active ones)
        System.out.println("\nAvailable Treatments (Active Only):");
        treatmentController.displayActiveTreatmentsOnly();

        System.out.print("\nEnter Treatment ID to edit diagnosis: ");
        String treatmentId = sc.nextLine().trim();

        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }

        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }

        if (treatment.isDeleted()) {
            System.out.println("Cannot edit deleted treatment.");
            return;
        }

        System.out.println("Current Diagnosis: " + treatment.getDescription());
        System.out.print("Enter new diagnosis: ");
        String newDiagnosis = sc.nextLine().trim();

        if (newDiagnosis.isEmpty()) {
            System.out.println("Diagnosis cannot be empty.");
            return;
        }

        treatmentController.updateTreatmentDiagnosis(treatmentId, newDiagnosis);
        System.out.println("Diagnosis updated successfully.");
    }

    private void editMedicinePrescriptions() {
        // Show available treatments (only active ones)
        System.out.println("\nAvailable Treatments (Active Only):");
        treatmentController.displayActiveTreatmentsOnly();

        System.out.print("\nEnter Treatment ID to edit medicine prescriptions: ");
        String treatmentId = sc.nextLine().trim();

        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }

        Treatment treatment = treatmentController.getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }

        if (treatment.isDeleted()) {
            System.out.println("Cannot edit deleted treatment.");
            return;
        }

        // Show current prescriptions
        System.out.println("\nCurrent Medicine Prescriptions:");
        treatmentController.displayTreatmentMedicines(treatmentId);

        System.out.println("\n=== Edit Medicine Prescriptions ===");
        System.out.println("1. Add new medicine");
        System.out.println("2. Update medicine quantity");
        System.out.println("3. Remove medicine");
        System.out.print("Choose option (1-3): ");

        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                addMedicineToExistingTreatment(treatmentId);
                break;
            case "2":
                updateMedicineQuantity(treatmentId);
                break;
            case "3":
                removeMedicineFromTreatment(treatmentId);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void addMedicineToExistingTreatment(String treatmentId) {
        System.out.println("\n--- Add Medicine to Treatment ---");

        // Show available medicines
        treatmentController.displayAvailableMedicines();

        System.out.print("Enter Medicine ID to add: ");
        String medicineId = sc.nextLine().trim();

        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty.");
            return;
        }

        int quantity = 0;
        while (true) {
            System.out.print("Enter quantity: ");
            String quantityStr = sc.nextLine().trim();
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity > 0) {
                    break;
                } else {
                    System.out.println("Quantity must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        boolean success = treatmentController.addMedicineToTreatment(treatmentId, medicineId, quantity);
        if (success) {
            System.out.println("Medicine added successfully.");
        } else {
            System.out.println("Failed to add medicine.");
        }
    }


}
