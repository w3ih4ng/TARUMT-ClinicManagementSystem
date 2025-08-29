package dao;

import entity.PatientQueueEntry;
import entity.QueueType;
import entity.QueueStatus;
import adt.HashMapADT;
import adt.HashMapInterface;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for PatientQueue persistence
 * @author Your Name
 */
public class PatientQueueDAO {
    private static final String FILE_NAME = "data/patient_queue.txt";
    private static int queueCounter = 1001; // Start from Q1001
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring patient queue file: " + e.getMessage());
        }
    }

    public static void savePatientQueue(HashMapInterface<String, PatientQueueEntry> queueMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < queueMap.keySet().size(); i++) {
                String key = queueMap.keySet().get(i);
                PatientQueueEntry entry = queueMap.get(key);
                if (entry != null)
                    pw.println(toFileString(entry));
            }
        } catch (IOException e) {
            System.out.println("Error saving patient queue: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, PatientQueueEntry> loadPatientQueue() {
        ensureFile();
        HashMapInterface<String, PatientQueueEntry> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                PatientQueueEntry entry = fromFileString(line);
                if (entry != null)
                    map.put(entry.getQueueId(), entry);
            }
        } catch (IOException e) {
            System.out.println("Error loading patient queue: " + e.getMessage());
        }

        return map;
    }

    private static String toFileString(PatientQueueEntry entry) {
        String schTime = entry.getScheduledStartTime() == null ? "NONE" : entry.getScheduledStartTime().format(FMT);
        String doctorId = entry.getAssignedDoctorId() != null ? entry.getAssignedDoctorId() : "NONE";
        return String.join("|",
                entry.getQueueId(),
                entry.getPatientId(),
                entry.getSpecialty(),
                entry.getQueueType().name(),
                entry.getArrivalTime().format(FMT),
                schTime,
                entry.getQueueStatus().name(),
                doctorId);
    }

    private static PatientQueueEntry fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 8) {
                throw new IllegalArgumentException("Expected 8 columns, got " + parts.length);
            }
            
            String queueId = parts[0];
            String patientId = parts[1];
            String specialty = parts[2];
            QueueType queueType = QueueType.valueOf(parts[3]);
            LocalDateTime arrivalTime = LocalDateTime.parse(parts[4], FMT);
            LocalDateTime scheduled = parts[5].equals("NONE") ? null : LocalDateTime.parse(parts[5], FMT);
            QueueStatus status = QueueStatus.valueOf(parts[6]);
            String doctorId = parts[7].equals("NONE") ? null : parts[7];

            PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, queueType, arrivalTime);
            entry.setScheduledStartTime(scheduled);
            entry.setQueueStatus(status);
            if (doctorId != null) entry.setAssignedDoctorId(doctorId);
            return entry;
        } catch (Exception e) {
            System.out.println("Error parsing patient queue entry: " + line + " -> " + e.getMessage());
        }
        return null;
    }

    public static String generateQueueId() {
        return "Q" + (queueCounter++);
    }
}
