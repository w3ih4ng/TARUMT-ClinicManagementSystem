package entity;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Doctor schedule entity for managing doctor availability
 * @author Your Name
 */
public class DoctorSchedule {
    private String scheduleId;
    private String doctorId;
    private String specialty;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isBooked;
    private String patientId; // patient for this appointment

    public DoctorSchedule(String scheduleId, String doctorId, String specialty, LocalDate appointmentDate,
                         LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.specialty = specialty;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = false;
        this.patientId = null;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public String getPatientId() {
        return patientId;
    }

    public void bookSlot(String patientId) {
        this.isBooked = true;
        this.patientId = patientId;
    }

    public void freeSlot() {
        this.isBooked = false;
        this.patientId = null;
    }

    public boolean isAvailable() {
        return !isBooked;
    }

    public String getTimeSlotString() {
        return startTime.toString() + " - " + endTime.toString();
    }

    public String getDateString() {
        return appointmentDate.toString();
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %s", 
            appointmentDate.toString(), 
            getTimeSlotString(), 
            specialty, 
            isBooked ? "Booked" : "Available");
    }
}
