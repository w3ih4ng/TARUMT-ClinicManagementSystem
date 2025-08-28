package entity;

import java.time.LocalDate;

/**
 * Tutor patient entity
 * @author Your Name
 */
public class Tutor extends Patient {
    private String tutorId; // school identity
    private String faculty;

    public Tutor(String patientId, String tutorId, String name, String gender, LocalDate birthdate, String phoneNumber,
            String faculty) {
        super(patientId, name, gender, birthdate, phoneNumber);
        this.tutorId = tutorId;
        this.faculty = faculty;
    }

    public String getTutorId() {
        return tutorId;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return super.toString() + " " +
                tutorId + " " +
                faculty;
    }

}
