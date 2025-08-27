package control;

import entity.*;
import adt.*;
import java.time.format.DateTimeFormatter;

public class ViewPatientControl {
    private PatientControl patientControl;
    private HashMapInterface<String, Patient> patientMap;
    private final ListInterface<String> activeCriteria = new ArrayList<>();
    private String currentSortOption = null;

    public ViewPatientControl(PatientControl patientControl) {
        this.patientControl = patientControl;
        this.patientMap = patientControl.getPatientMap();
    }

    public HashMapInterface<String, Patient> getPatientMap() {
        return this.patientMap;
    }

    public void clearCriteria() {
        activeCriteria.clear();
        currentSortOption = null;
    }

    public void addCriteria(String text) {
        activeCriteria.add(text);
    }

    private void removeOldSortCriteria() {
        for (int i = 0; i < activeCriteria.size(); i++) {
            if (activeCriteria.get(i).startsWith("Sort =")) {
                activeCriteria.remove(i);
                i--; // adjust index since list shrinks
            }
        }
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

        return map.filter(p -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String searchText = String.join(" ",
                    p.getPatientId(),
                    p.getName(),
                    p.getGender(),
                    p.getPhoneNumber(),
                    p.getBirthdate().format(fmt),
                    p.getClass().getSimpleName(),
                    p.isDeleted() ? "deleted" : "active").toLowerCase();

            return searchText.contains(lower);
        });
    }

    // --- Sort ---
    // Ascending sort
    public void sortPatients(ListInterface<Patient> list, String option) {
        removeOldSortCriteria();
        currentSortOption = option;

        switch (option) {
            case "1":
                list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
                addCriteria("Sort = Patient ID (Asc)");
                break;
            case "2":
                list.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                addCriteria("Sort = Name (Asc)");
                break;
            case "3":
                list.sort((p1, p2) -> p1.getGender().compareToIgnoreCase(p2.getGender()));
                addCriteria("Sort = Gender (Asc)");
                break;
            case "4":
                list.sort((p1, p2) -> p1.getBirthdate().compareTo(p2.getBirthdate()));
                addCriteria("Sort = Birthdate (Asc)");
                break;
            default:
                currentSortOption = null;
                break;
        }
    }

    // Descending sort
    public void reverseSortPatients(ListInterface<Patient> list, String option) {
        removeOldSortCriteria();
        currentSortOption = option + "_desc"; // mark as descending

        switch (option) {
            case "1":
                list.reverseSort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
                addCriteria("Sort = Patient ID (Desc)");
                break;
            case "2":
                list.reverseSort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                addCriteria("Sort = Name (Desc)");
                break;
            case "3":
                list.reverseSort((p1, p2) -> p1.getGender().compareToIgnoreCase(p2.getGender()));
                addCriteria("Sort = Gender (Desc)");
                break;
            case "4":
                list.reverseSort((p1, p2) -> p1.getBirthdate().compareTo(p2.getBirthdate()));
                addCriteria("Sort = Birthdate (Desc)");
                break;
            default:
                currentSortOption = null;
                break;
        }
    }

    public ListInterface<Patient> toList(HashMapInterface<String, Patient> map) {
        ListInterface<Patient> list = map.toList();
        if (currentSortOption != null) {
            if (currentSortOption.endsWith("_desc")) {
                String originalOption = currentSortOption.replace("_desc", "");
                reverseSortPatients(list, originalOption);
            } else {
                sortPatients(list, currentSortOption);
            }
        } else {
            // Default sort by Patient ID ascending
            list.sort((p1, p2) -> p1.getPatientId().compareTo(p2.getPatientId()));
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
