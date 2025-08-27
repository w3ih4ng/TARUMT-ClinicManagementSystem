package control;

import entity.Doctor;
import adt.*;
import java.time.format.DateTimeFormatter;

public class ViewDoctorControl {
    private DoctorControl doctorControl;
    private HashMapInterface<String, Doctor> doctorMap;
    private final ListInterface<String> activeCriteria = new ArrayList<>();
    private String currentSortOption = null;

    public ViewDoctorControl(DoctorControl doctorControl) {
        this.doctorControl = doctorControl;
        this.doctorMap = doctorControl.getDoctorMap();
    }

    public HashMapInterface<String, Doctor> getDoctorMap() {
        return doctorMap;
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
                i--;
            }
        }
    }

    public String getCriteriaSummary() {
        if (activeCriteria.isEmpty())
            return "Filters : None";
        return "Filters : " + String.join(" | ", activeCriteria);
    }

    // --- Filter ---
    public HashMapInterface<String, Doctor> filterBySpecialty(HashMapInterface<String, Doctor> map, String specialty) {
        addCriteria("Specialty = " + specialty);
        return map.filter(d -> d.getSpecialty().equalsIgnoreCase(specialty));
    }

    public HashMapInterface<String, Doctor> filterByGender(HashMapInterface<String, Doctor> map, String gender) {
        addCriteria("Gender = " + gender);
        return map.filter(d -> d.getGender().equalsIgnoreCase(gender));
    }

    public HashMapInterface<String, Doctor> filterShowDeleted(HashMapInterface<String, Doctor> map) {
        addCriteria("Show Deleted");
        return map.filter(Doctor::isDeleted);
    }

    public HashMapInterface<String, Doctor> filterNotDeleted(HashMapInterface<String, Doctor> map) {
        addCriteria("Hide Deleted");
        return map.filter(d -> !d.isDeleted());
    }

    // --- Search ---
    public HashMapInterface<String, Doctor> searchDoctors(HashMapInterface<String, Doctor> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();

        return map.filter(d -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String searchText = String.join(" ",
                    d.getDoctorId(),
                    d.getName(),
                    d.getGender(),
                    d.getSpecialty(),
                    d.getPhoneNumber(),
                    d.isDeleted() ? "deleted" : "active").toLowerCase();
            return searchText.contains(lower);
        });
    }

    // --- Sort ---
    public void sortDoctors(ListInterface<Doctor> list, String option) {
        removeOldSortCriteria();
        currentSortOption = option;

        switch (option) {
            case "1":
                list.sort((d1, d2) -> d1.getDoctorId().compareTo(d2.getDoctorId()));
                addCriteria("Sort = Doctor ID (Asc)");
                break;
            case "2":
                list.sort((d1, d2) -> d1.getName().compareToIgnoreCase(d2.getName()));
                addCriteria("Sort = Name (Asc)");
                break;
            case "3":
                list.sort((d1, d2) -> d1.getGender().compareToIgnoreCase(d2.getGender()));
                addCriteria("Sort = Gender (Asc)");
                break;
            case "4":
                list.sort((d1, d2) -> d1.getSpecialty().compareToIgnoreCase(d2.getSpecialty()));
                addCriteria("Sort = Specialty (Asc)");
                break;
            default:
                currentSortOption = null;
                break;
        }
    }

    public void reverseSortDoctors(ListInterface<Doctor> list, String option) {
        removeOldSortCriteria();
        currentSortOption = option;

        switch (option) {
            case "1":
                list.sort((d1, d2) -> d2.getDoctorId().compareTo(d1.getDoctorId()));
                addCriteria("Sort = Doctor ID (Desc)");
                break;
            case "2":
                list.sort((d1, d2) -> d2.getName().compareToIgnoreCase(d1.getName()));
                addCriteria("Sort = Name (Desc)");
                break;
            case "3":
                list.sort((d1, d2) -> d2.getGender().compareToIgnoreCase(d1.getGender()));
                addCriteria("Sort = Gender (Desc)");
                break;
            case "4":
                list.sort((d1, d2) -> d2.getSpecialty().compareToIgnoreCase(d1.getSpecialty()));
                addCriteria("Sort = Specialty (Desc)");
                break;
            default:
                currentSortOption = null;
                break;
        }
    }

    public ListInterface<Doctor> toList(HashMapInterface<String, Doctor> map) {
        ListInterface<Doctor> list = map.toList();
        if (currentSortOption != null) {
            sortDoctors(list, currentSortOption);
        } else {
            // Default ascending sort by Doctor ID
            list.sort((d1, d2) -> d1.getDoctorId().compareTo(d2.getDoctorId()));
        }
        return list;
    }

    // --- Display wrapper ---
    public void printDoctors(HashMapInterface<String, Doctor> map) {
        ListInterface<Doctor> list = map.toList();
        list.sort((d1, d2) -> d1.getDoctorId().compareTo(d2.getDoctorId()));
        doctorControl.printDoctorsTable(list, getCriteriaSummary());
    }

    public void printDoctorsFromList(ListInterface<Doctor> list) {
        doctorControl.printDoctorsTable(list, getCriteriaSummary());
    }
}
