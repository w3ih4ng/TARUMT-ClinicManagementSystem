package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class for all human entities in the system
 * @author Your Name
 */
public abstract class Human {
    protected String name;
    protected String gender;
    protected LocalDate birthdate; // changed from String to LocalDate
    protected String phoneNumber;
    protected boolean isDeleted; // soft delete flag

    public Human(String name, String gender, LocalDate birthdate, String phoneNumber) {
        this.name = name;
        this.gender = gender;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.isDeleted = false; // default = active
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // soft delete
    public void delete() {
        this.isDeleted = true;
    }

    // restore
    public void restore() {
        this.isDeleted = false;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("Name: %s, Gender: %s, Birthdate: %s, Phone: %s, Deleted: %s",
                name, gender, birthdate.format(fmt), phoneNumber, isDeleted ? "Yes" : "No");
    }
}
