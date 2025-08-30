package control;

import entity.*;
import adt.*;
import dao.PatientDAO;
import dao.PatientQueueDAO;
import dao.DoctorDAO;
import utility.FilterCriteriaUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Consolidated Patient Controller - combines all patient-related control logic
 * Handles patient records, queue management, and viewing operations
 * @author Your Name
 */
public class PatientController {
    private HashMapInterface<String, Patient> patientMap; // key = patientId
    private ListInterface<String> patientQueue; // store patient IDs in order
    private HashMapInterface<String, PatientQueueEntry> queueMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private ConsultationController consultationControl;
    private Scanner sc;
    private int patientCounter = 1000; // re-initialized later
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public PatientController() {
        this.patientMap = PatientDAO.loadPatients();
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    public PatientController(ConsultationController consultationControl) {
        this.patientMap = PatientDAO.loadPatients();
        this.queueMap = PatientQueueDAO.loadPatientQueue();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.consultationControl = consultationControl;
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    // ==================== PATIENT RECORD MANAGEMENT (from PatientRecordControl) ====================

    // Initialize counter by scanning existing patient IDs
    public void initCounterFromMap() {
        int max = 999; // so first patient will be P1000
        ListInterface<String> keys = patientMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i); // e.g., "P1050"
            try {
                int num = Integer.parseInt(key.substring(1)); // remove 'P'
                if (num > max) {
                    max = num;
                }
            } catch (NumberFormatException e) {
                // ignore malformed IDs
            }
        }
        patientCounter = max + 1;
    }

    // Generate new Patient ID
    private String generatePatientId() {
        String id;
        do {
            id = "P" + (patientCounter++);
        } while (patientMap.containsKey(id));
        return id;
    }

    public void registerPatient() {
        System.out.println("Type 'exit' at any point to cancel.\n");

        // --- Name ---
        String name = "";
        while (true) {
            System.out.print("Name: ");
            name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("exit"))
                return;
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Name cannot be empty. Please enter a valid name.");
        }

        // --- Gender validation ---
        String gender = "";
        while (true) {
            System.out.print("Gender (M/F): ");
            gender = sc.nextLine().trim().toUpperCase();
            if (gender.equalsIgnoreCase("exit"))
                return;
            if (gender.equals("M") || gender.equals("F"))
                break;
            System.out.println("Invalid gender. Please enter M or F.");
        }

        // --- Birthdate validation ---
        LocalDate birthdate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Birthdate (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit"))
                return;
            try {
                birthdate = LocalDate.parse(input, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid format! Please use yyyy-MM-dd.");
            }
        }

        // --- Phone number validation ---
        String phone = "";
        while (true) {
            System.out.print("Phone (digits only): ");
            phone = sc.nextLine().trim();
            if (phone.equalsIgnoreCase("exit"))
                return;
            if (phone.matches("\\d{7,}"))
                break;
            System.out.println("Invalid phone number. Use digits only, min 7 digits.");
        }

        // --- Role selection ---
        String roleChoice = "";
        while (true) {
            System.out.println("Role: \n1. Student  \n2. Tutor  \n3. Staff");
            System.out.print("Choose: ");
            roleChoice = sc.nextLine().trim();
            if (roleChoice.equalsIgnoreCase("exit"))
                return;
            if (roleChoice.equals("1") || roleChoice.equals("2") || roleChoice.equals("3"))
                break;
            System.out.println("Invalid choice. Enter 1, 2, or 3.");
        }

        Patient patient = null;
        String patientId = generatePatientId();

        switch (roleChoice) {
            case "1": // Student
                String studentId = "";
                while (true) {
                    System.out.print("Student ID: ");
                    studentId = sc.nextLine().trim();
                    if (studentId.equalsIgnoreCase("exit"))
                        return;
                    if (!studentId.isEmpty()) {
                        break;
                    }
                    System.out.println("Student ID cannot be empty. Please enter a valid Student ID.");
                }
                patient = new Student(patientId, studentId, name, gender, birthdate, phone);
                break;

            case "2": // Tutor
                String tutorId = "";
                while (true) {
                    System.out.print("Tutor ID: ");
                    tutorId = sc.nextLine().trim();
                    if (tutorId.equalsIgnoreCase("exit"))
                        return;
                    if (!tutorId.isEmpty()) {
                        break;
                    }
                    System.out.println("Tutor ID cannot be empty. Please enter a valid Tutor ID.");
                }
                
                String faculty = "";
                while (true) {
                    System.out.print("Faculty: ");
                    faculty = sc.nextLine().trim();
                    if (faculty.equalsIgnoreCase("exit"))
                        return;
                    if (!faculty.isEmpty()) {
                        break;
                    }
                    System.out.println("Faculty cannot be empty. Please enter a valid Faculty.");
                }
                patient = new Tutor(patientId, tutorId, name, gender, birthdate, phone, faculty);
                break;

            case "3": // Staff
                String staffId = "";
                while (true) {
                    System.out.print("Staff ID: ");
                    staffId = sc.nextLine().trim();
                    if (staffId.equalsIgnoreCase("exit"))
                        return;
                    if (!staffId.isEmpty()) {
                        break;
                    }
                    System.out.println("Staff ID cannot be empty. Please enter a valid Staff ID.");
                }
                
                String department = "";
                while (true) {
                    System.out.print("Department: ");
                    department = sc.nextLine().trim();
                    if (department.equalsIgnoreCase("exit"))
                        return;
                    if (!department.isEmpty()) {
                        break;
                    }
                    System.out.println("Department cannot be empty. Please enter a valid Department.");
                }
                patient = new Staff(patientId, staffId, name, gender, birthdate, phone, department);
                break;
        }

        patientMap.put(patientId, patient);
        PatientDAO.savePatients(patientMap); // save immediately
        System.out.println("\nPatient registered successfully! Patient ID: " + patientId);
    }

