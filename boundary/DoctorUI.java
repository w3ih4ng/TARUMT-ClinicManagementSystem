package boundary;

import control.DoctorController;
import entity.*;
import adt.*;
import utility.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Consolidated Doctor UI - combines all doctor-related boundary functionality
 * Handles doctor management, viewing, and schedule operations
 * @author Your Name
 */
public class DoctorUI {
    private Scanner sc;
    private DoctorController doctorController;

    public DoctorUI(DoctorController doctorController) {
        this.sc = new Scanner(System.in);
        this.doctorController = doctorController;
    }

    // ==================== MAIN DOCTOR MANAGEMENT MENU ====================

    public void mainMenu() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Doctor Management Module");
            
            System.out.println("1. Doctor Details Management");
            System.out.println("2. Doctor Schedule Management");
            System.out.println("0. Back to Staff Menu");
            System.out.print("\n\nEnter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.pushNavigation("Doctor Details");
                    doctorDetailsManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("Doctor Schedule");
                    scheduleManagement(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "0": 
                    return; // back to Staff menu
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== DOCTOR DETAILS MANAGEMENT ====================

    private void doctorDetailsManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Doctor Details Management");
            
            System.out.println("1. Add Doctor");
            System.out.println("2. View Doctors");
            System.out.println("3. Update Doctor");
            System.out.println("4. Delete Doctor");
            System.out.println("5. Restore Doctor");
            System.out.println("0. Back to Doctor Management");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Add New Doctor");
                    doctorController.registerDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.pushNavigation("View All Doctors");
                    viewAllDoctors(); 
                    utility.SystemUtil.popNavigation();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Update Doctor");
                    doctorController.updateDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Delete Doctor");
                    doctorController.deleteDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("Restore Doctor");
                    doctorController.restoreDoctor(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== VIEW ALL DOCTORS ====================

    private void viewAllDoctors() {
        while (true) {
            utility.SystemUtil.showMenuHeader("View All Doctors");
            
            System.out.println("1. View All Doctors");
            System.out.println("2. Filter Doctors");
            System.out.println("3. Sort Doctors");
            System.out.println("4. Search Doctor");
            System.out.println("5. View Doctor Details");
            System.out.println("0. Back to Doctor Details Management");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("All Doctors");
                    doctorController.viewAllDoctors(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Filter Doctors");
                    filterDoctors(); 
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Sort Doctors");
                    sortDoctors(); 
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Search Doctor");
                    searchDoctors(); 
                    break;
                case "5": 
                    utility.SystemUtil.showSectionHeader("View Doctor Details");
                    doctorController.viewDoctorDetails(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== FILTER DOCTORS ====================

    private void filterDoctors() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Filter Doctors");
            
            System.out.println("1. Filter by Specialty");
            System.out.println("2. Filter by Gender");
            System.out.println("3. Filter by Active Status");
            System.out.println("0. Back to View All Doctors");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Filter by Specialty");
                    filterBySpecialty(); 
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Filter by Gender");
                    filterByGender(); 
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Filter by Active Status");
                    filterByStatus(); 
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== SORT DOCTORS ====================

    private void sortDoctors() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Sort Doctors");
            
            System.out.println("1. Sort by Name");
            System.out.println("2. Sort by Specialty");
            System.out.println("3. Sort by ID");
            System.out.println("0. Back to View All Doctors");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("Sort by Name");
                    sortByName(); 
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("Sort by Specialty");
                    sortBySpecialty(); 
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Sort by ID");
                    sortById(); 
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== SCHEDULE MANAGEMENT ====================

    private void scheduleManagement() {
        while (true) {
            utility.SystemUtil.showMenuHeader("Doctor Schedule Management");
            
            System.out.println("1. View All Schedules");
            System.out.println("2. View Doctor Schedule");
            System.out.println("3. View Available Schedules");
            System.out.println("4. Book Appointment");
            System.out.println("0. Back to Doctor Management");
            System.out.print("\n\nChoose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": 
                    utility.SystemUtil.showSectionHeader("All Doctor Schedules");
                    viewAllSchedules(); 
                    break;
                case "2": 
                    utility.SystemUtil.showSectionHeader("View Doctor Schedule");
                    doctorController.viewDoctorSchedules(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "3": 
                    utility.SystemUtil.showSectionHeader("Available Schedules");
                    viewAvailableSchedules(); 
                    break;
                case "4": 
                    utility.SystemUtil.showSectionHeader("Book Appointment");
                    doctorController.bookAppointment(); 
                    utility.SystemUtil.pauseForUser();
                    break;
                case "0": 
                    return;
                default: 
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // ==================== HELPER METHODS ====================

    private void searchDoctors() {
        System.out.println("\n--- Search Doctors ---");
        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty!");
            return;
        }
        
        // Use the search functionality from DoctorController
        HashMapInterface<String, Doctor> searchResults = doctorController.searchByName(doctorController.getDoctorMap(), keyword);
        
        if (searchResults.isEmpty()) {
            System.out.println("No doctors found matching: " + keyword);
        } else {
            System.out.println("\nSearch Results:");
            displayDoctorsFromMap(searchResults);
        }
        utility.SystemUtil.pauseForUser();
    }

    private void filterBySpecialty() {
        System.out.println("\n--- Filter by Specialty ---");
        System.out.println("Available specialties:");
        Specialty[] specialties = Specialty.values();
        for (int i = 0; i < specialties.length; i++) {
            System.out.println((i + 1) + ". " + specialties[i]);
        }
        
        System.out.print("Select specialty number: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= specialties.length) {
                Specialty selectedSpecialty = specialties[choice - 1];
                HashMapInterface<String, Doctor> filtered = doctorController.filterBySpecialty(doctorController.getDoctorMap(), selectedSpecialty);
                displayDoctorsFromMap(filtered);
            } else {
                System.out.println("Invalid specialty selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void filterByGender() {
        System.out.println("\n--- Filter by Gender ---");
        System.out.print("Enter gender (M/F): ");
        String input = sc.nextLine().trim().toUpperCase();
        
        if (input.isEmpty()) {
            System.out.println("Gender cannot be empty!");
            return;
        }
        
        // Convert input to consistent format
        String gender;
        if (input.equals("M") || input.equals("MALE")) {
            gender = "M";
        } else if (input.equals("F") || input.equals("FEMALE")) {
            gender = "F";
        } else {
            System.out.println("Please enter 'M' or 'F'.");
            return;
        }
        
        HashMapInterface<String, Doctor> filtered = doctorController.filterByGender(doctorController.getDoctorMap(), gender);
        displayDoctorsFromMap(filtered);
        utility.SystemUtil.pauseForUser();
    }

    private void filterByStatus() {
        System.out.println("\n--- Filter by Status ---");
        System.out.println("1. Active Doctors");
        System.out.println("2. Deleted Doctors");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            HashMapInterface<String, Doctor> filtered;
            if (choice == 1) {
                filtered = doctorController.filterNotDeleted(doctorController.getDoctorMap());
                System.out.println("\n--- Active Doctors ---");
            } else if (choice == 2) {
                filtered = doctorController.filterShowDeleted(doctorController.getDoctorMap());
                System.out.println("\n--- Deleted Doctors ---");
            } else {
                System.out.println("Invalid choice.");
                return;
            }
            displayDoctorsFromMap(filtered);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void sortByName() {
        System.out.println("\n--- Sort by Name ---");
        System.out.println("1. A-Z (Ascending)");
        System.out.println("2. Z-A (Descending)");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            boolean ascending = (choice == 1);
            ListInterface<Doctor> sorted = doctorController.sortByName(doctorController.getDoctorMap(), ascending);
            displayDoctorsFromList(sorted);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void sortBySpecialty() {
        System.out.println("\n--- Sort by Specialty ---");
        System.out.println("1. A-Z (Ascending)");
        System.out.println("2. Z-A (Descending)");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            boolean ascending = (choice == 1);
            ListInterface<Doctor> sorted = doctorController.sortBySpecialty(doctorController.getDoctorMap(), ascending);
            displayDoctorsFromList(sorted);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void sortById() {
        System.out.println("\n--- Sort by ID ---");
        System.out.println("1. A-Z (Ascending)");
        System.out.println("2. Z-A (Descending)");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            boolean ascending = (choice == 1);
            ListInterface<Doctor> sorted = doctorController.sortById(doctorController.getDoctorMap(), ascending);
            displayDoctorsFromList(sorted);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        utility.SystemUtil.pauseForUser();
    }

    private void viewAllSchedules() {
        System.out.println("\n--- All Doctor Schedules ---");
        if (doctorController.getScheduleMap().isEmpty()) {
            System.out.println("No schedules found.");
        } else {
            displaySchedulesFromMap(doctorController.getScheduleMap());
        }
        utility.SystemUtil.pauseForUser();
    }

    private void viewAvailableSchedules() {
        System.out.println("\n--- Available Schedules ---");
        ListInterface<DoctorSchedule> available = doctorController.getAvailableSchedules();
        if (available.isEmpty()) {
            System.out.println("No available schedules found.");
        } else {
            displaySchedulesFromList(available);
        }
        utility.SystemUtil.pauseForUser();
    }

    // ==================== DISPLAY METHODS ====================

    private void displayDoctorsFromMap(HashMapInterface<String, Doctor> doctorMap) {
        if (doctorMap.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        String borderLine = "+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-25s | %-25s |%n", "Doctor ID", "Name", "Specialty", "Phone");
        System.out.println(borderLine);

        for (String key : doctorMap.keySet()) {
            Doctor doctor = doctorMap.get(key);
            if (!doctor.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-25s | %-25s |%n",
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        doctor.getPhoneNumber());
            }
        }
        System.out.println(borderLine);
    }

    private void displayDoctorsFromList(ListInterface<Doctor> doctorList) {
        if (doctorList.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        String borderLine = "+------------+---------------------------+---------------------------+---------------------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-25s | %-25s | %-25s |%n", "Doctor ID", "Name", "Specialty", "Phone");
        System.out.println(borderLine);

        for (int i = 0; i < doctorList.size(); i++) {
            Doctor doctor = doctorList.get(i);
            if (!doctor.isDeleted()) {
                System.out.printf("| %-10s | %-25s | %-25s | %-25s |%n",
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        doctor.getPhoneNumber());
            }
        }
        System.out.println(borderLine);
    }

    private void displaySchedulesFromMap(HashMapInterface<String, DoctorSchedule> scheduleMap) {
        if (scheduleMap.isEmpty()) {
            System.out.println("No schedules found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-10s | %-10s |%n", 
            "Schedule ID", "Doctor ID", "Patient ID", "Date", "Start Time", "Status");
        System.out.println(borderLine);

        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-10s | %-10s |%n",
                    schedule.getScheduleId(),
                    schedule.getDoctorId(),
                    schedule.getPatientId() != null ? schedule.getPatientId() : "N/A",
                    schedule.getAppointmentDate(),
                    schedule.getStartTime(),
                    schedule.isBooked() ? "Booked" : "Available");
        }
        System.out.println(borderLine);
    }

    private void displaySchedulesFromList(ListInterface<DoctorSchedule> scheduleList) {
        if (scheduleList.isEmpty()) {
            System.out.println("No schedules found.");
            return;
        }

        String borderLine = "+------------+------------+------------+------------+------------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-10s | %-10s |%n", 
            "Schedule ID", "Doctor ID", "Patient ID", "Date", "Start Time", "Status");
        System.out.println(borderLine);

        for (int i = 0; i < scheduleList.size(); i++) {
            DoctorSchedule schedule = scheduleList.get(i);
            System.out.printf("| %-10s | %-10s | %-10s | %-10s | %-10s | %-10s |%n",
                    schedule.getScheduleId(),
                    schedule.getDoctorId(),
                    schedule.getPatientId() != null ? schedule.getPatientId() : "N/A",
                    schedule.getAppointmentDate(),
                    schedule.getStartTime(),
                    schedule.isBooked() ? "Booked" : "Available");
        }
        System.out.println(borderLine);
    }
}
