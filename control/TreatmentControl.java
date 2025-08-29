package control;

import entity.*;
import adt.*;
import dao.TreatmentDAO;
import dao.MedicineDAO;
import java.util.Scanner;

/**
 * Control class for treatment management
 * @author Your Name
 */
public class TreatmentControl {
    private HashMapInterface<String, Treatment> treatmentMap;
    private HashMapInterface<String, Medicine> medicineMap;
    private Scanner sc;

    public TreatmentControl() {
        this.treatmentMap = TreatmentDAO.loadTreatments();
        this.medicineMap = MedicineDAO.loadMedicines();
        this.sc = new Scanner(System.in);
    }

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
     * Calculate total cost for a treatment including medicines
     */
    public double calculateTreatmentTotalCost(String treatmentId) {
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment == null) return 0.0;
        
        double total = treatment.getTreatmentFee();
        
        // Add medicine costs
        ListInterface<MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
        for (int i = 0; i < medicines.size(); i++) {
            MedicinePrescribed medicine = medicines.get(i);
            Medicine med = medicineMap.get(medicine.getMedicineId());
            if (med != null) {
                total += medicine.calculateCost(med);
            }
        }
        
        return total;
    }

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
     * Get treatment map for other controls
     */
    public HashMapInterface<String, Treatment> getTreatmentMap() {
        return treatmentMap;
    }

    /**
     * Save treatments
     */
    public void saveTreatments() {
        TreatmentDAO.saveTreatments(treatmentMap);
    }
}
