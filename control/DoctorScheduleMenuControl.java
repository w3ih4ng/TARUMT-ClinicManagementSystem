package control;

import entity.*;
import adt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Control class for doctor schedule menu business logic
 * @author Your Name
 */
public class DoctorScheduleMenuControl {
    private DoctorScheduleControl scheduleControl;
    private DoctorRecordControl doctorControl;
    private PatientQueueControl queueControl;
    private Scanner sc;

    public DoctorScheduleMenuControl(DoctorScheduleControl scheduleControl, DoctorRecordControl doctorControl, PatientQueueControl queueControl) {
        this.scheduleControl = scheduleControl;
        this.doctorControl = doctorControl;
        this.queueControl = queueControl;
        this.sc = new Scanner(System.in);
    }

    /**
     * Get all doctors for display
     */
    public ListInterface<Doctor> getAllDoctors() {
        return doctorControl.getAllDoctors();
    }

    /**
     * Get today's booked schedules
     */
    public ListInterface<DoctorSchedule> getTodaysBookedSchedules() {
        return scheduleControl.getTodaysBookedSchedules();
    }

    /**
     * Get schedule by ID
     */
    public DoctorSchedule getScheduleById(String scheduleId) {
        return scheduleControl.getScheduleById(scheduleId);
    }

    /**
     * Mark schedule as checked in
     */
    public void markCheckedIn(String scheduleId) {
        scheduleControl.markCheckedIn(scheduleId);
    }

    /**
     * Get time slot from user selection
     */
    public LocalTime getTimeSlotFromChoice(int timeChoice) {
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
                return null;
        }
        return selectedTime;
    }

    /**
     * Get specialty from user selection
     */
    public String getSpecialtyFromChoice(int specialtyChoice) {
        String selectedSpecialty = null;
        
        switch (specialtyChoice) {
            case 1: selectedSpecialty = "CARDIOLOGY"; break;
            case 2: selectedSpecialty = "NEUROLOGY"; break;
            case 3: selectedSpecialty = "PEDIATRICS"; break;
            case 4: selectedSpecialty = "ORTHOPEDICS"; break;
            case 5: selectedSpecialty = "DERMATOLOGY"; break;
            case 6: selectedSpecialty = "GENERAL MEDICINE"; break;
            default:
                return null;
        }
        return selectedSpecialty;
    }

    /**
     * Check if appointment is for today
     */
    public boolean isAppointmentForToday(LocalDate appointmentDate) {
        return appointmentDate.equals(LocalDate.now());
    }

    /**
     * Check if schedule is booked
     */
    public boolean isScheduleBooked(DoctorSchedule schedule) {
        return schedule.isBooked();
    }

    /**
     * Get available schedules for check-in
     */
    public ListInterface<DoctorSchedule> getAvailableSchedulesForCheckIn(ListInterface<DoctorSchedule> todaysSchedules) {
        ListInterface<DoctorSchedule> availableSchedules = new ArrayList<>();
        for (int i = 0; i < todaysSchedules.size(); i++) {
            DoctorSchedule schedule = todaysSchedules.get(i);
            String status = schedule.getConsultationId(); // Using consultationId as status field
            if (status == null || (!status.equals("CHECKED_IN") && !status.equals("DONE"))) {
                availableSchedules.add(schedule);
            }
        }
        return availableSchedules;
    }

    /**
     * Get doctor name from schedule
     */
    public String getDoctorNameFromSchedule(DoctorSchedule schedule) {
        Doctor doctor = doctorControl.getDoctorMap().get(schedule.getDoctorId());
        return doctor != null ? doctor.getName() : "Unknown";
    }

    /**
     * Format time for display
     */
    public String formatTime(LocalTime time) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(timeFormatter);
    }

    /**
     * Check-in appointment patient
     */
    public void checkInAppointmentPatient(String scheduleId) {
        queueControl.checkInAppointment(scheduleId, scheduleControl);
    }
}
