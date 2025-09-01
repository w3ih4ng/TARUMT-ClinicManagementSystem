package boundary;

import control.PatientController;
import control.DoctorController;
import control.PatientQueueController;
import entity.*;
import adt.*;
import java.util.Scanner;

/**
 * Consolidated Patient UI - combines all patient-related boundary functionality
 * Handles patient management, viewing, and queue operations
 * @author Your Name
 */
public class PatientUI {
    private Scanner sc;
    private PatientController patientController;
    private DoctorController doctorController;
    private PatientQueueController queueController;

    public PatientUI(PatientController patientController, DoctorController doctorController) {
        this.sc = new Scanner(System.in);
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.queueController = new PatientQueueController();
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
        System.out.println("4. Show Active");
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
            System.out.println("=== QUEUE OPERATIONS ===");
            System.out.println("1. Add Walk-in Patient");
            System.out.println("2. Add Scheduled Appointment Patient");
            System.out.println("3. Assign Doctor to Patient");
            System.out.println();
            System.out.println("=== QUEUE VIEWING ===");
            System.out.println("4. View All Queue Entries");
            System.out.println("5. View Waiting Entries");
            System.out.println("6. View Assigned Entries");
            System.out.println("7. View Queue by Specialty");
            System.out.println("8. View Queue by Patient");
            System.out.println();
            System.out.println("0. Back to Patient Management");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addWalkInPatient();
                    break;
                case "2":
                    addScheduledAppointmentPatient();
                    break;
                case "3":
                    assignDoctorToPatient();
                    break;
                case "4":
                    viewAllQueueEntries();
                    break;
                case "5":
                    viewWaitingEntries();
                    break;
                case "6":
                    viewAssignedEntries();
                    break;
                case "7":
                    viewQueueBySpecialty();
                    break;
                case "8":
                    viewQueueByPatient();
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

        // Check if patient exists
        if (!patientController.getPatientMap().containsKey(patientId)) {
            System.out.println("Patient not found. Please register patient first.");
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
            
            String queueId = queueController.addPatientToQueue(patientId, specialty, QueueType.WALK_IN);
            if (queueId != null) {
                System.out.println("Patient added to queue successfully. Queue ID: " + queueId);
            } else {
                System.out.println("Failed to add patient to queue.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
        utility.SystemUtil.pauseForUser();
    }

    /**
     * Add scheduled appointment patient to queue (when patient arrives)
     */
    private void addScheduledAppointmentPatient() {
        System.out.println("\n---------------------------- Add Scheduled Appointment Patient to Queue ----------------------------");
        System.out.println("This will show all today's appointments and allow you to add patients to the queue.");

        // Get ALL today's appointments from all doctors
        ListInterface<entity.DoctorSchedule> todaysAppointments = queueController.getAllTodaysAppointments();

        if (todaysAppointments.isEmpty()) {
            System.out.println("No appointments found for today.");
            utility.SystemUtil.pauseForUser();
            return;
        }

        // Display today's appointments from all doctors
        System.out.println("\n============================== ALL TODAY'S APPOINTMENTS ==============================");
        System.out.println("Date: " + java.time.LocalDate.now());
        System.out.println("-".repeat(100));
        System.out.printf("%-3s %-15s %-10s %-12s %-20s %-20s %-10s%n",
            "No", "Schedule ID", "Doctor ID", "Patient ID", "Specialty", "Time", "Status");
        System.out.println("-".repeat(100));

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        int displayIndex = 1;
        for (int i = 0; i < todaysAppointments.size(); i++) {
            entity.DoctorSchedule schedule = todaysAppointments.get(i);
            
            // Skip missed appointments - they shouldn't be shown for queue addition
            if (schedule.isMissed()) {
                continue;
            }
            
            String patientId = schedule.getPatientId() != null ? schedule.getPatientId() : "N/A";
            String timeSlot = schedule.getTimeSlotString();
            
            // Determine status based on the actual status field
            String status;
            if (schedule.isBooked()) {
                status = "Booked";
            } else if (schedule.isCancelled()) {
                status = "Cancelled";
            } else if (schedule.isCompleted()) {
                status = "Completed";
            } else {
                status = "Available";
            }

            java.time.LocalDateTime appointmentTime = java.time.LocalDateTime.of(
                schedule.getAppointmentDate(),
                schedule.getStartTime()
            );

            System.out.printf("%-3d %-15s %-10s %-12s %-20s %-20s %-10s%n",
                displayIndex, schedule.getScheduleId(), schedule.getDoctorId(), patientId,
                schedule.getSpecialty(), timeSlot, status);
            displayIndex++;
        }

        System.out.println("-".repeat(100));
        System.out.println("Note: You can add patients to booked appointments as long as it's before their appointment time.");

        // Get appointment selection
        System.out.print("\nEnter appointment number to add patient to queue (0 to cancel): ");
        String choice = sc.nextLine().trim();

        try {
            int selectedDisplayIndex = Integer.parseInt(choice);

            if (selectedDisplayIndex == 0) { // User entered 0
                System.out.println("Operation cancelled.");
                utility.SystemUtil.pauseForUser();
                return;
            }

            // Find the actual appointment index based on display index
            int actualAppointmentIndex = -1;
            int currentDisplayIndex = 1;
            
            for (int i = 0; i < todaysAppointments.size(); i++) {
                entity.DoctorSchedule schedule = todaysAppointments.get(i);
                if (!schedule.isMissed()) {
                    if (currentDisplayIndex == selectedDisplayIndex) {
                        actualAppointmentIndex = i;
                        break;
                    }
                    currentDisplayIndex++;
                }
            }

            if (actualAppointmentIndex == -1) {
                System.out.println("Invalid appointment number.");
                utility.SystemUtil.pauseForUser();
                return;
            }

            entity.DoctorSchedule selectedSchedule = todaysAppointments.get(actualAppointmentIndex);

            // Validate the selected appointment
            if (!selectedSchedule.isBooked()) {
                System.out.println("This appointment slot is not booked.");
                utility.SystemUtil.pauseForUser();
                return;
            }

            if (selectedSchedule.getPatientId() == null) {
                System.out.println("This appointment has no patient assigned.");
                utility.SystemUtil.pauseForUser();
                return;
            }

            java.time.LocalDateTime appointmentTime = java.time.LocalDateTime.of(
                selectedSchedule.getAppointmentDate(),
                selectedSchedule.getStartTime()
            );

            // Check if appointment is today and not in the past
            if (appointmentTime.isBefore(now)) {
                System.out.println("This appointment time has already passed.");
                utility.SystemUtil.pauseForUser();
                return;
            }

            // Check if patient is already in queue for this doctor
            String patientId = selectedSchedule.getPatientId();
            String doctorId = selectedSchedule.getDoctorId();
            if (queueController.isPatientInQueue(patientId, doctorId)) {
                System.out.println("Patient " + patientId + " is already in the queue for doctor " + doctorId + ".");
                utility.SystemUtil.pauseForUser();
                return;
            }

            // Add the patient to queue
            String queueId = queueController.addPatientToScheduledAppointmentQueue(
                selectedSchedule.getScheduleId(),
                patientId,
                doctorId,
                selectedSchedule.getSpecialty(),
                appointmentTime
            );

            if (queueId != null) {
                System.out.println("Patient " + patientId + " successfully added to queue!");
                System.out.println("Queue ID: " + queueId);
                System.out.println("Appointment Time: " + appointmentTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("Doctor: " + doctorId);
            } else {
                System.out.println("Failed to add patient to queue.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }

        utility.SystemUtil.pauseForUser();
    }

    /**
     * Assign doctor to patient in queue
     */
    private void assignDoctorToPatient() {
        System.out.println("\n--- Assign Doctor to Patient ---");
        
        // Show waiting entries
        ListInterface<PatientQueueEntry> waitingEntries = queueController.getWaitingEntries();
        if (waitingEntries.isEmpty()) {
            System.out.println("No patients waiting for doctor assignment.");
            return;
        }
        
        System.out.println("Patients waiting for doctor assignment:");
        displayQueueEntries(waitingEntries);
        
        System.out.print("Enter Queue ID to assign doctor: ");
        String queueId = sc.nextLine().trim();
        
        // Get the queue entry to check if it's an appointment
        PatientQueueEntry entry = queueController.getQueueEntry(queueId);
        if (entry == null) {
            System.out.println("Queue entry not found.");
            return;
        }
        
        // For appointment patients, doctor is already fixed based on schedule
        if (entry.getQueueType() == QueueType.APPOINTMENT) {
            System.out.println("This is an appointment patient. Doctor assignment is handled automatically based on schedule.");
            System.out.println("No manual assignment needed.");
            return;
        }
        
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim();
        
        // Check if doctor exists
        if (!doctorController.getDoctorMap().containsKey(doctorId)) {
            System.out.println("Doctor not found.");
            return;
        }
        
        boolean success = queueController.assignDoctor(queueId, doctorId);
        if (success) {
            System.out.println("Doctor assigned successfully.");
        } else {
            System.out.println("Failed to assign doctor. Patient may not be waiting or queue ID invalid.");
        }
        utility.SystemUtil.pauseForUser();
    }





    /**
     * View all queue entries
     */
    private void viewAllQueueEntries() {
        System.out.println("\n--- All Queue Entries ---");
        HashMapInterface<String, PatientQueueEntry> allEntries = queueController.getAllQueueEntries();
        
        if (allEntries.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        
        ListInterface<PatientQueueEntry> entries = new adt.ArrayList<>();
        for (int i = 0; i < allEntries.keySet().size(); i++) {
            String key = allEntries.keySet().get(i);
            PatientQueueEntry entry = allEntries.get(key);
            if (entry != null) {
                entries.add(entry);
            }
        }
        
        displayQueueEntries(entries);
        utility.SystemUtil.pauseForUser();
    }

    /**
     * View waiting entries
     */
    private void viewWaitingEntries() {
        System.out.println("\n--- Waiting Entries ---");
        ListInterface<PatientQueueEntry> waitingEntries = queueController.getWaitingEntries();
        
        if (waitingEntries.isEmpty()) {
            System.out.println("No patients waiting.");
            return;
        }
        
        displayQueueEntries(waitingEntries);
        utility.SystemUtil.pauseForUser();
    }

    /**
     * View assigned entries
     */
    private void viewAssignedEntries() {
        System.out.println("\n--- Assigned Entries ---");
        ListInterface<PatientQueueEntry> assignedEntries = queueController.getAssignedEntries();
        
        if (assignedEntries.isEmpty()) {
            System.out.println("No patients assigned to doctors.");
            return;
        }
        
        displayQueueEntries(assignedEntries);
        utility.SystemUtil.pauseForUser();
    }



    /**
     * View queue entries by specialty
     */
    private void viewQueueBySpecialty() {
        System.out.println("\n--- Queue Entries by Specialty ---");
        System.out.print("Enter specialty: ");
        String specialty = sc.nextLine().trim();
        
        ListInterface<PatientQueueEntry> specialtyEntries = queueController.getQueueEntriesBySpecialty(specialty);
        
        if (specialtyEntries.isEmpty()) {
            System.out.println("No queue entries found for specialty: " + specialty);
            return;
        }
        
        displayQueueEntries(specialtyEntries);
        utility.SystemUtil.pauseForUser();
    }

    /**
     * View queue entries by patient
     */
    private void viewQueueByPatient() {
        System.out.println("\n--- Queue Entries by Patient ---");
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();
        
        ListInterface<PatientQueueEntry> patientEntries = queueController.getQueueEntriesByPatient(patientId);
        
        if (patientEntries.isEmpty()) {
            System.out.println("No queue entries found for patient: " + patientId);
            return;
        }
        
        displayQueueEntries(patientEntries);
        utility.SystemUtil.pauseForUser();
    }

    /**
     * Display queue entries in a formatted table
     */
    private void displayQueueEntries(ListInterface<PatientQueueEntry> entries) {
        if (entries.isEmpty()) {
            System.out.println("No entries to display.");
            return;
        }
        
        // Define table format
        String format = "| %-10s | %-10s | %-20s | %-15s | %-15s | %-20s | %-10s |%n";
        String border = "+------------+------------+----------------------+-----------------+-----------------+----------------------+------------+";
        
        System.out.println(border);
        System.out.printf(format, "Queue ID", "Patient ID", "Specialty", "Queue Type", "Status", "Arrival Time", "Doctor ID");
        System.out.println(border);
        
        for (int i = 0; i < entries.size(); i++) {
            PatientQueueEntry entry = entries.get(i);
            String doctorId = entry.getAssignedDoctorId() != null ? entry.getAssignedDoctorId() : "N/A";
            String arrivalTime = entry.getArrivalTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            System.out.printf(format,
                entry.getQueueId(),
                entry.getPatientId(),
                entry.getSpecialty(),
                entry.getQueueType(),
                entry.getQueueStatus(),
                arrivalTime,
                doctorId);
        }
        System.out.println(border);
    }
}
