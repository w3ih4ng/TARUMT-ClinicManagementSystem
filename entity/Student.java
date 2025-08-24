package entity;

public class Student extends Patient {
    private String studentId; // school identity

    public Student(String patientId, String studentId, String name, String gender, String birthdate, String phoneNumber) {
        super(patientId, name, gender, birthdate, phoneNumber);
        this.studentId = studentId;
    }

    public String getStudentId() { return studentId; }

    @Override
    public String toString() {
        return super.toString() + String.format(" [StudentID: %s] [Student]", studentId);
    }
}