    // patientmap
    public HashMapInterface<String, Patient> getPatientMap() {
        return this.patientMap;
    }

    public void printPatientsTable(ListInterface<Patient> patients, String criteriaSummary) {
        if (patients.isEmpty()) {
            System.out.println(
                    "------------------------------------------------ No patients found. ------------------------------------------------");
            return;
        }

        if (!criteriaSummary.isEmpty()) {
            System.out.println(criteriaSummary);
        } else {
            System.out.println(
                    "------------------------------------------------ No active filter ------------------------------------------------");
        }
        System.out.println();

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-15s | %-6s | %-10s | %-12s | %-8s | %-12s | %-12s | %-15s |%n";

        // Define border line
        String borderLine = "+--------------+-----------------+--------+------------+--------------+----------+--------------+--------------+-----------------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Patient ID", "Name", "Gender", "Birthdate", "Phone", "Role", "Role ID", "Faculty", "Department");

        // Print header separator
        System.out.println(borderLine);

        // Print each row + row separator
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);

            String role = "-", roleId = "-", faculty = "-", department = "-";

            if (p instanceof Student) {
                role = "Student";
                roleId = ((Student) p).getStudentId();
            } else if (p instanceof Tutor) {
                role = "Tutor";
                roleId = ((Tutor) p).getTutorId();
                faculty = ((Tutor) p).getFaculty();
            } else if (p instanceof Staff) {
                role = "Staff";
                roleId = ((Staff) p).getStaffId();
                department = ((Staff) p).getDepartment();
            }

            // Print row
            System.out.printf(leftAlignFormat,
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getBirthdate(),
                    p.getPhoneNumber(),
                    role,
                    roleId,
                    faculty,
                    department);

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }

    public void updatePatient() {
        System.out.print("\nEnter Patient ID to update: ");
        String id = sc.nextLine().trim();
        Patient p = patientMap.get(id);

        if (p == null || p.isDeleted()) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.println("\nUpdating patient:");

        patientDetailsTable(p);

        // --- Name ---
        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            p.setName(name);
            System.out.println("Name updated.");
        }

        // --- Gender ---
        System.out.print("New gender (M/F, leave blank to keep): ");
        String gender = sc.nextLine().trim().toUpperCase();
        if (!gender.isEmpty()) {
            if (gender.equals("M") || gender.equals("F")) {
                p.setGender(gender);
                System.out.println("Gender updated.");
            } else {
                System.out.println("Invalid gender. Not updated.");
            }
        }

        // --- Birthdate ---
        System.out.print("New birthdate (yyyy-mm-dd, leave blank to keep): ");
        String birthdate = sc.nextLine().trim();
        if (!birthdate.isEmpty()) {
            try {
                p.setBirthdate(java.time.LocalDate.parse(birthdate));
                System.out.println("Birthdate updated.");
            } catch (Exception e) {
                System.out.println("Invalid date format. Not updated.");
            }
        }

        // --- Phone ---
        System.out.print("New phone number (leave blank to keep): ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty()) {
            if (phone.matches("\\d{7,}")) {
                p.setPhoneNumber(phone);
                System.out.println("Phone number updated.");
            } else {
                System.out.println("Invalid phone number. Not updated.");
            }
        }

        // --- Role-specific fields ---
        if (p instanceof Student) {
            Student s = (Student) p;
            System.out.print("New student ID (leave blank to keep): ");
            String sid = sc.nextLine().trim();
            if (!sid.isEmpty()) {
                s.setStudentId(sid);
                System.out.println("Student ID updated.");
            }

        } else if (p instanceof Tutor) {
            Tutor t = (Tutor) p;
            System.out.print("New tutor ID (leave blank to keep): ");
            String tid = sc.nextLine().trim();
            if (!tid.isEmpty()) {
                t.setTutorId(tid);
                System.out.println("Tutor ID updated.");
            }

            System.out.print("New faculty (leave blank to keep): ");
            String faculty = sc.nextLine().trim();
            if (!faculty.isEmpty()) {
                t.setFaculty(faculty);
                System.out.println("Faculty updated.");
            }

        } else if (p instanceof Staff) {
            Staff st = (Staff) p;
            System.out.print("New staff ID (leave blank to keep): ");
            String stid = sc.nextLine().trim();
            if (!stid.isEmpty()) {
                st.setStaffId(stid);
                System.out.println("Staff ID updated.");
            }

            System.out.print("New department (leave blank to keep): ");
            String dept = sc.nextLine().trim();
            if (!dept.isEmpty()) {
                st.setDepartment(dept);
                System.out.println("Department updated.");
            }
        }

        // --- Save changes ---
        PatientDAO.savePatients(patientMap);
        System.out.println("\nPatient updated successfully.");
    }

    public void deletePatient() {
        System.out.print("\nEnter Patient ID to delete: ");
        String id = sc.nextLine().trim();
        Patient p = patientMap.get(id);

        if (p == null || p.isDeleted()) {
            System.out.println("Patient not found.");
            return;
        }

        patientDetailsTable(p);

        // Confirm deletion
        System.out.print("Are you sure you want to delete this patient? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            p.delete();
            PatientDAO.savePatients(patientMap);
            System.out.println("Patient deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public void restorePatient() {
        System.out.print("\nEnter Patient ID to restore: ");
        String id = sc.nextLine().trim();
        Patient p = patientMap.get(id);

        if (p == null) {
            System.out.println("Patient not found.");
            return;
        }

        if (!p.isDeleted()) {
            System.out.println("Patient is already active.");
            return;
        }

        p.restore();
        PatientDAO.savePatients(patientMap);
        System.out.println("Patient restored successfully.");
    }

    public void patientDetailsTable(Patient p) {
        System.out.println("\nPatient details:");

        if (p instanceof Student) {
            System.out.println("+------------+----------------+--------+------------+----------------+------------+");
            System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-10s |%n",
                    "Patient ID", "Name", "Gender", "Birthdate", "Phone", "Student ID");
            System.out.println("+------------+----------------+--------+------------+----------------+------------+");
            System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-10s |%n",
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getBirthdate(),
                    p.getPhoneNumber(),
                    ((Student) p).getStudentId());
            System.out.println("+------------+----------------+--------+------------+----------------+------------+");
        } else if (p instanceof Tutor) {
            System.out.println(
                    "+------------+----------------+--------+------------+----------------+------------+----------------+");
            System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-10s | %-14s |%n",
                    "Patient ID", "Name", "Gender", "Birthdate", "Phone", "Tutor ID", "Faculty");
            System.out.println(
                    "+------------+----------------+--------+------------+----------------+------------+----------------+");
            System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-10s | %-14s |%n",
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getBirthdate(),
                    p.getPhoneNumber(),
                    ((Tutor) p).getTutorId(),
                    ((Tutor) p).getFaculty());
            System.out.println(
                    "+------------+----------------+--------+------------+----------------+------------+----------------+");
        } else if (p instanceof Staff) {
            System.out.println(
                    "+------------+----------------+--------+------------+----------------+------------+----------------+");
            System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-10s | %-14s |%n",
                    "Patient ID", "Name", "Gender", "Birthdate", "Phone", "Staff ID", "Department");
            System.out.println(
                    "+------------+----------------+--------+------------+----------------+------------+----------------+");
            System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-10s | %-14s |%n",
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getBirthdate(),
                    p.getPhoneNumber(),
                    ((Staff) p).getStaffId(),
                    ((Staff) p).getDepartment());
            System.out.println(
                    "+------------+----------------+--------+------------+----------------+------------+----------------+");
        }

    }

    // ========== Reports ==========
    public void reportPatientsByRole() {
        int students = 0, tutors = 0, staff = 0;

        ListInterface<String> keys = patientMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            Patient p = patientMap.get(keys.get(i));
            if (!p.isDeleted()) {
                if (p instanceof Student)
                    students++;
                else if (p instanceof Tutor)
                    tutors++;
                else if (p instanceof Staff)
                    staff++;
            }
        }

        System.out.println("\n--- Patients by Role ---");
        System.out.println("Students: " + students);
        System.out.println("Tutors: " + tutors);
        System.out.println("Staff: " + staff);
    }

    public void reportQueueSize() {
        System.out.println("\n--- Queue Report ---");
        System.out.println("Current queue size: " + patientQueue.size());
    }

    // ==================== PATIENT QUEUE MANAGEMENT (from PatientQueueControl) ====================

    // === NEW BUSINESS METHODS ===

    /**
     * Add walk-in patient to queue
     */
    public void addWalkIn(String patientId, String specialty) {
        String queueId = PatientQueueDAO.generateQueueId();
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, 
                                                        QueueType.WALK_IN, LocalDateTime.now());
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);
        System.out.println("Walk-in patient added to queue: " + queueId);
    }

    /**
     * Check-in appointment patient from schedule
     */
    public void checkInAppointment(String scheduleId, DoctorController doctorController) {
        // Get schedule info from consolidated doctor controller
        // Note: This method needs to be implemented in DoctorController if not available
        System.out.println("Check-in appointment functionality needs to be implemented via DoctorController");
        System.out.println("Schedule ID: " + scheduleId);
    }

    /**
     * Assign doctor to waiting patient
     */
    public void assignPatientToDoctor(String queueId, String doctorId) {
        PatientQueueEntry entry = queueMap.get(queueId);
        if (entry == null) {
            System.out.println("Queue entry not found: " + queueId);
            return;
        }

        if (entry.getQueueStatus() != QueueStatus.WAITING) {
            System.out.println("Patient is not waiting: " + entry.getQueueStatus());
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        // Update queue entry
        entry.assignToDoctor(doctorId);
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);

        // Create consultation
        if (consultationControl != null) {
            consultationControl.createConsultationForQueue(queueId);
        }
        
        System.out.println("Patient assigned to Dr. " + doctorId + " - Consultation created");
    }

    /**
     * Get next eligible patient for doctor to call
     */
    public PatientQueueEntry getNextEligiblePatient() {
        ListInterface<PatientQueueEntry> sortedQueue = getSortedQueue();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < sortedQueue.size(); i++) {
            PatientQueueEntry entry = sortedQueue.get(i);
            if (entry.getQueueStatus() == QueueStatus.WAITING) {
                if (entry.getQueueType() == QueueType.WALK_IN) {
                    return entry; // Walk-ins always eligible
                }
                if (entry.getQueueType() == QueueType.APPOINTMENT &&
                    entry.getScheduledStartTime() != null &&
                    !now.isBefore(entry.getScheduledStartTime())) {
                    return entry; // Appointment time has arrived
                }
            }
        }
        return null; // No eligible patients
    }

    /**
     * Get queue sorted by scheduled time then arrival time
     */
    private ListInterface<PatientQueueEntry> getSortedQueue() {
        ListInterface<PatientQueueEntry> queue = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            queue.add(queueMap.get(key));
        }
        
        // Simple bubble sort by scheduled time / arrival time
        for (int i = 0; i < queue.size() - 1; i++) {
            for (int j = 0; j < queue.size() - i - 1; j++) {
                if (shouldSwap(queue.get(j), queue.get(j + 1))) {
                    PatientQueueEntry temp = queue.get(j);
                    queue.set(j, queue.get(j + 1));
                    queue.set(j + 1, temp);
                }
            }
        }
        return queue;
    }

    private boolean shouldSwap(PatientQueueEntry a, PatientQueueEntry b) {
        // Appointments with scheduled times come first, sorted by time
        if (a.getScheduledStartTime() != null && b.getScheduledStartTime() != null) {
            return a.getScheduledStartTime().isAfter(b.getScheduledStartTime());
        }
        // Appointments before walk-ins
        if (a.getScheduledStartTime() != null && b.getScheduledStartTime() == null) {
            return false;
        }
        if (a.getScheduledStartTime() == null && b.getScheduledStartTime() != null) {
            return true;
        }
        // Both walk-ins, sort by arrival time
        return a.getArrivalTime().isAfter(b.getArrivalTime());
    }

    public HashMapInterface<String, PatientQueueEntry> getQueueMap() {
        return queueMap;
    }

    /**
     * Get sorted queue entries for display
     */
    public ListInterface<PatientQueueEntry> getSortedQueueEntries() {
        return getSortedQueue();
    }

    /**
     * Get waiting patients for doctor assignment
     */
    public ListInterface<PatientQueueEntry> getWaitingPatientsForAssignment() {
        ListInterface<PatientQueueEntry> waiting = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.getQueueStatus() == QueueStatus.WAITING) {
                waiting.add(entry);
            }
        }
        return waiting;
    }

    /**
     * Get available doctors for a specialty
     */
    public ListInterface<Doctor> getAvailableDoctorsForSpecialty(String specialty) {
        ListInterface<Doctor> available = new ArrayList<>();
        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted()) {
                available.add(doctor);
            }
        }
        return available;
    }

    // --- Add patient to queue ---
    public void addPatientToQueue() {
        System.out.println("\n--- Add Patient to Queue ---");
        System.out.println("Type 'exit' at any point to cancel.\n");

        // Show available patients
        System.out.println("Available Patients:");
        displayPatientList();

        // --- Select Patient ID ---
        String patientId = selectPatient();
        if (patientId == null) return;

        // --- Select Specialty ---
        String specialty = selectSpecialty();
        if (specialty == null) return;

        // --- Select Queue Type ---
        QueueType queueType = selectQueueType();
        if (queueType == null) return;

        // --- Create queue entry ---
        String queueId = PatientQueueDAO.generateQueueId();
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, queueType, LocalDateTime.now());
        
        queueMap.put(queueId, entry);
        PatientQueueDAO.savePatientQueue(queueMap);

        System.out.println("\nPatient added to queue successfully!");
        System.out.println("Queue ID: " + queueId);
        System.out.println("Patient: " + patientId);
        System.out.println("Specialty: " + specialty);
        System.out.println("Type: " + queueType);
        System.out.println("Status: WAITING");
    }

    // --- View current queue ---
    public void viewCurrentQueue() {
        if (queueMap.isEmpty()) {
            System.out.println("\nNo patients in queue.");
            return;
        }

        System.out.println("\n--- Current Patient Queue ---");
        
        // Beautiful table header with assigned doctor column
        String borderLine = "+---------+------------+---------------------------+----------------------+-----------------+-----------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-7s | %-10s | %-25s | %-20s | %-15s | %-15s | %-10s | %-10s |%n",
                "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Status", "Arrival", "Doctor");
        System.out.println(borderLine);

        // Get all queue entries and sort by arrival time
        ListInterface<PatientQueueEntry> entries = queueMap.toList();
        entries.sort((e1, e2) -> e1.getArrivalTime().compareTo(e2.getArrivalTime()));

        for (int i = 0; i < entries.size(); i++) {
            PatientQueueEntry entry = entries.get(i);
            Patient patient = patientMap.get(entry.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown";
            
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String arrivalTime = entry.getArrivalTime().format(timeFormatter);
            
            // Get doctor name if assigned
            String doctorInfo = "N/A";
            if (entry.getAssignedDoctorId() != null) {
                Doctor doctor = doctorMap.get(entry.getAssignedDoctorId());
                if (doctor != null) {
                    doctorInfo = doctor.getName();
                } else {
                    doctorInfo = entry.getAssignedDoctorId(); // Show ID if name not found
                }
            }
            
            System.out.printf("| %-7s | %-10s | %-25s | %-20s | %-15s | %-15s | %-10s | %-10s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    entry.getQueueStatus(),
                    arrivalTime,
                    doctorInfo);
            System.out.println(borderLine);
        }
    }

    // --- Assign doctor to patient ---
    public void assignDoctorToPatient() {
        System.out.println("\n--- Assign Doctor to Patient ---");
        
        // Show waiting patients
        ListInterface<PatientQueueEntry> waitingPatients = getWaitingPatients();
        if (waitingPatients.isEmpty()) {
            System.out.println("No patients waiting for doctor assignment.");
            return;
        }

        System.out.println("\nPatients waiting for doctor assignment:");
        displayWaitingPatients(waitingPatients);

        // --- Select queue entry ---
        String queueId = selectQueueEntry(waitingPatients);
        if (queueId == null) return;

        PatientQueueEntry entry = queueMap.get(queueId);
        String specialty = entry.getSpecialty();

        // Check if this is an appointment patient
        if (entry.getQueueType() == QueueType.APPOINTMENT) {
            System.out.println("This is an appointment patient. They should already be assigned to a doctor.");
            System.out.println("   If no doctor is assigned, please check the appointment booking in Doctor Schedule Management.");
            return;
        }

        // For walk-in patients, proceed with manual assignment
        System.out.println("This is a walk-in patient. Proceeding with manual doctor assignment...");

        // --- Show available doctors for specialty ---
        ListInterface<Doctor> availableDoctors = getAvailableDoctors(specialty);
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors available for " + specialty + " specialty.");
            return;
        }

        System.out.println("\nAvailable doctors for " + specialty + ":");
        displayDoctorList(availableDoctors);

        // --- Select doctor ---
        String doctorId = selectDoctor(availableDoctors);
        if (doctorId == null) return;

        // --- Assign doctor ---
        entry.assignToDoctor(doctorId);
        PatientQueueDAO.savePatientQueue(queueMap);

        // --- Create consultation automatically ---
        consultationControl.createConsultationForQueue(queueId);

        System.out.println("\nWalk-in patient assigned to doctor successfully!");
        System.out.println("Patient: " + entry.getPatientId());
        System.out.println("Doctor: " + doctorId);
        System.out.println("Specialty: " + specialty);
    }

    // --- Complete patient consultation ---
    public void completePatientConsultation() {
        System.out.println("\n--- Complete Patient Consultation ---");
        
        // Show assigned patients
        ListInterface<PatientQueueEntry> assignedPatients = getAssignedPatients();
        if (assignedPatients.isEmpty()) {
            System.out.println("No patients currently assigned to doctors.");
            return;
        }

        System.out.println("\nPatients assigned to doctors:");
        displayAssignedPatients(assignedPatients);

        // --- Select queue entry ---
        String queueId = selectQueueEntry(assignedPatients);
        if (queueId == null) return;

        PatientQueueEntry entry = queueMap.get(queueId);
        
        // Remove from active queue (history logging removed)
        queueMap.remove(queueId);
        PatientQueueDAO.savePatientQueue(queueMap);
        
        System.out.println("\nPatient consultation completed and removed from active queue!");
        System.out.println("Patient: " + entry.getPatientId());
        System.out.println("Doctor: " + entry.getAssignedDoctorId());
        System.out.println("Queue ID: " + entry.getQueueId());
    }

    // --- Helper methods ---
    private void displayPatientList() {
        ListInterface<Patient> patients = patientMap.toList();
        patients.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));

        String borderLine = "+------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s |%n", "PatientID", "Name");
        System.out.println(borderLine);
        
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            if (!p.isDeleted()) {
                System.out.printf("| %-10s | %-25s |%n", p.getPatientId(), p.getName());
            }
        }
        System.out.println(borderLine);
    }

    private String selectPatient() {
        while (true) {
            System.out.print("Enter Patient ID: ");
            String patientId = sc.nextLine().trim();

            if (patientId.equalsIgnoreCase("exit")) {
                System.out.println("Patient selection cancelled.");
                return null;
            }

            Patient patient = patientMap.get(patientId);
            if (patient != null && !patient.isDeleted()) {
                return patientId;
            }

            System.out.println("Invalid Patient ID. Please choose from the list above.");
        }
    }

    private String selectSpecialty() {
        System.out.println("\nAvailable Specialties:");
        Specialty[] specialties = Specialty.values();
        for (int i = 0; i < specialties.length; i++) {
            System.out.println((i + 1) + ". " + specialties[i]);
        }

        while (true) {
            System.out.print("Enter specialty number: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Specialty selection cancelled.");
                return null;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= specialties.length) {
                    return specialties[choice - 1].toString();
                }
            } catch (NumberFormatException e) {
                // Continue to error message
            }
            System.out.println("Invalid choice. Please enter a number between 1 and " + specialties.length);
        }
    }

    private QueueType selectQueueType() {
        System.out.println("\nQueue Type:");
        System.out.println("1. WALK IN");
        System.out.println("2. APPOINTMENT");

        while (true) {
            System.out.print("Enter choice (1 or 2): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Queue type selection cancelled.");
                return null;
            }

            switch (input) {
                case "1": return QueueType.WALK_IN;
                case "2": return QueueType.APPOINTMENT;
                default: System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    private ListInterface<PatientQueueEntry> getWaitingPatients() {
        ListInterface<PatientQueueEntry> waiting = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.isWaiting()) {
                waiting.add(entry);
            }
        }
        return waiting;
    }

    private ListInterface<PatientQueueEntry> getAssignedPatients() {
        ListInterface<PatientQueueEntry> assigned = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            PatientQueueEntry entry = queueMap.get(key);
            if (entry.isAssigned()) {
                assigned.add(entry);
            }
        }
        return assigned;
    }

    private void displayWaitingPatients(ListInterface<PatientQueueEntry> waiting) {
        String borderLine = "+--------+------------+---------------------------+----------------+----------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Doctor");
        System.out.println(borderLine);

        for (int i = 0; i < waiting.size(); i++) {
            PatientQueueEntry entry = waiting.get(i);
            Patient patient = patientMap.get(entry.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown";
            
            // Get doctor name if assigned (should be N/A for waiting patients)
            String doctorInfo = "N/A";
            if (entry.getAssignedDoctorId() != null) {
                Doctor doctor = doctorMap.get(entry.getAssignedDoctorId());
                if (doctor != null) {
                    doctorInfo = doctor.getName();
                } else {
                    doctorInfo = entry.getAssignedDoctorId();
                }
            }
            
            System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    doctorInfo);
            System.out.println(borderLine);
        }
    }

    private void displayAssignedPatients(ListInterface<PatientQueueEntry> assigned) {
        String borderLine = "+--------+------------+---------------------------+----------------+----------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                "QueueID", "PatientID", "Patient Name", "Specialty", "Type", "Doctor");
        System.out.println(borderLine);

        for (int i = 0; i < assigned.size(); i++) {
            PatientQueueEntry entry = assigned.get(i);
            Patient patient = patientMap.get(entry.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown";
            
            System.out.printf("| %-6s | %-10s | %-25s | %-14s | %-8s | %-10s |%n",
                    entry.getQueueId(),
                    entry.getPatientId(),
                    patientName,
                    entry.getSpecialty(),
                    entry.getQueueType(),
                    entry.getAssignedDoctorId());
            System.out.println(borderLine);
        }
    }

    private String selectQueueEntry(ListInterface<PatientQueueEntry> entries) {
        while (true) {
            System.out.print("Enter Queue ID: ");
            String queueId = sc.nextLine().trim();

            if (queueId.equalsIgnoreCase("exit")) {
                System.out.println("Queue selection cancelled.");
                return null;
            }

            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getQueueId().equals(queueId)) {
                    return queueId;
                }
            }

            System.out.println("Invalid Queue ID. Please choose from the list above.");
        }
    }

    private ListInterface<Doctor> getAvailableDoctors(String specialty) {
        ListInterface<Doctor> available = new ArrayList<>();
        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted() && doctor.getSpecialty().toString().equals(specialty)) {
                available.add(doctor);
            }
        }
        return available;
    }

    private void displayDoctorList(ListInterface<Doctor> doctors) {
        String borderLine = "+------------+---------------------------+----------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-14s |%n", "DoctorID", "Name", "Specialty");
        System.out.println(borderLine);

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("| %-10s | %-25s | %-14s |%n",
                    doctor.getDoctorId(),
                    doctor.getName(),
                    doctor.getSpecialty());
            System.out.println(borderLine);
        }
    }

    private String selectDoctor(ListInterface<Doctor> doctors) {
        while (true) {
            System.out.print("Enter Doctor ID: ");
            String doctorId = sc.nextLine().trim();

            if (doctorId.equalsIgnoreCase("exit")) {
                System.out.println("Doctor selection cancelled.");
                return null;
            }

            for (int i = 0; i < doctors.size(); i++) {
                if (doctors.get(i).getDoctorId().equals(doctorId)) {
                    return doctorId;
                }
            }

            System.out.println("Invalid Doctor ID. Please choose from the list above.");
        }
    }

    // ==================== PATIENT VIEWING OPERATIONS (from ViewPatientControl) ====================

    public void clearCriteria() {
        criteriaUtil.clearCriteria();
    }

    public void addCriteria(String text) {
        criteriaUtil.addCriteria(text);
    }

    private void removeOldSortCriteria() {
        criteriaUtil.removeOldSortCriteria();
    }

    public String getCriteriaSummary() {
        return criteriaUtil.getCriteriaSummary();
    }

    // --- Filter ---
    public HashMapInterface<String, Patient> filterByRole(HashMapInterface<String, Patient> map, String roleChoice) {
        switch (roleChoice) {
            case "1":
                addCriteria("Role = Student");
                break;
            case "2":
                addCriteria("Role = Tutor");
                break;
            case "3":
                addCriteria("Role = Staff");
                break;
            default:
                addCriteria("Role = Unknown");
        }
        return map.filter(p -> {
            switch (roleChoice) {
                case "1":
                    return p instanceof Student;
                case "2":
                    return p instanceof Tutor;
                case "3":
                    return p instanceof Staff;
                default:
                    return false;
            }
        });
    }

    public HashMapInterface<String, Patient> filterByGender(HashMapInterface<String, Patient> map, String gender) {
        addCriteria("Gender = " + gender);
        return map.filter(p -> p.getGender().equalsIgnoreCase(gender));
    }

    public HashMapInterface<String, Patient> filterShowDeleted(HashMapInterface<String, Patient> map) {
        addCriteria("Show Deleted");
        return map.filter(Patient::isDeleted);
    }

    public HashMapInterface<String, Patient> filterNotDeleted(HashMapInterface<String, Patient> map) {
        addCriteria("Hide Deleted");
        return map.filter(p -> !p.isDeleted());
    }

    // --- Search ---
    public HashMapInterface<String, Patient> searchPatients(HashMapInterface<String, Patient> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();

        return map.filter(p -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String searchText = String.join(" ",
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getPhoneNumber(),
                    p.getBirthdate().format(fmt),
                    p.getClass().getSimpleName(),
                    p.isDeleted() ? "deleted" : "active").toLowerCase();

            return searchText.contains(lower);
        });
    }

    // --- Sort ---
    // Ascending sort
    public void sortPatients(ListInterface<Patient> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

        switch (option) {
            case "1":
                list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
                addCriteria("Sort = Patient ID (Asc)");
                break;
            case "2":
                list.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                addCriteria("Sort = Name (Asc)");
                break;
            case "3":
                list.sort((p1, p2) -> p1.getGender().compareToIgnoreCase(p2.getGender()));
                addCriteria("Sort = Gender (Asc)");
                break;
            case "4":
                list.sort((p1, p2) -> p1.getBirthdate().compareTo(p2.getBirthdate()));
                addCriteria("Sort = Birthdate (Asc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    // Descending sort
    public void reverseSortPatients(ListInterface<Patient> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option + "_desc"); // mark as descending

        switch (option) {
            case "1":
                list.reverseSort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
                addCriteria("Sort = Patient ID (Desc)");
                break;
            case "2":
                list.reverseSort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                addCriteria("Sort = Name (Desc)");
                break;
            case "3":
                list.reverseSort((p1, p2) -> p1.getGender().compareToIgnoreCase(p2.getGender()));
                addCriteria("Sort = Gender (Desc)");
                break;
            case "4":
                list.reverseSort((p1, p2) -> p1.getBirthdate().compareTo(p2.getBirthdate()));
                addCriteria("Sort = Birthdate (Desc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public ListInterface<Patient> toList(HashMapInterface<String, Patient> map) {
        ListInterface<Patient> list = map.toList();
        if (criteriaUtil.getCurrentSortOption() != null) {
            if (criteriaUtil.getCurrentSortOption().endsWith("_desc")) {
                String originalOption = criteriaUtil.getCurrentSortOption().replace("_desc", "");
                reverseSortPatients(list, originalOption);
            } else {
                sortPatients(list, criteriaUtil.getCurrentSortOption());
            }
        } else {
            // Default sort by Patient ID ascending
            list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
        }
        return list;
    }

    // --- Display wrapper ---
    public void printPatients(HashMapInterface<String, Patient> map) {
        ListInterface<Patient> list = map.toList();
        list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
        printPatientsTable(list, getCriteriaSummary());
    }

    public void printPatientsFromList(ListInterface<Patient> list) {
        printPatientsTable(list, getCriteriaSummary());
    }
}
