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
    private String status; // BOOKED, CANCELLED, MISSED, COMPLETED
    private String patientId; // patient for this appointment

    public DoctorSchedule(String scheduleId, String doctorId, String specialty, LocalDate appointmentDate,
                         LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.specialty = specialty;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "BOOKED"; // Default status when created
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void bookSlot(String patientId) {
        this.status = "BOOKED";
        this.patientId = patientId;
    }

    public void cancelSlot() {
        this.status = "CANCELLED";
        this.patientId = null;
    }

    public void markAsMissed() {
        this.status = "MISSED";
    }

    public void markAsCompleted() {
        this.status = "COMPLETED";
    }

    public boolean isBooked() {
        return "BOOKED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public boolean isMissed() {
        return "MISSED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isAvailable() {
        return "CANCELLED".equals(status) || "MISSED".equals(status);
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
            status);
    }
}
