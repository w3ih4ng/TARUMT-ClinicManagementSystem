package control;

import entity.*;
import adt.*;
import dao.TreatmentDAO;
import dao.MedicineDAO;
import utility.FilterCriteriaUtil;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Consolidated Treatment Controller - combines all treatment-related control functionality
 * Handles treatment management, viewing, editing, and business logic
 * @author Your Name
 */
public class TreatmentController {
    private HashMapInterface<String, Treatment> treatmentMap;
    private HashMapInterface<String, Medicine> medicineMap;
    private Scanner sc;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public TreatmentController() {
        this.treatmentMap = TreatmentDAO.loadTreatments();
        this.medicineMap = MedicineDAO.loadMedicines();
        this.sc = new Scanner(System.in);
    }

    // ==================== TREATMENT CRUD OPERATIONS ====================

    /**
     * Create a new treatment for a consultation
     */
    public String createTreatment(String doctorId, String patientId, String consultationId, 
                                String diagnosis, double treatmentFee, 
                                ListInterface<MedicinePrescribed> medicines) {
        
        // Generate treatment ID
        String treatmentId = TreatmentDAO.generateTreatmentId();
        
        // Create treatment
        Treatment treatment = new Treatment(treatmentId, doctorId, patientId, consultationId, 
                                          diagnosis, treatmentFee);
        
        // Add prescribed medicines
        for (int i = 0; i < medicines.size(); i++) {
            treatment.addPrescribedMedicine(medicines.get(i));
        }
        
        // Save treatment
        treatmentMap.put(treatmentId, treatment);
        TreatmentDAO.saveTreatments(treatmentMap);
        
        return treatmentId;
    }

    /**
     * Get treatment by ID
     */
    public Treatment getTreatmentById(String treatmentId) {
        return treatmentMap.get(treatmentId);
    }

    /**
     * Get treatments by consultation ID
     */
    public ListInterface<Treatment> getTreatmentsByConsultation(String consultationId) {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (treatment.getConsultationId().equals(consultationId)) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }

    /**
     * Get treatments by doctor ID
     */
    public ListInterface<Treatment> getTreatmentsByDoctor(String doctorId) {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (treatment.getDoctorId().equals(doctorId)) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }

    /**
     * Get treatments by patient ID
     */
    public ListInterface<Treatment> getTreatmentsByPatient(String patientId) {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (treatment.getPatientId().equals(patientId)) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }

