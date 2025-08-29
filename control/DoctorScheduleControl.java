package control;

import entity.*;
import adt.*;
import dao.DoctorScheduleDAO;
import dao.DoctorDAO;
import dao.ConsultationDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Control class for doctor schedule management
 * @author Your Name
 */
public class DoctorScheduleControl {
    private HashMapInterface<String, DoctorSchedule> scheduleMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private Scanner sc;

    public DoctorScheduleControl() {
        this.scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.sc = new Scanner(System.in);
    }

    // === NEW BUSINESS METHODS ===

    /**
     * Get schedule by ID
     */
    public DoctorSchedule getScheduleById(String scheduleId) {
        return scheduleMap.get(scheduleId);
    }

    /**
     * Mark schedule as checked in
     */
    public void markCheckedIn(String scheduleId) {
        DoctorSchedule schedule = scheduleMap.get(scheduleId);
        if (schedule != null) {
            // For now we'll use consultationId field to track status
            // "CHECKED_IN" indicates patient checked in but consultation not started
            if (schedule.isBooked()) {
                schedule.bookSlot("CHECKED_IN", schedule.getPatientId());
                scheduleMap.put(scheduleId, schedule);
                DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
                System.out.println("Schedule marked as checked in: " + scheduleId);
            }
        }
    }

    /**
     * Mark schedule as done (consultation completed)
     */
    public void markDone(String scheduleId) {
        DoctorSchedule schedule = scheduleMap.get(scheduleId);
        if (schedule != null) {
            // Mark as DONE in consultationId field
            schedule.bookSlot("DONE", schedule.getPatientId());
            scheduleMap.put(scheduleId, schedule);
            DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
            System.out.println("Schedule marked as done: " + scheduleId);
        }
    }

    /**
     * Get today's booked schedules for check-in
     */
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

    // --- Get all schedules for a specific doctor ---
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

    // --- Get today's schedules for a specific doctor ---
    public ListInterface<DoctorSchedule> getTodaysSchedulesForDoctor(String doctorId) {
        return getSchedulesForDate(doctorId, LocalDate.now());
    }
    
