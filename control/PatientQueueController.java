package control;

import entity.PatientQueueEntry;
import entity.QueueStatus;
import entity.QueueType;
import entity.DoctorSchedule;
import dao.PatientQueueDAO;
import dao.DoctorScheduleDAO;
import adt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Controller for managing patient queue operations
 * @author Your Name
 */
public class PatientQueueController {
    private HashMapInterface<String, PatientQueueEntry> queueMap;

    public PatientQueueController() {
        this.queueMap = PatientQueueDAO.loadPatientQueue();
    }

    // Add patient to queue
    public String addPatientToQueue(String patientId, String specialty, QueueType queueType) {
        String queueId = PatientQueueDAO.generateQueueId();
        LocalDateTime arrivalTime = LocalDateTime.now();
        
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, queueType, arrivalTime);
        queueMap.put(queueId, entry);
        
        PatientQueueDAO.savePatientQueue(queueMap);
        return queueId;
    }

    // Add scheduled appointment to queue
    public String addScheduledAppointment(String patientId, String specialty, LocalDateTime scheduledTime) {
        String queueId = PatientQueueDAO.generateQueueId();
        LocalDateTime arrivalTime = LocalDateTime.now();
        
        PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, QueueType.APPOINTMENT, arrivalTime);
        entry.setScheduledStartTime(scheduledTime);
        queueMap.put(queueId, entry);
        
        PatientQueueDAO.savePatientQueue(queueMap);
        return queueId;
    }

    // Assign doctor to queue entry
    public boolean assignDoctor(String queueId, String doctorId) {
        PatientQueueEntry entry = queueMap.get(queueId);
        if (entry != null && entry.isWaiting()) {
            entry.assignToDoctor(doctorId);
            PatientQueueDAO.savePatientQueue(queueMap);
            return true;
        }
        return false;
    }



    // Get all queue entries
    public HashMapInterface<String, PatientQueueEntry> getAllQueueEntries() {
        return queueMap;
    }

    // Get waiting queue entries
    public ListInterface<PatientQueueEntry> getWaitingEntries() {
        ListInterface<PatientQueueEntry> waiting = new ArrayList<>();
        for (int i = 0; i < queueMap.keySet().size(); i++) {
            String key = queueMap.keySet().get(i);
            PatientQueueEntry entry = queueMap.get(key);
            if (entry != null && entry.isWaiting()) {
                waiting.add(entry);
            }
        }
        return waiting;
    }

    // Get assigned queue entries (ready for consultation)
    public ListInterface<PatientQueueEntry> getAssignedEntries() {
        ListInterface<PatientQueueEntry> assigned = new ArrayList<>();
        for (int i = 0; i < queueMap.keySet().size(); i++) {
            String key = queueMap.keySet().get(i);
            PatientQueueEntry entry = queueMap.get(key);
            if (entry != null && entry.isAssigned()) {
                assigned.add(entry);
            }
        }
        return assigned;
    }



    // Get queue entry by ID
    public PatientQueueEntry getQueueEntry(String queueId) {
        return queueMap.get(queueId);
    }

    // Remove queue entry
    public boolean removeQueueEntry(String queueId) {
        PatientQueueEntry entry = queueMap.remove(queueId);
        if (entry != null) {
            PatientQueueDAO.savePatientQueue(queueMap);
            return true;
        }
        return false;
    }

    // Get queue entries by specialty
    public ListInterface<PatientQueueEntry> getQueueEntriesBySpecialty(String specialty) {
        ListInterface<PatientQueueEntry> specialtyEntries = new ArrayList<>();
        for (int i = 0; i < queueMap.keySet().size(); i++) {
            String key = queueMap.keySet().get(i);
            PatientQueueEntry entry = queueMap.get(key);
            if (entry != null && entry.getSpecialty().equalsIgnoreCase(specialty)) {
                specialtyEntries.add(entry);
            }
        }
        return specialtyEntries;
    }

    // Get queue entries by patient
    public ListInterface<PatientQueueEntry> getQueueEntriesByPatient(String patientId) {
        ListInterface<PatientQueueEntry> patientEntries = new ArrayList<>();
        for (int i = 0; i < queueMap.keySet().size(); i++) {
            String key = queueMap.keySet().get(i);
            PatientQueueEntry entry = queueMap.get(key);
            if (entry != null && entry.getPatientId().equals(patientId)) {
                patientEntries.add(entry);
            }
        }
        return patientEntries;
    }

    // Save queue data
    public void saveQueueData() {
        PatientQueueDAO.savePatientQueue(queueMap);
    }

    // Get available appointments from doctor schedules
    public ListInterface<PatientQueueEntry> getAvailableAppointments() {
        ListInterface<PatientQueueEntry> availableAppointments = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Load doctor schedules
        HashMapInterface<String, DoctorSchedule> scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();

        // Convert available schedules to PatientQueueEntry objects
        for (int i = 0; i < scheduleMap.keySet().size(); i++) {
            String scheduleId = scheduleMap.keySet().get(i);
            DoctorSchedule schedule = scheduleMap.get(scheduleId);

            if (schedule != null && !schedule.isBooked()) {
                // Create LocalDateTime from schedule date and time
                LocalDateTime scheduleDateTime = LocalDateTime.of(
                    schedule.getAppointmentDate(),
                    schedule.getStartTime()
                );

                // Only include future appointments
                if (scheduleDateTime.isAfter(now)) {
                    // Create a PatientQueueEntry representation of the schedule
                    PatientQueueEntry entry = new PatientQueueEntry(
                        scheduleId, // Use schedule ID as queue ID
                        null, // No patient assigned yet
                        schedule.getSpecialty(),
                        QueueType.APPOINTMENT,
                        null // No arrival time for scheduled appointments
                    );

                    // Set schedule-specific information
                    entry.setAssignedDoctorId(schedule.getDoctorId());
                    entry.setScheduledStartTime(scheduleDateTime);

                    availableAppointments.add(entry);
                }
            }
        }

        return availableAppointments;
    }

    // Add patient to scheduled appointment
    public boolean addPatientToScheduledAppointment(String patientId, String scheduleId, String specialty,
                                                   String doctorId, LocalDateTime scheduledTime) {
        try {
            // Create patient queue entry
            String queueId = PatientQueueDAO.generateQueueId();
            PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty, QueueType.APPOINTMENT, null);

            // Set scheduled appointment details
            entry.setAssignedDoctorId(doctorId);
            entry.setScheduledStartTime(scheduledTime);
            entry.setQueueStatus(QueueStatus.ASSIGNED);

            // Save to queue
            queueMap.put(queueId, entry);
            PatientQueueDAO.savePatientQueue(queueMap);

            // Mark the schedule as booked
            HashMapInterface<String, DoctorSchedule> scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();
            DoctorSchedule schedule = scheduleMap.get(scheduleId);
            if (schedule != null) {
                schedule.bookSlot(patientId);
                scheduleMap.put(scheduleId, schedule);
                DoctorScheduleDAO.saveDoctorSchedules(scheduleMap);
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error adding patient to scheduled appointment: " + e.getMessage());
            return false;
        }
    }

    // Get today's appointments for a specific doctor
    public ListInterface<DoctorSchedule> getTodaysAppointmentsForDoctor(String doctorId) {
        ListInterface<DoctorSchedule> todaysAppointments = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Load doctor schedules
        HashMapInterface<String, DoctorSchedule> scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();

        // Filter today's appointments for this doctor
        for (int i = 0; i < scheduleMap.keySet().size(); i++) {
            String key = scheduleMap.keySet().get(i);
            DoctorSchedule schedule = scheduleMap.get(key);

            if (schedule != null && schedule.getDoctorId().equals(doctorId) &&
                schedule.getAppointmentDate().equals(today)) {
                todaysAppointments.add(schedule);
            }
        }

        return todaysAppointments;
    }

    // Get ALL today's appointments from all doctors
    public ListInterface<DoctorSchedule> getAllTodaysAppointments() {
        ListInterface<DoctorSchedule> todaysAppointments = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Load doctor schedules
        HashMapInterface<String, DoctorSchedule> scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();

        // Filter all today's appointments from all doctors
        for (int i = 0; i < scheduleMap.keySet().size(); i++) {
            String key = scheduleMap.keySet().get(i);
            DoctorSchedule schedule = scheduleMap.get(key);

            if (schedule != null && schedule.getAppointmentDate().equals(today)) {
                todaysAppointments.add(schedule);
            }
        }

        return todaysAppointments;
    }

    // Check if patient is already in queue for a specific doctor
    public boolean isPatientInQueue(String patientId, String doctorId) {
        for (int i = 0; i < queueMap.keySet().size(); i++) {
            String key = queueMap.keySet().get(i);
            PatientQueueEntry entry = queueMap.get(key);
            if (entry != null && entry.getPatientId().equals(patientId) &&
                entry.getAssignedDoctorId() != null && entry.getAssignedDoctorId().equals(doctorId)) {
                return true;
            }
        }
        return false;
    }

    // Add patient to scheduled appointment queue
    public String addPatientToScheduledAppointmentQueue(String scheduleId, String patientId,
                                                       String doctorId, String specialty,
                                                       LocalDateTime appointmentTime) {
        try {
            // Check if patient is already in queue
            if (isPatientInQueue(patientId, doctorId)) {
                System.out.println("Patient " + patientId + " is already in queue for doctor " + doctorId);
                return null;
            }

            // Create patient queue entry for the scheduled appointment
            String queueId = PatientQueueDAO.generateQueueId();
            PatientQueueEntry entry = new PatientQueueEntry(queueId, patientId, specialty,
                                                          QueueType.APPOINTMENT, LocalDateTime.now());

            // Set appointment details
            entry.setAssignedDoctorId(doctorId);
            entry.setScheduledStartTime(appointmentTime);
            entry.setQueueStatus(QueueStatus.ASSIGNED);

            // Save to queue
            queueMap.put(queueId, entry);
            PatientQueueDAO.savePatientQueue(queueMap);

            return queueId;

        } catch (Exception e) {
            System.out.println("Error adding patient to scheduled appointment queue: " + e.getMessage());
            return null;
        }
    }

    // Legacy method - kept for backward compatibility
    public String addScheduledAppointmentPatientToQueue(String patientId, String doctorId) {
        try {
            // Load doctor schedules to find the patient's appointment
            HashMapInterface<String, DoctorSchedule> scheduleMap = DoctorScheduleDAO.loadDoctorSchedules();
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();

            // Find the scheduled appointment for this patient and doctor today
            DoctorSchedule foundSchedule = null;
            LocalDateTime appointmentTime = null;

            for (int i = 0; i < scheduleMap.keySet().size(); i++) {
                String key = scheduleMap.keySet().get(i);
                DoctorSchedule schedule = scheduleMap.get(key);

                if (schedule != null && schedule.getDoctorId().equals(doctorId) &&
                    schedule.getPatientId() != null && schedule.getPatientId().equals(patientId) &&
                    schedule.isBooked() && schedule.getAppointmentDate().equals(today)) {

                    // Create LocalDateTime from schedule
                    LocalDateTime scheduleTime = LocalDateTime.of(
                        schedule.getAppointmentDate(),
                        schedule.getStartTime()
                    );

                    // Check if appointment is today and not in the past
                    if (scheduleTime.isAfter(now)) {
                        foundSchedule = schedule;
                        appointmentTime = scheduleTime;
                        break;
                    }
                }
            }

            if (foundSchedule == null) {
                System.out.println("No suitable scheduled appointment found for patient " + patientId + " with doctor " + doctorId + " today");
                return null;
            }

            return addPatientToScheduledAppointmentQueue(foundSchedule.getScheduleId(), patientId,
                                                       doctorId, foundSchedule.getSpecialty(), appointmentTime);

        } catch (Exception e) {
            System.out.println("Error adding scheduled appointment patient to queue: " + e.getMessage());
            return null;
        }
    }
}

