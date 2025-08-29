package control;

import entity.*;
import adt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Control class for doctor menu business logic
 * @author Your Name
 */
public class DoctorMenuControl {
    private DoctorRecordControl doctorRecordControl;
    private ConsultationControl consultationControl;
    private DoctorScheduleControl scheduleControl;
    private Scanner sc;

    public DoctorMenuControl(DoctorRecordControl doctorRecordControl, ConsultationControl consultationControl, DoctorScheduleControl scheduleControl) {
        this.doctorRecordControl = doctorRecordControl;
        this.consultationControl = consultationControl;
        this.scheduleControl = scheduleControl;
        this.sc = new Scanner(System.in);
    }

    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(String doctorId) {
        return doctorRecordControl.getDoctorById(doctorId);
    }

    /**
     * Get all doctors
     */
    public ListInterface<Doctor> getAllDoctors() {
        return doctorRecordControl.getAllDoctors();
    }

    /**
     * Get today's schedules for a doctor
     */
    public ListInterface<DoctorSchedule> getTodaysSchedulesForDoctor(String doctorId) {
        return scheduleControl.getTodaysSchedulesForDoctor(doctorId);
    }
    
    /**
     * Get schedules for a specific date for a doctor
     */
    public ListInterface<DoctorSchedule> getSchedulesForDate(String doctorId, java.time.LocalDate date) {
        return scheduleControl.getSchedulesForDate(doctorId, date);
    }

    /**
     * Get pending consultations for a doctor
     */
    public ListInterface<Consultation> getPendingConsultationsForDoctor(String doctorId) {
        return consultationControl.getPendingConsultationsForDoctor(doctorId);
    }

    /**
     * Get all consultations for a doctor
     */
    public ListInterface<Consultation> getAllConsultationsForDoctor(String doctorId) {
        return consultationControl.getAllConsultationsForDoctor(doctorId);
    }

    /**
     * Complete consultation for a doctor
     */
    public boolean completeConsultationForDoctor(String consultationId, String doctorId) {
        return consultationControl.completeConsultationForDoctor(consultationId, doctorId);
    }
    
    /**
     * Complete consultation with treatment details
     */
    public boolean completeConsultationWithTreatment(String consultationId, String doctorId, 
                                                   String diagnosis, double treatmentFee,
                                                   ListInterface<entity.MedicinePrescribed> medicines) {
        return consultationControl.completeConsultationWithTreatment(consultationId, doctorId, 
                                                                    diagnosis, treatmentFee, medicines);
    }

    /**
     * Format date for display
     */
    public String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Format time for display
     */
    public String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Get current date
     */
    public String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Check if doctor is valid and active
     */
    public boolean isDoctorValid(Doctor doctor) {
        return doctor != null && !doctor.isDeleted();
    }
}
