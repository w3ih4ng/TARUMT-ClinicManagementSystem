package control;

import entity.Doctor;
import entity.Specialty;
import adt.*;
import dao.DoctorDAO;

import java.util.Scanner;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DoctorRecordControl {
    private HashMapInterface<String, Doctor> doctorMap; // key = doctorId
    private Scanner sc;
    private int doctorCounter = 1000;

    public DoctorRecordControl() {
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

        // Specialty - user chooses from numbered list
        Specialty specialty = null;
        while (specialty == null) {
            System.out.println("Select Specialty:");
            Specialty[] specs = Specialty.values();
            for (int i = 0; i < specs.length; i++) {
                System.out.println((i + 1) + ". " + specs[i].name());
            }
            System.out.print("Enter choice number: ");
            String choice = sc.nextLine().trim();
            try {
                int index = Integer.parseInt(choice) - 1;
                if (index >= 0 && index < specs.length) {
                    specialty = specs[index];
                } else {
                    System.out.println("Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }

        // Consultation Fee
        double fee = 0;
        while (true) {
            System.out.print("Consultation Fee (RM): ");
            String input = sc.nextLine().trim();
            try {
                fee = Double.parseDouble(input);
                if (fee >= 0)
                    break;
                System.out.println("Fee must be non-negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }

        String doctorId = generateDoctorId();
        Doctor doctor = new Doctor(doctorId, name, gender, birthdate, phone, specialty, fee);

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

        // Format
        String leftAlignFormat = "| %-10s | %-20s | %-6s | %-10s | %-12s | %-18s | %-21s |%n";
        String borderLine = "+------------+----------------------+--------+------------+--------------+--------------------+-----------------------+";

        // Header
        System.out.println(borderLine);
        System.out.printf(leftAlignFormat,
                "Doctor ID", "Name", "Gender", "Birthdate", "Phone", "Specialty", "Consultation Fee (RM)");
        System.out.println(borderLine);

        // Rows
        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            System.out.printf(leftAlignFormat,
                    d.getDoctorId(),
                    d.getName(),
                    d.getGender(),
                    d.getBirthdate(),
                    d.getPhoneNumber(),
                    d.getSpecialty().name(),
                    String.format("%.2f", d.getConsultationFee()));
            System.out.println(borderLine);
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

        System.out.println("\nUpdating doctor:");

        doctorDetailsTable(d);

        // --- Name ---
        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            d.setName(name);
            System.out.println("Name updated.");
        }

        // --- Gender ---
        System.out.print("New gender (leave blank to keep): ");
        String gender = sc.nextLine().trim().toUpperCase();
        if (!gender.isEmpty() && (gender.equals("M") || gender.equals("F"))) {
            d.setGender(gender);
            System.out.println("Gender updated.");
        }

        // --- Birthdate ---
        System.out.print("New birthdate (yyyy-mm-dd, leave blank to keep): ");
        String birthdate = sc.nextLine().trim();
        if (!birthdate.isEmpty()) {
            try {
                d.setBirthdate(LocalDate.parse(birthdate));
                System.out.println("Birthdate updated.");
            } catch (Exception e) {
                System.out.println("Invalid date. Not updated.");
            }
        }

        // --- Phone ---
        System.out.print("New phone number (leave blank to keep): ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty() && phone.matches("\\d{7,}")) {
            d.setPhoneNumber(phone);
            System.out.println("Phone number updated.");
        } else {
            System.out.println("Invalid phone number. Not updated.");
        }

        // --- Specialty ---
        System.out.print("Change specialty? (y/n): ");
        String changeSpec = sc.nextLine().trim().toLowerCase();
        if (changeSpec.equals("y")) {
            Specialty specialty = null;
            while (specialty == null) {
                System.out.println("Select Specialty:");
                Specialty[] specs = Specialty.values();
                for (int i = 0; i < specs.length; i++) {
                    System.out.println((i + 1) + ". " + specs[i].name());
                }
                System.out.print("Enter choice number: ");
                String choice = sc.nextLine().trim();
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < specs.length) {
                        specialty = specs[index];
                        d.setSpecialty(specialty);
                        System.out.println("Specialty updated.");
                    } else {
                        System.out.println("Invalid number. Try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter a number.");
                }
            }
        }

        // --- Consultation Fee ---
        System.out.print("New consultation fee (leave blank to keep): ");
        String feeInput = sc.nextLine().trim();
        if (!feeInput.isEmpty()) {
            try {
                double fee = Double.parseDouble(feeInput);
                if (fee >= 0) {
                    d.setConsultationFee(fee);
                    System.out.println("Consultation fee updated.");
                } else {
                    System.out.println("Fee must be non-negative. Not updated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Fee not updated.");
            }
        }

        DoctorDAO.saveDoctors(doctorMap);
        System.out.println("\nDoctor updated successfully.");
    }

    public void deleteDoctor() {
        System.out.print("\nEnter Doctor ID to delete: ");
        String id = sc.nextLine().trim();
        Doctor d = doctorMap.get(id);

        if (d == null) {
            System.out.println("Doctor not found.");
            return;
        }

        doctorDetailsTable(d);

        // Confirm deletion
        System.out.print("Are you sure you want to delete this doctor? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) {
            d.delete();
            DoctorDAO.saveDoctors(doctorMap);
            System.out.println("Doctor soft-deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public void restoreDoctor() {
        System.out.print("\nEnter Doctor ID to restore: ");
        String id = sc.nextLine().trim();
        Doctor d = doctorMap.get(id);

        if (d == null) {
            System.out.println("Doctor not found.");
            return;
        }

        if (!d.isDeleted()) {
            System.out.println("Doctor is already active.");
            return;
        }

        d.restore();
        DoctorDAO.saveDoctors(doctorMap);
        System.out.println("Doctor restored successfully.");
    }

    public void doctorDetailsTable(Doctor d) {
        // Display current doctor info in a table
        System.out.println(
                "+------------+----------------+--------+------------+----------------+-----------------+----------------+");
        System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-15s | %-14s |%n",
                "Doctor ID", "Name", "Gender", "Birthdate", "Phone", "Specialty", "Fee (RM)");
        System.out.println(
                "+------------+----------------+--------+------------+----------------+-----------------+----------------+");
        System.out.printf("| %-10s | %-14s | %-6s | %-10s | %-14s | %-15s | %14.2f |%n",
                d.getDoctorId(),
                d.getName(),
                d.getGender(),
                d.getBirthdate(),
                d.getPhoneNumber(),
                d.getSpecialty().name(),
                d.getConsultationFee());
        System.out.println(
                "+------------+----------------+--------+------------+----------------+-----------------+----------------+");

    }

    // --- Reports ---
    public void reportDoctorsBySpecialty() {
        HashMapInterface<String, Integer> countMap = new HashMapADT<>();
        ListInterface<String> keys = doctorMap.keySet();
        for (int i = 0; i < keys.size(); i++) {
            Doctor d = doctorMap.get(keys.get(i));
            String spec = d.getSpecialty().name();
            countMap.put(spec, countMap.containsKey(spec) ? countMap.get(spec) + 1 : 1);
        }

        System.out.println("\n--- Doctors by Specialty ---");
        ListInterface<String> specs = countMap.keySet();
        for (int i = 0; i < specs.size(); i++) {
            System.out.println(specs.get(i) + ": " + countMap.get(specs.get(i)));
        }
    }
}
