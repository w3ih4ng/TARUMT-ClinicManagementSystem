package entity;

/**
 * Abstract Patient class
 * Represents a generic patient (student, tutor, lecturer, staff).
 */
public abstract class Patient extends Human {
    protected String condition;

    public Patient(String id, String name, int age, String gender, String condition) {
        super(id, name, age, gender);
        this.condition = condition;
    }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    // Force subclasses to declare their type
    public abstract String getType();

    @Override
    public String toString() {
        return "[" + getType() + "] " + super.toString() + " | Condition: " + condition;
    }
}
