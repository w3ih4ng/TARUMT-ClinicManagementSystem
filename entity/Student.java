package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Student extends Patient {
    private String studentId; // school identity

    public Student(String patientId, String studentId, String name, String gender, LocalDate birthdate,
            String phoneNumber) {
        super(patientId, name, gender, birthdate, phoneNumber);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return super.toString() + " " +
                studentId + " " ;
    }

}
