package dao;

import entity.PatientQueueEntry;
import entity.QueueType;
import entity.QueueStatus;
import adt.HashMapADT;
import adt.HashMapInterface;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatientQueueDAO {
    private static final String FILE_NAME = "data/patient_queue.txt";
    private static int queueCounter = 1001; // Start from Q1001

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String doctorId = entry.getAssignedDoctorId() != null ? entry.getAssignedDoctorId() : "NONE";
        
        return entry.getQueueId() + "|" + 
               entry.getPatientId() + "|" + 
               entry.getSpecialty() + "|" + 
               entry.getQueueType() + "|" + 
               entry.getArrivalTime().format(formatter) + "|" + 
               entry.getQueueStatus() + "|" + 
               doctorId;
    }

    private static PatientQueueEntry fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length == 7) {
                String queueId = parts[0];
                String patientId = parts[1];
                String specialty = parts[2];
                QueueType queueType = QueueType.valueOf(parts[3]);
                LocalDateTime arrivalTime = LocalDateTime.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                QueueStatus status = QueueStatus.valueOf(parts[5]);
                String doctorId = parts[6].equals("NONE") ? null : parts[6];

                PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, queueType);
                entry.setQueueStatus(status);
                if (doctorId != null) {
                    entry.setAssignedDoctorId(doctorId);
                }
                return entry;
            }
        } catch (Exception e) {
            System.out.println("Error parsing patient queue entry: " + line);
        }
        return null;
    }

    public static String generateQueueId() {
        return "Q" + (queueCounter++);
    }
}
