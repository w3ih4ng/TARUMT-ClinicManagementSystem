package boundary;

import control.TreatmentControl;
import control.PatientRecordControl;
import control.DoctorRecordControl;
import control.PaymentControl;
import control.InvoiceControl;
import entity.*;
import adt.ListInterface;
import java.util.Scanner;

/**
 * Boundary class for Medical Treatment Management Module
 * Manages patient diagnosis and treatment history records
 * @author Your Name
 */
public class TreatmentManagementBoundary {
    private Scanner sc;
    private TreatmentControl treatmentControl;
    private PatientRecordControl patientControl;
    private DoctorRecordControl doctorControl;
    private ViewAllTreatmentBoundary viewAllTreatmentBoundary;

    public TreatmentManagementBoundary(TreatmentControl treatmentControl, 
                                     PatientRecordControl patientControl,
                                     DoctorRecordControl doctorControl) {
        this.sc = new Scanner(System.in);
        this.treatmentControl = treatmentControl;
        this.patientControl = patientControl;
        this.doctorControl = doctorControl;
        this.viewAllTreatmentBoundary = new ViewAllTreatmentBoundary(treatmentControl, new PaymentControl(), new InvoiceControl());
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Medical Treatment Management");
            
            System.out.println("1. View All Treatments (with Edit/Filter/Search)");
            System.out.println("2. View Treatment Details");
            System.out.println("3. Treatment History Report by Patient");
            System.out.println("4. Doctor Treatment Performance Report");
            System.out.println("5. Medicine Prescription Analysis Report");
            System.out.println("0. Back to Staff Menu");
            System.out.println();
            System.out.println("=".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    viewAllTreatmentBoundary.show();
                    break;
                case "2": 
                    viewTreatmentDetails();
                    break;
                case "3": 
                    generatePatientTreatmentHistoryReport();
                    break;
                case "4": 
                    generateDoctorPerformanceReport();
                    break;
                case "5": 
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

    private void viewTreatmentDetails() {
        utility.SystemUtil.showSectionHeader("View Treatment Details");
        
        System.out.print("Enter Treatment ID: ");
        String treatmentId = sc.nextLine().trim().toUpperCase();
        
        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty!");
            utility.SystemUtil.pauseForUser();
            return;
        }

        treatmentControl.displayTreatmentDetails(treatmentId);
        utility.SystemUtil.pauseForUser();
    }



    private void generatePatientTreatmentHistoryReport() {
        utility.SystemUtil.showSectionHeader("REPORT 1: Patient Treatment History Analysis");
        
        // Get all patients and their treatment statistics
        System.out.println("=== PATIENT TREATMENT HISTORY SUMMARY ===");
        System.out.println();
        
        // Display report header
        System.out.println("-".repeat(80));
        System.out.printf("%-10s %-20s %-15s %-15s %-15s%n", 
                         "Patient", "Name", "Total Treats", "Total Cost", "Avg Cost");
        System.out.println("-".repeat(80));
        
        // Process all patients
        var patientMap = patientControl.getPatientMap();
        int totalTreatments = 0;
        double totalCost = 0.0;
        
        for (String patientId : patientMap.keySet()) {
            Patient patient = patientMap.get(patientId);
            if (patient.isDeleted()) continue;
            
            ListInterface<Treatment> treatments = treatmentControl.getTreatmentsByPatient(patientId);
            if (treatments.isEmpty()) continue;
            
            double patientTotalCost = 0.0;
            for (int i = 0; i < treatments.size(); i++) {
                patientTotalCost += treatmentControl.calculateTreatmentTotalCost(treatments.get(i).getTreatmentId());
            }
            
            double avgCost = treatments.size() > 0 ? patientTotalCost / treatments.size() : 0.0;
            
            String name = patient.getName();
            if (name.length() > 18) name = name.substring(0, 15) + "...";
            
            System.out.printf("%-10s %-20s %15d %15.2f %15.2f%n",
                             patientId, name, treatments.size(), patientTotalCost, avgCost);
            
            totalTreatments += treatments.size();
            totalCost += patientTotalCost;
        }
        
        System.out.println("-".repeat(80));
        System.out.printf("%-32s %15d %15.2f %15.2f%n",
                         "OVERALL TOTALS:", totalTreatments, totalCost, 
                         totalTreatments > 0 ? totalCost / totalTreatments : 0.0);
        System.out.println("-".repeat(80));
        
        utility.SystemUtil.pauseForUser();
    }

    private void generateDoctorPerformanceReport() {
        utility.SystemUtil.showSectionHeader("REPORT 2: Doctor Treatment Performance Analysis");
        
        System.out.println("=== DOCTOR TREATMENT PERFORMANCE SUMMARY ===");
        System.out.println();
        
        // Display report header
        System.out.println("-".repeat(85));
        System.out.printf("%-10s %-25s %-15s %-15s %-15s%n", 
                         "Doctor", "Name", "Total Treats", "Revenue", "Avg Fee");
        System.out.println("-".repeat(85));
        
        // Process all doctors
        var doctorMap = doctorControl.getDoctorMap();
        int totalTreatments = 0;
        double totalRevenue = 0.0;
        
        for (String doctorId : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(doctorId);
            if (doctor.isDeleted()) continue;
            
            ListInterface<Treatment> treatments = treatmentControl.getTreatmentsByDoctor(doctorId);
            if (treatments.isEmpty()) continue;
            
            double doctorRevenue = 0.0;
            for (int i = 0; i < treatments.size(); i++) {
                doctorRevenue += treatments.get(i).getTreatmentFee();
            }
            
            double avgFee = treatments.size() > 0 ? doctorRevenue / treatments.size() : 0.0;
            
            String name = doctor.getName();
            if (name.length() > 23) name = name.substring(0, 20) + "...";
            
            System.out.printf("%-10s %-25s %15d %15.2f %15.2f%n",
                             doctorId, name, treatments.size(), doctorRevenue, avgFee);
            
            totalTreatments += treatments.size();
            totalRevenue += doctorRevenue;
        }
        
        System.out.println("-".repeat(85));
        System.out.printf("%-37s %15d %15.2f %15.2f%n",
                         "OVERALL TOTALS:", totalTreatments, totalRevenue, 
                         totalTreatments > 0 ? totalRevenue / totalTreatments : 0.0);
        System.out.println("-".repeat(85));
        
        utility.SystemUtil.pauseForUser();
    }

    private void generateMedicinePrescriptionReport() {
        utility.SystemUtil.showSectionHeader("REPORT 3: Medicine Prescription Analysis");
        
        System.out.println("=== MEDICINE PRESCRIPTION FREQUENCY ANALYSIS ===");
        System.out.println();
        
        // Track medicine prescription frequency
        var medicineFrequency = new adt.HashMapADT<String, Integer>();
        var medicineQuantity = new adt.HashMapADT<String, Integer>();
        
        // Process all treatments
        var treatmentMap = treatmentControl.getTreatmentMap();
        for (String treatmentId : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(treatmentId);
            
            ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
            for (int i = 0; i < medicines.size(); i++) {
                MedicinePrescribed medicine = medicines.get(i);
                String medicineId = medicine.getMedicineId();
                
                // Count frequency
                Integer currentFreq = medicineFrequency.get(medicineId);
                medicineFrequency.put(medicineId, currentFreq == null ? 1 : currentFreq + 1);
                
                // Count quantity
                Integer currentQty = medicineQuantity.get(medicineId);
                medicineQuantity.put(medicineId, currentQty == null ? medicine.getQuantity() : currentQty + medicine.getQuantity());
            }
        }
        
        if (medicineFrequency.isEmpty()) {
            System.out.println("No medicine prescriptions found in the system.");
            utility.SystemUtil.pauseForUser();
            return;
        }
        
        // Display report
        System.out.println("-".repeat(70));
        System.out.printf("%-15s %-30s %-10s %-10s%n", 
                         "Medicine ID", "Medicine Name", "Frequency", "Total Qty");
        System.out.println("-".repeat(70));
        
        for (String medicineId : medicineFrequency.keySet()) {
            String medicineName = "Unknown Medicine";
            // You could load medicine name from MedicineControl if needed
            
            System.out.printf("%-15s %-30s %10d %10d%n",
                             medicineId, medicineName, 
                             medicineFrequency.get(medicineId),
                             medicineQuantity.get(medicineId));
        }
        
        System.out.println("-".repeat(70));
        System.out.println("Total unique medicines prescribed: " + medicineFrequency.size());
        System.out.println("-".repeat(70));
        
        utility.SystemUtil.pauseForUser();
    }
}
