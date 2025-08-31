package boundary;

import control.PatientController;
import control.DoctorController;
import entity.*;
import adt.*;
import utility.*;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

/**
 * Consolidated Patient UI - combines all patient-related boundary functionality
 * Handles patient management, viewing, and queue operations
 * @author Your Name
 */
public class PatientUI {
    private Scanner sc;
    private PatientController patientController;
    private DoctorController doctorController;

    public PatientUI(PatientController patientController, DoctorController doctorController) {
        this.sc = new Scanner(System.in);
        this.patientController = patientController;
        this.doctorController = doctorController;
    }

    // ==================== MAIN PATIENT MANAGEMENT MENU ====================

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Patient Management Module");
            
            System.out.println("1. Patient Details Management");
            System.out.println("2. Manage Patient Queue");
            System.out.println("0. Back to Staff Menu");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("Patient Details");
                    patientDetailsManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("Patient Queue");
                    queueManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "0": 
                    return; // back to Staff menu
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== PATIENT DETAILS MANAGEMENT ====================

    private void patientDetailsManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Patient Details Management");
            
            System.out.println("1. Register new patient");
            System.out.println("2. View all patients");
            System.out.println("3. Update patient information");
            System.out.println("4. Delete patient");
            System.out.println("5. Restore patient");
            System.out.println("0. Back to Patient Management");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Register New Patient");
                    patientController.registerPatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View All Patients");
                    viewAllPatients(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Update Patient Information");
                    patientController.updatePatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Delete Patient");
                    patientController.deletePatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Restore Patient");
                    patientController.restorePatient(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return; // back to Patient Management
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== VIEW ALL PATIENTS FUNCTIONALITY ====================

    private void viewAllPatients() {
        HashMapInterface<String, Patient> baseView = patientController.getPatientMap();
        HashMapInterface<String, Patient> currentMap = baseView;
        ListInterface<Patient> currentList = patientController.toList(currentMap);

        while (true) {
            utility.SystemUtil.pushNavigation("View All Patients");
            utility.SystemUtil.showMenuHeader("Patient List");
            
            patientController.printPatientsFromList(currentList);

            System.out.println("\nOptions:");
            System.out.println("1. Filter");
            System.out.println("2. Search");
            System.out.println("3. Sort");
            System.out.println("4. Reset");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Filter Patients");
                    currentMap = handleFilter(sc, currentMap);
                    currentList = patientController.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Search Patients");
                    currentMap = handleSearch(sc, currentMap);
                    currentList = patientController.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Sort Patients");
                    handleSort(sc, currentList);
                    utility.SystemUtil.popNavigation();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Reset View");
                    currentMap = baseView; // reset
                    currentList = patientController.toList(currentMap);
                    patientController.clearCriteria();
                    System.out.println("View reset to show all patients.");
                    utility.SystemUtil.pauseForUser();
                    utility.SystemUtil.popNavigation();
                    break;
                case "0":
                    patientController.clearCriteria();
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private HashMapInterface<String, Patient> handleFilter(Scanner sc, HashMapInterface<String, Patient> map) {
        System.out.println("\nFilter Options:");
        System.out.println("1. Role (Student/Tutor/Staff)");
        System.out.println("2. Gender (M/F)");
        System.out.println("3. Show Deleted");
        System.out.println("4. Do Not Show Deleted");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\nSelect a Role\n1. Student \n2. Tutor \n3. Staff");
                String roleChoice = sc.nextLine().trim();
                return patientController.filterByRole(map, roleChoice);
            case "2":
                System.out.print("\nEnter Gender (M/F): ");
                String gender = sc.nextLine().trim().toUpperCase();
                return patientController.filterByGender(map, gender);
            case "3":
                return patientController.filterShowDeleted(map);
            case "4":
                return patientController.filterNotDeleted(map);
            case "0":
                return map;
            default:
                System.out.println("\nInvalid filter option.");
                return map;
        }
    }

    private HashMapInterface<String, Patient> handleSearch(Scanner sc, HashMapInterface<String, Patient> map) {
        System.out.print("\nEnter keyword to search: ");
        String keyword = sc.nextLine().trim();
        HashMapInterface<String, Patient> results = patientController.searchPatients(map, keyword);
        return results;
    }

    private void handleSort(Scanner sc, ListInterface<Patient> list) {
        System.out.println("\nSort Options:");
        System.out.println("1. Patient ID");
        System.out.println("2. Name");
        System.out.println("3. Gender");
        System.out.println("4. Birthdate");
        System.out.println("Enter 0 to exit.");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        if (choice.equals("0") || choice.isEmpty()) {
            return;
        }

        System.out.println("Sort Order:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Choose: ");
        String orderChoice = sc.nextLine().trim();

        switch (orderChoice) {
            case "1":
                patientController.sortPatients(list, choice); // ascending
                break;
            case "2":
                patientController.reverseSortPatients(list, choice); // descending
                break;
            default:
                System.out.println("Invalid order. Defaulting to ascending.");
                patientController.sortPatients(list, choice);
                break;
        }
    }

    // ==================== PATIENT QUEUE MANAGEMENT ====================

    private void queueManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Patient Queue Management");
            System.out.println("1. Add Walk-in Patient");
            System.out.println("2. View Current Queue");
            System.out.println("3. Assign Doctor to Patient");
            System.out.println("4. Call Next Patient (Doctor View)");
            System.out.println("5. Clear Queue (Reset All Data)");
            System.out.println("0. Back to Patient Management");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addWalkInPatient();
                    break;
                case "2":
                    viewCurrentQueue();
                    break;
                case "3":
                    assignDoctorToPatient();
                    break;
                case "4":
                    callNextPatient();
                    break;
                case "5":
                    clearQueue();
                    break;
                case "0":
                    return; // back to Patient Management
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

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
            
            patientController.addWalkIn(patientId, specialty);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * View current queue with patient names
     */
    private void viewCurrentQueue() {
        System.out.println("\n--- Current Queue ---");
        HashMapInterface<String, PatientQueueEntry> queueMap = patientController.getQueueMap();
        
        if (queueMap.isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }

        // Convert to sorted list
        ListInterface<PatientQueueEntry> sortedQueue = patientController.getSortedQueueEntries();
        
        // Define table format widths
        String leftAlignFormat = "| %-10s | %-12s | %-20s | %-20s | %-10s | %-12s | %-10s | %-12s | %-15s |%n";

        // Define border line
        String borderLine = "+------------+--------------+----------------------+----------------------+------------+--------------+------------+--------------+-----------------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Queue ID", "Patient ID", "Patient Name", "Specialty", "Type", "Status", "Arrival", "Scheduled", "Doctor");

        // Print header separator
        System.out.println(borderLine);

        // Display entries
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < sortedQueue.size(); i++) {
            PatientQueueEntry entry = sortedQueue.get(i);
            String scheduledTime = entry.getScheduledStartTime() != null ? 
                entry.getScheduledStartTime().format(timeFormatter) : "N/A";
            String arrivalTime = entry.getArrivalTime().format(timeFormatter);
            String doctorInfo = entry.isAssigned() ? entry.getAssignedDoctorId() : "Not Assigned";
            
            // Get patient name
            String patientName = "Unknown";
            try {
                Patient patient = patientController.getPatientById(entry.getPatientId());
                if (patient != null) {
                    patientName = patient.getName();
                }
            } catch (Exception e) {
                // Keep as "Unknown" if there's an error
            }
            
            // Print row
            System.out.printf(leftAlignFormat,
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    entry.getQueueStatus(),
                    arrivalTime,
                    scheduledTime,
                    doctorInfo);

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }

    /**
     * Assign doctor to waiting patient
     */
    private void assignDoctorToPatient() {
        System.out.println("\n--- Assign Doctor to Patient ---");
        
        // Refresh doctor data to ensure we have the latest doctors
        patientController.refreshDoctorData();
        
        // Show waiting patients
        ListInterface<PatientQueueEntry> waitingPatients = patientController.getWaitingPatientsForAssignment();
        
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
             ListInterface<Doctor> availableDoctors = patientController.getAvailableDoctorsForSpecialty(selectedEntry.getSpecialty());
             ListInterface<Doctor> differentSpecialtyDoctors = patientController.getAvailableDoctorsFromDifferentSpecialties(selectedEntry.getSpecialty());
             
             if (availableDoctors.isEmpty() && differentSpecialtyDoctors.isEmpty()) {
                 System.out.println("No doctors available");
                 return;
             }
             
             // Show same specialty doctors first
             if (!availableDoctors.isEmpty()) {
                 System.out.println("\n--- Doctors with Same Specialty (" + selectedEntry.getSpecialty() + ") ---");
                 for (int i = 0; i < availableDoctors.size(); i++) {
                     Doctor doctor = availableDoctors.get(i);
                     System.out.println((i + 1) + ". Dr. " + doctor.getName() + 
                         " (" + doctor.getSpecialty() + ") - " + doctor.getDoctorId());
                 }
             }
             
             // Show different specialty doctors
             if (!differentSpecialtyDoctors.isEmpty()) {
                 System.out.println("\n--- Doctors with Different Specialties ---");
                 for (int i = 0; i < differentSpecialtyDoctors.size(); i++) {
                     Doctor doctor = differentSpecialtyDoctors.get(i);
                     System.out.println((availableDoctors.size() + i + 1) + ". Dr. " + doctor.getName() + 
                         " (" + doctor.getSpecialty() + ") - " + doctor.getDoctorId());
                 }
             }
            
             // Select doctor
             int totalDoctors = availableDoctors.size() + differentSpecialtyDoctors.size();
             System.out.print("Select doctor (1-" + totalDoctors + "): ");
             int doctorChoice = Integer.parseInt(sc.nextLine().trim());
             if (doctorChoice < 1 || doctorChoice > totalDoctors) {
                 System.out.println("Invalid doctor selection");
                 return;
             }
             
             Doctor selectedDoctor;
             if (doctorChoice <= availableDoctors.size()) {
                 // Selected from same specialty doctors
                 selectedDoctor = availableDoctors.get(doctorChoice - 1);
             } else {
                 // Selected from different specialty doctors
                 int index = doctorChoice - availableDoctors.size() - 1;
                 selectedDoctor = differentSpecialtyDoctors.get(index);
             }
            patientController.assignPatientToDoctor(selectedEntry.getQueueId(), selectedDoctor.getDoctorId());
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

        /**
     * Show next eligible patient for doctor to call
     */
    private void callNextPatient() {
        System.out.println("\n--- Call Next Patient ---");
        
        // Refresh doctor data to ensure we have the latest doctors
        patientController.refreshDoctorData();
        
        PatientQueueEntry nextPatient = patientController.getNextEligiblePatient();
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
        
        // Check if patient already has a doctor assigned
        if (nextPatient.isAssigned()) {
            System.out.println("Doctor already assigned: " + nextPatient.getAssignedDoctorId());
            System.out.println("Patient can proceed to consultation.");
            return;
        }
        
        // Auto-assign if only one doctor available for this specialty
        ListInterface<Doctor> availableDoctors = patientController.getAvailableDoctorsForSpecialty(nextPatient.getSpecialty());
        ListInterface<Doctor> differentSpecialtyDoctors = patientController.getAvailableDoctorsFromDifferentSpecialties(nextPatient.getSpecialty());
        
        if (availableDoctors.isEmpty() && differentSpecialtyDoctors.isEmpty()) {
            System.out.println("No doctors available for " + nextPatient.getSpecialty());
            System.out.println("Please use 'Assign Doctor to Patient' when a doctor becomes available.");
        } else if (availableDoctors.size() == 1 && differentSpecialtyDoctors.isEmpty()) {
            // Auto-assign if only one same-specialty doctor
            Doctor doctor = availableDoctors.get(0);
            patientController.assignPatientToDoctor(nextPatient.getQueueId(), doctor.getDoctorId());
            System.out.println("Patient automatically assigned to Dr. " + doctor.getName() + " (same specialty)");
            System.out.println("Patient can now proceed to consultation.");
        } else {
            // Multiple doctors available - show options
            System.out.println("\nMultiple doctors available:");
            
            if (!availableDoctors.isEmpty()) {
                System.out.println("\n--- Same Specialty Doctors ---");
                for (int i = 0; i < availableDoctors.size(); i++) {
                    Doctor doctor = availableDoctors.get(i);
                    System.out.println("  " + (i + 1) + ". Dr. " + doctor.getName() + " (" + doctor.getDoctorId() + ")");
                }
            }
            
            if (!differentSpecialtyDoctors.isEmpty()) {
                System.out.println("\n--- Different Specialty Doctors ---");
                for (int i = 0; i < differentSpecialtyDoctors.size(); i++) {
                    Doctor doctor = differentSpecialtyDoctors.get(i);
                    System.out.println("  " + (availableDoctors.size() + i + 1) + ". Dr. " + doctor.getName() + 
                        " (" + doctor.getDoctorId() + ") - " + doctor.getSpecialty());
                }
            }
            
            System.out.println("\n💡 Use 'Assign Doctor to Patient' to choose a specific doctor.");
        }
    }
    
    /**
     * Clear all queue data and start fresh
     */
    private void clearQueue() {
        System.out.println("\n--- Clear Queue ---");
        System.out.println("⚠️  WARNING: This will remove ALL patients from the queue!");
        System.out.println("This action cannot be undone.");
        System.out.print("Are you sure you want to continue? (yes/no): ");
        
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            patientController.clearQueue();
            System.out.println("Queue cleared successfully.");
        } else {
            System.out.println("Queue clearing cancelled.");
        }
    }
}
