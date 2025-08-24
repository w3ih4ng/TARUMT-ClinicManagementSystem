package entity;

public class Tutor extends Patient {
    private String tutorId; // school identity

    public Tutor(String patientId, String tutorId, String name, String gender, String birthdate, String phoneNumber) {
        super(patientId, name, gender, birthdate, phoneNumber);
        this.tutorId = tutorId;
    }

    public String getTutorId() { return tutorId; }

    @Override
    public String toString() {
        return super.toString() + String.format(" [TutorID: %s] [Tutor]", tutorId);
    }
}