    /**
     * Update treatment diagnosis
     */
    public void updateTreatmentDiagnosis(String treatmentId, String newDiagnosis) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null) {
            treatment.setDescription(newDiagnosis);
            TreatmentDAO.saveTreatments(treatmentMap);
        }
    }

    /**
     * Update treatment fee
     */
    public void updateTreatmentFee(String treatmentId, double newFee) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null) {
            treatment.setTreatmentFee(newFee);
            TreatmentDAO.saveTreatments(treatmentMap);
        }
    }

    /**
     * Update prescribed medicines
     */
    public void updatePrescribedMedicines(String treatmentId, ListInterface<MedicinePrescribed> newMedicines) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null) {
            // Create a new treatment with updated medicines
            Treatment updatedTreatment = new Treatment(
                treatment.getTreatmentId(),
                treatment.getDoctorId(),
                treatment.getPatientId(),
                treatment.getConsultationId(),
                treatment.getDescription(),
                treatment.getTreatmentFee()
            );
            
            // Add new medicines
            for (int i = 0; i < newMedicines.size(); i++) {
                updatedTreatment.addPrescribedMedicine(newMedicines.get(i));
            }
            
            // Replace in map
            treatmentMap.put(treatmentId, updatedTreatment);
            TreatmentDAO.saveTreatments(treatmentMap);
        }
    }

    /**
     * Delete treatment
     */
    public void deleteTreatment(String treatmentId) {
        treatmentMap.remove(treatmentId);
        TreatmentDAO.saveTreatments(treatmentMap);
    }

    // ==================== TREATMENT DISPLAY OPERATIONS ====================

    /**
     * Display treatment details
     */
    public void displayTreatmentDetails(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return;
        }
        
        System.out.println("\n--- Treatment Details ---");
        System.out.println("Treatment ID: " + treatment.getTreatmentId());
        System.out.println("Doctor ID: " + treatment.getDoctorId());
        System.out.println("Patient ID: " + treatment.getPatientId());
        System.out.println("Consultation ID: " + treatment.getConsultationId());
        System.out.println("Diagnosis: " + treatment.getDescription());
        System.out.println("Treatment Fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
        
        // Display prescribed medicines
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        if (!medicines.isEmpty()) {
            System.out.println("\nPrescribed Medicines:");
            for (int i = 0; i < medicines.size(); i++) {
                MedicinePrescribed medicine = medicines.get(i);
                Medicine med = medicineMap.get(medicine.getMedicineId());
                String medicineName = med != null ? med.getName() : "Unknown";
                System.out.println("  - " + medicineName + " x" + medicine.getQuantity());
            }
        }
        
        double totalCost = calculateTreatmentTotalCost(treatmentId);
        System.out.println("Total Cost: RM " + String.format("%.2f", totalCost));
    }

    /**
     * Display all treatments in table format
     */
    public void displayAllTreatments() {
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-25s |%n", 
                "TreatmentID", "DoctorID", "PatientID", "ConsultationID", "Diagnosis", "Fee");
        System.out.println(borderLine);

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-25s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    treatment.getDescription(),
                    "RM " + String.format("%.2f", treatment.getTreatmentFee()));
        }
        System.out.println(borderLine);
    }

    // ==================== FILTERING OPERATIONS ====================

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

    // ==================== SEARCH OPERATIONS ====================

    public HashMapInterface<String, Treatment> searchByDiagnosis(HashMapInterface<String, Treatment> map, String keyword) {
        addCriteria("Search Diagnosis = " + keyword);
        return map.filter(treatment -> treatment.getDescription().toLowerCase().contains(keyword.toLowerCase()));
    }

    public HashMapInterface<String, Treatment> searchByTreatmentId(HashMapInterface<String, Treatment> map, String treatmentId) {
        addCriteria("Search ID = " + treatmentId);
        return map.filter(treatment -> treatment.getTreatmentId().toLowerCase().contains(treatmentId.toLowerCase()));
    }

    // ==================== SORTING OPERATIONS ====================

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

    // ==================== UTILITY OPERATIONS ====================

    public ListInterface<Treatment> toList(HashMapInterface<String, Treatment> map) {
        ListInterface<Treatment> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    public HashMapInterface<String, Treatment> getTreatmentMap() {
        return treatmentMap;
    }

    public void saveTreatments() {
        TreatmentDAO.saveTreatments(treatmentMap);
    }

    /**
     * Calculate total cost of treatment including medicines
     */
    public double calculateTreatmentTotalCost(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            return 0.0;
        }

        double totalCost = treatment.getTreatmentFee();
        
        // Add medicine costs
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        for (int i = 0; i < medicines.size(); i++) {
            MedicinePrescribed medicine = medicines.get(i);
            Medicine med = medicineMap.get(medicine.getMedicineId());
            if (med != null) {
                totalCost += med.getPrice() * medicine.getQuantity();
            }
        }
        
        return totalCost;
    }

    /**
     * Check if consultation is paid (placeholder for future enhancement)
     */
    public boolean isConsultationPaid(String consultationId) {
        // This would need to be implemented to check payment status
        // For now, return false to allow editing
        return false;
    }

    // ==================== REPORTING OPERATIONS ====================

    /**
     * Generate patient treatment history report
     */
    public void generatePatientTreatmentHistoryReport() {
        System.out.println("\n=== PATIENT TREATMENT HISTORY REPORT ===");
        
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found for analysis.");
            return;
        }

        // Group treatments by patient
        HashMapInterface<String, ListInterface<Treatment>> patientTreatments = new HashMapADT<>();
        
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String patientId = treatment.getPatientId();
            
            if (!patientTreatments.containsKey(patientId)) {
                patientTreatments.put(patientId, new ArrayList<>());
            }
            
            patientTreatments.get(patientId).add(treatment);
        }

        System.out.println("Patient Treatment Summary:");
        System.out.println("+------------+------------+---------------------------+---------------------------+");
        System.out.printf("| %-10s | %-10s | %-25s | %-25s |%n", "Patient ID", "Treatment Count", "Total Fees", "Last Treatment");
        System.out.println("+------------+------------+---------------------------+---------------------------+");

        for (String patientId : patientTreatments.keySet()) {
            ListInterface<Treatment> treatments = patientTreatments.get(patientId);
            int count = treatments.size();
            
            double totalFees = 0.0;
            String lastTreatmentDate = "N/A";
            
            for (int i = 0; i < treatments.size(); i++) {
                Treatment treatment = treatments.get(i);
                totalFees += treatment.getTreatmentFee();
                // Note: Treatment entity doesn't have date field, so using "N/A"
            }
            
            System.out.printf("| %-10s | %-10s | %-25s | %-25s |%n",
                    patientId,
                    count,
                    "RM " + String.format("%.2f", totalFees),
                    lastTreatmentDate);
        }
        System.out.println("+------------+------------+---------------------------+---------------------------+");
    }

    /**
     * Generate doctor performance report
     */
    public void generateDoctorPerformanceReport() {
        System.out.println("\n=== DOCTOR TREATMENT PERFORMANCE REPORT ===");
        
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found for analysis.");
            return;
        }

        // Group treatments by doctor
        HashMapInterface<String, ListInterface<Treatment>> doctorTreatments = new HashMapADT<>();
        
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String doctorId = treatment.getDoctorId();
            
            if (!doctorTreatments.containsKey(doctorId)) {
                doctorTreatments.put(doctorId, new ArrayList<>());
            }
            
            doctorTreatments.get(doctorId).add(treatment);
        }

        System.out.println("Doctor Performance Summary:");
        System.out.println("+------------+------------+---------------------------+---------------------------+");
        System.out.printf("| %-10s | %-10s | %-25s | %-25s |%n", "Doctor ID", "Treatment Count", "Total Revenue", "Average Fee");
        System.out.println("+------------+------------+---------------------------+---------------------------+");

        for (String doctorId : doctorTreatments.keySet()) {
            ListInterface<Treatment> treatments = doctorTreatments.get(doctorId);
            int count = treatments.size();
            
            double totalRevenue = 0.0;
            for (int i = 0; i < treatments.size(); i++) {
                Treatment treatment = treatments.get(i);
                totalRevenue += treatment.getTreatmentFee();
            }
            
            double averageFee = count > 0 ? totalRevenue / count : 0.0;
            
            System.out.printf("| %-10s | %-10s | %-25s | %-25s |%n",
                    doctorId,
                    count,
                    "RM " + String.format("%.2f", totalRevenue),
                    "RM " + String.format("%.2f", averageFee));
        }
        System.out.println("+------------+------------+---------------------------+---------------------------+");
    }

    /**
     * Generate medicine prescription analysis report
     */
    public void generateMedicinePrescriptionReport() {
        System.out.println("\n=== MEDICINE PRESCRIPTION ANALYSIS REPORT ===");
        
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found for analysis.");
            return;
        }

        // Count medicine prescriptions
        HashMapInterface<String, Integer> medicineCount = new HashMapADT<>();
        
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
            
            for (int i = 0; i < medicines.size(); i++) {
                MedicinePrescribed medicine = medicines.get(i);
                String medicineId = medicine.getMedicineId();
                
                if (!medicineCount.containsKey(medicineId)) {
                    medicineCount.put(medicineId, 0);
                }
                
                medicineCount.put(medicineId, medicineCount.get(medicineId) + medicine.getQuantity());
            }
        }

        if (medicineCount.isEmpty()) {
            System.out.println("No medicine prescriptions found.");
            return;
        }

        System.out.println("Medicine Prescription Summary:");
        System.out.println("+------------+---------------------------+------------+---------------------------+");
        System.out.printf("| %-10s | %-25s | %-10s | %-25s |%n", "Medicine ID", "Medicine Name", "Quantity", "Total Value");
        System.out.println("+------------+---------------------------+------------+---------------------------+");

        for (String medicineId : medicineCount.keySet()) {
            int quantity = medicineCount.get(medicineId);
            Medicine medicine = medicineMap.get(medicineId);
            String medicineName = medicine != null ? medicine.getName() : "Unknown";
            double totalValue = medicine != null ? medicine.getPrice() * quantity : 0.0;
            
            System.out.printf("| %-10s | %-25s | %-10s | %-25s |%n",
                    medicineId,
                    medicineName,
                    quantity,
                    "RM " + String.format("%.2f", totalValue));
        }
        System.out.println("+------------+---------------------------+------------+---------------------------+");
    }
}
