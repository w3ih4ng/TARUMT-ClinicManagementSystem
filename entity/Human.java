package entity;

public abstract class Human {
    protected String name;
    protected String gender;
    protected String birthdate;
    protected String phoneNumber;
    protected boolean isDeleted; // NEW field

    public Human(String name, String gender, String birthdate, String phoneNumber) {
        this.name = name;
        this.gender = gender;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.isDeleted = false; // default = active
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getBirthdate() { return birthdate; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isDeleted() { return isDeleted; }

    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // soft delete
    public void delete() { this.isDeleted = true; }

    // restore
    public void restore() { this.isDeleted = false; }

    @Override
    public String toString() {
        return String.format("Name: %s, Gender: %s, Birthdate: %s, Phone: %s, Deleted: %s",
                name, gender, birthdate, phoneNumber, isDeleted ? "Yes" : "No");
    }
}
