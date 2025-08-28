package control;

import entity.Stock;
import adt.*;
import utility.FilterCriteriaUtil;
import java.time.LocalDate;

/**
 * Control class for viewing and filtering stock data
 * @author Your Name
 */
public class ViewStockControl {
    private StockControl stockControl;
    private HashMapInterface<String, Stock> stockMap;
    private final FilterCriteriaUtil criteriaUtil = new FilterCriteriaUtil();

    public ViewStockControl(StockControl stockControl) {
        this.stockControl = stockControl;
        this.stockMap = stockControl.getStockMap();
    }

    public HashMapInterface<String, Stock> getStockMap() {
        return stockMap;
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

    // --- Filters ---
    public HashMapInterface<String, Stock> filterExpired(HashMapInterface<String, Stock> map) {
        addCriteria("Expired Stock");
        LocalDate today = LocalDate.now();
        return map.filter(s -> s.getExpiryDate().isBefore(today));
    }

    public HashMapInterface<String, Stock> filterActive(HashMapInterface<String, Stock> map) {
        addCriteria("Active Stock");
        LocalDate today = LocalDate.now();
        return map.filter(s -> !s.getExpiryDate().isBefore(today));
    }

    public HashMapInterface<String, Stock> filterByExpiryBefore(HashMapInterface<String, Stock> map, LocalDate date) {
        addCriteria("Expiry before " + date.toString());
        return map.filter(s -> s.getExpiryDate().isBefore(date));
    }

    public HashMapInterface<String, Stock> filterByMedicineId(HashMapInterface<String, Stock> map, String medicineId) {
        addCriteria("Medicine ID = " + medicineId);
        return map.filter(s -> s.getMedicineId().equalsIgnoreCase(medicineId));
    }

    public HashMapInterface<String, Stock> filterByStockId(HashMapInterface<String, Stock> map, String stockId) {
        addCriteria("Stock ID = " + stockId);
        return map.filter(s -> s.getStockId().equalsIgnoreCase(stockId));
    }

    // --- Search ---
    public HashMapInterface<String, Stock> searchByStockId(HashMapInterface<String, Stock> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();
        return map.filter(s -> s.getStockId().toLowerCase().contains(lower));
    }

    public HashMapInterface<String, Stock> searchByMedicineId(HashMapInterface<String, Stock> map, String keyword) {
        addCriteria("Search = \"" + keyword + "\"");
        String lower = keyword.toLowerCase();
        return map.filter(s -> s.getMedicineId().toLowerCase().contains(lower));
    }

    // --- Sorting ---
    public void sortStock(ListInterface<Stock> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

        switch (option) {
            case "1": // Stock ID
                list.sort((s1, s2) -> s1.getStockId().compareTo(s2.getStockId()));
                addCriteria("Sort = Stock ID (Asc)");
                break;
            case "2": // Medicine ID
                list.sort((s1, s2) -> s1.getMedicineId().compareTo(s2.getMedicineId()));
                addCriteria("Sort = Medicine ID (Asc)");
                break;
            case "3": // Quantity
                list.sort((s1, s2) -> Integer.compare(s1.getQuantity(), s2.getQuantity()));
                addCriteria("Sort = Quantity (Asc)");
                break;
            case "4": // Expiry Date
                list.sort((s1, s2) -> s1.getExpiryDate().compareTo(s2.getExpiryDate()));
                addCriteria("Sort = Expiry Date (Asc)");
                break;
            case "5": // Received Date
                list.sort((s1, s2) -> s1.getReceivedDate().compareTo(s2.getReceivedDate()));
                addCriteria("Sort = Received Date (Asc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public void reverseSortStock(ListInterface<Stock> list, String option) {
        removeOldSortCriteria();
        criteriaUtil.setCurrentSortOption(option);

        switch (option) {
            case "1": // Stock ID
                list.sort((s1, s2) -> s2.getStockId().compareTo(s1.getStockId()));
                addCriteria("Sort = Stock ID (Desc)");
                break;
            case "2": // Medicine ID
                list.sort((s1, s2) -> s2.getMedicineId().compareTo(s1.getMedicineId()));
                addCriteria("Sort = Medicine ID (Desc)");
                break;
            case "3": // Quantity
                list.sort((s1, s2) -> Integer.compare(s2.getQuantity(), s1.getQuantity()));
                addCriteria("Sort = Quantity (Desc)");
                break;
            case "4": // Expiry Date
                list.sort((s1, s2) -> s2.getExpiryDate().compareTo(s1.getExpiryDate()));
                addCriteria("Sort = Expiry Date (Desc)");
                break;
            case "5": // Received Date
                list.sort((s1, s2) -> s2.getReceivedDate().compareTo(s1.getReceivedDate()));
                addCriteria("Sort = Received Date (Desc)");
                break;
            default:
                criteriaUtil.setCurrentSortOption(null);
                break;
        }
    }

    public ListInterface<Stock> toList(HashMapInterface<String, Stock> map) {
        ListInterface<Stock> list = map.toList();
        if (criteriaUtil.getCurrentSortOption() != null) {
            sortStock(list, criteriaUtil.getCurrentSortOption());
        } else {
            list.sort((s1, s2) -> s1.getStockId().compareTo(s2.getStockId()));
        }
        return list;
    }

    // --- Display ---
    public void printStock(HashMapInterface<String, Stock> map) {
        ListInterface<Stock> list = map.toList();
        list.sort((s1, s2) -> s1.getStockId().compareTo(s2.getStockId()));
        stockControl.printStockTable(list, getCriteriaSummary());
    }

    public void printStockFromList(ListInterface<Stock> list) {
        stockControl.printStockTable(list, getCriteriaSummary());
    }
}
