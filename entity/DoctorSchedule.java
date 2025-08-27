package entity;

import java.time.LocalDateTime;

public class DoctorSchedule {
    private String scheduleId;
    private String doctorId;
    private String specialty;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBooked;
    private String consultationId; // link if booked

    public DoctorSchedule(String scheduleId, String doctorId, String specialty, LocalDateTime startTime,
            LocalDateTime endTime) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.specialty = specialty;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = false;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void bookSlot(String consultationId) {
        this.isBooked = true;
        this.consultationId = consultationId;
    }

    public void freeSlot() {
        this.isBooked = false;
        this.consultationId = null;
    }
}
