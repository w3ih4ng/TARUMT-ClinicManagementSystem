package dao;

import entity.Doctor;
import entity.Specialty;
import adt.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for Doctor persistence
 * @author Your Name
 */
public class DoctorDAO {
    private static final String FILE_NAME = "data/doctors.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring doctors file: " + e.getMessage());
        }
    }

    public static void saveDoctors(HashMapInterface<String, Doctor> doctorMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < doctorMap.keySet().size(); i++) {
                String key = doctorMap.keySet().get(i);
                Doctor d = doctorMap.get(key);
                if (d != null)
                    pw.println(toFileString(d));
            }
        } catch (IOException e) {
            System.out.println("Error saving doctors: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, Doctor> loadDoctors() {
        ensureFile();
        HashMapInterface<String, Doctor> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Doctor d = fromFileString(line);
                if (d != null)
                    map.put(d.getDoctorId(), d);
            }
        } catch (IOException e) {
            System.out.println("Error loading doctors: " + e.getMessage());
        }

        return map;
    }

    private static String toFileString(Doctor d) {
        return String.join("|",
                d.getDoctorId(),
                d.getName(),
                d.getGender(),
                d.getBirthdate().format(formatter),
                d.getPhoneNumber(),
                d.getSpecialty().name(),         // save enum name
                String.valueOf(d.getConsultationFee()),
                Boolean.toString(d.isDeleted())
        );
    }

    private static Doctor fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 8) {
                throw new IllegalArgumentException("Expected 8 columns, got " + parts.length);
            }
            String doctorId = parts[0];
            String name = parts[1];
            String gender = parts[2];
            LocalDate birthdate = LocalDate.parse(parts[3], formatter);
            String phone = parts[4];
            Specialty specialty = Specialty.valueOf(parts[5]); // parse enum
            double fee = Double.parseDouble(parts[6]);
            boolean deleted = Boolean.parseBoolean(parts[7]);

            Doctor d = new Doctor(doctorId, name, gender, birthdate, phone, specialty, fee);
            if (deleted)
                d.delete();

            return d;
        } catch (Exception e) {
            System.out.println("Error parsing doctor line: " + line + " -> " + e.getMessage());
            return null;
        }
    }
}
