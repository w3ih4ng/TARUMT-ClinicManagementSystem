package boundary;

import java.util.Scanner;
import control.*;

/**
 * Boundary class for comprehensive reporting interface
 * Provides access to all system reports across all modules
 * @author Your Name
 */
public class ReportsUI {
    private Scanner sc;
    private PatientController patientController;
    private DoctorController doctorController;
    private ConsultationController consultationController;
    private TreatmentController treatmentController;
    private PharmacyController pharmacyController;

    public ReportsUI() {
        this.sc = new Scanner(System.in);
        this.patientController = new PatientController();
        this.doctorController = new DoctorController();
        this.consultationController = new ConsultationController();
        this.treatmentController = new TreatmentController();
        this.pharmacyController = new PharmacyController();
    }

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports");
            utility.SystemUtil.showMenuHeader("COMPREHENSIVE REPORTS MODULE");
            System.out.println();
            System.out.println("=================================================");
            System.out.println("1: Patient Management Reports");
            System.out.println("2: Doctor Management Reports");
            System.out.println("3: Consultation Management Reports");
            System.out.println("4: Medical Treatment Reports");
            System.out.println("5: Pharmacy Management Reports");
            System.out.println("6: Generate All Reports");
            System.out.println("0. Return to Main Menu");
            System.out.println("=================================================");
            System.out.print("Select Report Category (1-6, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    showPatientReports();
                    break;
                case "2":
                    showDoctorReports();
                    break;
                case "3":
                    showConsultationReports();
                    break;
                case "4":
                    showTreatmentReports();
                    break;
                case "5":
                    showPharmacyReports();
                    break;
                case "6":
                    generateAllReports();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-6 or 0.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    private void showPatientReports() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports", "Patient Reports");
            utility.SystemUtil.showMenuHeader("PATIENT MANAGEMENT REPORTS");
            System.out.println();
            System.out.println("=================================================");
            System.out.println("1. Patient Registration Summary Report");
            System.out.println("2. Queue Analysis Report");
            System.out.println("0. Return to Reports Menu");
            System.out.println("=================================================");
            System.out.print("Select Report (1-2, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING PATIENT REGISTRATION SUMMARY REPORT...");
                    System.out.println("=".repeat(80));
                    patientController.generatePatientRegistrationSummaryReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING QUEUE ANALYSIS REPORT...");
                    System.out.println("=".repeat(80));
                    patientController.generateQueueAnalysisReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-2 or 0.");
            }
        }
    }

    private void showDoctorReports() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports", "Doctor Reports");
            utility.SystemUtil.showMenuHeader("DOCTOR MANAGEMENT REPORTS");
            System.out.println();
            System.out.println("=================================================");
            System.out.println("1. Doctor Workload Report");
            System.out.println("2. Doctor Availability Report");
            System.out.println("3. Specialty Coverage Report");
            System.out.println("0. Return to Reports Menu");
            System.out.println("=================================================");
            System.out.print("Select Report (1-3, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING DOCTOR WORKLOAD REPORT...");
                    System.out.println("=".repeat(80));
                    doctorController.generateDoctorWorkloadReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING DOCTOR AVAILABILITY REPORT...");
                    System.out.println("=".repeat(80));
                    doctorController.generateDoctorAvailabilityReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING SPECIALTY COVERAGE REPORT...");
                    System.out.println("=".repeat(80));
                    doctorController.generateSpecialtyCoverageReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-3 or 0.");
            }
        }
    }

    private void showConsultationReports() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports", "Consultation Reports");
            utility.SystemUtil.showMenuHeader("CONSULTATION MANAGEMENT REPORTS");
            System.out.println();
            System.out.println("=================================================");
            System.out.println("1. Consultation Frequency Report");
            System.out.println("2. Follow-up Appointment Report");
            System.out.println("3. Daily Consultation Summary");
            System.out.println("0. Return to Reports Menu");
            System.out.println("=================================================");
            System.out.print("Select Report (1-3, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING CONSULTATION FREQUENCY REPORT...");
                    System.out.println("=".repeat(80));
                    consultationController.generateConsultationFrequencyReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING FOLLOW-UP APPOINTMENT REPORT...");
                    System.out.println("=".repeat(80));
                    consultationController.generateFollowUpAppointmentReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING DAILY CONSULTATION SUMMARY...");
                    System.out.println("=".repeat(80));
                    consultationController.generateDailyConsultationSummary();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-3 or 0.");
            }
        }
    }

    private void showTreatmentReports() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports", "Treatment Reports");
            utility.SystemUtil.showMenuHeader("MEDICAL TREATMENT REPORTS");
            System.out.println();
            System.out.println("=================================================");
            System.out.println("1. Diagnosis Statistics Report");
            System.out.println("2. Treatment History Report");
            System.out.println("0. Return to Reports Menu");
            System.out.println("=================================================");
            System.out.print("Select Report (1-2, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING DIAGNOSIS STATISTICS REPORT...");
                    System.out.println("=".repeat(80));
                    treatmentController.generateDiagnosisStatisticsReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING TREATMENT HISTORY REPORT...");
                    System.out.println("=".repeat(80));
                    treatmentController.generateTreatmentHistoryReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-2 or 0.");
            }
        }
    }

    private void showPharmacyReports() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports", "Pharmacy Reports");
            utility.SystemUtil.showMenuHeader("PHARMACY MANAGEMENT REPORTS");
            System.out.println();
            System.out.println("=================================================");
            System.out.println("1. Medicine Usage Report");
            System.out.println("2. Stock Alert Report");
            System.out.println("0. Return to Reports Menu");
            System.out.println("=================================================");
            System.out.print("Select Report (1-2, 0): ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING MEDICINE USAGE REPORT...");
                    System.out.println("=".repeat(80));
                    pharmacyController.generateMedicineUsageReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2":
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("GENERATING STOCK ALERT REPORT...");
                    System.out.println("=".repeat(80));
                    pharmacyController.generateStockAlertReport();
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1-2 or 0.");
            }
        }
    }

    private void generateAllReports() {
        utility.SystemUtil.setNavigationPath("Home", "Clinic Management", "Comprehensive Reports", "All Reports");
        utility.SystemUtil.showMenuHeader("GENERATING ALL REPORTS");
        System.out.println();
        System.out.println("=================================================");
        System.out.println("This will generate all available reports.");
        System.out.println("Reports will be displayed sequentially.");
        System.out.println("=================================================");
        System.out.print("Continue? (y/n): ");

        String choice = sc.nextLine().trim().toLowerCase();
        if (!choice.equals("y") && !choice.equals("yes")) {
            System.out.println("Report generation cancelled.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("GENERATING COMPREHENSIVE SYSTEM REPORT...");
        System.out.println("=".repeat(80));

        // Generate all reports sequentially
        System.out.println("\n" + "=".repeat(80));
        System.out.println("1. PATIENT MANAGEMENT REPORTS");
        System.out.println("=".repeat(80));
        patientController.generatePatientRegistrationSummaryReport();
        System.out.println();
        patientController.generateQueueAnalysisReport();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("2. DOCTOR MANAGEMENT REPORTS");
        System.out.println("=".repeat(80));
        doctorController.generateDoctorWorkloadReport();
        System.out.println();
        doctorController.generateDoctorAvailabilityReport();
        System.out.println();
        doctorController.generateSpecialtyCoverageReport();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("3. CONSULTATION MANAGEMENT REPORTS");
        System.out.println("=".repeat(80));
        consultationController.generateConsultationFrequencyReport();
        System.out.println();
        consultationController.generateFollowUpAppointmentReport();
        System.out.println();
        consultationController.generateDailyConsultationSummary();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("4. MEDICAL TREATMENT REPORTS");
        System.out.println("=".repeat(80));
        treatmentController.generateDiagnosisStatisticsReport();
        System.out.println();
        treatmentController.generateTreatmentHistoryReport();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("5. PHARMACY MANAGEMENT REPORTS");
        System.out.println("=".repeat(80));
        pharmacyController.generateMedicineUsageReport();
        System.out.println();
        pharmacyController.generateStockAlertReport();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL REPORTS GENERATED SUCCESSFULLY!");
        System.out.println("=".repeat(80));
        
        utility.SystemUtil.pauseForUser();
    }
}
