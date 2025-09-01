package dao;

import entity.Treatment;
import entity.MedicinePrescribed;
import adt.*;

import java.io.*;

/**
 * Data Access Object for Treatment persistence
 * @author Your Name
 */
public class TreatmentDAO {
    private static final String FILE_NAME = "data/treatments.txt";
    private static int treatmentCounter = 1001; // Start from T1001

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring treatments file: " + e.getMessage());
        }
    }

    public static void saveTreatments(HashMapInterface<String, Treatment> treatmentMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Write header comment
            pw.println("# TreatmentID|DoctorID|PatientID|ConsultationID|Description|TreatmentFee|PrescribedMedicines|isDeleted");
            
            for (int i = 0; i < treatmentMap.keySet().size(); i++) {
                String key = treatmentMap.keySet().get(i);
                Treatment t = treatmentMap.get(key);
                if (t != null)
                    pw.println(toFileString(t));
            }
        } catch (IOException e) {
            System.out.println("Error saving treatments: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, Treatment> loadTreatments() {
        ensureFile();
        HashMapInterface<String, Treatment> map = new HashMapADT<>();
        int maxTreatmentNumber = 1000; // Start from 1000

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Treatment t = fromFileString(line);
                if (t != null) {
                    map.put(t.getTreatmentId(), t);
                    
                    // Update counter to ensure unique IDs
                    String treatmentId = t.getTreatmentId();
                    if (treatmentId.startsWith("T")) {
                        try {
                            int number = Integer.parseInt(treatmentId.substring(1));
                            if (number > maxTreatmentNumber) {
                                maxTreatmentNumber = number;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading treatments: " + e.getMessage());
        }
        
        // Set the counter to the next available number
        treatmentCounter = maxTreatmentNumber + 1;

        return map;
    }

    private static String toFileString(Treatment t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getTreatmentId()).append("|");
        sb.append(t.getDoctorId()).append("|");
        sb.append(t.getPatientId()).append("|");
        sb.append(t.getConsultationId()).append("|");
        sb.append(t.getDescription()).append("|");
        sb.append(t.getTreatmentFee()).append("|");
        
        // Add prescribed medicines
        ListInterface<MedicinePrescribed> medicines = t.getPrescribedMedicines();
        if (medicines.isEmpty()) {
            sb.append("NONE");
        } else {
            for (int i = 0; i < medicines.size(); i++) {
                MedicinePrescribed med = medicines.get(i);
                if (i > 0) sb.append(",");
                sb.append(med.getMedicineId()).append(":").append(med.getQuantity());
            }
        }
        
        sb.append("|").append(Boolean.toString(t.isDeleted()));
        
        return sb.toString();
    }

    private static Treatment fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 7) {
                throw new IllegalArgumentException("Expected at least 7 columns, got " + parts.length);
            }
            
            String treatmentId = parts[0];
            String doctorId = parts[1];
            String patientId = parts[2];
            String consultationId = parts[3];
            String description = parts[4];
            double treatmentFee = Double.parseDouble(parts[5]);
            String medicinesStr = parts[6];

            Treatment treatment = new Treatment(treatmentId, doctorId, patientId, consultationId, description, treatmentFee);
            
            // Parse prescribed medicines
            if (!medicinesStr.equals("NONE")) {
                String[] medicinePairs = medicinesStr.split(",");
                for (String pair : medicinePairs) {
                    String[] medParts = pair.split(":");
                    if (medParts.length == 2) {
                        String medicineId = medParts[0];
                        int quantity = Integer.parseInt(medParts[1]);
                        MedicinePrescribed medicine = new MedicinePrescribed(medicineId, quantity);
                        treatment.addPrescribedMedicine(medicine);
                    }
                }
            }
            
            // Parse isDeleted field (optional for backward compatibility)
            if (parts.length > 7) {
                boolean isDeleted = Boolean.parseBoolean(parts[7]);
                treatment.setIsDeleted(isDeleted);
            }
            
            return treatment;
        } catch (Exception e) {
            System.out.println("Error parsing treatment line: " + line + " -> " + e.getMessage());
        }
        return null;
    }

    public static String generateTreatmentId() {
        return "T" + (treatmentCounter++);
    }
}
