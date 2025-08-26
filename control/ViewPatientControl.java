package control;

import entity.*;
import adt.*;


public class ViewPatientControl {
    private PatientControl patientControl;
    private HashMapInterface<String, Patient> patientMap;
    private final ListInterface<String> activeCriteria = new ArrayList<>();

    public ViewPatientControl(PatientControl patientControl) {
        this.patientControl = patientControl;
        this.patientMap = patientControl.getPatientMap();
    }

    public HashMapInterface<String, Patient> getPatientMap() {
        return this.patientMap;
    }

    public void clearCriteria() {
        activeCriteria.clear();
    }

    public void addCriteria(String text) {
        activeCriteria.add(text);
    }

    public String getCriteriaSummary() {
        if (activeCriteria.isEmpty()) {
            return "Filters : None";
        } else {
            return "Filters : " + String.join(" | ", activeCriteria); 
        }
        
    }

    // --- Filter ---
    public HashMapInterface<String, Patient> filterByRole(HashMapInterface<String, Patient> map, String roleChoice) {
        switch (roleChoice) {
            case "1": addCriteria("Role = Student"); break;
            case "2": addCriteria("Role = Tutor"); break;
            case "3": addCriteria("Role = Staff"); break;
            default:  addCriteria("Role = Unknown");
        }
        return map.filter(p -> {
            if (p.isDeleted())
                return false;
            switch (roleChoice) {
                case "1":
                    return p instanceof Student;
                case "2":
                    return p instanceof Tutor;
                case "3":
                    return p instanceof Staff;
                default:
                    return false;
            }
        });
    }

    public HashMapInterface<String, Patient> filterByGender(HashMapInterface<String, Patient> map, String gender) {
        addCriteria("Gender = " + gender);
        return map.filter(p -> !p.isDeleted() && p.getGender().equalsIgnoreCase(gender));
    }

    public HashMapInterface<String, Patient> filterShowDeleted(HashMapInterface<String, Patient> map) {
        addCriteria("Show Deleted");
        return map.filter(p -> p.isDeleted());
    }

    public HashMapInterface<String, Patient> filterNotDeleted(HashMapInterface<String, Patient> map) {
        addCriteria("Do Not Show Deleted");
        return map.filter(p -> !p.isDeleted());
    }

    // --- Search ---
    public HashMapInterface<String, Patient> searchPatients(HashMapInterface<String, Patient> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();
        return map.filter(p -> p.toString().toLowerCase().contains(lower));
    }

    // --- Display wrapper ---
    public void printPatients(HashMapInterface<String, Patient> map) {
        patientControl.printPatientsTable(map.toList(), getCriteriaSummary()); // convert to list just for printing
    }
}
