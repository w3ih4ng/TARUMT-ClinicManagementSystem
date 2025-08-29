package dao;

import entity.*;
import adt.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for Patient persistence
 * @author Your Name
 */
public class PatientDAO {
    private static final String FILE_NAME = "data/patients.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Ensure file exists (create if missing)
    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs(); // create /data folder if missing
            }
            if (!file.exists()) {
                file.createNewFile(); // create empty patients.txt
            }
        } catch (IOException e) {
            System.out.println("Error ensuring patients file: " + e.getMessage());
        }
    }

    // Save all patients to file
    public static void savePatients(HashMapInterface<String, Patient> patientMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < patientMap.keySet().size(); i++) {
                String key = patientMap.keySet().get(i);
                Patient p = patientMap.get(key);
                if (p != null) {
                    pw.println(toFileString(p));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving patients: " + e.getMessage());
        }
    }

    // Load all patients from file
    public static HashMapInterface<String, Patient> loadPatients() {
        ensureFile();
        HashMapInterface<String, Patient> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Patient p = fromFileString(line);
                if (p != null) {
                    map.put(p.getPatientId(), p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading patients: " + e.getMessage());
        }

        return map;
    }

    // Convert patient to a line in the file
    private static String toFileString(Patient p) {
        String base = String.join("|",
                p.getClass().getSimpleName(), // Student/Tutor/Staff
                p.getPatientId(),
                p.getName(),
                p.getGender(),
                p.getBirthdate().format(formatter),
                p.getPhoneNumber(),
                Boolean.toString(p.isDeleted())
        );

        if (p instanceof Student) {
            return base + "|" + ((Student) p).getStudentId();
        } else if (p instanceof Tutor) {
            return base + "|" + ((Tutor) p).getTutorId() + "|" + ((Tutor) p).getFaculty();
        } else if (p instanceof Staff) {
            return base + "|" + ((Staff) p).getStaffId() + "|" + ((Staff) p).getDepartment();
        }
        return base;
    }

    // Convert line from file back to patient object
    private static Patient fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            String type = parts[0];
            String patientId = parts[1];
            String name = parts[2];
            String gender = parts[3];
            LocalDate birthdate = LocalDate.parse(parts[4], formatter);
            String phone = parts[5];
            boolean deleted = Boolean.parseBoolean(parts[6]);

            Patient p = null;
            switch (type) {
                case "Student": {
                    String studentId = parts[7];
                    p = new Student(patientId, studentId, name, gender, birthdate, phone);
                    break;
                }
                case "Tutor": {
                    String tutorId = parts[7];
                    String faculty = parts[8];
                    p = new Tutor(patientId, tutorId, name, gender, birthdate, phone, faculty);
                    break;
                }
                case "Staff": {
                    String staffId = parts[7];
                    String department = parts[8];
                    p = new Staff(patientId, staffId, name, gender, birthdate, phone, department);
                    break;
                }
            }

            if (p != null && deleted) p.delete();
            return p;
        } catch (Exception e) {
            System.out.println("Error parsing patient line: " + line);
            return null;
        }
    }
}
