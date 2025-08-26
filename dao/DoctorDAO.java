package dao;

import entity.Doctor;
import adt.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DoctorDAO {
    private static final String FILE_NAME = "data/doctors.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Ensure file exists
    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring doctors file: " + e.getMessage());
        }
    }

    // Save all doctors
    public static void saveDoctors(HashMapInterface<String, Doctor> doctorMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < doctorMap.keySet().size(); i++) {
                String key = doctorMap.keySet().get(i);
                Doctor d = doctorMap.get(key);
                if (d != null) pw.println(toFileString(d));
            }
        } catch (IOException e) {
            System.out.println("Error saving doctors: " + e.getMessage());
        }
    }

    // Load all doctors
    public static HashMapInterface<String, Doctor> loadDoctors() {
        ensureFile();
        HashMapInterface<String, Doctor> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Doctor d = fromFileString(line);
                if (d != null) map.put(d.getDoctorId(), d);
            }
        } catch (IOException e) {
            System.out.println("Error loading doctors: " + e.getMessage());
        }

        return map;
    }

    // Convert Doctor to file line
    private static String toFileString(Doctor d) {
        return String.join("|",
                d.getDoctorId(),
                d.getName(),
                d.getGender(),
                d.getBirthdate().format(formatter),
                d.getPhoneNumber(),
                d.getSpecialty(),
                Boolean.toString(d.isDeleted())   // ✅ include deleted flag
        );
    }

    // Convert file line to Doctor
    private static Doctor fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            String doctorId = parts[0];
            String name = parts[1];
            String gender = parts[2];
            LocalDate birthdate = LocalDate.parse(parts[3], formatter);
            String phone = parts[4];
            String specialty = parts[5];
            boolean deleted = (parts.length > 6) && Boolean.parseBoolean(parts[6]); // ✅ handle isDeleted

            Doctor d = new Doctor(doctorId, name, gender, birthdate, phone, specialty);
            if (deleted) d.delete(); // mark as deleted if flag is true

            return d;
        } catch (Exception e) {
            System.out.println("Error parsing doctor line: " + line);
            return null;
        }
    }
}
