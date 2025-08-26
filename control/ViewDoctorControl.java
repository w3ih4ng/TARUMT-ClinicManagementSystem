package control;

import entity.Doctor;
import adt.*;

public class ViewDoctorControl {
    private DoctorControl doctorControl;
    private HashMapInterface<String, Doctor> doctorMap;
    private final ListInterface<String> activeCriteria = new ArrayList<>();

    public ViewDoctorControl(DoctorControl doctorControl) {
        this.doctorControl = doctorControl;
        this.doctorMap = doctorControl.getDoctorMap();
    }

    public HashMapInterface<String, Doctor> getDoctorMap() {
        return this.doctorMap;
    }

    public void clearCriteria() {
        activeCriteria.clear();
    }

    public void addCriteria(String text) {
        activeCriteria.add(text);
    }

    public String getCriteriaSummary() {
        if (activeCriteria.isEmpty()) return "Filters : None";
        return "Filters : " + String.join(" | ", activeCriteria);
    }

    // --- Filter ---
    public HashMapInterface<String, Doctor> filterBySpecialty(HashMapInterface<String, Doctor> map, String specialty) {
        addCriteria("Specialty = " + specialty);
        return map.filter(d -> d.getSpecialty().equalsIgnoreCase(specialty));
    }

    // --- Search ---
    public HashMapInterface<String, Doctor> searchDoctors(HashMapInterface<String, Doctor> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();
        return map.filter(d -> d.toString().toLowerCase().contains(lower));
    }

    // --- Display wrapper ---
    public void printDoctors(HashMapInterface<String, Doctor> map) {
        doctorControl.printDoctorsTable(map.toList(), getCriteriaSummary()); // convert to list just for printing
    }
}
