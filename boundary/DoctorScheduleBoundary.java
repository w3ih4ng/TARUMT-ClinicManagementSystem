package boundary;

import adt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import control.DoctorRecordControl;
import control.DoctorScheduleControl;
import control.PatientQueueControl;
import entity.Doctor;
import entity.DoctorSchedule;

/**
 * Boundary class for doctor schedule management interface
 * @author Your Name
 */
public class DoctorScheduleBoundary {
    private DoctorScheduleControl scheduleControl;
    private DoctorRecordControl doctorControl;
    private PatientQueueControl queueControl;
    private Scanner sc;

    public DoctorScheduleBoundary(DoctorScheduleControl scheduleControl, DoctorRecordControl doctorControl, PatientQueueControl queueControl) {
        this.scheduleControl = scheduleControl;
        this.doctorControl = doctorControl;
        this.queueControl = queueControl;
        this.sc = new Scanner(System.in);
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n=== Doctor Schedule Management ===");
            System.out.println("1. View Doctor's Booked Appointments");
            System.out.println("2. Book Appointment");
            System.out.println("3. Cancel Appointment");
            System.out.println("4. View Booked Appointments by Specialty");
            System.out.println("5. Check-in Appointment (Staff)");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    viewDoctorBookedAppointments();
                    break;
                case "2":
                    bookAppointment();
                    break;
                case "3":
                    cancelAppointment();
                    break;
                case "4":
                    viewBookedAppointmentsBySpecialty();
                    break;
                case "5":
                    checkInAppointment();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private void viewDoctorBookedAppointments() {
        System.out.println("\n--- View Doctor's Booked Appointments ---");
        
        // Display available doctors
        System.out.println("Available Doctors:");
        ListInterface<Doctor> doctors = doctorControl.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("❌ No doctors found.");
            return;
        }

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%d. %s (%s) - %s%n", 
                i + 1, 
                doctor.getName(), 
                doctor.getDoctorId(), 
                doctor.getSpecialty());
        }

        System.out.print("Select doctor number: ");
        try {
            int doctorIndex = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (doctorIndex >= 0 && doctorIndex < doctors.size()) {
                Doctor selectedDoctor = doctors.get(doctorIndex);
                scheduleControl.displayBookedAppointments(selectedDoctor.getDoctorId());
            } else {
                System.out.println("❌ Invalid doctor number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private void bookAppointment() {
        System.out.println("\n--- Book Appointment ---");
        
        // Display available doctors
        System.out.println("Available Doctors:");
        ListInterface<Doctor> doctors = doctorControl.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("❌ No doctors found.");
            return;
        }

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%d. %s (%s) - %s%n", 
                i + 1, 
                doctor.getName(), 
                doctor.getDoctorId(), 
                doctor.getSpecialty());
        }

        System.out.print("Select doctor number: ");
        try {
            int doctorIndex = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (doctorIndex >= 0 && doctorIndex < doctors.size()) {
                Doctor selectedDoctor = doctors.get(doctorIndex);
                System.out.println("\nBooking appointment with Dr. " + selectedDoctor.getName());
                
                // Get patient ID
                System.out.print("Enter Patient ID: ");
                String patientId = sc.nextLine().trim();
                
                // Get appointment date
                System.out.print("Enter appointment date (YYYY-MM-DD): ");
                String dateStr = sc.nextLine().trim();
                LocalDate appointmentDate;
                try {
                    appointmentDate = LocalDate.parse(dateStr);
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Invalid date format. Please use YYYY-MM-DD.");
                    return;
                }
                
                // Get time selection
                System.out.println("\nSelect Time Slot:");
                System.out.println("1. 9:00 AM - 10:00 AM");
                System.out.println("2. 10:00 AM - 11:00 AM");
                System.out.println("3. 11:00 AM - 12:00 PM");
                System.out.println("4. 1:00 PM - 2:00 PM");
                System.out.println("5. 2:00 PM - 3:00 PM");
                System.out.println("6. 3:00 PM - 4:00 PM");
                System.out.println("7. 4:00 PM - 5:00 PM");
                System.out.print("Enter time slot number: ");
                
                int timeChoice = Integer.parseInt(sc.nextLine().trim());
                LocalTime selectedTime = null;
                
                switch (timeChoice) {
                    case 1: selectedTime = LocalTime.of(9, 0); break;
                    case 2: selectedTime = LocalTime.of(10, 0); break;
                    case 3: selectedTime = LocalTime.of(11, 0); break;
                    case 4: selectedTime = LocalTime.of(13, 0); break;
                    case 5: selectedTime = LocalTime.of(14, 0); break;
                    case 6: selectedTime = LocalTime.of(15, 0); break;
                    case 7: selectedTime = LocalTime.of(16, 0); break;
                    default:
                        System.out.println("❌ Invalid time selection.");
                        return;
                }
                
                // Book the appointment
                scheduleControl.bookAppointment(selectedDoctor.getDoctorId(), patientId, appointmentDate, selectedTime);
                
            } else {
                System.out.println("❌ Invalid doctor number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private void cancelAppointment() {
        System.out.println("\n--- Cancel Appointment ---");
        
        // Display available doctors
        System.out.println("Available Doctors:");
        ListInterface<Doctor> doctors = doctorControl.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("❌ No doctors found.");
            return;
        }

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%d. %s (%s) - %s%n", 
                i + 1, 
                doctor.getName(), 
                doctor.getDoctorId(), 
                doctor.getSpecialty());
        }

        System.out.print("Select doctor number: ");
        try {
            int doctorIndex = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (doctorIndex >= 0 && doctorIndex < doctors.size()) {
                Doctor selectedDoctor = doctors.get(doctorIndex);
                System.out.println("\nCancelling appointment with Dr. " + selectedDoctor.getName());
                
                // Show doctor's booked appointments first
                scheduleControl.displayBookedAppointments(selectedDoctor.getDoctorId());
                
                // Get appointment date
                System.out.print("Enter appointment date (YYYY-MM-DD): ");
                String dateStr = sc.nextLine().trim();
                LocalDate appointmentDate;
                try {
                    appointmentDate = LocalDate.parse(dateStr);
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Invalid date format. Please use YYYY-MM-DD.");
                    return;
                }
                
                // Get time selection
                System.out.println("\nSelect Time Slot:");
                System.out.println("1. 9:00 AM - 10:00 AM");
                System.out.println("2. 10:00 AM - 11:00 AM");
                System.out.println("3. 11:00 AM - 12:00 PM");
                System.out.println("4. 1:00 PM - 2:00 PM");
                System.out.println("5. 2:00 PM - 3:00 PM");
                System.out.println("6. 3:00 PM - 4:00 PM");
                System.out.println("7. 4:00 PM - 5:00 PM");
                System.out.print("Enter time slot number: ");
                
                int timeChoice = Integer.parseInt(sc.nextLine().trim());
                LocalTime selectedTime = null;
                
                switch (timeChoice) {
                    case 1: selectedTime = LocalTime.of(9, 0); break;
                    case 2: selectedTime = LocalTime.of(10, 0); break;
                    case 3: selectedTime = LocalTime.of(11, 0); break;
                    case 4: selectedTime = LocalTime.of(13, 0); break;
                    case 5: selectedTime = LocalTime.of(14, 0); break;
                    case 6: selectedTime = LocalTime.of(15, 0); break;
                    case 7: selectedTime = LocalTime.of(16, 0); break;
                    default:
                        System.out.println("❌ Invalid time selection.");
                        return;
                }
                
                // Cancel the appointment
                scheduleControl.cancelAppointment(selectedDoctor.getDoctorId(), appointmentDate, selectedTime);
                
            } else {
                System.out.println("❌ Invalid doctor number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private void viewBookedAppointmentsBySpecialty() {
        System.out.println("\n--- View Booked Appointments by Specialty ---");
        System.out.println("Available Specialties:");
        System.out.println("1. CARDIOLOGY");
        System.out.println("2. NEUROLOGY");
        System.out.println("3. PEDIATRICS");
        System.out.println("4. ORTHOPEDICS");
        System.out.println("5. DERMATOLOGY");
        System.out.println("6. GENERAL MEDICINE");
        System.out.print("Select specialty number: ");
        
        try {
            int specialtyChoice = Integer.parseInt(sc.nextLine().trim());
            String selectedSpecialty = null;
            
            switch (specialtyChoice) {
                case 1: selectedSpecialty = "CARDIOLOGY"; break;
                case 2: selectedSpecialty = "NEUROLOGY"; break;
                case 3: selectedSpecialty = "PEDIATRICS"; break;
                case 4: selectedSpecialty = "ORTHOPEDICS"; break;
                case 5: selectedSpecialty = "DERMATOLOGY"; break;
                case 6: selectedSpecialty = "GENERAL MEDICINE"; break;
                default:
                    System.out.println("❌ Invalid specialty selection.");
                    return;
            }
            
            scheduleControl.displayBookedAppointmentsBySpecialty(selectedSpecialty);
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    /**
     * Check-in appointment patient to queue
     */
    private void checkInAppointment() {
        System.out.println("\n--- Check-in Appointment ---");
        
        // Get today's booked schedules
        ListInterface<DoctorSchedule> todaysSchedules = scheduleControl.getTodaysBookedSchedules();
        
        if (todaysSchedules.isEmpty()) {
            System.out.println("📋 No booked appointments for today");
            return;
        }
        
        // Filter for appointments that haven't been checked in yet (status != CHECKED_IN and != DONE)
        ListInterface<DoctorSchedule> availableSchedules = new ArrayList<>();
        for (int i = 0; i < todaysSchedules.size(); i++) {
            DoctorSchedule schedule = todaysSchedules.get(i);
            String status = schedule.getConsultationId(); // Using consultationId as status field
            if (status == null || (!status.equals("CHECKED_IN") && !status.equals("DONE"))) {
                availableSchedules.add(schedule);
            }
        }
        
        if (availableSchedules.isEmpty()) {
            System.out.println("📋 All today's appointments have already been checked in");
            return;
        }
        
        System.out.println("\nToday's Appointments Ready for Check-in:");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (int i = 0; i < availableSchedules.size(); i++) {
            DoctorSchedule schedule = availableSchedules.get(i);
            Doctor doctor = doctorControl.getDoctorMap().get(schedule.getDoctorId());
            String doctorName = doctor != null ? doctor.getName() : "Unknown";
            
            System.out.println((i + 1) + ". " + schedule.getScheduleId() + 
                " - Patient: " + schedule.getPatientId() + 
                " - Dr. " + doctorName +
                " - Time: " + schedule.getStartTime().format(timeFormatter) + 
                " - " + schedule.getEndTime().format(timeFormatter));
        }
        
        // Select appointment to check in
        System.out.print("Select appointment to check-in (1-" + availableSchedules.size() + "): ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice < 1 || choice > availableSchedules.size()) {
                System.out.println("❌ Invalid appointment selection");
                return;
            }
            
            DoctorSchedule selectedSchedule = availableSchedules.get(choice - 1);
            
            // Confirm check-in
            System.out.println("\nChecking in appointment:");
            System.out.println("Schedule ID: " + selectedSchedule.getScheduleId());
            System.out.println("Patient ID: " + selectedSchedule.getPatientId());
            System.out.println("Time: " + selectedSchedule.getStartTime().format(timeFormatter) + 
                " - " + selectedSchedule.getEndTime().format(timeFormatter));
            
            System.out.print("Confirm check-in? (y/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                // Check in the appointment - this will create a queue entry and update schedule status
                queueControl.checkInAppointment(selectedSchedule.getScheduleId(), scheduleControl);
                System.out.println("✅ Patient successfully checked in to queue!");
                System.out.println("📋 Patient added to appointment queue and can now be assigned to doctor");
            } else {
                System.out.println("❌ Check-in cancelled");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number");
        }
    }
}
