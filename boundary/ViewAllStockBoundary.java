package boundary;

import control.StockControl;
import control.ViewStockControl;
import entity.Stock;
import adt.*;
import java.util.Scanner;
import java.time.format.*;
import java.time.LocalDate;

public class ViewAllStockBoundary {
    private final ViewStockControl viewStockControl;

    public ViewAllStockBoundary(StockControl stockControl) {
        this.viewStockControl = new ViewStockControl(stockControl);
    }

    public void show() {
        HashMapInterface<String, Stock> baseMap = viewStockControl.getStockMap();
        HashMapInterface<String, Stock> currentMap = baseMap;
        ListInterface<Stock> currentList = viewStockControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n---------------------------------------- Stock Batch List ----------------------------------------\n");
            viewStockControl.printStockFromList(currentList);

            System.out.println("\nOptions:");
            System.out.println("1. Filter");
            System.out.println("2. Search");
            System.out.println("3. Sort");
            System.out.println("4. Reset");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    currentMap = handleFilter(sc, currentMap);
                    currentList = viewStockControl.toList(currentMap);
                    break;
                case "2":
                    currentMap = handleSearch(sc, currentMap);
                    currentList = viewStockControl.toList(currentMap);
                    break;
                case "3":
                    handleSort(sc, currentList);
                    break;
                case "4":
                    currentMap = baseMap;
                    currentList = viewStockControl.toList(currentMap);
                    viewStockControl.clearCriteria();
                    break;
                case "0":
                    viewStockControl.clearCriteria();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private HashMapInterface<String, Stock> handleFilter(Scanner sc, HashMapInterface<String, Stock> map) {
        System.out.println("\nFilter Options:");
        System.out.println("1. Show Expired Stock");
        System.out.println("2. Show Active Stock");
        System.out.println("3. Expiry Before a Date");
        System.out.println("4. By Medicine ID");
        System.out.println("5. By Stock ID");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                return viewStockControl.filterExpired(map);
            case "2":
                return viewStockControl.filterActive(map);
            case "3":
                System.out.print("Enter date (yyyy-MM-dd): ");
                String dateInput = sc.nextLine().trim();
                LocalDate date;
                try {
                    date = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    return viewStockControl.filterByExpiryBefore(map, date);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Returning without filter.");
                    return map;
                }
            case "4":
                System.out.print("Enter Medicine ID: ");
                String medId = sc.nextLine().trim();
                return viewStockControl.filterByMedicineId(map, medId);
            case "5":
                System.out.print("Enter Stock ID: ");
                String stockId = sc.nextLine().trim();
                return viewStockControl.filterByStockId(map, stockId);
            case "0":
                return map;
            default:
                System.out.println("Invalid filter option.");
                return map;
        }
    }

    private HashMapInterface<String, Stock> handleSearch(Scanner sc, HashMapInterface<String, Stock> map) {
        System.out.println("\nSearch Options:");
        System.out.println("1. By Stock ID");
        System.out.println("2. By Medicine ID");
        System.out.print("Choose: ");
        String searchChoice = sc.nextLine().trim();
        
        switch (searchChoice) {
            case "1":
                System.out.print("Enter Stock ID to search: ");
                String stockId = sc.nextLine().trim();
                return viewStockControl.searchByStockId(map, stockId);
            case "2":
                System.out.print("Enter Medicine ID to search: ");
                String medId = sc.nextLine().trim();
                return viewStockControl.searchByMedicineId(map, medId);
            default:
                System.out.println("Invalid search option.");
                return map;
        }
    }

    private void handleSort(Scanner sc, ListInterface<Stock> list) {
        System.out.println("\nSort Options:");
        System.out.println("1. Stock ID");
        System.out.println("2. Medicine ID");
        System.out.println("3. Quantity");
        System.out.println("4. Expiry Date");
        System.out.println("5. Received Date");
        System.out.print("Choose: ");
        String choice = sc.nextLine().trim();

        if (choice.equals("0") || choice.isEmpty()) {
            return;
        }

        System.out.println("Sort Order:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Choose: ");
        String orderChoice = sc.nextLine().trim();

        if (orderChoice.equals("2")) {
            viewStockControl.reverseSortStock(list, choice);
        } else {
            viewStockControl.sortStock(list, choice);
        }
    }
}
