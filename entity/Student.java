package entity;

public class Student extends Patient {
    private String program;

    public Student(String id, String name, int age, String gender, String condition, String program) {
        super(id, name, age, gender, condition);
        this.program = program;
    }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    @Override
    public String getType() {
        return "Student";
    }

    @Override
    public String toString() {
        return super.toString() + " | Program: " + program;
    }
}
