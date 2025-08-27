package control;

import entity.*;
import adt.*;
import dao.PatientDAO;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientRecordControl {
    private HashMapInterface<String, Patient> patientMap; // key = patientId
    private ListInterface<String> patientQueue; // store patient IDs in order
    private Scanner sc;
    private int patientCounter = 1000; // re-initialized later

    public PatientRecordControl() {
        this.patientMap = PatientDAO.loadPatients();
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
        System.out.println("Type 'exit' at any point to cancel.\n");

        // --- Name ---
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        if (name.equalsIgnoreCase("exit"))
            return;

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
                System.out.print("Student ID: ");
                String studentId = sc.nextLine().trim();
                if (studentId.equalsIgnoreCase("exit"))
                    return;
                patient = new Student(patientId, studentId, name, gender, birthdate, phone);
                break;

            case "2": // Tutor
                System.out.print("Tutor ID: ");
                String tutorId = sc.nextLine().trim();
                if (tutorId.equalsIgnoreCase("exit"))
                    return;
                System.out.print("Faculty: ");
                String faculty = sc.nextLine().trim();
                if (faculty.equalsIgnoreCase("exit"))
                    return;
                patient = new Tutor(patientId, tutorId, name, gender, birthdate, phone, faculty);
                break;

            case "3": // Staff
                System.out.print("Staff ID: ");
                String staffId = sc.nextLine().trim();
                if (staffId.equalsIgnoreCase("exit"))
                    return;
                System.out.print("Department: ");
                String department = sc.nextLine().trim();
                if (department.equalsIgnoreCase("exit"))
                    return;
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
        String leftAlignFormat = "| %-12s | %-15s | %-6s | %-10s | %-12s | %-8s | %-12s | %-12s | %-12s |%n";

        // Define border line
        String borderLine = "+--------------+-----------------+--------+------------+--------------+----------+--------------+--------------+--------------+";

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
}
