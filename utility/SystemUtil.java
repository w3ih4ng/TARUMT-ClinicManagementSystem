package utility;

import adt.ArrayList;
import adt.ListInterface;

/**
 * System utility class with helper methods including navigation tracking
 * @author Your Name
 */
public class SystemUtil {
    private static ListInterface<String> navigationPage = new ArrayList<>();
    
    /**
     * Clear screen for better UI experience
     * Works well in both terminal and IDE environments like NetBeans
     */
    public static void clearScreen() {
        try {
            // For terminal environments
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // IDE-friendly fallback: print newlines to create visual separation
            printSeparator();
        }
    }
    
    /**
     * Print visual separator for IDE environments
     */
    public static void printSeparator() {
        // Print multiple newlines for visual separation
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
        System.out.println("=".repeat(100));
    }
    
    /**
     * Set the navigation path (breadcrumb)
     */
    public static void setNavigationPath(String... pages) {
        navigationPage.clear();
        for (String page : pages) {
            navigationPage.add(page);
        }
    }
    
    /**
     * Add a page to the navigation path
     */
    public static void pushNavigation(String page) {
        navigationPage.add(page);
    }
    
    /**
     * Remove the last page from navigation path
     */
    public static void popNavigation() {
        if (navigationPage.size() > 0) {
            navigationPage.remove(navigationPage.size() - 1);
        }
    }
    
    /**
     * Clear the navigation stack
     */
    public static void clearNavigation() {
        navigationPage.clear();
    }
    
    /**
     * Get the current navigation breadcrumb
     */
    public static String getNavigationBreadcrumb() {
        if (navigationPage.isEmpty()) {
            return "Home";
        }
        
        StringBuilder breadcrumb = new StringBuilder();
        for (int i = 0; i < navigationPage.size(); i++) {
            if (i > 0) {
                breadcrumb.append(" > ");
            }
            breadcrumb.append(navigationPage.get(i));
        }
        return breadcrumb.toString();
    }
    
    /**
     * Display navigation header with breadcrumb
     */
    public static void showNavigationHeader() {
        System.out.println("    Navigation: " + getNavigationBreadcrumb());
        System.out.println("=".repeat(100));
    }
    
    /**
     * Clear screen and show navigation header
     */
    public static void clearScreenWithNav() {
        clearScreen();
        showNavigationHeader();
    }
    
    /**
     * Display a formatted menu header with navigation
     */
    public static void showMenuHeader(String menuTitle) {
        printSeparator();
        showNavigationHeader();
        System.out.println();
        System.out.println("=".repeat(100));
        System.out.println("    " + menuTitle.toUpperCase());
        System.out.println("=".repeat(100));
        System.out.println();
    }
    
    /**
     * Display a formatted section header
     */
    public static void showSectionHeader(String sectionTitle) {
        System.out.println();
        System.out.println("-".repeat(80));
        System.out.println("  " + sectionTitle);
        System.out.println("-".repeat(80));
    }
    
    /**
     * Pause and wait for user to continue
     */
    public static void pauseForUser() {
        System.out.println();
        System.out.println("Press Enter to continue...");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        sc.nextLine();
    }
}
