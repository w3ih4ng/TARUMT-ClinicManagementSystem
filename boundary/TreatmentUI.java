package boundary;

import control.TreatmentController;
import control.PaymentController;
import control.PatientController;
import control.DoctorController;
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

    public TreatmentUI(TreatmentController treatmentController, 
                      PatientController patientController,
                      DoctorController doctorController) {
        this.sc = new Scanner(System.in);
        this.treatmentController = treatmentController;
        this.paymentController = new PaymentController();
        this.patientController = patientController;
        this.doctorController = doctorController;
    }

    // ==================== MAIN TREATMENT MANAGEMENT MENU ====================

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Medical Treatment Management");
            
            System.out.println("1. View All Treatments");
            System.out.println("2. View Treatment Details");
            System.out.println("3. Treatment History Report by Patient");
            System.out.println("4. Doctor Treatment Performance Report");
            System.out.println("5. Medicine Prescription Analysis Report");
            System.out.println("0. Back to Staff Menu");
            System.out.println();
            System.out.println("=".repeat(50));
            System.out.print("\n\nEnter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("View All Treatments");
                    viewAllTreatments();
                    utility.SystemUtil.popNavigation();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View Treatment Details");
                    viewTreatmentDetails();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("REPORT 1: Patient Treatment History Analysis");
                    generatePatientTreatmentHistoryReport();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("REPORT 2: Doctor Treatment Performance Analysis");
                    generateDoctorPerformanceReport();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("REPORT 3: Medicine Prescription Analysis");
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
