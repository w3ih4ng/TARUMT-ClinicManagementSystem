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
            System.out.println();
            System.out.println("=".repeat(60));
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
        while (true) {
            utility.SystemUtil.showMenuHeader("Treatment List");
            
            // Display treatments
            treatmentController.displayAllTreatments();
            
            System.out.println("\nOptions:");
            System.out.println("1. Filter by Doctor");
            System.out.println("2. Filter by Patient");
            System.out.println("3. Filter by Fee Range");
            System.out.println("4. Search by Diagnosis");
            System.out.println("5. Search by Treatment ID");
            System.out.println("6. Sort by Treatment ID");
            System.out.println("7. Sort by Fee");
            System.out.println("8. Sort by Doctor");
            System.out.println("0. Back");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Filter by Doctor");
                    filterByDoctor();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Filter by Patient");
                    filterByPatient();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Filter by Fee Range");
                    filterByFeeRange();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Search by Diagnosis");
                    searchByDiagnosis();
                    break;
                case "5":
                    utility.SystemUtil.showSectionHeader("Search by Treatment ID");
                    searchByTreatmentId();
                    break;
                case "6":
                    utility.SystemUtil.showSectionHeader("Sort by Treatment ID");
                    sortByTreatmentId();
                    break;
                case "7":
                    utility.SystemUtil.showSectionHeader("Sort by Fee");
                    sortByFee();
                    break;
                case "8":
                    utility.SystemUtil.showSectionHeader("Sort by Doctor");
                    sortByDoctor();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== FILTER AND SEARCH METHODS ====================

    private void filterByDoctor() {
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim().toUpperCase();
        
        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }
        
        HashMapInterface<String, Treatment> filtered = treatmentController.filterByDoctor(treatmentController.getTreatmentMap(), doctorId);
        System.out.println("\nFiltered Results:");
        displayTreatmentsFromMap(filtered);
        utility.SystemUtil.pauseForUser();
    }

    private void filterByPatient() {
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim().toUpperCase();
        
        if (patientId.isEmpty()) {
            System.out.println("Patient ID cannot be empty!");
            return;
        }
        
        HashMapInterface<String, Treatment> filtered = treatmentController.filterByPatient(treatmentController.getTreatmentMap(), patientId);
        System.out.println("\nFiltered Results:");
        displayTreatmentsFromMap(filtered);
        utility.SystemUtil.pauseForUser();
    }

    private void filterByFeeRange() {
        System.out.println("Enter fee range:");
        System.out.print("Minimum fee: ");
        String minFeeStr = sc.nextLine().trim();
        System.out.print("Maximum fee: ");
        String maxFeeStr = sc.nextLine().trim();
        
        if (minFeeStr.isEmpty() || maxFeeStr.isEmpty()) {
            System.out.println("Both fee values are required!");
            return;
        }
        
        try {
            double minFee = Double.parseDouble(minFeeStr);
            double maxFee = Double.parseDouble(maxFeeStr);
            HashMapInterface<String, Treatment> filtered = treatmentController.filterByFeeRange(treatmentController.getTreatmentMap(), minFee, maxFee);
            System.out.println("\nFiltered Results:");
            displayTreatmentsFromMap(filtered);
        } catch (NumberFormatException e) {
            System.out.println("Invalid fee format. Please enter valid numbers.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void searchByDiagnosis() {
        System.out.print("Enter diagnosis keywords: ");
        String keywords = sc.nextLine().trim();
        
        if (keywords.isEmpty()) {
            System.out.println("Keywords cannot be empty!");
            return;
        }
        
        HashMapInterface<String, Treatment> searchResults = treatmentController.searchByDiagnosis(treatmentController.getTreatmentMap(), keywords);
        System.out.println("\nSearch Results:");
        displayTreatmentsFromMap(searchResults);
        utility.SystemUtil.pauseForUser();
    }

    private void searchByTreatmentId() {
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim().toUpperCase();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty!");
            return;
        }
        
        HashMapInterface<String, Treatment> searchResults = treatmentController.searchByTreatmentId(treatmentController.getTreatmentMap(), treatmentId);
        System.out.println("\nSearch Results:");
        displayTreatmentsFromMap(searchResults);
        utility.SystemUtil.pauseForUser();
    }

    // ==================== SORT METHODS ====================

    private void sortByTreatmentId() {
        System.out.println("Sort order:");
        System.out.println("1. A-Z (Ascending)");
        System.out.println("2. Z-A (Descending)");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            boolean ascending = (choice == 1);
            ListInterface<Treatment> sorted = treatmentController.sortByTreatmentId(treatmentController.getTreatmentMap(), ascending);
            System.out.println("\nSorted Results:");
            displayTreatmentsFromList(sorted);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void sortByFee() {
        System.out.println("Sort order:");
        System.out.println("1. Lowest First");
        System.out.println("2. Highest First");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            boolean ascending = (choice == 1);
            ListInterface<Treatment> sorted = treatmentController.sortByFee(treatmentController.getTreatmentMap(), ascending);
            System.out.println("\nSorted Results:");
            displayTreatmentsFromList(sorted);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void sortByDoctor() {
        System.out.println("Sort order:");
        System.out.println("1. A-Z (Ascending)");
        System.out.println("2. Z-A (Descending)");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            boolean ascending = (choice == 1);
            ListInterface<Treatment> sorted = treatmentController.sortByDoctor(treatmentController.getTreatmentMap(), ascending);
            System.out.println("\nSorted Results:");
            displayTreatmentsFromList(sorted);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
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
        } else {
            System.out.println("Failed to complete consultation with treatment.");
        }
    }

    private void addMedicinePrescription() {
        System.out.println("\n--- Add Medicine Prescription to Treatment ---");
        
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
        
        // Show available medicines
        System.out.println("\nAvailable medicines:");
        treatmentController.displayAvailableMedicines();
        
        // Get medicine selection
        System.out.print("Enter Medicine ID: ");
        String medicineId = sc.nextLine().trim();
        
        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty.");
            return;
        }
        
        // Validate medicine exists
        if (!treatmentController.isMedicineExists(medicineId)) {
            System.out.println("Medicine ID '" + medicineId + "' does not exist. Please check the available medicines list above.");
            return;
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
        } else {
            System.out.println("Failed to add medicine prescription.");
        }
    }

    private void viewAvailableConsultations() {
        System.out.println("\n--- Available Consultations for Completion ---");
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

        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n", 
            "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee");
        System.out.println(borderLine);

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String description = treatment.getDescription();
            if (description.length() > 23) {
                description = description.substring(0, 20) + "...";
            }
            
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    description,
                    String.format("%.2f", treatment.getTreatmentFee()));
        }
        System.out.println(borderLine);
    }

    private void displayTreatmentsFromList(ListInterface<Treatment> treatmentList) {
        if (treatmentList.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n", 
            "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee");
        System.out.println(borderLine);

        for (int i = 0; i < treatmentList.size(); i++) {
            Treatment treatment = treatmentList.get(i);
            String description = treatment.getDescription();
            if (description.length() > 23) {
                description = description.substring(0, 20) + "...";
            }
            
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    description,
                    String.format("%.2f", treatment.getTreatmentFee()));
        }
        System.out.println(borderLine);
    }
}
