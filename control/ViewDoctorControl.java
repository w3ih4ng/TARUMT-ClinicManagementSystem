package control;

import entity.Doctor;
import entity.Specialty;
import adt.*;
import utility.FilterCriteriaUtil;

/**
 * Control class for viewing and filtering doctor data
 * @author Your Name
 */
public class ViewDoctorControl {
    private DoctorRecordControl doctorRecordControl;
    private HashMapInterface<String, Doctor> doctorMap;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public ViewDoctorControl(DoctorRecordControl doctorRecordControl) {
        this.doctorRecordControl = doctorRecordControl;
        this.doctorMap = doctorRecordControl.getDoctorMap();
    }

    public HashMapInterface<String, Doctor> getDoctorMap() {
        return doctorMap;
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
    public HashMapInterface<String, Doctor> filterBySpecialty(HashMapInterface<String, Doctor> map,
            Specialty specialty) {
        addCriteria("Specialty = " + specialty.name());
        return map.filter(d -> d.getSpecialty() == specialty);
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
            String searchText = String.join(" ",
                    d.getDoctorId(),
                    d.getName(),
                    d.getGender(),
                    d.getSpecialty().name(),
                    d.getPhoneNumber(),
                    String.valueOf(d.getConsultationFee()),
                    d.isDeleted() ? "deleted" : "active").toLowerCase();
            return searchText.contains(lower);
        });
    }

    // --- Sort ---
    public void sortDoctors(ListInterface<Doctor> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

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
                list.sort((d1, d2) -> d1.getSpecialty().name().compareToIgnoreCase(d2.getSpecialty().name()));
                addCriteria("Sort = Specialty (Asc)");
                break;
            case "5":
                list.sort((d1, d2) -> Double.compare(d1.getConsultationFee(), d2.getConsultationFee()));
                addCriteria("Sort = Consultation Fee (Asc)");
                break;
            case "6":
                list.sort((d1, d2) -> d1.getBirthdate().compareTo(d2.getBirthdate()));
                addCriteria("Sort = Birthdate (Asc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public void reverseSortDoctors(ListInterface<Doctor> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

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
                list.sort((d1, d2) -> d2.getSpecialty().name().compareToIgnoreCase(d1.getSpecialty().name()));
                addCriteria("Sort = Specialty (Desc)");
                break;
            case "5":
                list.sort((d1, d2) -> Double.compare(d2.getConsultationFee(), d1.getConsultationFee()));
                addCriteria("Sort = Consultation Fee (Desc)");
                break;
            case "6":
                list.sort((d1, d2) -> d2.getBirthdate().compareTo(d1.getBirthdate()));
                addCriteria("Sort = Birthdate (Desc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public ListInterface<Doctor> toList(HashMapInterface<String, Doctor> map) {
        ListInterface<Doctor> list = map.toList();
        if (criteriaUtil.getCurrentSortOption() != null) {
            sortDoctors(list, criteriaUtil.getCurrentSortOption());
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
        doctorRecordControl.printDoctorsTable(list, getCriteriaSummary());
    }

    public void printDoctorsFromList(ListInterface<Doctor> list) {
        doctorRecordControl.printDoctorsTable(list, getCriteriaSummary());
    }
}
