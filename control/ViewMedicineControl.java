package control;

import entity.Medicine;
import adt.*;

public class ViewMedicineControl {
    private MedicineControl medicineControl;
    private HashMapInterface<String, Medicine> medicineMap;
    private final ListInterface<String> activeCriteria = new ArrayList<>();

    public ViewMedicineControl(MedicineControl medicineControl) {
        this.medicineControl = medicineControl;
        this.medicineMap = medicineControl.getMedicineMap();
    }

    public HashMapInterface<String, Medicine> getMedicineMap() {
        return this.medicineMap;
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
    public HashMapInterface<String, Medicine> filterByDosage(HashMapInterface<String, Medicine> map, String dosage) {
        addCriteria("Dosage = " + dosage);
        return map.filter(m -> !m.isDeleted() && m.getDosage().equalsIgnoreCase(dosage));
    }

    public HashMapInterface<String, Medicine> filterByStockLessThan(HashMapInterface<String, Medicine> map, int qty) {
        addCriteria("Stock < " + qty);
        return map.filter(m -> !m.isDeleted() && m.getQuantity() < qty);
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
        return map.filter(m -> m.toString().toLowerCase().contains(lower));
    }

    // --- Display wrapper ---
    public void printMedicines(HashMapInterface<String, Medicine> map) {
        ListInterface<Medicine> list = map.toList();
        list.sort((p1, p2) -> p1.getMedicineId().compareTo(p2.getMedicineId()));
        medicineControl.printMedicinesTable(map.toList(), getCriteriaSummary()); // convert to list for printing
    }
}
