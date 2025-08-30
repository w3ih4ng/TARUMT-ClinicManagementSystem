package control;

import entity.*;
import adt.*;
import utility.FilterCriteriaUtil;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Control class for viewing, filtering and editing treatment data
 * @author Your Name
 */
public class ViewTreatmentControl {
    private TreatmentControl treatmentControl;
    private PaymentControl paymentControl;
    private InvoiceControl invoiceControl;
    private HashMapInterface<String, Treatment> treatmentMap;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public ViewTreatmentControl(TreatmentControl treatmentControl, PaymentControl paymentControl, InvoiceControl invoiceControl) {
        this.treatmentControl = treatmentControl;
        this.paymentControl = paymentControl;
        this.invoiceControl = invoiceControl;
        this.treatmentMap = treatmentControl.getTreatmentMap();
    }

    public HashMapInterface<String, Treatment> getTreatmentMap() {
        return this.treatmentMap;
    }

    public void clearCriteria() {
        criteriaUtil.clearCriteria();
    }

    public void addCriteria(String text) {
        criteriaUtil.addCriteria(text);
    }

    private void removeOldSortCriteria() {
        criteriaUtil.removeOldSortCriteria();
    }

    public String getCriteriaSummary() {
        return criteriaUtil.getCriteriaSummary();
    }

    // --- Filter ---
    public HashMapInterface<String, Treatment> filterByDoctor(HashMapInterface<String, Treatment> map, String doctorId) {
        addCriteria("Doctor = " + doctorId);
        return map.filter(treatment -> treatment.getDoctorId().equalsIgnoreCase(doctorId));
    }

    public HashMapInterface<String, Treatment> filterByPatient(HashMapInterface<String, Treatment> map, String patientId) {
        addCriteria("Patient = " + patientId);
        return map.filter(treatment -> treatment.getPatientId().equalsIgnoreCase(patientId));
    }

    public HashMapInterface<String, Treatment> filterByConsultation(HashMapInterface<String, Treatment> map, String consultationId) {
        addCriteria("Consultation = " + consultationId);
        return map.filter(treatment -> treatment.getConsultationId().equalsIgnoreCase(consultationId));
    }

    public HashMapInterface<String, Treatment> filterByFeeRange(HashMapInterface<String, Treatment> map, double minFee, double maxFee) {
        addCriteria("Fee Range = " + minFee + " - " + maxFee);
        return map.filter(treatment -> treatment.getTreatmentFee() >= minFee && treatment.getTreatmentFee() <= maxFee);
    }

    // --- Search ---
    public HashMapInterface<String, Treatment> searchByDiagnosis(HashMapInterface<String, Treatment> map, String keyword) {
        addCriteria("Search Diagnosis = " + keyword);
        return map.filter(treatment -> treatment.getDescription().toLowerCase().contains(keyword.toLowerCase()));
    }

    public HashMapInterface<String, Treatment> searchByTreatmentId(HashMapInterface<String, Treatment> map, String treatmentId) {
        addCriteria("Search ID = " + treatmentId);
        return map.filter(treatment -> treatment.getTreatmentId().toLowerCase().contains(treatmentId.toLowerCase()));
    }

    // --- Sort ---
    public ListInterface<Treatment> sortByTreatmentId(HashMapInterface<String, Treatment> map, boolean ascending) {
        removeOldSortCriteria();
        addCriteria("Sort by ID (" + (ascending ? "A-Z" : "Z-A") + ")");
        
        ListInterface<Treatment> list = toList(map);
        if (ascending) {
            list.sort((t1, t2) -> t1.getTreatmentId().compareTo(t2.getTreatmentId()));
        } else {
            list.reverseSort((t1, t2) -> t1.getTreatmentId().compareTo(t2.getTreatmentId()));
        }
        return list;
    }

    public ListInterface<Treatment> sortByFee(HashMapInterface<String, Treatment> map, boolean ascending) {
        removeOldSortCriteria();
        addCriteria("Sort by Fee (" + (ascending ? "Low-High" : "High-Low") + ")");
        
        ListInterface<Treatment> list = toList(map);
        if (ascending) {
            list.sort((t1, t2) -> Double.compare(t1.getTreatmentFee(), t2.getTreatmentFee()));
        } else {
            list.reverseSort((t1, t2) -> Double.compare(t1.getTreatmentFee(), t2.getTreatmentFee()));
        }
        return list;
    }

