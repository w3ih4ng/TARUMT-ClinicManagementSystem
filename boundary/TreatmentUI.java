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
            
            System.out.println("=== CONSULTATION COMPLETION ===");
            System.out.println("  1. Complete Consultation with Treatment");
            System.out.println("  2. Add Medicine Prescription to Treatment");
            System.out.println();
            
            System.out.println("=== TREATMENT MANAGEMENT ===");
            System.out.println("  3. View All Treatments");
            System.out.println("  4. View Treatment Details");
            System.out.println("  5. Update Treatment Diagnosis");
            System.out.println("  6. Update Treatment Fee");
            System.out.println("  7. Delete Treatment");
            System.out.println();
            
            System.out.println("=== TREATMENT REPORTS ===");
            System.out.println("  8. Patient Treatment History Report");
            System.out.println("  9. Doctor Treatment Performance Report");
            System.out.println("  10. Medicine Prescription Analysis Report");
            System.out.println();
            
            System.out.println("=== NAVIGATION ===");
            System.out.println("  0. Back to Staff Menu");
            System.out.println();
                    System.out.println("Note: Consultations must be completed before creating treatments");
        System.out.println("      Medicine dispensing is handled in Pharmacy Module");
        System.out.println("      After completing treatment, use Pharmacy Module to dispense medicines");
            System.out.println();
            System.out.println("=".repeat(80));
            System.out.print("\n\nEnter your choice (1-10, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Complete Consultation with Treatment");
                    completeConsultationWithTreatment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Add Medicine Prescription");
                    addMedicinePrescription();
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
                case "5": 
                    utility.SystemUtil.showSectionHeader("Update Treatment Diagnosis");
                    updateTreatmentDiagnosis();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "6": 
                    utility.SystemUtil.showSectionHeader("Update Treatment Fee");
                    updateTreatmentFee();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "7": 
                    utility.SystemUtil.showSectionHeader("Delete Treatment");
                    deleteTreatment();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "8": 
                    utility.SystemUtil.showSectionHeader("Patient Treatment History Report");
                    generatePatientTreatmentHistoryReport();
                    break;
                case "9": 
                    utility.SystemUtil.showSectionHeader("Doctor Treatment Performance Report");
                    generateDoctorPerformanceReport();
                    break;
                case "10": 
                    utility.SystemUtil.showSectionHeader("Medicine Prescription Analysis Report");
                    generateMedicinePrescriptionReport();
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
        
        treatmentController.displayTreatmentDetails(treatmentId);
    }

    // ==================== CONSULTATION COMPLETION ====================

    private void completeConsultationWithTreatment() {
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
        
        // Complete consultation with treatment
        boolean success = treatmentController.completeConsultationWithTreatment(consultationId, diagnosis, treatmentFee);
        if (success) {
            System.out.println("Consultation completed successfully with treatment!");
            System.out.println("You can now add medicine prescriptions to the treatment.");
            System.out.println();
            System.out.println("Next steps:");
            System.out.println("1. Add medicine prescriptions to the treatment");
            System.out.println("2. Use Pharmacy Module to dispense medicines (reduces stock, generates invoice)");
        } else {
            System.out.println("Failed to complete consultation with treatment.");
        }
    }

    private void addMedicinePrescription() {
        System.out.println("\n--- Add Medicine Prescriptions to Treatment ---");
        
        // Show treatments
        System.out.println("Available treatments:");
        treatmentController.displayAllTreatments();
        
        // Get treatment selection
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
        // Validate treatment exists
        if (treatmentController.getTreatmentById(treatmentId) == null) {
            System.out.println("Treatment ID '" + treatmentId + "' does not exist.");
            return;
        }
        
        // Show available medicines
        System.out.println("\nAvailable medicines:");
        treatmentController.displayAvailableMedicines();
        
        // Allow multiple medicines to be added
        while (true) {
            System.out.println("\n--- Add Medicine ---");
            
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
            
            // Validate medicine exists
            if (!treatmentController.isMedicineExists(medicineId)) {
                System.out.println("Medicine ID '" + medicineId + "' does not exist. Please check the available medicines list above.");
                continue;
            }
            
            // Get quantity
            int quantity = 0;
            while (true) {
                System.out.print("Enter quantity: ");
                String qtyStr = sc.nextLine().trim();
                try {
                    quantity = Integer.parseInt(qtyStr);
                    if (quantity > 0) {
                        break;
                    } else {
                        System.out.println("Quantity must be positive.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            
            // Add medicine prescription
            boolean success = treatmentController.addMedicinePrescription(treatmentId, medicineId, quantity);
            if (success) {
                System.out.println("✓ Medicine '" + medicineId + "' added successfully with quantity: " + quantity);
            } else {
                System.out.println("✗ Failed to add medicine prescription for '" + medicineId + "'.");
            }
            
            System.out.println("\nContinue adding medicines or type 'done' to finish.");
        }
        
        System.out.println("\nMedicine prescription process completed for treatment: " + treatmentId);
    }

    private void viewAvailableConsultations() {
        consultationController.viewAvailableConsultations();
    }

    // ==================== TREATMENT CRUD OPERATIONS ====================

    private void updateTreatmentDiagnosis() {
        System.out.println("\n--- Update Treatment Diagnosis ---");
        
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
        System.out.print("Enter new diagnosis: ");
        String newDiagnosis = sc.nextLine().trim();
        
        if (newDiagnosis.isEmpty()) {
            System.out.println("Diagnosis cannot be empty.");
            return;
        }
        
        treatmentController.updateTreatmentDiagnosis(treatmentId, newDiagnosis);
        System.out.println("Treatment diagnosis updated successfully!");
    }

    private void updateTreatmentFee() {
        System.out.println("\n--- Update Treatment Fee ---");
        
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
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

    private void deleteTreatment() {
        System.out.println("\n--- Delete Treatment ---");
        
        System.out.print("Enter Treatment ID to delete: ");
        String treatmentId = sc.nextLine().trim();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty.");
            return;
        }
        
        System.out.println("Warning: This will permanently delete the treatment!");
        System.out.print("Are you sure? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if (confirm.equals("yes")) {
            boolean success = treatmentController.deleteTreatment(treatmentId);
            if (success) {
                System.out.println("Treatment deleted successfully!");
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

        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s |%n", 
            "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee", "Medicine Prescribed");
        System.out.println(borderLine);

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String description = treatment.getDescription();
            if (description.length() > 23) {
                description = description.substring(0, 20) + "...";
            }
            
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    description,
                    String.format("%.2f", treatment.getTreatmentFee()),
                    medicineStatus);
        }
        System.out.println(borderLine);
    }

    private void displayTreatmentsFromList(ListInterface<Treatment> treatmentList) {
        if (treatmentList.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s |%n", 
            "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee", "Medicine Prescribed");
        System.out.println(borderLine);

        for (int i = 0; i < treatmentList.size(); i++) {
            Treatment treatment = treatmentList.get(i);
            String description = treatment.getDescription();
            if (description.length() > 23) {
                description = description.substring(0, 20) + "...";
            }
            
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s | %-16s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    description,
                    String.format("%.2f", treatment.getTreatmentFee()),
                    medicineStatus);
        }
        System.out.println(borderLine);
    }
}
