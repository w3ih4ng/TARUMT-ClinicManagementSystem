package dao;

import entity.DoctorSchedule;
import adt.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        String consultationId = schedule.getConsultationId() != null ? schedule.getConsultationId() : "NONE";
        String patientId = schedule.getPatientId() != null ? schedule.getPatientId() : "NONE";
        
        return String.join("|",
                schedule.getScheduleId(),
                schedule.getDoctorId(),
                schedule.getSpecialty(),
                schedule.getAppointmentDate().format(dateFormatter),
                schedule.getStartTime().format(timeFormatter),
                schedule.getEndTime().format(timeFormatter),
                String.valueOf(schedule.isBooked()),
                consultationId,
                patientId
        );
    }

    private static DoctorSchedule fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length == 9) {
                String scheduleId = parts[0];
                String doctorId = parts[1];
                String specialty = parts[2];
                LocalDate appointmentDate = LocalDate.parse(parts[3], dateFormatter);
                LocalTime startTime = LocalTime.parse(parts[4], timeFormatter);
                LocalTime endTime = LocalTime.parse(parts[5], timeFormatter);
                boolean isBooked = Boolean.parseBoolean(parts[6]);
                String consultationId = parts[7].equals("NONE") ? null : parts[7];
                String patientId = parts[8].equals("NONE") ? null : parts[8];

                DoctorSchedule schedule = new DoctorSchedule(scheduleId, doctorId, specialty, appointmentDate, startTime, endTime);
                
                if (isBooked && consultationId != null) {
                    schedule.bookSlot(consultationId, patientId);
                }
                
                return schedule;
            }
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
