package entity;

/**
 * Abstract Human class
 * Base class for all people in the system.
 * Author: [Your Name]
 */
public abstract class Human {
    protected String id;
    protected String name;
    protected int age;
    protected String gender;

    public Human(String id, String name, int age, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }

    @Override
    public String toString() {
        return id + " - " + name + " (" + age + ", " + gender + ")";
    }
}