    public ListInterface<Treatment> sortByDoctor(HashMapInterface<String, Treatment> map, boolean ascending) {
        removeOldSortCriteria();
        addCriteria("Sort by Doctor (" + (ascending ? "A-Z" : "Z-A") + ")");
        
        ListInterface<Treatment> list = toList(map);
        if (ascending) {
            list.sort((t1, t2) -> t1.getDoctorId().compareTo(t2.getDoctorId()));
        } else {
            list.reverseSort((t1, t2) -> t1.getDoctorId().compareTo(t2.getDoctorId()));
        }
        return list;
    }

    // --- Utility ---
    public ListInterface<Treatment> toList(HashMapInterface<String, Treatment> map) {
        return map.toList();
    }

    public void printTreatmentsFromList(ListInterface<Treatment> treatments) {
        if (treatments.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-12s %-12s %-12s %-15s %-40s %-12s %-10s%n", 
                         "Treatment ID", "Doctor ID", "Patient ID", "Consultation", "Diagnosis", "Fee (RM)", "Medicines");
        System.out.println("=".repeat(120));

        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            String diagnosis = treatment.getDescription();
            if (diagnosis.length() > 38) {
                diagnosis = diagnosis.substring(0, 35) + "...";
            }

            int medicineCount = treatment.getPrescribedMedicines().size();
            
            System.out.printf("%-12s %-12s %-12s %-15s %-40s %12.2f %10d%n",
                             treatment.getTreatmentId(),
                             treatment.getDoctorId(),
                             treatment.getPatientId(),
                             treatment.getConsultationId(),
                             diagnosis,
                             treatment.getTreatmentFee(),
                             medicineCount);
        }
        System.out.println("=".repeat(120));
        System.out.println("Total: " + treatments.size() + " treatment(s)");
    }

    // === BUSINESS LOGIC METHODS ===

    public HashMapInterface<String, Treatment> handleFilter(Scanner sc, HashMapInterface<String, Treatment> currentMap) {
        System.out.println("Filter by:");
        System.out.println("1. Doctor ID");
        System.out.println("2. Patient ID");
        System.out.println("3. Consultation ID");
        System.out.println("4. Fee Range");
        System.out.print("Choose: ");
        String filterChoice = sc.nextLine().trim();

        switch (filterChoice) {
            case "1":
                System.out.print("Enter Doctor ID: ");
                String doctorId = sc.nextLine().trim();
                return filterByDoctor(currentMap, doctorId);
            case "2":
                System.out.print("Enter Patient ID: ");
                String patientId = sc.nextLine().trim();
                return filterByPatient(currentMap, patientId);
            case "3":
                System.out.print("Enter Consultation ID: ");
                String consultationId = sc.nextLine().trim();
                return filterByConsultation(currentMap, consultationId);
            case "4":
                System.out.print("Enter minimum fee: ");
                try {
                    double minFee = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Enter maximum fee: ");
                    double maxFee = Double.parseDouble(sc.nextLine().trim());
                    return filterByFeeRange(currentMap, minFee, maxFee);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid fee range entered.");
                    utility.SystemUtil.pauseForUser();
                    return currentMap;
                }
            default:
                System.out.println("Invalid filter choice.");
                utility.SystemUtil.pauseForUser();
                return currentMap;
        }
    }

    public HashMapInterface<String, Treatment> handleSearch(Scanner sc, HashMapInterface<String, Treatment> currentMap) {
        System.out.println("Search by:");
        System.out.println("1. Treatment ID");
        System.out.println("2. Diagnosis keyword");
        System.out.print("Choose: ");
        String searchChoice = sc.nextLine().trim();

        switch (searchChoice) {
            case "1":
                System.out.print("Enter Treatment ID: ");
                String treatmentId = sc.nextLine().trim();
                return searchByTreatmentId(currentMap, treatmentId);
            case "2":
                System.out.print("Enter diagnosis keyword: ");
                String keyword = sc.nextLine().trim();
                return searchByDiagnosis(currentMap, keyword);
            default:
                System.out.println("Invalid search choice.");
                utility.SystemUtil.pauseForUser();
                return currentMap;
        }
    }

    public ListInterface<Treatment> handleSort(Scanner sc, HashMapInterface<String, Treatment> currentMap) {
        System.out.println("Sort by:");
        System.out.println("1. Treatment ID");
        System.out.println("2. Treatment Fee");
        System.out.println("3. Doctor ID");
        System.out.print("Choose: ");
        String sortChoice = sc.nextLine().trim();

        System.out.println("Order:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Choose: ");
        String orderChoice = sc.nextLine().trim();
        boolean ascending = orderChoice.equals("1");

        switch (sortChoice) {
            case "1":
                return sortByTreatmentId(currentMap, ascending);
            case "2":
                return sortByFee(currentMap, ascending);
            case "3":
                return sortByDoctor(currentMap, ascending);
            default:
                System.out.println("Invalid sort choice.");
                utility.SystemUtil.pauseForUser();
                return toList(currentMap);
        }
    }

    public void viewTreatmentDetails(Scanner sc) {
        System.out.print("Enter Treatment ID to view details: ");
        String treatmentId = sc.nextLine().trim().toUpperCase();

        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty!");
            utility.SystemUtil.pauseForUser();
            return;
        }

        treatmentControl.displayTreatmentDetails(treatmentId);
        utility.SystemUtil.pauseForUser();
    }

    public void editTreatment(Scanner sc) {
        System.out.print("Enter Treatment ID to edit: ");
        String treatmentId = sc.nextLine().trim().toUpperCase();

        if (treatmentId.isEmpty()) {
            System.out.println("Treatment ID cannot be empty!");
            utility.SystemUtil.pauseForUser();
            return;
        }

        Treatment treatment = treatmentControl.getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            utility.SystemUtil.pauseForUser();
            return;
        }

        // Check if consultation is paid
        String consultationId = treatment.getConsultationId();
        if (isConsultationPaid(consultationId)) {
            System.out.println("Cannot edit treatment - consultation has been paid for.");
            utility.SystemUtil.pauseForUser();
            return;
        }

        // Show current treatment details
        System.out.println("\n=== Current Treatment Details ===");
        treatmentControl.displayTreatmentDetails(treatmentId);

        while (true) {
            System.out.println("\n=== Edit Treatment ===");
            System.out.println("1. Edit Diagnosis");
            System.out.println("2. Edit Treatment Fee");
            System.out.println("3. Edit Prescribed Medicines");
            System.out.println("4. View Updated Details");
            System.out.println("0. Finish Editing");
            System.out.print("Choose what to edit: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    editDiagnosis(sc, treatment);
                    break;
                case "2":
                    editTreatmentFee(sc, treatment);
                    break;
                case "3":
                    editPrescribedMedicines(sc, treatment);
                    break;
                case "4":
                    treatmentControl.displayTreatmentDetails(treatmentId);
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0":
                    // Save the changes
                    treatmentControl.saveTreatments();
                    System.out.println("Treatment updated successfully!");
                    utility.SystemUtil.pauseForUser();
                    return;
                default:
                    System.out.println("Invalid choice.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    private void editDiagnosis(Scanner sc, Treatment treatment) {
        System.out.println("Current diagnosis: " + treatment.getDescription());
        System.out.print("Enter new diagnosis (press Enter to keep current): ");
        String newDiagnosis = sc.nextLine().trim();

        if (!newDiagnosis.isEmpty()) {
            treatment.setDescription(newDiagnosis);
            System.out.println("Diagnosis updated successfully!");
        } else {
            System.out.println("Diagnosis unchanged.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void editTreatmentFee(Scanner sc, Treatment treatment) {
        System.out.println("Current treatment fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
        System.out.print("Enter new treatment fee (press Enter to keep current): ");
        String feeInput = sc.nextLine().trim();

        if (!feeInput.isEmpty()) {
            try {
                double newFee = Double.parseDouble(feeInput);
                if (newFee < 0) {
                    System.out.println("Treatment fee cannot be negative!");
                } else {
                    treatment.setTreatmentFee(newFee);
                    System.out.println("Treatment fee updated successfully!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid fee format!");
            }
        } else {
            System.out.println("Treatment fee unchanged.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void editPrescribedMedicines(Scanner sc, Treatment treatment) {
        while (true) {
            System.out.println("\n=== Current Prescribed Medicines ===");
            ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
            if (medicines.isEmpty()) {
                System.out.println("No medicines currently prescribed.");
            } else {
                for (int i = 0; i < medicines.size(); i++) {
                    MedicinePrescribed medicine = medicines.get(i);
                    System.out.println((i + 1) + ". " + medicine.getMedicineId() + " - Quantity: " + medicine.getQuantity());
                }
            }

            System.out.println("\n=== Edit Prescribed Medicines ===");
            System.out.println("1. Add Medicine");
            System.out.println("2. Remove Medicine");
            System.out.println("3. Change Quantity");
            System.out.println("0. Done");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addMedicineToTreatment(sc, treatment);
                    break;
                case "2":
                    removeMedicineFromTreatment(sc, treatment);
                    break;
                case "3":
                    changeMedicineQuantity(sc, treatment);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }

    private void addMedicineToTreatment(Scanner sc, Treatment treatment) {
        System.out.print("Enter Medicine ID to add: ");
        String medicineId = sc.nextLine().trim().toUpperCase();

        if (medicineId.isEmpty()) {
            System.out.println("Medicine ID cannot be empty!");
            utility.SystemUtil.pauseForUser();
            return;
        }

        System.out.print("Enter quantity: ");
        try {
            int quantity = Integer.parseInt(sc.nextLine().trim());
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                utility.SystemUtil.pauseForUser();
                return;
            }

            MedicinePrescribed newMedicine = new MedicinePrescribed(medicineId, quantity);
            treatment.addPrescribedMedicine(newMedicine);
            System.out.println("Medicine added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity format!");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void removeMedicineFromTreatment(Scanner sc, Treatment treatment) {
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        if (medicines.isEmpty()) {
            System.out.println("No medicines to remove.");
            utility.SystemUtil.pauseForUser();
            return;
        }

        System.out.print("Enter the number of medicine to remove (1-" + medicines.size() + "): ");
        try {
            int index = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (index >= 0 && index < medicines.size()) {
                MedicinePrescribed removed = medicines.remove(index);
                System.out.println("Removed: " + removed.getMedicineId());
            } else {
                System.out.println("Invalid medicine number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void changeMedicineQuantity(Scanner sc, Treatment treatment) {
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        if (medicines.isEmpty()) {
            System.out.println("No medicines to modify.");
            utility.SystemUtil.pauseForUser();
            return;
        }

        System.out.print("Enter the number of medicine to modify (1-" + medicines.size() + "): ");
        try {
            int index = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (index >= 0 && index < medicines.size()) {
                MedicinePrescribed medicine = medicines.get(index);
                System.out.println("Current quantity for " + medicine.getMedicineId() + ": " + medicine.getQuantity());
                System.out.print("Enter new quantity: ");
                
                int newQuantity = Integer.parseInt(sc.nextLine().trim());
                if (newQuantity <= 0) {
                    System.out.println("Quantity must be positive!");
                } else {
                    medicine.setQuantity(newQuantity);
                    System.out.println("Quantity updated successfully!");
                }
            } else {
                System.out.println("Invalid medicine number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
        utility.SystemUtil.pauseForUser();
    }

    private boolean isConsultationPaid(String consultationId) {
        // Check if there's an invoice for this consultation and if it's paid
        var invoiceMap = invoiceControl.getInvoiceMap();
        for (String invoiceId : invoiceMap.keySet()) {
            Invoice invoice = invoiceMap.get(invoiceId);
            if (invoice.getConsultationId().equals(consultationId) && invoice.isPaid()) {
                return true;
            }
        }
        return false;
    }
}
