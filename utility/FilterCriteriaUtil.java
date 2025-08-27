package utility;

import adt.ArrayList;
import adt.ListInterface;

/**
 * Utility to manage filter/sort criteria strings and current sort option.
 * Centralizes common logic used across various View*Control classes.
 */
public class FilterCriteriaUtil {
    private final ListInterface<String> activeCriteria = new ArrayList<>();
    private String currentSortOption = null;

    public void clearCriteria() {
        activeCriteria.clear();
        currentSortOption = null;
    }

    public void addCriteria(String text) {
        activeCriteria.add(text);
    }

    public void removeOldSortCriteria() {
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

    public String getCurrentSortOption() {
        return currentSortOption;
    }

    public void setCurrentSortOption(String option) {
        this.currentSortOption = option;
    }
}


