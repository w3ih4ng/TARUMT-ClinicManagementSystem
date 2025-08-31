package control;

import entity.*;
import adt.*;
import dao.TreatmentDAO;
import dao.MedicineDAO;
import dao.ConsultationDAO;
import utility.FilterCriteriaUtil;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Consolidated Treatment Controller - combines all treatment-related control
 * functionality
 * Handles treatment management, viewing, editing, and business logic
 * 
 * @author Your Name
 */
public class TreatmentController {
    private HashMapInterface<String, Treatment> treatmentMap;
    private HashMapInterface<String, Medicine> medicineMap;
    private HashMapInterface<String, Consultation> consultationMap;
    private Scanner sc;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public TreatmentController() {
        this.treatmentMap = TreatmentDAO.loadTreatments();
        this.medicineMap = MedicineDAO.loadMedicines();
        this.consultationMap = ConsultationDAO.loadConsultations();
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
        
        // Update consultation status to TREATMENT_CREATED
        updateConsultationStatusAfterTreatment(consultationId, treatmentId);

        return treatmentId;
    }
    
    /**
     * Complete consultation with treatment and medicines in one unified process
     */
    public boolean completeConsultationWithTreatmentAndMedicines(String consultationId, 
            String diagnosis, double treatmentFee, ListInterface<MedicinePrescribed> medicines) {
        
        try {
            // Get consultation details
            Consultation consultation = consultationMap.get(consultationId);
            if (consultation == null) {
                System.out.println("Consultation not found: " + consultationId);
                return false;
            }
            
            // Create treatment with medicines
            String treatmentId = createTreatment(
                consultation.getDoctorId(), 
                consultation.getPatientId(), 
                consultationId, 
                diagnosis, 
                treatmentFee, 
                medicines
            );
            
            if (treatmentId != null) {
                System.out.println("✅ Treatment created successfully: " + treatmentId);
                System.out.println("✅ Medicines prescribed: " + medicines.size() + " medicine(s)");
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Error completing consultation with treatment: " + e.getMessage());
            return false;
        }
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
     * Get treatments ready for medicine dispensing
     */
    public ListInterface<Treatment> getTreatmentsReadyForMedicineDispensing() {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String consultationId = treatment.getConsultationId();
            Consultation consultation = consultationMap.get(consultationId);
            
            if (consultation != null && consultation.getStatus().equals("TREATMENT_CREATED")) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }
    
    /**
     * Display treatments ready for medicine dispensing
     */
    public void displayTreatmentsReadyForMedicineDispensing() {
        ListInterface<Treatment> treatments = getTreatmentsReadyForMedicineDispensing();
        if (treatments.isEmpty()) {
            System.out.println("No treatments ready for medicine dispensing.");
            return;
        }
        
        System.out.println("\n--- Treatments Ready for Medicine Dispensing ---");
        String borderLine = "+------------+------------+------------+------------+---------------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n",
                "TreatmentID", "DoctorID", "PatientID", "ConsultationID", "Diagnosis", "Fee");
        System.out.println(borderLine);
        
        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s | %-10s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    treatment.getDescription(),
                    String.format("%.2f", treatment.getTreatmentFee()));
        }
        System.out.println(borderLine);
    }
    
    
    /**
     * Update consultation status after treatment creation
     */
    private void updateConsultationStatusAfterTreatment(String consultationId, String treatmentId) {
        try {
            // Get consultation and update its status
            Consultation consultation = consultationMap.get(consultationId);
            if (consultation != null) {
                consultation.markTreatmentCreated(treatmentId);
                consultationMap.put(consultationId, consultation);
                ConsultationDAO.saveConsultations(consultationMap);
                
                System.out.println("Consultation " + consultationId + " status updated to TREATMENT_CREATED");
            }
        } catch (Exception e) {
            System.out.println("Error updating consultation status: " + e.getMessage());
        }
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
                    treatment.getTreatmentFee());

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
     * Add medicine to existing treatment
     */
    public boolean addMedicineToTreatment(String treatmentId, String medicineId, int quantity) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
        
        // Check if medicine already exists in treatment
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        for (int i = 0; i < medicines.size(); i++) {
            MedicinePrescribed existing = medicines.get(i);
            if (existing.getMedicineId().equals(medicineId)) {
                System.out.println("Medicine " + medicineId + " already exists in treatment. Use update quantity instead.");
                return false;
            }
        }
        
        // Add new medicine
        MedicinePrescribed newMedicine = new MedicinePrescribed(medicineId, quantity);
        treatment.addPrescribedMedicine(newMedicine);
        
        // Save updated treatment
        treatmentMap.put(treatmentId, treatment);
        TreatmentDAO.saveTreatments(treatmentMap);
        
        return true;
    }
    
    /**
     * Remove medicine from treatment
     */
    public boolean removeMedicineFromTreatment(String treatmentId, String medicineId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
        
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        boolean removed = false;
        
        // Find and remove the medicine
        for (int i = 0; i < medicines.size(); i++) {
            MedicinePrescribed medicine = medicines.get(i);
            if (medicine.getMedicineId().equals(medicineId)) {
                medicines.remove(i);
                removed = true;
                break;
            }
        }
        
        if (removed) {
            // Save updated treatment
            treatmentMap.put(treatmentId, treatment);
            TreatmentDAO.saveTreatments(treatmentMap);
            return true;
        } else {
            System.out.println("Medicine " + medicineId + " not found in treatment " + treatmentId);
            return false;
        }
    }
    
    /**
     * Update medicine quantity in treatment
     */
    public boolean updateMedicineQuantity(String treatmentId, String medicineId, int newQuantity) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
        
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        
        // Find and update the medicine quantity
        for (int i = 0; i < medicines.size(); i++) {
            MedicinePrescribed medicine = medicines.get(i);
            if (medicine.getMedicineId().equals(medicineId)) {
                medicine.setQuantity(newQuantity);
                
                // Save updated treatment
                treatmentMap.put(treatmentId, treatment);
                TreatmentDAO.saveTreatments(treatmentMap);
                return true;
            }
        }
        
        System.out.println("Medicine " + medicineId + " not found in treatment " + treatmentId);
        return false;
    }
    
    /**
     * Get medicine by ID (helper method)
     */
    public Medicine getMedicineById(String medicineId) {
        return medicineMap.get(medicineId);
    }

    /**
     * Delete treatment
     */
    public boolean deleteTreatment(String treatmentId) {
        if (treatmentMap.containsKey(treatmentId)) {
            treatmentMap.remove(treatmentId);
            TreatmentDAO.saveTreatments(treatmentMap);
            System.out.println("Treatment deleted successfully: " + treatmentId);
            return true;
        } else {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
    }
    
    /**
     * Mark medicines as dispensed for a treatment
     */
    public void markMedicinesDispensed(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return;
        }
        
        // Update consultation status to MEDICINES_DISPENSED
        String consultationId = treatment.getConsultationId();
        Consultation consultation = consultationMap.get(consultationId);
        
        if (consultation != null) {
            consultation.markMedicinesDispensed();
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            System.out.println("Medicines dispensed for treatment: " + treatmentId);
            System.out.println("Consultation " + consultationId + " status updated to MEDICINES_DISPENSED");
            System.out.println("Consultation ready for final completion.");
        } else {
            System.out.println("Consultation not found for treatment: " + consultationId);
        }
    }
    
    /**
     * Complete consultation after medicines dispensed
     */
    public void completeConsultationAfterDispensing(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return;
        }
        
        String consultationId = treatment.getConsultationId();
        Consultation consultation = consultationMap.get(consultationId);
        
        if (consultation != null && consultation.getStatus().equals("MEDICINES_DISPENSED")) {
            // Mark consultation as ready for payment
            consultation.markFullyCompleted();
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
            
            System.out.println("Consultation " + consultationId + " ready for payment!");
            System.out.println("Invoice will be generated. Patient can proceed to payment.");
        } else {
            System.out.println("Consultation must be in MEDICINES_DISPENSED status to complete.");
        }
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

        String borderLine = "+--------------+------------+------------+------------------+---------------------------+------------+---------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-12s | %-10s | %-10s | %-16s | %-25s | %-10s | %-19s |%n",
                "TreatmentID", "DoctorID", "PatientID", "ConsultationID", "Diagnosis", "Fee", "Medicine Prescribed");
        System.out.println(borderLine);

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            System.out.printf("| %-12s | %-10s | %-10s | %-16s | %-25s | %-10s | %-19s |%n",
                    treatment.getTreatmentId(),
                    treatment.getDoctorId(),
                    treatment.getPatientId(),
                    treatment.getConsultationId(),
                    treatment.getDescription(),
                    "RM " + String.format("%.2f", treatment.getTreatmentFee()),
                    medicineStatus);
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

    public HashMapInterface<String, Treatment> filterByDoctor(HashMapInterface<String, Treatment> map,
            String doctorId) {
        addCriteria("Doctor = " + doctorId);
        return map.filter(treatment -> treatment.getDoctorId().equalsIgnoreCase(doctorId));
    }

    public HashMapInterface<String, Treatment> filterByPatient(HashMapInterface<String, Treatment> map,
            String patientId) {
        addCriteria("Patient = " + patientId);
        return map.filter(treatment -> treatment.getPatientId().equalsIgnoreCase(patientId));
    }

    public HashMapInterface<String, Treatment> filterByConsultation(HashMapInterface<String, Treatment> map,
            String consultationId) {
        addCriteria("Consultation = " + consultationId);
        return map.filter(treatment -> treatment.getConsultationId().equalsIgnoreCase(consultationId));
    }

    public HashMapInterface<String, Treatment> filterByFeeRange(HashMapInterface<String, Treatment> map, double minFee,
            double maxFee) {
        addCriteria("Fee Range = " + minFee + " - " + maxFee);
        return map.filter(treatment -> treatment.getTreatmentFee() >= minFee && treatment.getTreatmentFee() <= maxFee);
    }

    // ==================== SEARCH OPERATIONS ====================

    public HashMapInterface<String, Treatment> searchByDiagnosis(HashMapInterface<String, Treatment> map,
            String keyword) {
        addCriteria("Search Diagnosis = " + keyword);
        return map.filter(treatment -> treatment.getDescription().toLowerCase().contains(keyword.toLowerCase()));
    }

    public HashMapInterface<String, Treatment> searchByTreatmentId(HashMapInterface<String, Treatment> map,
            String treatmentId) {
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

    // ==================== NEW WORKFLOW METHODS ====================

    /**
     * Complete consultation with treatment
     */
    public boolean completeConsultationWithTreatment(String consultationId, String diagnosis, double treatmentFee) {
        // Check if consultation exists and is ready for payment
        if (consultationMap == null || !consultationMap.containsKey(consultationId)) {
            System.out.println("Consultation not found: " + consultationId);
            return false;
        }

        Consultation consultation = consultationMap.get(consultationId);
        if (!consultation.getStatus().equals("SCHEDULED")) {
            System.out.println("Consultation is not ready for payment. Status: " + consultation.getStatus());
            return false;
        }

        // Create treatment - use existing constructor
        String treatmentId = TreatmentDAO.generateTreatmentId();
        Treatment treatment = new Treatment(treatmentId, consultation.getDoctorId(), consultation.getPatientId(),
                consultationId, diagnosis, treatmentFee);

        // Add to treatment map
        treatmentMap.put(treatmentId, treatment);
        TreatmentDAO.saveTreatments(treatmentMap);

        // Update consultation status and link to treatment
        consultation.completeConsultation(treatmentId);
        consultationMap.put(consultationId, consultation);
        ConsultationDAO.saveConsultations(consultationMap);

        System.out.println("Treatment created successfully: " + treatmentId);
        System.out.println("Consultation completed and linked to treatment.");
        return true;
    }

    /**
     * Add medicine prescription to treatment
     */
    public boolean addMedicinePrescription(String treatmentId, String medicineId, int quantity) {
        // Check if treatment exists
        if (!treatmentMap.containsKey(treatmentId)) {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }

        // Check if medicine exists
        if (medicineMap == null || !medicineMap.containsKey(medicineId)) {
            System.out.println("Medicine not found: " + medicineId);
            return false;
        }

        // Create medicine prescription - check constructor
        MedicinePrescribed prescription = new MedicinePrescribed(medicineId, quantity);

        // Add to treatment's medicine list
        Treatment treatment = treatmentMap.get(treatmentId);
        treatment.addPrescribedMedicine(prescription);

        // Update treatment
        treatmentMap.put(treatmentId, treatment);
        TreatmentDAO.saveTreatments(treatmentMap);

        System.out.println("\nMedicine prescription added successfully!");
        System.out.println("\nMedicine: " + medicineId + ", Quantity: " + quantity);
        return true;
    }

    /**
     * Check if consultation is paid (placeholder for future enhancement)
     */
    public boolean isConsultationPaid(String consultationId) {
        // This would need to be implemented to check payment status
        // For now, return false to allow editing
        return false;
    }

    /**
     * Check if medicine exists
     */
    public boolean isMedicineExists(String medicineId) {
        return medicineMap != null && medicineMap.containsKey(medicineId);
    }

    /**
     * Display available medicines in table format
     */
    public void displayAvailableMedicines() {
        if (medicineMap == null || medicineMap.isEmpty()) {
            System.out.println("No medicines available.");
            return;
        }

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-25s | %-8s | %-8s | %-10s |%n";

        // Define border line
        String borderLine = "+--------------+---------------------------+----------+----------+------------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Medicine ID", "Name", "Dosage", "Unit", "Price");

        // Print header separator
        System.out.println(borderLine);

        // Convert to list and sort by Medicine ID (ascending)
        ListInterface<Medicine> sortedMedicines = new ArrayList<>();
        for (String key : medicineMap.keySet()) {
            Medicine medicine = medicineMap.get(key);
            if (medicine != null && !medicine.isDeleted()) {
                sortedMedicines.add(medicine);
            }
        }
        
        // Sort medicines by ID (ascending)
        sortMedicinesById(sortedMedicines);
        
        // Print each row + row separator
        for (int i = 0; i < sortedMedicines.size(); i++) {
            Medicine medicine = sortedMedicines.get(i);
            // Print row
            System.out.printf(leftAlignFormat,
                    medicine.getMedicineId(),
                    medicine.getName(),
                    String.format("%.1f", medicine.getDosage()),
                    medicine.getUnit(),
                    String.format("%.2f", medicine.getPrice()));

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }
    
    /**
     * Sort medicines by ID in ascending order
     */
    private void sortMedicinesById(ListInterface<Medicine> medicines) {
        // Simple bubble sort for Medicine ID (ascending)
        for (int i = 0; i < medicines.size() - 1; i++) {
            for (int j = 0; j < medicines.size() - i - 1; j++) {
                Medicine current = medicines.get(j);
                Medicine next = medicines.get(j + 1);
                
                if (current.getMedicineId().compareTo(next.getMedicineId()) > 0) {
                    // Swap medicines
                    medicines.set(j, next);
                    medicines.set(j + 1, current);
                }
            }
        }
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
        System.out.printf("| %-10s | %-10s | %-25s | %-25s |%n", "Patient ID", "Treatment Count", "Total Fees",
                "Last Treatment");
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
        System.out.printf("| %-10s | %-10s | %-25s | %-25s |%n", "Doctor ID", "Treatment Count", "Total Revenue",
                "Average Fee");
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
        System.out.printf("| %-10s | %-25s | %-10s | %-25s |%n", "Medicine ID", "Medicine Name", "Quantity",
                "Total Value");
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

    // ==================== TABLE DISPLAY METHODS ====================

    public void printTreatmentsTable(ListInterface<Treatment> treatments, String title) {
        if (treatments.isEmpty()) {
            System.out.println("------------------------------------------------ No treatments found. ------------------------------------------------");
            return;
        }

        if (!title.isEmpty()) {
            System.out.println(title);
        }
        System.out.println();

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-12s | %-12s | %-12s | %-30s | %-12s | %-20s |%n";

        // Define border line
        String borderLine = "+--------------+--------------+--------------+--------------+--------------------------------+--------------+--------------------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Treatment ID", "Doctor ID", "Patient ID", "Consultation ID", "Description", "Fee", "Medicines");

        // Print header separator
        System.out.println(borderLine);

        // Print each row + row separator
        for (int i = 0; i < treatments.size(); i++) {
            Treatment t = treatments.get(i);
            
            // Get medicines count
            int medicineCount = t.getPrescribedMedicines().size();
            String medicinesInfo = medicineCount + " medicine(s)";
            
            // Print row
            System.out.printf(leftAlignFormat,
                    t.getTreatmentId(),
                    t.getDoctorId(),
                    t.getPatientId(),
                    t.getConsultationId(),
                    t.getDescription().length() > 28 ? t.getDescription().substring(0, 25) + "..." : t.getDescription(),
                    String.format("%.2f", t.getTreatmentFee()),
                    medicinesInfo);

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }

    public void printTreatmentDetails(Treatment treatment) {
        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }

        System.out.println("\n=== TREATMENT DETAILS ===");
        System.out.println("Treatment ID: " + treatment.getTreatmentId());
        System.out.println("Doctor ID: " + treatment.getDoctorId());
        System.out.println("Patient ID: " + treatment.getPatientId());
        System.out.println("Consultation ID: " + treatment.getConsultationId());
        System.out.println("Description: " + treatment.getDescription());
        System.out.println("Treatment Fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
        
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        if (!medicines.isEmpty()) {
            System.out.println("\n--- Prescribed Medicines ---");
            String leftAlignFormat = "| %-12s | %-25s | %-8s | %-10s |%n";
            String borderLine = "+--------------+---------------------------+----------+------------+";
            
            System.out.println(borderLine);
            System.out.printf(leftAlignFormat, "Medicine ID", "Name", "Quantity", "Price");
            System.out.println(borderLine);
            
            for (int i = 0; i < medicines.size(); i++) {
                MedicinePrescribed mp = medicines.get(i);
                Medicine medicine = medicineMap.get(mp.getMedicineId());
                String medicineName = medicine != null ? medicine.getName() : "Unknown";
                double price = medicine != null ? medicine.getPrice() : 0.0;
                
                System.out.printf(leftAlignFormat,
                        mp.getMedicineId(),
                        medicineName,
                        mp.getQuantity(),
                        String.format("%.2f", price));
            }
            System.out.println(borderLine);
        } else {
            System.out.println("\nNo medicines prescribed.");
        }
    }
}
