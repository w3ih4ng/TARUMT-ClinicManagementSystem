package control;

import entity.Medicine;
import entity.Medicine.Unit;
import adt.*;
import utility.FilterCriteriaUtil;

public class ViewMedicineControl {
    private MedicineControl medicineControl;
    private HashMapInterface<String, Medicine> medicineMap;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public ViewMedicineControl(MedicineControl medicineControl) {
        this.medicineControl = medicineControl;
        this.medicineMap = medicineControl.getMedicineMap();
    }

    public HashMapInterface<String, Medicine> getMedicineMap() {
        return medicineMap;
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

    // Filter by numeric dosage only
    public HashMapInterface<String, Medicine> filterByDosageValue(HashMapInterface<String, Medicine> map,
            double dosage) {
        addCriteria("Dosage = " + dosage);
        return map.filter(m -> !m.isDeleted() && m.getDosage() == dosage);
    }

    // Filter by unit only
    public HashMapInterface<String, Medicine> filterByUnit(HashMapInterface<String, Medicine> map, Unit unit) {
        addCriteria("Unit = " + unit.name());
        return map.filter(m -> !m.isDeleted() && m.getUnit() == unit);
    }

    public HashMapInterface<String, Medicine> filterShowDeleted(HashMapInterface<String, Medicine> map) {
        addCriteria("Show Deleted");
        return map.filter(Medicine::isDeleted);
    }

    public HashMapInterface<String, Medicine> filterNotDeleted(HashMapInterface<String, Medicine> map) {
        addCriteria("Show Active Only");
        return map.filter(m -> !m.isDeleted());
    }

    // --- Search ---
    public HashMapInterface<String, Medicine> searchMedicines(HashMapInterface<String, Medicine> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();

        return map.filter(m -> {
            String dosageStr;
            if (m.getDosage() % 1 == 0) { // whole number
                dosageStr = String.valueOf((int) m.getDosage());
            } else {
                dosageStr = String.valueOf(m.getDosage());
            }

            String searchText = String.join(" ",
                    m.getMedicineId(),
                    m.getName(),
                    dosageStr + m.getUnit().name(), // e.g., "500MG"
                    m.isDeleted() ? "deleted" : "active").toLowerCase();

            return searchText.contains(lower);
        });
    }

    public void sortMedicines(ListInterface<Medicine> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

        switch (option) {
            case "1":
                list.sort((m1, m2) -> m1.getMedicineId().compareTo(m2.getMedicineId()));
                addCriteria("Sort = Medicine ID (Asc)");
                break;
            case "2":
                list.sort((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()));
                addCriteria("Sort = Name (Asc)");
                break;
            case "3": // Sort by numeric dosage
                list.sort((m1, m2) -> Double.compare(m1.getDosage(), m2.getDosage()));
                addCriteria("Sort = Dosage (Asc)");
                break;
            case "4": // Sort by unit
                list.sort((m1, m2) -> m1.getUnit().compareTo(m2.getUnit()));
                addCriteria("Sort = Unit (Asc)");
                break;
            case "5": // Sort by price
                list.sort((m1, m2) -> Double.compare(m1.getPrice(), m2.getPrice()));
                addCriteria("Sort = Price (Asc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public void reverseSortMedicines(ListInterface<Medicine> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

        switch (option) {
            case "1":
                list.sort((m1, m2) -> m2.getMedicineId().compareTo(m1.getMedicineId()));
                addCriteria("Sort = Medicine ID (Desc)");
                break;
            case "2":
                list.sort((m1, m2) -> m2.getName().compareToIgnoreCase(m1.getName()));
                addCriteria("Sort = Name (Desc)");
                break;
            case "3":
                list.sort((m1, m2) -> Double.compare(m2.getDosage(), m1.getDosage()));
                addCriteria("Sort = Dosage (Desc)");
                break;
            case "4":
                list.sort((m1, m2) -> m2.getUnit().compareTo(m1.getUnit()));
                addCriteria("Sort = Unit (Desc)");
                break;
            case "5":
                list.sort((m1, m2) -> Double.compare(m2.getPrice(), m1.getPrice()));
                addCriteria("Sort = Price (Desc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public ListInterface<Medicine> toList(HashMapInterface<String, Medicine> map) {
        ListInterface<Medicine> list = map.toList();
        if (criteriaUtil.getCurrentSortOption() != null) {
            sortMedicines(list, criteriaUtil.getCurrentSortOption());
        } else {
            // Default ascending sort by Medicine ID
            list.sort((m1, m2) -> m1.getMedicineId().compareTo(m2.getMedicineId()));
        }
        return list;
    }

    // --- Display wrapper ---
    public void printMedicines(HashMapInterface<String, Medicine> map) {
        ListInterface<Medicine> list = map.toList();
        list.sort((m1, m2) -> m1.getMedicineId().compareTo(m2.getMedicineId()));
        medicineControl.printMedicinesTable(list, getCriteriaSummary());
    }

    public void printMedicinesFromList(ListInterface<Medicine> list) {
        medicineControl.printMedicinesTable(list, getCriteriaSummary());
    }
}
