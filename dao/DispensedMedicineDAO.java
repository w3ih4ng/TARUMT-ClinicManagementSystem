package dao;

import entity.DispensedMedicine;
import adt.HashMapInterface;
import adt.HashMapADT;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DAO class for dispensed medicine data persistence
 * @author Your Name
 */
public class DispensedMedicineDAO {
    private static final String FILE_PATH = "data/dispensed_medicines.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static int dispenseCounter = 1000; // Start from DISP1000

    /**
     * Load dispensed medicines from file
     */
    public static HashMapInterface<String, DispensedMedicine> loadDispensedMedicines() {
        HashMapInterface<String, DispensedMedicine> dispensedMedicineMap = new HashMapADT<>();
        

        
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                try {
                    DispensedMedicine dispensedMedicine = fromFileString(line);
                    if (dispensedMedicine != null) {
                        dispensedMedicineMap.put(dispensedMedicine.getDispenseId(), dispensedMedicine);
                        
                        // Update counter
                        try {
                            int num = Integer.parseInt(dispensedMedicine.getDispenseId().substring(4));
                            if (num >= dispenseCounter) {
                                dispenseCounter = num + 1;
                            }
                        } catch (NumberFormatException e) {
                            // ignore invalid IDs
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing dispensed medicine line: " + line + " -> " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, create it with header
            createFileWithHeader();
        } catch (IOException e) {
            System.out.println("Error reading dispensed medicines file: " + e.getMessage());
        }
        
        return dispensedMedicineMap;
    }

    /**
     * Save dispensed medicines to file
     */
    public static void saveDispensedMedicines(HashMapInterface<String, DispensedMedicine> dispensedMedicineMap) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            // Write header
            pw.println("# DispenseID|TreatmentID|ConsultationID|PatientID|MedicineID|Quantity|UnitPrice|TotalPrice|DispenseDate|StockID|isDeleted");
            
            // Write data
            for (String key : dispensedMedicineMap.keySet()) {
                DispensedMedicine dispensedMedicine = dispensedMedicineMap.get(key);
                if (dispensedMedicine != null) {
                    pw.println(toFileString(dispensedMedicine));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving dispensed medicines: " + e.getMessage());
        }
    }

    /**
     * Generate new dispense ID
     */
    public static String generateDispenseId() {
        String id;
        do {
            id = String.format("DISP%04d", dispenseCounter++);
        } while (false); // We'll check uniqueness when saving
        return id;
    }

    /**
     * Convert DispensedMedicine to file string format
     */
    private static String toFileString(DispensedMedicine dm) {
        return String.format("%s|%s|%s|%s|%s|%d|%.2f|%.2f|%s|%s|%s",
            dm.getDispenseId(),
            dm.getTreatmentId(),
            dm.getConsultationId(),
            dm.getPatientId(),
            dm.getMedicineId(),
            dm.getQuantity(),
            dm.getUnitPrice(),
            dm.getTotalPrice(),
            dm.getDispenseDateString(),
            dm.getStockId(),
            Boolean.toString(dm.isDeleted())
        );
    }

    /**
     * Convert file string to DispensedMedicine object
     */
    private static DispensedMedicine fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 11) {
                System.out.println("Invalid dispensed medicine line format: " + line + " (expected 11 parts, got " + parts.length + ")");
                return null;
            }
            
            String dispenseId = parts[0];
            String treatmentId = parts[1];
            String consultationId = parts[2];
            String patientId = parts[3];
            String medicineId = parts[4];
            int quantity = Integer.parseInt(parts[5]);
            double unitPrice = Double.parseDouble(parts[6]);
            double totalPrice = Double.parseDouble(parts[7]);
            
            LocalDateTime dispenseDate = LocalDateTime.parse(parts[8], DATE_FORMATTER);
            
            String stockId = parts[9];
            boolean isDeleted = Boolean.parseBoolean(parts[10]);
            
            return new DispensedMedicine(dispenseId, treatmentId, consultationId, patientId,
                                       medicineId, quantity, unitPrice, totalPrice,
                                       dispenseDate, stockId, isDeleted);
        } catch (Exception e) {
            System.out.println("Error parsing dispensed medicine line: " + line + " -> " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create file with header if it doesn't exist
     */
    private static void createFileWithHeader() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("# DispenseID|TreatmentID|ConsultationID|PatientID|MedicineID|Quantity|UnitPrice|TotalPrice|DispenseDate|StockID|isDeleted");
        } catch (IOException e) {
            System.out.println("Error creating dispensed medicines file: " + e.getMessage());
        }
    }
}
