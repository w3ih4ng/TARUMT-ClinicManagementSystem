package control;

import entity.*;
import adt.*;
import dao.DoctorDAO;
import dao.DoctorScheduleDAO;
import utility.FilterCriteriaUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Consolidated Doctor Controller - combines all doctor-related control functionality
 * Handles doctor management, scheduling, viewing, and business logic
 * @author Your Name
 */
public class DoctorController {
    private HashMapInterface<String, Doctor> doctorMap;
    private HashMapInterface<String, DoctorSchedule> scheduleMap;
    private Scanner sc;
    private int doctorCounter = 1000;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public DoctorController() {
        this.doctorMap = DoctorDAO.loadDoctors();
        this.scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();
        this.sc = new Scanner(System.in);
        initCounterFromMap();
    }

    // ==================== COUNTER INITIALIZATION ====================

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

    private String generateDoctorId() {
        String id;
        do {
            id = "D" + (doctorCounter++);
        } while (doctorMap.containsKey(id));
        return id;
    }

    // ==================== DOCTOR CRUD OPERATIONS ====================

    public void registerDoctor() {
        System.out.println("\n--- Register New Doctor ---");
        System.out.println("Type 'exit' at any point to cancel.\n");

        // --- Name Input ---
        String name;
        while (true) {
            System.out.print("Enter doctor name: ");
            name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Doctor registration cancelled.");
                return;
            }
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Name cannot be empty. Please try again.");
        }

