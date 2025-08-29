package dao;

import entity.Consultation;
import adt.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for Consultation persistence
 * @author Your Name
 */
public class ConsultationDAO {
    private static final String FILE_NAME = "data/consultations.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static int consultationCounter = 1001; // Start from C1001

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring consultations file: " + e.getMessage());
        }
    }

    public static void saveConsultations(HashMapInterface<String, Consultation> consultationMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < consultationMap.keySet().size(); i++) {
                String key = consultationMap.keySet().get(i);
                Consultation c = consultationMap.get(key);
                if (c != null)
                    pw.println(toFileString(c));
            }
        } catch (IOException e) {
            System.out.println("Error saving consultations: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, Consultation> loadConsultations() {
        ensureFile();
        HashMapInterface<String, Consultation> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Consultation c = fromFileString(line);
                if (c != null)
                    map.put(c.getConsultationId(), c);
            }
        } catch (IOException e) {
            System.out.println("Error loading consultations: " + e.getMessage());
        }

        // Initialize counter from existing consultations
        initCounterFromMap(map);

        return map;
    }

    private static void initCounterFromMap(HashMapInterface<String, Consultation> map) {
        int max = 1000; // Start from C1001
        for (String key : map.keySet()) {
            Consultation c = map.get(key);
            if (c != null) {
                String id = c.getConsultationId();
                try {
                    int num = Integer.parseInt(id.substring(1)); // Remove 'C' prefix
                    if (num > max) max = num;
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        consultationCounter = max + 1;
    }

    private static String toFileString(Consultation c) {
        String scheduleId = c.getScheduleId() != null ? c.getScheduleId() : "NONE";
        String treatmentId = c.getTreatmentId() != null ? c.getTreatmentId() : "NONE";
        String paymentId = c.getPaymentId() != null ? c.getPaymentId() : "NONE";
        String consultationTime = c.getConsultationTime() != null ? 
            c.getConsultationTime().format(formatter) : "NONE";
        String queueId = c.getQueueId() != null ? c.getQueueId() : "NONE"; // new
        return String.join("|",
                c.getConsultationId(),
                c.getPatientId(),
                c.getDoctorId() != null ? c.getDoctorId() : "NONE",
                c.getSpecialty(),
                scheduleId,
                consultationTime,
                treatmentId,
                paymentId,
                c.getStatus(),
                queueId); // column 10
    }

    private static Consultation fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 10) {
                throw new IllegalArgumentException("Expected 10 columns, got " + parts.length);
            }
            
            String consultationId = parts[0];
            String patientId = parts[1];
            String doctorId = parts[2].equals("NONE") ? null : parts[2];
            String specialty = parts[3];
            String scheduleId = parts[4].equals("NONE") ? null : parts[4];
            LocalDateTime consultationTime = parts[5].equals("NONE") ? null : 
                LocalDateTime.parse(parts[5], formatter);
            String treatmentId = parts[6].equals("NONE") ? null : parts[6];
            String paymentId = parts[7].equals("NONE") ? null : parts[7];
            String status = parts[8];
            String queueId = parts[9].equals("NONE") ? null : parts[9];

            Consultation c = new Consultation(consultationId, patientId, specialty, queueId);
            if (doctorId != null && consultationTime != null) {
                c.assignDoctor(doctorId, scheduleId, consultationTime);
            }
            if (treatmentId != null) c.completeConsultation(treatmentId);
            if (paymentId != null) c.setPayment(paymentId);
            return c;
        } catch (Exception e) {
            System.out.println("Error parsing consultation line: " + line + " -> " + e.getMessage());
        }
        return null;
    }

    public static String generateConsultationId() {
        return "C" + (consultationCounter++);
    }
}
