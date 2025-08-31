package dao;

import entity.DoctorSchedule;
import adt.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for DoctorSchedule persistence
 * @author Your Name
 */
public class DoctorScheduleDAO {
    private static final String FILE_NAME = "data/doctor_schedules.txt";
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static int scheduleCounter = 1001; // Start from S1001

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring doctor schedules file: " + e.getMessage());
        }
    }

    public static void saveDoctorSchedules(HashMapInterface<String, DoctorSchedule> scheduleMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Write header comment
            pw.println("# ScheduleID|DoctorID|Specialty|AppointmentDate|StartTime|EndTime|isBooked|PatientID");
            
            for (int i = 0; i < scheduleMap.keySet().size(); i++) {
                String key = scheduleMap.keySet().get(i);
                DoctorSchedule schedule = scheduleMap.get(key);
                if (schedule != null)
                    pw.println(toFileString(schedule));
            }
        } catch (IOException e) {
            System.out.println("Error saving doctor schedules: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, DoctorSchedule> loadDoctorSchedules() {
        ensureFile();
        HashMapInterface<String, DoctorSchedule> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                DoctorSchedule schedule = fromFileString(line);
                if (schedule != null)
                    map.put(schedule.getScheduleId(), schedule);
            }
        } catch (IOException e) {
            System.out.println("Error loading doctor schedules: " + e.getMessage());
        }

        return map;
    }

    private static String toFileString(DoctorSchedule schedule) {
        String patientId = schedule.getPatientId() != null ? schedule.getPatientId() : "NONE";

        return String.join("|",
                schedule.getScheduleId(),
                schedule.getDoctorId(),
                schedule.getSpecialty(),
                schedule.getAppointmentDate().format(dateFormatter),
                schedule.getStartTime().format(timeFormatter),
                schedule.getEndTime().format(timeFormatter),
                String.valueOf(schedule.isBooked()),
                patientId
        );
    }

    private static DoctorSchedule fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 8) {
                throw new IllegalArgumentException("Expected 8 columns, got " + parts.length);
            }

            String scheduleId = parts[0];
            String doctorId = parts[1];
            String specialty = parts[2];
            LocalDate appointmentDate = LocalDate.parse(parts[3], dateFormatter);
            LocalTime startTime = LocalTime.parse(parts[4], timeFormatter);
            LocalTime endTime = LocalTime.parse(parts[5], timeFormatter);
            boolean isBooked = Boolean.parseBoolean(parts[6]);
            String patientId = parts[7].equals("NONE") ? null : parts[7];

            DoctorSchedule schedule = new DoctorSchedule(scheduleId, doctorId, specialty, appointmentDate, startTime, endTime);

            if (isBooked && patientId != null) {
                schedule.bookSlot(patientId);
            }

            return schedule;
        } catch (Exception e) {
            System.out.println("Error parsing doctor schedule line: " + line + " -> " + e.getMessage());
        }
        return null;
    }

    public static String generateScheduleId() {
        return "S" + (scheduleCounter++);
    }

    // Helper method to create a single appointment slot
    public static DoctorSchedule createAppointmentSlot(String doctorId, String specialty, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {
        String scheduleId = generateScheduleId();
        return new DoctorSchedule(scheduleId, doctorId, specialty, appointmentDate, startTime, endTime);
    }
}
