package boundary;

import control.PatientQueueControl;
import control.DoctorRecordControl;
import entity.*;
import adt.*;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

/**
 * Boundary class for patient queue management interface
 * @author Your Name
 */
public class PatientQueueBoundary {
    private Scanner sc;
    private PatientQueueControl queueControl;
    private DoctorRecordControl doctorControl;
    private control.QueueHistoryControl historyControl;

    public PatientQueueBoundary(PatientQueueControl queueControl, DoctorRecordControl doctorControl) {
        this.sc = new Scanner(System.in);
        this.queueControl = queueControl;
        this.doctorControl = doctorControl;
        this.historyControl = new control.QueueHistoryControl();
    }

    public void menu() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   Patient Queue Management ");
            System.out.println("=================================");
            System.out.println("1. Add Walk-in Patient");
            System.out.println("2. View Current Queue (Sorted)");
            System.out.println("3. Assign Doctor to Patient");
            System.out.println("4. Call Next Patient (Doctor View)");
            System.out.println("5. View Queue History");
            System.out.println("0. Back to Patient Management");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addWalkInPatient();
                    break;
                case "2":
                    viewSortedQueue();
                    break;
                case "3":
                    assignDoctorToPatient();
                    break;
                case "4":
                    callNextPatient();
                    break;
                case "5":
                    viewQueueHistory();
                    break;
                case "0":
                    return; // back to Patient Management
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // === NEW METHODS ===

    /**
     * Add walk-in patient interface
     */
    private void addWalkInPatient() {
        System.out.println("\n--- Add Walk-in Patient ---");
        
        // Get patient ID
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        if (patientId.isEmpty()) {
            System.out.println("Patient ID cannot be empty");
            return;
        }

        // Get specialty
        System.out.println("\nAvailable Specialties:");
        Specialty[] specialties = Specialty.values();
        for (int i = 0; i < specialties.length; i++) {
            System.out.println((i + 1) + ". " + specialties[i]);
        }
        System.out.print("Select specialty (1-" + specialties.length + "): ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice < 1 || choice > specialties.length) {
                System.out.println("Invalid specialty choice");
                return;
            }
            String specialty = specialties[choice - 1].toString();
            
            queueControl.addWalkIn(patientId, specialty);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * View queue sorted by scheduled time and arrival time
     */
    private void viewSortedQueue() {
        System.out.println("\n--- Current Queue (Sorted) ---");
        HashMapInterface<String, PatientQueueEntry> queueMap = queueControl.getQueueMap();
        
        if (queueMap.isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }

        // Convert to sorted list
        ListInterface<PatientQueueEntry> sortedQueue = queueControl.getSortedQueueEntries();
        
        // Display header
        System.out.println("\n" + String.format("%-8s %-12s %-15s %-12s %-12s %-10s %-12s",
            "Queue ID", "Patient ID", "Specialty", "Type", "Status", "Arrival", "Scheduled"));
        System.out.println("-".repeat(95));
        
        // Display entries
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < sortedQueue.size(); i++) {
            PatientQueueEntry entry = sortedQueue.get(i);
            String scheduledTime = entry.getScheduledStartTime() != null ? 
                entry.getScheduledStartTime().format(timeFormatter) : "N/A";
            String arrivalTime = entry.getArrivalTime().format(timeFormatter);
            
            System.out.println(String.format("%-8s %-12s %-15s %-12s %-10s %-12s",
                entry.getQueueId(),
                entry.getPatientId(),
                entry.getSpecialty(),
                entry.getQueueType(),
                entry.getQueueStatus(),
                arrivalTime,
                scheduledTime));
        }
    }

    /**
     * Assign doctor to waiting patient
     */
    private void assignDoctorToPatient() {
        System.out.println("\n--- Assign Doctor to Patient ---");
        
        // Show waiting patients
        ListInterface<PatientQueueEntry> waitingPatients = queueControl.getWaitingPatientsForAssignment();
        
        if (waitingPatients.isEmpty()) {
            System.out.println("No patients waiting for doctor assignment");
            return;
        }
        
        // Display waiting patients
        System.out.println("\nWaiting Patients:");
        for (int i = 0; i < waitingPatients.size(); i++) {
            PatientQueueEntry entry = waitingPatients.get(i);
            System.out.println((i + 1) + ". " + entry.getQueueId() + " - " +
                entry.getPatientId() + " (" + entry.getSpecialty() + ")");
        }
        
        // Select patient
        System.out.print("Select patient (1-" + waitingPatients.size() + "): ");
        try {
            int patientChoice = Integer.parseInt(sc.nextLine().trim());
            if (patientChoice < 1 || patientChoice > waitingPatients.size()) {
                System.out.println("Invalid patient selection");
                return;
            }
            
            PatientQueueEntry selectedEntry = waitingPatients.get(patientChoice - 1);
            
            // Show available doctors for specialty
            ListInterface<Doctor> availableDoctors = queueControl.getAvailableDoctorsForSpecialty(selectedEntry.getSpecialty());
            
            if (availableDoctors.isEmpty()) {
                System.out.println("No doctors available");
                return;
            }
            
            System.out.println("\nAvailable Doctors:");
            for (int i = 0; i < availableDoctors.size(); i++) {
                Doctor doctor = availableDoctors.get(i);
                System.out.println((i + 1) + ". Dr. " + doctor.getName() + 
                    " (" + doctor.getSpecialty() + ") - " + doctor.getDoctorId());
            }
            
            // Select doctor
            System.out.print("Select doctor (1-" + availableDoctors.size() + "): ");
            int doctorChoice = Integer.parseInt(sc.nextLine().trim());
            if (doctorChoice < 1 || doctorChoice > availableDoctors.size()) {
                System.out.println("Invalid doctor selection");
                return;
            }
            
            Doctor selectedDoctor = availableDoctors.get(doctorChoice - 1);
            queueControl.assignPatientToDoctor(selectedEntry.getQueueId(), selectedDoctor.getDoctorId());
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * Show next eligible patient for doctor to call
     */
    private void callNextPatient() {
        System.out.println("\n--- Call Next Patient ---");
        
        PatientQueueEntry nextPatient = queueControl.getNextEligiblePatient();
        if (nextPatient == null) {
            System.out.println("No eligible patients to call at this time");
            System.out.println("Note: Appointment patients can only be called at or after their scheduled time");
            return;
        }
        
        System.out.println("Next Patient to Call:");
        System.out.println("Queue ID: " + nextPatient.getQueueId());
        System.out.println("Patient ID: " + nextPatient.getPatientId());
        System.out.println("Specialty: " + nextPatient.getSpecialty());
        System.out.println("Type: " + nextPatient.getQueueType());
        
        if (nextPatient.getScheduledStartTime() != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            System.out.println("Scheduled Time: " + nextPatient.getScheduledStartTime().format(timeFormatter));
        }
        
        System.out.println("\nPatient is ready to be assigned to a doctor.");
    }

    /**
     * View queue history
     */
    private void viewQueueHistory() {
        System.out.println("\n--- Queue History ---");
        historyControl.viewQueueHistory();
    }


}
