package control;

import entity.*;
import adt.*;
import utility.FilterCriteriaUtil;
import java.time.format.DateTimeFormatter;

public class ViewPatientControl {
    private PatientRecordControl patientRecordControl;
    private HashMapInterface<String, Patient> patientMap;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public ViewPatientControl(PatientRecordControl patientRecordControl) {
        this.patientRecordControl = patientRecordControl;
        this.patientMap = patientRecordControl.getPatientMap();
    }

    public HashMapInterface<String, Patient> getPatientMap() {
        return this.patientMap;
    }

    public void clearCriteria() {
        criteriaUtil.clearCriteria();
    }

    public void addCriteria(String text) {
        criteriaUtil.addCriteria(text);
    }

    private void removeOldSortCriteria() {
        criteriaUtil.removeOldSortCriteria();
    }

    public String getCriteriaSummary() {
        return criteriaUtil.getCriteriaSummary();
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
        criteriaUtil.setCurrentSortOption(option);

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
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    // Descending sort
    public void reverseSortPatients(ListInterface<Patient> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option + "_desc"); // mark as descending

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
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public ListInterface<Patient> toList(HashMapInterface<String, Patient> map) {
        ListInterface<Patient> list = map.toList();
        if (criteriaUtil.getCurrentSortOption() != null) {
            if (criteriaUtil.getCurrentSortOption().endsWith("_desc")) {
                String originalOption = criteriaUtil.getCurrentSortOption().replace("_desc", "");
                reverseSortPatients(list, originalOption);
            } else {
                sortPatients(list, criteriaUtil.getCurrentSortOption());
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
        patientRecordControl.printPatientsTable(list, getCriteriaSummary());
    }

    public void printPatientsFromList(ListInterface<Patient> list) {
        patientRecordControl.printPatientsTable(list, getCriteriaSummary());
    }

}
