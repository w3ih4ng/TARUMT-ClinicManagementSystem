package control;

import entity.*;
import adt.*;
import dao.PatientQueueDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.Scanner;

/**
 * Control class for managing queue history
 * @author Your Name
 */
public class QueueHistoryControl {
    private static final String HISTORY_FILE = "data/queue_history.txt";
    private Scanner sc;

    public QueueHistoryControl() {
        this.sc = new Scanner(System.in);
        // Ensure history file exists
        createHistoryFileIfNotExists();
    }

    /**
     * Store completed queue entry in history
     */
    public void storeCompletedQueueEntry(PatientQueueEntry entry, String completionReason) {
        try {
            // Create history record
            String historyRecord = createHistoryRecord(entry, completionReason);
            
            // Append to history file
            FileWriter fw = new FileWriter(HISTORY_FILE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            
            pw.println(historyRecord);
            pw.close();
            
            System.out.println("✅ Queue entry " + entry.getQueueId() + " stored in history");
            
        } catch (IOException e) {
            System.out.println("❌ Error storing queue entry in history: " + e.getMessage());
        }
    }

    /**
     * Create history record string
     */
    private String createHistoryRecord(PatientQueueEntry entry, String completionReason) {
        StringBuilder record = new StringBuilder();
        
        // Format: queueId|patientId|specialty|queueType|arrivalTime|assignedDoctorId|completionTime|completionReason
        record.append(entry.getQueueId()).append("|");
        record.append(entry.getPatientId()).append("|");
        record.append(entry.getSpecialty()).append("|");
        record.append(entry.getQueueType()).append("|");
        record.append(entry.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("|");
        record.append(entry.getAssignedDoctorId() != null ? entry.getAssignedDoctorId() : "N/A").append("|");
        record.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("|");
        record.append(completionReason);
        
        return record.toString();
    }

    /**
     * View queue history
     */
    public void viewQueueHistory() {
        try {
            File file = new File(HISTORY_FILE);
            if (!file.exists() || file.length() == 0) {
                System.out.println("\nNo queue history found.");
                return;
            }

            System.out.println("\n--- Queue History ---");
            String borderLine = "+---------+------------+---------------------------+----------------------+-----------------+------------+------------+----------------------+";
            System.out.println(borderLine);
            System.out.printf("| %-7s | %-10s | %-25s | %-20s | %-15s | %-10s | %-10s | %-20s |%n",
                    "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Arrival", "Doctor", "Completion Reason");
            System.out.println(borderLine);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            
            while ((line = br.readLine()) != null && count < 50) { // Limit to last 50 entries
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String queueId = parts[0];
                    String patientId = parts[1];
                    String specialty = parts[2];
                    String queueType = parts[3];
                    String arrivalTime = parts[4];
                    String doctorId = parts[5];
                    String completionTime = parts[6];
                    String completionReason = parts[7];
                    
                    // Get patient name (you might want to load patient data here)
                    String patientName = "Unknown"; // Could be enhanced to load from patient data
                    
                    // Format arrival time for display
                    String displayArrivalTime = formatDisplayTime(arrivalTime);
                    
                    System.out.printf("| %-7s | %-10s | %-25s | %-20s | %-15s | %-10s | %-10s | %-20s |%n",
                            queueId, patientId, patientName, specialty, queueType, displayArrivalTime, 
                            doctorId.equals("N/A") ? "N/A" : doctorId, completionReason);
                    System.out.println(borderLine);
                    count++;
                }
            }
            br.close();
            
            if (count == 0) {
                System.out.println("No completed queue entries found in history.");
            }
            
        } catch (IOException e) {
            System.out.println("❌ Error reading queue history: " + e.getMessage());
        }
    }

    /**
     * Format time for display (show only time part)
     */
    private String formatDisplayTime(String fullDateTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(fullDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return fullDateTime;
        }
    }

    /**
     * Search queue history by patient ID
     */
    public void searchHistoryByPatient(String patientId) {
        try {
            File file = new File(HISTORY_FILE);
            if (!file.exists() || file.length() == 0) {
                System.out.println("\nNo queue history found for patient: " + patientId);
                return;
            }

            System.out.println("\n--- Queue History for Patient: " + patientId + " ---");
            String borderLine = "+---------+----------------------+-----------------+------------+------------+----------------------+";
            System.out.println(borderLine);
            System.out.printf("| %-7s | %-20s | %-15s | %-10s | %-10s | %-20s |%n",
                    "QueueID", "Specialty", "Type", "Arrival", "Doctor", "Completion Reason");
            System.out.println(borderLine);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean found = false;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 8 && parts[1].equals(patientId)) {
                    String queueId = parts[0];
                    String specialty = parts[2];
                    String queueType = parts[3];
                    String arrivalTime = parts[4];
                    String doctorId = parts[5];
                    String completionReason = parts[7];
                    
                    String displayArrivalTime = formatDisplayTime(arrivalTime);
                    
                    System.out.printf("| %-7s | %-20s | %-15s | %-10s | %-10s | %-20s |%n",
                            queueId, specialty, queueType, displayArrivalTime, 
                            doctorId.equals("N/A") ? "N/A" : doctorId, completionReason);
                    System.out.println(borderLine);
                    found = true;
                }
            }
            br.close();
            
            if (!found) {
                System.out.println("No history found for patient: " + patientId);
            }
            
        } catch (IOException e) {
            System.out.println("❌ Error searching queue history: " + e.getMessage());
        }
    }

    /**
     * Create history file if it doesn't exist
     */
    private void createHistoryFileIfNotExists() {
        try {
            File file = new File(HISTORY_FILE);
            if (!file.exists()) {
                // Create directory if it doesn't exist
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                
                // Create file with header
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                
                pw.println("# Queue History Data File");
                pw.println("# Format: queueId|patientId|specialty|queueType|arrivalTime|assignedDoctorId|completionTime|completionReason");
                pw.println("# Generated automatically when queue entries are completed");
                pw.close();
                
                System.out.println("✅ Queue history file created: " + HISTORY_FILE);
            }
        } catch (IOException e) {
            System.out.println("❌ Error creating queue history file: " + e.getMessage());
        }
    }

    /**
     * Get total completed consultations count
     */
    public int getTotalCompletedCount() {
        try {
            File file = new File(HISTORY_FILE);
            if (!file.exists()) return 0;
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    count++;
                }
            }
            br.close();
            
            return count;
        } catch (IOException e) {
            return 0;
        }
    }
}