    // --- Get schedules for a specific date for a specific doctor ---
    public ListInterface<DoctorSchedule> getSchedulesForDate(String doctorId, LocalDate date) {
        ListInterface<DoctorSchedule> dateSchedules = new ArrayList<>();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId) && 
                schedule.getAppointmentDate().equals(date) && 
                schedule.isBooked()) {
                dateSchedules.add(schedule);
            }
        }
        
        // Sort by start time for better display
        dateSchedules.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
        
        return dateSchedules;
    }
    
    // --- Create consultation record for appointment ---
    private void createConsultationForAppointment(String consultationId, String patientId, String doctorId, String specialty) {
        // Create consultation directly and save to file
        Consultation consultation = new Consultation(consultationId, patientId, specialty, null);
        consultation.assignDoctor(doctorId, null, java.time.LocalDateTime.now());
        
        // Load existing consultations, add new one, and save
        adt.HashMapInterface<String, Consultation> consultationMap = dao.ConsultationDAO.loadConsultations();
        consultationMap.put(consultationId, consultation);
        dao.ConsultationDAO.saveConsultations(consultationMap);
        
        System.out.println("Consultation created for appointment: " + consultationId);
    }

    // --- Get booked appointments for a specific doctor ---
    public ListInterface<DoctorSchedule> getBookedAppointments(String doctorId) {
        ListInterface<DoctorSchedule> bookedAppointments = new ArrayList<>();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId) && schedule.isBooked()) {
                bookedAppointments.add(schedule);
            }
        }
        
        return bookedAppointments;
    }

    // --- Get all schedules for a specific specialty ---
    public ListInterface<DoctorSchedule> getSchedulesBySpecialty(String specialty) {
        ListInterface<DoctorSchedule> specialtySchedules = new ArrayList<>();
        
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getSpecialty().equals(specialty)) {
                specialtySchedules.add(schedule);
            }
        }
        
        return specialtySchedules;
    }

    // --- Book appointment for a patient ---
    public boolean bookAppointment(String doctorId, String patientId, LocalDate appointmentDate, LocalTime startTime) {
        // Check if the slot is already booked
        if (isSlotBooked(doctorId, appointmentDate, startTime)) {
            System.out.println("This time slot is already booked.");
            return false;
        }

        // Create a new schedule slot for this appointment
        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return false;
        }

        // Calculate end time (1 hour appointment)
        LocalTime endTime = startTime.plusHours(1);

        // Create the schedule slot
        DoctorSchedule schedule = DoctorScheduleDAO.createAppointmentSlot(doctorId, doctor.getSpecialty().toString(), 
                                                                         appointmentDate, startTime, endTime);

        // Generate consultation ID for the appointment
        String consultationId = ConsultationDAO.generateConsultationId();
        
        // Book the slot
        schedule.bookSlot(consultationId, patientId);
        scheduleMap.put(schedule.getScheduleId(), schedule);
        DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
        
        // Create consultation record for the appointment
        createConsultationForAppointment(consultationId, patientId, doctorId, doctor.getSpecialty().toString());
        
        System.out.println("Appointment booked successfully!");
        System.out.println("   - Doctor: " + doctorId);
        System.out.println("   - Patient: " + patientId);
        System.out.println("   - Date: " + appointmentDate);
        System.out.println("   - Time: " + startTime + " - " + endTime);
        System.out.println("   - Consultation ID: " + consultationId);
        
        return true;
    }

    // --- Cancel appointment ---
    public boolean cancelAppointment(String doctorId, LocalDate appointmentDate, LocalTime startTime) {
        String scheduleId = findScheduleId(doctorId, appointmentDate, startTime);
        if (scheduleId == null) {
            System.out.println("Schedule slot not found for the specified time.");
            return false;
        }

        DoctorSchedule schedule = scheduleMap.get(scheduleId);
        if (schedule == null) {
            System.out.println("Schedule not found.");
            return false;
        }

        if (!schedule.isBooked()) {
            System.out.println("This time slot is not booked.");
            return false;
        }

        String consultationId = schedule.getConsultationId();
        String patientId = schedule.getPatientId();
        schedule.freeSlot();
        DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
        
        System.out.println("Appointment cancelled successfully!");
        System.out.println("   - Doctor: " + doctorId);
        System.out.println("   - Patient: " + patientId);
        System.out.println("   - Date: " + appointmentDate);
        System.out.println("   - Time: " + startTime + " - " + schedule.getEndTime());
        System.out.println("   - Consultation ID: " + consultationId);
        
        return true;
    }

    // --- Check if a slot is already booked ---
    private boolean isSlotBooked(String doctorId, LocalDate appointmentDate, LocalTime startTime) {
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId) && 
                schedule.getAppointmentDate().equals(appointmentDate) && 
                schedule.getStartTime().equals(startTime) &&
                schedule.isBooked()) {
                return true;
            }
        }
        return false;
    }

    // --- Find schedule ID by doctor, date, and time ---
    private String findScheduleId(String doctorId, LocalDate appointmentDate, LocalTime startTime) {
        for (String key : scheduleMap.keySet()) {
            DoctorSchedule schedule = scheduleMap.get(key);
            if (schedule.getDoctorId().equals(doctorId) && 
                schedule.getAppointmentDate().equals(appointmentDate) && 
                schedule.getStartTime().equals(startTime)) {
                return key;
            }
        }
        return null;
    }

    // --- Display booked appointments for a doctor ---
    public void displayBookedAppointments(String doctorId) {
        Doctor doctor = doctorMap.get(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found: " + doctorId);
            return;
        }

        ListInterface<DoctorSchedule> bookedAppointments = getBookedAppointments(doctorId);
        if (bookedAppointments.isEmpty()) {
            System.out.println("No booked appointments found for Dr. " + doctor.getName());
            return;
        }

        System.out.println("\n--- Dr. " + doctor.getName() + " (" + doctor.getSpecialty() + ") Booked Appointments ---");
        String borderLine = "+------------+------------+----------+----------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-8s | %-8s | %-10s |%n", "Date", "Patient", "Start", "End", "Consultation");
        System.out.println(borderLine);

        for (int i = 0; i < bookedAppointments.size(); i++) {
            DoctorSchedule schedule = bookedAppointments.get(i);
            System.out.printf("| %-10s | %-10s | %-8s | %-8s | %-10s |%n",
                    schedule.getAppointmentDate().toString(),
                    schedule.getPatientId(),
                    schedule.getStartTime().toString(),
                    schedule.getEndTime().toString(),
                    schedule.getConsultationId());
        }
        System.out.println(borderLine);
    }

    // --- Display all booked appointments by specialty ---
    public void displayBookedAppointmentsBySpecialty(String specialty) {
        ListInterface<DoctorSchedule> specialtySchedules = getSchedulesBySpecialty(specialty);
        ListInterface<DoctorSchedule> bookedAppointments = new ArrayList<>();
        
        // Filter only booked appointments
        for (int i = 0; i < specialtySchedules.size(); i++) {
            DoctorSchedule schedule = specialtySchedules.get(i);
            if (schedule.isBooked()) {
                bookedAppointments.add(schedule);
            }
        }
        
        if (bookedAppointments.isEmpty()) {
            System.out.println("No booked appointments found for specialty: " + specialty);
            return;
        }

        System.out.println("\n--- " + specialty + " Booked Appointments ---");
        String borderLine = "+------------+------------+----------+----------+------------+";
        System.out.println(borderLine);
        System.out.printf("| %-10s | %-10s | %-8s | %-8s | %-10s |%n", "Doctor", "Date", "Start", "End", "Patient");
        System.out.println(borderLine);

        for (int i = 0; i < bookedAppointments.size(); i++) {
            DoctorSchedule schedule = bookedAppointments.get(i);
            Doctor doctor = doctorMap.get(schedule.getDoctorId());
            String doctorName = doctor != null ? doctor.getName() : schedule.getDoctorId();
            
            System.out.printf("| %-10s | %-10s | %-8s | %-8s | %-10s |%n",
                    doctorName,
                    schedule.getAppointmentDate().toString(),
                    schedule.getStartTime().toString(),
                    schedule.getEndTime().toString(),
                    schedule.getPatientId());
        }
        System.out.println(borderLine);
    }

    // --- Save schedules ---
    public void saveSchedules() {
        DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
    }

    // --- Get schedule map for other controls ---
    public HashMapInterface<String, DoctorSchedule> getScheduleMap() {
        return scheduleMap;
    }
}
