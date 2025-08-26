package control;

import entity.Doctor;
import adt.*;
import dao.DoctorDAO;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DoctorControl {
    private HashMapInterface<String, Doctor> doctorMap; // key = doctorId
    private Scanner sc;
    private int doctorCounter = 1000;

    public DoctorControl() {
        this.doctorMap = DoctorDAO.loadDoctors(); // load from DAO
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    // Initialize counter by scanning existing doctor IDs
    private void initCounterFromMap() {
        int max = 999; // first doctor = D1000
        ListInterface<String> keys = doctorMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i); // e.g., "D1050"
            try {
                int num = Integer.parseInt(key.substring(1));
                if (num > max)
                    max = num;
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        doctorCounter = max + 1;
    }

    // Generate new Doctor ID
    private String generateDoctorId() {
        String id;
        do {
            id = "D" + (doctorCounter++);
        } while (doctorMap.containsKey(id));
        return id;
    }

    // --- CRUD ---
    public void registerDoctor() {
        System.out.println("\n--- Register New Doctor ---");

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        // Gender validation
        String gender = "";
        while (true) {
            System.out.print("Gender (M/F): ");
            gender = sc.nextLine().trim().toUpperCase();
            if (gender.equals("M") || gender.equals("F"))
                break;
            System.out.println("Invalid gender. Please enter M or F.");
        }

        // Birthdate
        LocalDate birthdate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Birthdate (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                birthdate = LocalDate.parse(input, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid format! Use yyyy-MM-dd.");
            }
        }

        // Phone
        String phone = "";
        while (true) {
            System.out.print("Phone (digits only): ");
            phone = sc.nextLine().trim();
            if (phone.matches("\\d{7,}"))
                break;
            System.out.println("Invalid phone number. Use digits only, min 7 digits.");
        }

        // Specialty
        System.out.print("Specialty: ");
        String specialty = sc.nextLine().trim();

        String doctorId = generateDoctorId();
        Doctor doctor = new Doctor(doctorId, name, gender, birthdate, phone, specialty);

        doctorMap.put(doctorId, doctor);
        DoctorDAO.saveDoctors(doctorMap); // persist
        System.out.println("\nDoctor registered successfully! Doctor ID: " + doctorId);
    }

    public HashMapInterface<String, Doctor> getDoctorMap() {
        return this.doctorMap;
    }

    public void printDoctorsTable(ListInterface<Doctor> doctors, String criteriaSummary) {
        if (doctors.isEmpty()) {
            System.out.println(
                    "--------------------------------------------- No doctors found. ---------------------------------------------");
            return;
        }

        if (!criteriaSummary.isEmpty()) {
            System.out.println(criteriaSummary);
        } else {
            System.out.println(
                    "--------------------------------------------- No active filter ---------------------------------------------");
        }
        System.out.println();

        System.out.printf("%-10s %-20s %-8s %-12s %-12s %-20s%n",
                "Doctor ID", "Name", "Gender", "Birthdate", "Phone", "Specialty");

        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);

            System.out.printf("%-10s %-20s %-8s %-12s %-12s %-20s%n",
                    d.getDoctorId(),
                    d.getName(),
                    d.getGender(),
                    d.getBirthdate(),
                    d.getPhoneNumber(),
                    d.getSpecialty());
        }
    }

    // --- Update ---
    public void updateDoctor() {
        System.out.print("\nEnter Doctor ID to update: ");
        String id = sc.nextLine().trim();
        Doctor d = doctorMap.get(id);

        if (d == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.println("\nUpdating doctor: " + d.getName());

        // Name
        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty())
            d.setName(name);

        // Gender
        System.out.print("New gender (M/F, leave blank to keep): ");
        String gender = sc.nextLine().trim().toUpperCase();
        if (!gender.isEmpty() && (gender.equals("M") || gender.equals("F")))
            d.setGender(gender);

        // Birthdate
        System.out.print("New birthdate (yyyy-mm-dd, leave blank to keep): ");
        String birthdate = sc.nextLine().trim();
        if (!birthdate.isEmpty()) {
            try {
                d.setBirthdate(LocalDate.parse(birthdate));
            } catch (Exception e) {
                System.out.println("Invalid date. Not updated.");
            }
        }

        // Phone
        System.out.print("New phone number (leave blank to keep): ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty() && phone.matches("\\d{7,}"))
            d.setPhoneNumber(phone);

        // Specialty
        System.out.print("New specialty (leave blank to keep): ");
        String specialty = sc.nextLine().trim();
        if (!specialty.isEmpty())
            d.setSpecialty(specialty);

        DoctorDAO.saveDoctors(doctorMap);
        System.out.println("Doctor updated successfully.");
    }

    // --- Delete ---
    public void deleteDoctor() {
        System.out.print("\nEnter Doctor ID to delete: ");
        String id = sc.nextLine().trim();
        Doctor d = doctorMap.get(id);

        if (d == null) {
            System.out.println("Doctor not found.");
            return;
        }

        doctorMap.remove(id);
        DoctorDAO.saveDoctors(doctorMap);
        System.out.println("Doctor deleted successfully.");
    }

    // --- Reports ---
    public void reportDoctorsBySpecialty() {
        HashMapInterface<String, Integer> countMap = new HashMapADT<>();
        ListInterface<String> keys = doctorMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            Doctor d = doctorMap.get(keys.get(i));
            String spec = d.getSpecialty();
            countMap.put(spec, countMap.containsKey(spec) ? countMap.get(spec) + 1 : 1);
        }

        System.out.println("\n--- Doctors by Specialty ---");
        ListInterface<String> specs = countMap.keySet();
        for (int i = 0; i < specs.size(); i++) {
            System.out.println(specs.get(i) + ": " + countMap.get(specs.get(i)));
        }
    }
}
