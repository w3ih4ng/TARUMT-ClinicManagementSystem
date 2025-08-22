package entity;

/**
 * Patient entity
 * Represents a patient in the clinic system.
 * Author: [Your Name]
 */
public class Patient {
    private String id;
    private String name;
    private int age;
    private String condition;

    public Patient(String id, String name, int age, String condition) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.condition = condition;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCondition() { return condition; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCondition(String condition) { this.condition = condition; }

    @Override
    public String toString() {
        return id + " - " + name + " (" + age + ") : " + condition;
    }
}
