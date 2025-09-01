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
                        System.out.println("Treatment created successfully: " + treatmentId);
        System.out.println("Medicines prescribed: " + medicines.size() + " medicine(s)");
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
     * Get treatment by ID (excludes deleted treatments)
     */
    public Treatment getTreatmentById(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null && !treatment.isDeleted()) {
            return treatment;
        }
        return null;
    }

    /**
     * Get treatments by consultation ID (excludes deleted treatments)
     */
    public ListInterface<Treatment> getTreatmentsByConsultation(String consultationId) {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (!treatment.isDeleted() && treatment.getConsultationId().equals(consultationId)) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }

    /**
     * Get treatments by doctor ID (excludes deleted treatments)
     */
    public ListInterface<Treatment> getTreatmentsByDoctor(String doctorId) {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (!treatment.isDeleted() && treatment.getDoctorId().equals(doctorId)) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }

    /**
     * Get treatments by patient ID (excludes deleted treatments)
     */
    public ListInterface<Treatment> getTreatmentsByPatient(String patientId) {
        ListInterface<Treatment> treatments = new ArrayList<>();
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (!treatment.isDeleted() && treatment.getPatientId().equals(patientId)) {
                treatments.add(treatment);
            }
        }
        return treatments;
    }
    
    /**
     * Get treatments ready for medicine dispensing
     */
    public ListInterface<Treatment> getTreatmentsReadyForMedicineDispensing() {
        // Refresh consultation data to get latest status updates
        this.consultationMap = ConsultationDAO.loadConsultations();
        // Refresh treatment data as well
        this.treatmentMap = TreatmentDAO.loadTreatments();

        ListInterface<Treatment> treatments = new ArrayList<>();

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            if (!treatment.isDeleted()) {
                String consultationId = treatment.getConsultationId();
                Consultation consultation = consultationMap.get(consultationId);

                if (consultation != null && consultation.getStatus().equals("MEDICINE_PRESCRIBED")) {
                    treatments.add(treatment);
                }
            }
        }

        return treatments;
    }
    
    /**
     * Display treatments ready for medicine dispensing
     */
    public void displayTreatmentsReadyForMedicineDispensing() {
        // Refresh treatment data as well
        this.treatmentMap = TreatmentDAO.loadTreatments();

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
                // Set treatment ID first
                consultation.setTreatmentId(treatmentId);

                // Get the treatment to check if it has medicines
                Treatment treatment = treatmentMap.get(treatmentId);
                if (treatment != null) {
                    boolean hasMedicines = !treatment.getPrescribedMedicines().isEmpty();
                    consultation.updateStatusBasedOnMedicinePrescription(hasMedicines);
                } else {
                    // If treatment not found, default to TREATMENT_CREATED
                    consultation.setStatus("TREATMENT_CREATED");
                }

                consultationMap.put(consultationId, consultation);
                ConsultationDAO.saveConsultations(consultationMap);

                String status = consultation.getStatus();
                System.out.println("Consultation " + consultationId + " status updated to " + status);
            }
        } catch (Exception e) {
            System.out.println("Error updating consultation status: " + e.getMessage());
        }
    }

    /**
     * Update consultation status to MEDICINE_PRESCRIBED when medicines are added
     */
    private void updateConsultationStatusToMedicinePrescribed(String consultationId) {
        try {
            // Refresh consultation data to get latest changes
            consultationMap = ConsultationDAO.loadConsultations();

            Consultation consultation = consultationMap.get(consultationId);
            if (consultation != null) {
                // Update status to MEDICINE_PRESCRIBED
                consultation.setStatus("MEDICINE_PRESCRIBED");
                consultationMap.put(consultationId, consultation);
                ConsultationDAO.saveConsultations(consultationMap);

                System.out.println("Consultation " + consultationId + " status updated to MEDICINE_PRESCRIBED");
            }
        } catch (Exception e) {
            System.out.println("Error updating consultation status to MEDICINE_PRESCRIBED: " + e.getMessage());
        }
    }

    /**
     * Check if treatment can be updated (consultation not completed)
     */
    private boolean canUpdateTreatment(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) {
            return false;
        }
        
        // Check if consultation is completed
        String consultationId = treatment.getConsultationId();
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null && consultation.getStatus().equals("COMPLETED")) {
            System.out.println("Cannot update treatment " + treatmentId + " - consultation is completed and locked.");
            return false;
        }
        return true;
    }

    /**
     * Update treatment diagnosis
     */
    public void updateTreatmentDiagnosis(String treatmentId, String newDiagnosis) {
        if (!canUpdateTreatment(treatmentId)) {
            return;
        }
        
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
        if (!canUpdateTreatment(treatmentId)) {
            return;
        }
        
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
        if (!canUpdateTreatment(treatmentId)) {
            return;
        }

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

            // Update consultation status if medicines were added
            if (!newMedicines.isEmpty()) {
                String consultationId = treatment.getConsultationId();
                updateConsultationStatusToMedicinePrescribed(consultationId);
            }
        }
    }
    
    /**
     * Add medicine to existing treatment
     */
    public boolean addMedicineToTreatment(String treatmentId, String medicineId, int quantity) {
        if (!canUpdateTreatment(treatmentId)) {
            return false;
        }
        
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

        // Update consultation status to MEDICINE_PRESCRIBED
        String consultationId = treatment.getConsultationId();
        updateConsultationStatusToMedicinePrescribed(consultationId);
        
        // Save updated treatment
        treatmentMap.put(treatmentId, treatment);
        TreatmentDAO.saveTreatments(treatmentMap);
        
        return true;
    }
    
    /**
     * Remove medicine from treatment
     */
    public boolean removeMedicineFromTreatment(String treatmentId, String medicineId) {
        if (!canUpdateTreatment(treatmentId)) {
            return false;
        }
        
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
            // Update consultation status based on remaining medicines
            String consultationId = treatment.getConsultationId();
            Consultation consultation = consultationMap.get(consultationId);
            if (consultation != null) {
                boolean hasMedicines = !treatment.getPrescribedMedicines().isEmpty();
                consultation.updateStatusBasedOnMedicinePrescription(hasMedicines);
                ConsultationDAO.saveConsultations(consultationMap);
            }
            
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
        if (!canUpdateTreatment(treatmentId)) {
            return false;
        }
        
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
     * Delete treatment (soft delete)
     */
    public boolean deleteTreatment(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null && !treatment.isDeleted()) {
            // Check if consultation is completed
            String consultationId = treatment.getConsultationId();
            Consultation consultation = consultationMap.get(consultationId);
            if (consultation != null && consultation.getStatus().equals("COMPLETED")) {
                System.out.println("Cannot delete treatment " + treatmentId + " - consultation is completed and locked.");
                return false;
            }
            
            // Soft delete the treatment
            treatment.delete();
            
            // Update consultation status back to IN_PROGRESS
            if (consultation != null) {
                consultation.setStatus("IN_PROGRESS");
                consultation.setTreatmentId(null); // Remove treatment reference
                
                // Save consultation changes
                ConsultationDAO.saveConsultations(consultationMap);
            }
            
            // Save treatment changes
            TreatmentDAO.saveTreatments(treatmentMap);
            
            return true;
        } else if (treatment != null && treatment.isDeleted()) {
            System.out.println("Treatment is already deleted: " + treatmentId);
            return false;
        } else {
            System.out.println("Treatment not found: " + treatmentId);
            return false;
        }
    }

    /**
     * Restore treatment (soft restore)
     */
    public boolean restoreTreatment(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null && treatment.isDeleted()) {
            // Check if consultation was completed before deletion
            String consultationId = treatment.getConsultationId();
            Consultation consultation = consultationMap.get(consultationId);
            if (consultation != null && consultation.getStatus().equals("COMPLETED")) {
                System.out.println("Cannot restore treatment " + treatmentId + " - consultation was completed and should remain locked.");
                return false;
            }
            
            // Restore the treatment
            treatment.restore();
            
            // Update consultation status based on medicine prescription
            if (consultation != null) {
                consultation.setTreatmentId(treatmentId); // Restore treatment reference
                boolean hasMedicines = !treatment.getPrescribedMedicines().isEmpty();
                consultation.updateStatusBasedOnMedicinePrescription(hasMedicines);
                
                // Save consultation changes
                ConsultationDAO.saveConsultations(consultationMap);
            }
            
            // Save treatment changes
            TreatmentDAO.saveTreatments(treatmentMap);
            
            return true;
        } else if (treatment != null && !treatment.isDeleted()) {
            System.out.println("Treatment is already active: " + treatmentId);
            return false;
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
        
        // Update consultation status to COMPLETED
        String consultationId = treatment.getConsultationId();
        Consultation consultation = consultationMap.get(consultationId);
        
        if (consultation != null) {
            // Consultation is already completed when treatment is created
            // No need to change status further
            System.out.println("Medicines dispensed for treatment: " + treatmentId);
            System.out.println("Consultation " + consultationId + " is already completed");
            System.out.println("Treatment ready for medicine dispensing.");
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

        if (consultation != null && consultation.getStatus().equals("COMPLETED")) {
            // Update queue status to COMPLETED as well
            if (consultation.getQueueId() != null) {
                // Refresh queue data and update queue status
                control.PatientQueueController queueController = new control.PatientQueueController();
                entity.PatientQueueEntry queueEntry = queueController.getQueueEntry(consultation.getQueueId());
                if (queueEntry != null) {
                    queueEntry.complete();
                    // Save the updated queue entry
                    queueController.saveQueueData();
                    System.out.println("Queue entry " + consultation.getQueueId() + " status updated to COMPLETED");
                }
            }

            System.out.println("Consultation " + consultationId + " is already completed!");
            System.out.println("Treatment and medicines are ready for processing.");
        } else {
            System.out.println("Consultation must be completed before dispensing medicines.");
        }
    }

    // ==================== TREATMENT DISPLAY OPERATIONS ====================

    /**
     * Display treatment details
     */
    public void displayTreatmentMedicines(String treatmentId) {
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            System.out.println("Treatment not found: " + treatmentId);
            return;
        }

        if (treatment.getPrescribedMedicines().isEmpty()) {
            System.out.println("No medicines prescribed for this treatment.");
            return;
        }

        System.out.println("Medicine Prescriptions for Treatment " + treatmentId + ":");
        System.out.println("=".repeat(60));

        for (int i = 0; i < treatment.getPrescribedMedicines().size(); i++) {
            MedicinePrescribed prescribed = treatment.getPrescribedMedicines().get(i);
            String medicineId = prescribed.getMedicineId();
            int quantity = prescribed.getQuantity();

            // Get medicine details
            Medicine medicine = medicineMap.get(medicineId);
            String medicineName = medicine != null ? medicine.getName() : "Unknown Medicine";

            System.out.println((i + 1) + ". " + medicineName + " (ID: " + medicineId + ") - Quantity: " + quantity);
        }
        System.out.println("=".repeat(60));
    }

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
     * Display all treatments in table format (includes both active and deleted treatments)
     */
    public void displayAllTreatments() {
        displayTreatmentsWithFilter(true); // Show all treatments including deleted
    }

    /**
     * Display only active treatments in table format (excludes soft-deleted treatments)
     */
    public void displayActiveTreatmentsOnly() {
        displayTreatmentsWithFilter(false); // Show only active treatments
    }

    /**
     * Display treatments with optional filter for deleted status
     */
    private void displayTreatmentsWithFilter(boolean includeDeleted) {
        if (treatmentMap.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        String borderLine = "+--------------+------------+------------+------------------+---------------------------+------------+---------------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-12s | %-10s | %-10s | %-16s | %-25s | %-10s | %-19s | %-10s |%n",
                "TreatmentID", "DoctorID", "PatientID", "ConsultationID", "Diagnosis", "Fee", "Medicine Prescribed", "Status");
        System.out.println(borderLine);

        int activeCount = 0;
        int deletedCount = 0;

        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            
            // Skip deleted treatments if not including them
            if (!includeDeleted && treatment.isDeleted()) {
                continue;
            }
            
            String medicineStatus = treatment.getPrescribedMedicines().isEmpty() ? "No" : "Yes";
            String status = treatment.isDeleted() ? "DELETED" : "ACTIVE";
            
            if (treatment.isDeleted()) {
                deletedCount++;
            } else {
                activeCount++;
            }
            
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
        
        if (includeDeleted) {
            System.out.println("Summary: " + activeCount + " active treatments, " + deletedCount + " deleted treatments");
        } else {
            System.out.println("Showing " + activeCount + " active treatments (deleted treatments hidden)");
        }
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

        // Update consultation status to MEDICINE_PRESCRIBED
        String consultationId = treatment.getConsultationId();
        updateConsultationStatusToMedicinePrescribed(consultationId);

        System.out.println("\nMedicine prescription added successfully!");
        System.out.println("Medicine: " + medicineId + ", Quantity: " + quantity);
        System.out.println("Consultation status updated to MEDICINE_PRESCRIBED");
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
    
    // ==================== REPORTING METHODS ====================
    
    /**
     * Generate Diagnosis Statistics Report
     */
    public void generateDiagnosisStatisticsReport() {
        System.out.println("=".repeat(90));
        System.out.println("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY");
        System.out.println("CLINIC MANAGEMENT SYSTEM");
        System.out.println("DIAGNOSIS STATISTICS REPORT");
        System.out.println("=".repeat(90));
        System.out.println();
        
        // Get current timestamp
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy, hh:mm a");
        System.out.println("Generated at: " + now.format(formatter));
        System.out.println();
        
        // Count diagnoses
        HashMapInterface<String, Integer> diagnosisCount = new adt.HashMapADT<>();
        int totalTreatments = 0;
        
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            totalTreatments++;
            String diagnosis = treatment.getDescription();
            Integer currentCount = diagnosisCount.get(diagnosis);
            diagnosisCount.put(diagnosis, currentCount != null ? currentCount + 1 : 1);
        }
        
        // Display diagnosis statistics
        System.out.println("Diagnosis Statistics:");
        System.out.println("-".repeat(70));
        System.out.printf("| %-30s | %-20s | %-15s |%n", "Diagnosis", "Occurrence Count", "Percentage");
        System.out.println("-".repeat(70));
        
        // Find top 5 diagnoses
        String[] topDiagnoses = new String[5];
        int[] topCounts = new int[5];
        
        for (String key : diagnosisCount.keySet()) {
            int count = diagnosisCount.get(key);
            
            // Check if this diagnosis is in top 5
            for (int i = 0; i < 5; i++) {
                if (count > topCounts[i]) {
                    // Shift down
                    for (int j = 4; j > i; j--) {
                        topDiagnoses[j] = topDiagnoses[j-1];
                        topCounts[j] = topCounts[j-1];
                    }
                    topDiagnoses[i] = key;
                    topCounts[i] = count;
                    break;
                }
            }
        }
        
        // Display top 5 diagnoses
        for (int i = 0; i < 5; i++) {
            if (topDiagnoses[i] != null) {
                double percentage = (double)topCounts[i]/totalTreatments*100;
                System.out.printf("| %-30s | %-20d | %-14.1f%% |%n", 
                    topDiagnoses[i], 
                    topCounts[i],
                    percentage);
            }
        }
        System.out.println("-".repeat(70));
        System.out.printf("Total Treatments: %d%n", totalTreatments);
        System.out.println();
        
        // Summary
        System.out.println("Diagnosis Summary:");
        System.out.println("-".repeat(50));
        if (topDiagnoses[0] != null) {
            System.out.println("Most Common Diagnosis: " + topDiagnoses[0] + " (" + topCounts[0] + " occurrences)");
        }
        System.out.println();
        
        System.out.println("=".repeat(90));
        System.out.println("END OF REPORT");
        System.out.println("=".repeat(90));
    }
    
    /**
     * Generate Treatment History Report
     */
    public void generateTreatmentHistoryReport() {
        System.out.println("=".repeat(90));
        System.out.println("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY");
        System.out.println("CLINIC MANAGEMENT SYSTEM");
        System.out.println("TREATMENT HISTORY REPORT");
        System.out.println("=".repeat(90));
        System.out.println();
        
        // Get current timestamp
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy, hh:mm a");
        System.out.println("Generated at: " + now.format(formatter));
        System.out.println();
        
        // Count treatments per patient
        HashMapInterface<String, Integer> patientTreatmentCount = new adt.HashMapADT<>();
        
        for (String key : treatmentMap.keySet()) {
            Treatment treatment = treatmentMap.get(key);
            String patientId = treatment.getPatientId();
            Integer currentCount = patientTreatmentCount.get(patientId);
            patientTreatmentCount.put(patientId, currentCount != null ? currentCount + 1 : 1);
        }
        
        // Analyze treatment patterns
        int singleTreatmentPatients = 0;
        int multipleTreatmentPatients = 0;
        int chronicPatients = 0; // 3+ treatments
        int totalPatients = patientTreatmentCount.size();
        int totalTreatments = treatmentMap.size();
        
        for (String key : patientTreatmentCount.keySet()) {
            int count = patientTreatmentCount.get(key);
            if (count == 1) {
                singleTreatmentPatients++;
            } else if (count == 2) {
                multipleTreatmentPatients++;
            } else {
                chronicPatients++;
            }
        }
        
        // Display treatment history analysis
        System.out.println("Treatment History Analysis:");
        System.out.println("-".repeat(70));
        System.out.printf("| %-25s | %-15s | %-15s | %-15s |%n", "Treatment Pattern", "Patient Count", "Percentage", "Distribution");
        System.out.println("-".repeat(70));
        
        double singlePercentage = totalPatients > 0 ? (double)singleTreatmentPatients/totalPatients*100 : 0.0;
        double multiplePercentage = totalPatients > 0 ? (double)multipleTreatmentPatients/totalPatients*100 : 0.0;
        double chronicPercentage = totalPatients > 0 ? (double)chronicPatients/totalPatients*100 : 0.0;
        
        System.out.printf("| %-25s | %-15d | %-14.1f%% | %-15s |%n", "Single Treatment", singleTreatmentPatients, singlePercentage, "*".repeat(singleTreatmentPatients));
        System.out.printf("| %-25s | %-15d | %-14.1f%% | %-15s |%n", "Multiple Treatments", multipleTreatmentPatients, multiplePercentage, "*".repeat(multipleTreatmentPatients));
        System.out.printf("| %-25s | %-15d | %-14.1f%% | %-15s |%n", "Chronic/Repeated", chronicPatients, chronicPercentage, "*".repeat(chronicPatients));
        System.out.println("-".repeat(70));
        System.out.printf("Total Unique Patients: %d%n", totalPatients);
        System.out.printf("Total Treatments: %d%n", totalTreatments);
        System.out.printf("Average Treatments per Patient: %.2f%n", totalPatients > 0 ? (double)totalTreatments/totalPatients : 0.0);
        System.out.println();
        
        // Summary
        System.out.println("Treatment History Summary:");
        System.out.println("-".repeat(50));
        System.out.printf("Chronic Patient Rate: %.1f%%%n", chronicPercentage);
        System.out.printf("Multiple Treatment Rate: %.1f%%%n", multiplePercentage);
        System.out.printf("Single Treatment Rate: %.1f%%%n", singlePercentage);
        System.out.println();
        
        System.out.println("=".repeat(90));
        System.out.println("END OF REPORT");
        System.out.println("=".repeat(90));
    }
}
