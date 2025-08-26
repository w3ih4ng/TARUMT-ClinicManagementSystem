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
            case "1":
                addCriteria("Role = Student");
                break;
            case "2":
                addCriteria("Role = Tutor");
                break;
            case "3":
                addCriteria("Role = Staff");
                break;
            default:
                addCriteria("Role = Unknown");
        }
        return map.filter(p -> {
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
        return map.filter(p -> p.getGender().equalsIgnoreCase(gender));
    }

    public HashMapInterface<String, Patient> filterShowDeleted(HashMapInterface<String, Patient> map) {
        addCriteria("Show Deleted");
        return map.filter(Patient::isDeleted);
    }

    public HashMapInterface<String, Patient> filterNotDeleted(HashMapInterface<String, Patient> map) {
        addCriteria("Hide Deleted");
        return map.filter(p -> !p.isDeleted());
    }

    // --- Search ---
    public HashMapInterface<String, Patient> searchPatients(HashMapInterface<String, Patient> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();
        return map.filter(p -> p.toString().toLowerCase().contains(lower));
    }

    // --- Sort ---
    public ListInterface<Patient> sortPatients(HashMapInterface<String, Patient> map, String option) {
        ListInterface<Patient> list = map.toList();

        switch (option) {
            case "1": // Patient ID
                list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
                break;
            case "2": // Name
                list.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case "3": // Gender
                list.sort((p1, p2) -> p1.getGender().compareToIgnoreCase(p2.getGender()));
                break;
            case "4": // Birthdate
                list.sort((p1, p2) -> p1.getBirthdate().compareTo(p2.getBirthdate()));
                break;
            default:
                // leave as is
                break;
        }

        return list;
    }

    // --- Display wrapper ---
    public void printPatients(HashMapInterface<String, Patient> map) {
        ListInterface<Patient> list = map.toList();
        list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
        patientControl.printPatientsTable(list, getCriteriaSummary());
    }

    public void printPatientsFromList(ListInterface<Patient> list) {
        patientControl.printPatientsTable(list, getCriteriaSummary());
    }

}
