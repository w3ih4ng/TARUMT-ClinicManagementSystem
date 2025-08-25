package control;

import entity.*;
import adt.*;
import dao.PatientDAO;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientControl {
    private HashMapInterface<String, Patient> patientMap; // key = patientId
    private ListInterface<String> patientQueue; // store patient IDs in order
    private Scanner sc;
    private int patientCounter = 1000; // re-initialized later

    public PatientControl() {
        this.patientMap = PatientDAO.loadPatients();
        this.patientQueue = new ArrayList<>();
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

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

    // ========== CRUD ==========

    public void registerPatient() {
        System.out.println("\n--- Register New Patient ---");

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        // Gender validation
        String gender = "";
        while (true) {
            System.out.print("Gender (M/F): ");
            gender = sc.nextLine().trim().toUpperCase();
            if (gender.equals("M") || gender.equals("F")) {
                break;
            }
            System.out.println("Invalid gender. Please enter M or F.");
        }

        // Birthdate validation
        LocalDate birthdate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Birthdate (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                birthdate = LocalDate.parse(input, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid format! Please use yyyy-MM-dd.");
            }
        }

        // Phone number validation
        String phone = "";
        while (true) {
            System.out.print("Phone (digits only): ");
            phone = sc.nextLine().trim();
            if (phone.matches("\\d{7,}")) {
                break;
            }
            System.out.println("Invalid phone number. Use digits only, min 7 digits.");
        }

        // Role validation
        String roleChoice = "";
        while (true) {
            System.out.println("Role: 1. Student  2. Tutor  3. Staff");
            System.out.print("Choose: ");
            roleChoice = sc.nextLine().trim();
            if (roleChoice.equals("1") || roleChoice.equals("2") || roleChoice.equals("3")) {
                break;
            }
            System.out.println("Invalid choice. Enter 1, 2, or 3.");
        }

        Patient patient = null;
        String patientId = generatePatientId();

        switch (roleChoice) {
            case "1":
                System.out.print("Student ID: ");
                String studentId = sc.nextLine().trim();
                patient = new Student(patientId, studentId, name, gender, birthdate, phone);
                break;
            case "2":
                System.out.print("Tutor ID: ");
                String tutorId = sc.nextLine().trim();
                System.out.print("Faculty: ");
                String faculty = sc.nextLine().trim();
                patient = new Tutor(patientId, tutorId, name, gender, birthdate, phone, faculty);
                break;
            case "3":
                System.out.print("Staff ID: ");
                String staffId = sc.nextLine().trim();
                System.out.print("Department: ");
                String department = sc.nextLine().trim();
                patient = new Staff(patientId, staffId, name, gender, birthdate, phone, department);
                break;
        }

        patientMap.put(patientId, patient);
        PatientDAO.savePatients(patientMap); // save immediately
        System.out.println("✅ Patient registered successfully! Patient ID: " + patientId);
    }

    // patientmap
    public HashMapInterface<String, Patient> getPatientMap() {
        return this.patientMap;
    }
    

    public void printPatientsTable(ListInterface<Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
    
        System.out.printf("%-15s %-15s %-8s %-12s %-12s %-10s %-15s %-15s %-15s%n",
                "Patient ID", "Name", "Gender", "Birthdate", "Phone", "Role", "Role ID", "Faculty", "Department");
    
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
    
            System.out.printf("%-15s %-15s %-8s %-12s %-12s %-10s %-15s %-15s %-15s%n",
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getBirthdate(),
                    p.getPhoneNumber(),
                    role,
                    roleId,
                    faculty,
                    department);
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

        System.out.println("Updating patient: " + p.getName());

        System.out.print("New phone number (leave blank to keep): ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty()) {
            if (phone.matches("\\d{7,}")) {
                p.setPhoneNumber(phone);
                System.out.println("Phone updated.");
            } else {
                System.out.println("Invalid phone number. Not updated.");
            }
        }

        PatientDAO.savePatients(patientMap); // persist changes
        System.out.println("Patient updated successfully.");
    }

    public void deletePatient() {
        System.out.print("\nEnter Patient ID to delete: ");
        String id = sc.nextLine().trim();
        Patient p = patientMap.get(id);

        if (p == null || p.isDeleted()) {
            System.out.println("Patient not found.");
            return;
        }

        p.delete();
        PatientDAO.savePatients(patientMap); // persist deletion
        System.out.println("Patient soft-deleted successfully.");
    }

    // ========== Queue Management ==========
    public void enqueuePatient() {
        System.out.print("\nEnter Patient ID to enqueue: ");
        String id = sc.nextLine().trim();
        Patient p = patientMap.get(id);

        if (p == null || p.isDeleted()) {
            System.out.println("Patient not found.");
            return;
        }

        patientQueue.add(id);
        System.out.println("Patient " + id + " added to queue.");
    }

    public void dequeuePatient() {
        if (patientQueue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        String id = patientQueue.get(0);
        patientQueue.remove(0);
        System.out.println("Patient " + id + " dequeued.");
    }

    public void viewQueue() {
        System.out.println("\n--- Patient Queue ---");
        if (patientQueue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        for (int i = 0; i < patientQueue.size(); i++) {
            Patient p = patientMap.get(patientQueue.get(i));
            if (p != null && !p.isDeleted()) {
                System.out.println((i + 1) + ". " + p);
            }
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
}