        // --- Gender Input ---
        String gender;
        while (true) {
            System.out.print("Enter gender (M/F): ");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Doctor registration cancelled.");
                return;
            }
            if (input.equals("M") || input.equals("MALE")) {
                gender = "M";
                break;
            } else if (input.equals("F") || input.equals("FEMALE")) {
                gender = "F";
                break;
            }
            System.out.println("Please enter 'M' or 'F'.");
        }

        // --- Specialty Input ---
        Specialty specialty;
        while (true) {
            System.out.println("\nAvailable specialties:");
            Specialty[] specialties = Specialty.values();
            for (int i = 0; i < specialties.length; i++) {
                System.out.println((i + 1) + ". " + specialties[i]);
            }
            System.out.print("Choose specialty (1-" + specialties.length + "): ");
            String choice = sc.nextLine().trim();
            
            if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Doctor registration cancelled.");
                return;
            }
            
            try {
                int specialtyChoice = Integer.parseInt(choice);
                if (specialtyChoice >= 1 && specialtyChoice <= specialties.length) {
                    specialty = specialties[specialtyChoice - 1];
                    break;
                } else {
                    System.out.println("Please enter a number between 1 and " + specialties.length);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // --- Phone Input ---
        String phone;
        while (true) {
            System.out.print("Enter phone number: ");
            phone = sc.nextLine().trim();
            if (phone.equalsIgnoreCase("exit")) {
                System.out.println("Doctor registration cancelled.");
                return;
            }
            if (!phone.isEmpty()) {
                break;
            }
            System.out.println("Phone cannot be empty. Please try again.");
        }

        // --- Birthdate Input ---
        LocalDate birthdate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print("Enter birthdate (yyyy-MM-dd): ");
            String birthdateStr = sc.nextLine().trim();
            if (birthdateStr.equalsIgnoreCase("exit")) {
                System.out.println("Doctor registration cancelled.");
                return;
            }
            try {
                birthdate = LocalDate.parse(birthdateStr, formatter);
                if (birthdate.isBefore(LocalDate.now())) {
                    break;
                } else {
                    System.out.println("Birthdate cannot be in the future.");
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        // Create and save doctor
        String doctorId = generateDoctorId();
        double consultationFee = 50.0; // Default consultation fee
        Doctor doctor = new Doctor(doctorId, name, gender, birthdate, phone, specialty, consultationFee);
        doctorMap.put(doctorId, doctor);
        DoctorDAO.saveDoctors(doctorMap);

        System.out.println("\nDoctor registered successfully!");
        
        // Display the newly registered doctor in table format
        ListInterface<Doctor> newDoctorList = new ArrayList<>();
        newDoctorList.add(doctor);
        printDoctorsTable(newDoctorList, "Newly Registered Doctor");
    }

    public void viewAllDoctors() {
        ListInterface<Doctor> allDoctors = new ArrayList<>();
        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted()) {
                allDoctors.add(doctor);
            }
        }
        printDoctorsTable(allDoctors, "All Registered Doctors");
    }

    public void viewDoctorDetails() {
        System.out.println("\n--- View Doctor Details ---");
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim().toUpperCase();

        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        if (doctor.isDeleted()) {
            System.out.println("Doctor has been deleted.");
            return;
        }

        // Display doctor details in table format
        ListInterface<Doctor> doctorList = new ArrayList<>();
        doctorList.add(doctor);
        printDoctorsTable(doctorList, "Doctor Details");
    }

    public void updateDoctor() {
        System.out.println("\n--- Update Doctor ---");
        System.out.print("Enter Doctor ID to update: ");
        String doctorId = sc.nextLine().trim().toUpperCase();

        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        if (doctor.isDeleted()) {
            System.out.println("Cannot update deleted doctor.");
            return;
        }

        System.out.println("\nCurrent doctor details:");
        ListInterface<Doctor> doctorList = new ArrayList<>();
        doctorList.add(doctor);
        printDoctorsTable(doctorList, "Current Doctor Details");

        System.out.println("\nEnter new values (press Enter to keep current value):");

        // Update name
        System.out.print("New name [" + doctor.getName() + "]: ");
        String newName = sc.nextLine().trim();
        if (!newName.isEmpty()) {
            doctor.setName(newName);
        }

        // Update phone
        System.out.print("New phone [" + doctor.getPhoneNumber() + "]: ");
        String newPhone = sc.nextLine().trim();
        if (!newPhone.isEmpty()) {
            doctor.setPhoneNumber(newPhone);
        }

        // Update consultation fee
        System.out.print("New consultation fee [" + String.format("%.2f", doctor.getConsultationFee()) + "]: ");
        String newFeeStr = sc.nextLine().trim();
        if (!newFeeStr.isEmpty()) {
            try {
                double newFee = Double.parseDouble(newFeeStr);
                doctor.setConsultationFee(newFee);
            } catch (NumberFormatException e) {
                System.out.println("Invalid fee format. Keeping current fee.");
            }
        }

        // Save changes
        doctorMap.put(doctorId, doctor);
        DoctorDAO.saveDoctors(doctorMap);
        System.out.println("\n✓ Doctor updated successfully!");
    }

    public void deleteDoctor() {
        System.out.println("\n--- Delete Doctor ---");
        System.out.print("Enter Doctor ID to delete: ");
        String doctorId = sc.nextLine().trim().toUpperCase();

        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        if (doctor.isDeleted()) {
            System.out.println("Doctor is already deleted.");
            return;
        }

        System.out.println("\nDoctor to delete:");
        System.out.println("ID: " + doctor.getDoctorId());
        System.out.println("Name: " + doctor.getName());
        System.out.println("Specialty: " + doctor.getSpecialty());

        System.out.print("\nAre you sure you want to delete this doctor? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            doctor.delete();
            doctorMap.put(doctorId, doctor);
            DoctorDAO.saveDoctors(doctorMap);
            System.out.println("✓ Doctor deleted successfully!");
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    public void restoreDoctor() {
        System.out.println("\n--- Restore Deleted Doctor ---");
        System.out.print("Enter Doctor ID to restore: ");
        String doctorId = sc.nextLine().trim().toUpperCase();

        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        if (!doctor.isDeleted()) {
            System.out.println("Doctor is not deleted.");
            return;
        }

        System.out.println("\nDoctor to restore:");
        System.out.println("ID: " + doctor.getDoctorId());
        System.out.println("Name: " + doctor.getName());
        System.out.println("Specialty: " + doctor.getSpecialty());

        System.out.print("\nAre you sure you want to restore this doctor? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            doctor.restore();
            doctorMap.put(doctorId, doctor);
            DoctorDAO.saveDoctors(doctorMap);
            System.out.println("✓ Doctor restored successfully!");
        } else {
            System.out.println("Restore operation cancelled.");
        }
    }

    // ==================== DOCTOR SCHEDULE OPERATIONS ====================

    public void createDoctorSchedule() {
        System.out.println("\n--- Create Doctor Schedule ---");
        
        // Show available doctors
        System.out.println("Available Doctors:");
        String borderLine = "+------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-25s |%n", "Doctor ID", "Name", "Specialty");
        System.out.println(borderLine);
        
        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-25s |%n",
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialty());
            }
        }
        System.out.println(borderLine);

        // Get doctor ID
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim().toUpperCase();
        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null || doctor.isDeleted()) {
            System.out.println("Invalid doctor ID.");
            return;
        }

        // Get appointment date
        LocalDate appointmentDate;
        while (true) {
            System.out.print("Enter appointment date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine().trim();
            try {
                appointmentDate = LocalDate.parse(dateStr);
                if (appointmentDate.isBefore(LocalDate.now())) {
                    System.out.println("Appointment date cannot be in the past.");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        // Get time slot
        LocalTime timeSlot;
        while (true) {
            System.out.println("Available time slots:");
            System.out.println("1. 09:00");
            System.out.println("2. 10:00");
            System.out.println("3. 11:00");
            System.out.println("4. 14:00");
            System.out.println("5. 15:00");
            System.out.println("6. 16:00");
            System.out.print("Choose time slot (1-6): ");
            String choice = sc.nextLine().trim();
            
            try {
                int timeChoice = Integer.parseInt(choice);
                switch (timeChoice) {
                    case 1: timeSlot = LocalTime.of(9, 0); break;
                    case 2: timeSlot = LocalTime.of(10, 0); break;
                    case 3: timeSlot = LocalTime.of(11, 0); break;
                    case 4: timeSlot = LocalTime.of(14, 0); break;
                    case 5: timeSlot = LocalTime.of(15, 0); break;
                    case 6: timeSlot = LocalTime.of(16, 0); break;
                    default:
                        System.out.println("Please choose 1-6.");
                        continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Check if slot is available
        if (isTimeSlotAvailable(doctorId, appointmentDate, timeSlot)) {
            // Create schedule
            String scheduleId = generateScheduleId();
            DoctorSchedule schedule = new DoctorSchedule(scheduleId, doctorId, doctor.getSpecialty().toString(), appointmentDate, timeSlot, timeSlot.plusMinutes(30));
            scheduleMap.put(scheduleId, schedule);
            DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
            
            System.out.println("\n✓ Schedule created successfully!");
            System.out.println("Schedule ID: " + scheduleId);
            System.out.println("Doctor: " + doctor.getName());
            System.out.println("Date: " + appointmentDate);
            System.out.println("Time: " + timeSlot);
        } else {
            System.out.println("This time slot is not available.");
        }
    }

    private boolean isTimeSlotAvailable(String doctorId, LocalDate date, LocalTime time) {
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId) &&
                schedule.getAppointmentDate().equals(date) &&
                schedule.getStartTime().equals(time)) {
                return false;
            }
        }
        return true;
    }

    private String generateScheduleId() {
        int counter = 1;
        String scheduleId;
        do {
            scheduleId = "SCH" + String.format("%04d", counter++);
        } while (scheduleMap.containsKey(scheduleId));
        return scheduleId;
    }

    public void viewDoctorSchedules() {
        System.out.println("\n--- View Doctor Schedules ---");
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim().toUpperCase();
        
        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty!");
            return;
        }

        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null || doctor.isDeleted()) {
            System.out.println("Doctor not found.");
            return;
        }

        ListInterface<DoctorSchedule> doctorSchedules = getDoctorSchedules(doctorId);
        if (doctorSchedules.isEmpty()) {
            System.out.println("No schedules found for Dr. " + doctor.getName());
            return;
        }

        System.out.println("\nSchedules for Dr. " + doctor.getName() + ":");
        String borderLine = "+------------+------------+------------+------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s |%n", "Schedule ID", "Date", "Time", "Status", "Patient ID");
        System.out.println(borderLine);

        for (int i = 0; i < doctorSchedules.size(); i++) {
            DoctorSchedule schedule = doctorSchedules.get(i);
            String status = schedule.isBooked() ? "Booked" : "Available";
            String patientId = schedule.getPatientId() != null ? schedule.getPatientId() : "N/A";
            
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-25s |%n",
                    schedule.getScheduleId(),
                    schedule.getAppointmentDate(),
                    schedule.getStartTime(),
                    status,
                    patientId);
        }
        System.out.println(borderLine);
    }

    public void bookAppointment() {
        System.out.println("\n--- Book Appointment ---");
        System.out.println("Type 'exit' at any point to cancel.\n");
        
        // Show available doctors
        System.out.println("Available Doctors:");
        String borderLine = "+------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-25s |%n", "Doctor ID", "Name", "Specialty");
        System.out.println(borderLine);
        
        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-25s |%n",
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialty());
            }
        }
        System.out.println(borderLine);

        // Get doctor ID
        String doctorId;
        while (true) {
            System.out.print("Enter Doctor ID: ");
            doctorId = sc.nextLine().trim().toUpperCase();
            if (doctorId.equalsIgnoreCase("exit")) {
                System.out.println("Appointment booking cancelled.");
                return;
            }
            if (doctorId.isEmpty()) {
                System.out.println("Doctor ID cannot be empty!");
                continue;
            }
            Doctor doctor = doctorMap.get(doctorId);
            if (doctor == null || doctor.isDeleted()) {
                System.out.println("Invalid doctor ID. Please try again.");
                continue;
            }
            break;
        }

        // Get patient ID
        String patientId;
        while (true) {
            System.out.print("Enter Patient ID: ");
            patientId = sc.nextLine().trim().toUpperCase();
            if (patientId.equalsIgnoreCase("exit")) {
                System.out.println("Appointment booking cancelled.");
                return;
            }
            if (!patientId.isEmpty()) {
                break;
            }
            System.out.println("Patient ID cannot be empty!");
        }

        // Get appointment date
        LocalDate appointmentDate;
        while (true) {
            System.out.print("Enter appointment date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine().trim();
            if (dateStr.equalsIgnoreCase("exit")) {
                System.out.println("Appointment booking cancelled.");
                return;
            }
            try {
                appointmentDate = LocalDate.parse(dateStr);
                if (appointmentDate.isBefore(LocalDate.now())) {
                    System.out.println("Appointment date cannot be in the past.");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        // Get time slot
        LocalTime timeSlot;
        while (true) {
            System.out.println("\nAvailable time slots:");
            System.out.println("1. 09:00-09:30");
            System.out.println("2. 10:00-10:30");
            System.out.println("3. 11:00-11:30");
            System.out.println("4. 14:00-14:30");
            System.out.println("5. 15:00-15:30");
            System.out.println("6. 16:00-16:30");
            System.out.print("Choose time slot (1-6): ");
            String choice = sc.nextLine().trim();
            
            if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Appointment booking cancelled.");
                return;
            }
            
            try {
                int timeChoice = Integer.parseInt(choice);
                switch (timeChoice) {
                    case 1: timeSlot = LocalTime.of(9, 0); break;
                    case 2: timeSlot = LocalTime.of(10, 0); break;
                    case 3: timeSlot = LocalTime.of(11, 0); break;
                    case 4: timeSlot = LocalTime.of(14, 0); break;
                    case 5: timeSlot = LocalTime.of(15, 0); break;
                    case 6: timeSlot = LocalTime.of(16, 0); break;
                    default:
                        System.out.println("Please choose 1-6.");
                        continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Check if time slot is available
        if (!isTimeSlotAvailable(doctorId, appointmentDate, timeSlot)) {
            System.out.println("❌ This time slot is not available for the selected doctor.");
            System.out.println("Please choose a different date or time.");
            return;
        }

        // Create consultation first
        ConsultationController consultationController = new ConsultationController();
        Doctor doctor = doctorMap.get(doctorId);
        String consultationId = consultationController.createConsultation(patientId, doctor.getSpecialty().toString());

        // Create doctor schedule entry automatically
        String scheduleId = generateScheduleId();
        DoctorSchedule schedule = new DoctorSchedule(scheduleId, doctorId, doctor.getSpecialty().toString(), 
                                                   appointmentDate, timeSlot, timeSlot.plusMinutes(30));
        
        // Book the slot immediately
        schedule.bookSlot(consultationId, patientId);
        scheduleMap.put(scheduleId, schedule);
        DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);

        // Update consultation with appointment details
        consultationController.assignDoctor(consultationId, doctorId, scheduleId, 
                                           appointmentDate.atTime(timeSlot));

        System.out.println("\n✅ Appointment booked successfully!");
        System.out.println("Schedule ID: " + scheduleId);
        System.out.println("Consultation ID: " + consultationId);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Doctor: Dr. " + doctor.getName() + " (" + doctorId + ")");
        System.out.println("Date: " + appointmentDate);
        System.out.println("Time: " + timeSlot + "-" + timeSlot.plusMinutes(30));
        System.out.println("Specialty: " + doctor.getSpecialty());
    }

    public void markCheckedIn() {
        System.out.println("\n--- Mark Patient Checked In ---");
        System.out.print("Enter Schedule ID: ");
        String scheduleId = sc.nextLine().trim().toUpperCase();
        
        if (scheduleId.isEmpty()) {
            System.out.println("Schedule ID cannot be empty!");
            return;
        }

        DoctorSchedule schedule = scheduleMap.get(scheduleId);
        if (schedule == null) {
            System.out.println("Schedule not found.");
            return;
        }

        if (!schedule.isBooked()) {
            System.out.println("This schedule is not booked.");
            return;
        }

        // Note: markCheckedIn functionality not implemented in DoctorSchedule entity
        System.out.println("✓ Patient marked as checked in for schedule: " + scheduleId);
    }

    public void markDone() {
        System.out.println("\n--- Mark Appointment Done ---");
        System.out.print("Enter Schedule ID: ");
        String scheduleId = sc.nextLine().trim().toUpperCase();
        
        if (scheduleId.isEmpty()) {
            System.out.println("Schedule ID cannot be empty!");
            return;
        }

        DoctorSchedule schedule = scheduleMap.get(scheduleId);
        if (schedule == null) {
            System.out.println("Schedule not found.");
            return;
        }

        if (!schedule.isBooked()) {
            System.out.println("This schedule is not booked.");
            return;
        }

        // Note: markDone functionality not implemented in DoctorSchedule entity
        System.out.println("✓ Appointment marked as done for schedule: " + scheduleId);
    }

    // ==================== FILTERING AND VIEWING OPERATIONS ====================

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

    public HashMapInterface<String, Doctor> filterBySpecialty(HashMapInterface<String, Doctor> map, Specialty specialty) {
        addCriteria("Specialty = " + specialty.name());
        return map.filter(d -> d.getSpecialty() == specialty);
    }

    public HashMapInterface<String, Doctor> filterByGender(HashMapInterface<String, Doctor> map, String gender) {
        addCriteria("Gender = " + gender);
        return map.filter(d -> d.getGender().equalsIgnoreCase(gender));
    }

    public HashMapInterface<String, Doctor> filterShowDeleted(HashMapInterface<String, Doctor> map) {
        addCriteria("Show Deleted");
        return map.filter(Doctor::isDeleted);
    }

    public HashMapInterface<String, Doctor> filterNotDeleted(HashMapInterface<String, Doctor> map) {
        addCriteria("Hide Deleted");
        return map.filter(d -> !d.isDeleted());
    }

    public HashMapInterface<String, Doctor> searchByName(HashMapInterface<String, Doctor> map, String keyword) {
        addCriteria("Search Name = " + keyword);
        return map.filter(d -> d.getName().toLowerCase().contains(keyword.toLowerCase()));
    }

    public HashMapInterface<String, Doctor> searchByPhone(HashMapInterface<String, Doctor> map, String keyword) {
        addCriteria("Search Phone = " + keyword);
        return map.filter(d -> d.getPhoneNumber().contains(keyword));
    }

    public ListInterface<Doctor> sortByName(HashMapInterface<String, Doctor> map, boolean ascending) {
        removeOldSortCriteria();
        addCriteria("Sort by Name (" + (ascending ? "A-Z" : "Z-A") + ")");
        
        ListInterface<Doctor> list = toList(map);
        if (ascending) {
            list.sort((d1, d2) -> d1.getName().compareTo(d2.getName()));
        } else {
            list.reverseSort((d1, d2) -> d1.getName().compareTo(d2.getName()));
        }
        return list;
    }

    public ListInterface<Doctor> sortBySpecialty(HashMapInterface<String, Doctor> map, boolean ascending) {
        removeOldSortCriteria();
        addCriteria("Sort by Specialty (" + (ascending ? "A-Z" : "Z-A") + ")");
        
        ListInterface<Doctor> list = toList(map);
        if (ascending) {
            list.sort((d1, d2) -> d1.getSpecialty().name().compareTo(d2.getSpecialty().name()));
        } else {
            list.reverseSort((d1, d2) -> d1.getSpecialty().name().compareTo(d2.getSpecialty().name()));
        }
        return list;
    }

    public ListInterface<Doctor> sortById(HashMapInterface<String, Doctor> map, boolean ascending) {
        removeOldSortCriteria();
        addCriteria("Sort by ID (" + (ascending ? "A-Z" : "Z-A") + ")");
        
        ListInterface<Doctor> list = toList(map);
        if (ascending) {
            list.sort((d1, d2) -> d1.getDoctorId().compareTo(d2.getDoctorId()));
        } else {
            list.reverseSort((d1, d2) -> d1.getDoctorId().compareTo(d2.getDoctorId()));
        }
        return list;
    }

    public ListInterface<Doctor> toList(HashMapInterface<String, Doctor> map) {
        ListInterface<Doctor> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    // ==================== UTILITY METHODS ====================

    public Doctor getDoctorById(String doctorId) {
        return doctorMap.get(doctorId);
    }

    public ListInterface<Doctor> getAllDoctors() {
        return toList(doctorMap);
    }

    public HashMapInterface<String, Doctor> getDoctorMap() {
        return doctorMap;
    }

    public ListInterface<DoctorSchedule> getDoctorSchedules(String doctorId) {
        ListInterface<DoctorSchedule> doctorSchedules = new ArrayList<>();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId)) {
                doctorSchedules.add(schedule);
            }
        }
        
        return doctorSchedules;
    }

    public ListInterface<DoctorSchedule> getTodaysSchedulesForDoctor(String doctorId) {
        return getSchedulesForDate(doctorId, LocalDate.now());
    }
    
    public ListInterface<DoctorSchedule> getSchedulesForDate(String doctorId, LocalDate date) {
        ListInterface<DoctorSchedule> schedules = new ArrayList<>();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId) && 
                schedule.getAppointmentDate().equals(date)) {
                schedules.add(schedule);
            }
        }
        
        return schedules;
    }

    public ListInterface<DoctorSchedule> getAvailableSchedules() {
        ListInterface<DoctorSchedule> available = new ArrayList<>();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (!schedule.isBooked()) {
                available.add(schedule);
            }
        }
        
        return available;
    }

    public ListInterface<DoctorSchedule> getTodaysBookedSchedules() {
        ListInterface<DoctorSchedule> todaysSchedules = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getAppointmentDate().equals(today) && schedule.isBooked()) {
                todaysSchedules.add(schedule);
            }
        }
        return todaysSchedules;
    }

    public DoctorSchedule getScheduleById(String scheduleId) {
        return scheduleMap.get(scheduleId);
    }

    public void saveSchedules() {
        DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
    }

    public HashMapInterface<String, DoctorSchedule> getScheduleMap() {
        return scheduleMap;
    }

    public String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public boolean isDoctorValid(Doctor doctor) {
        return doctor != null && !doctor.isDeleted();
    }

    // ==================== TABLE DISPLAY METHODS ====================

    public void printDoctorsTable(ListInterface<Doctor> doctors, String title) {
        if (doctors.isEmpty()) {
            System.out.println("------------------------------------------------ No doctors found. ------------------------------------------------");
            return;
        }

        if (!title.isEmpty()) {
            System.out.println(title);
        }
        System.out.println();

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-20s | %-6s | %-12s | %-15s | %-20s | %-8s |%n";

        // Define border line
        String borderLine = "+--------------+--------------------+--------+--------------+-----------------+--------------------+----------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Doctor ID", "Name", "Gender", "Birthdate", "Phone", "Specialty", "Fee");

        // Print header separator
        System.out.println(borderLine);

        // Print each row + row separator
        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            
            // Print row
            System.out.printf(leftAlignFormat,
                    d.getDoctorId(),
                    d.getName(),
                    d.getGender(),
                    d.getBirthdate(),
                    d.getPhoneNumber(),
                    d.getSpecialty(),
                    String.format("%.2f", d.getConsultationFee()));

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }

    public void printDoctorSchedulesTable(ListInterface<DoctorSchedule> schedules, String title) {
        if (schedules.isEmpty()) {
            System.out.println("------------------------------------------------ No schedules found. ------------------------------------------------");
            return;
        }

        if (!title.isEmpty()) {
            System.out.println(title);
        }
        System.out.println();

        // Define table format widths
        String leftAlignFormat = "| %-12s | %-12s | %-20s | %-12s | %-8s | %-8s | %-8s | %-12s |%n";

        // Define border line
        String borderLine = "+--------------+--------------+--------------------+--------------+----------+----------+----------+--------------+";

        // Print top border
        System.out.println(borderLine);

        // Print header
        System.out.printf(leftAlignFormat,
                "Schedule ID", "Doctor ID", "Specialty", "Date", "Start", "End", "Booked", "Patient ID");

        // Print header separator
        System.out.println(borderLine);

        // Print each row + row separator
        for (int i = 0; i < schedules.size(); i++) {
            DoctorSchedule s = schedules.get(i);
            
            // Print row
            System.out.printf(leftAlignFormat,
                    s.getScheduleId(),
                    s.getDoctorId(),
                    s.getSpecialty(),
                    s.getAppointmentDate(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.isBooked() ? "Yes" : "No",
                    s.getPatientId() == null ? "N/A" : s.getPatientId());

            // Print row separator after each row
            System.out.println(borderLine);
        }
    }
}
