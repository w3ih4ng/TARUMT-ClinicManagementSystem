package utility;

import adt.ArrayList;
import adt.ListInterface;

/**
 * System utility class with helper methods including navigation tracking
 * @author Your Name
 */
public class SystemUtil {
    private static ListInterface<String> navigationStack = new ArrayList<>();
    
    /**
     * Clear screen for better UI experience
     */
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback: print newlines to simulate clear screen
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Set the navigation path (breadcrumb)
     */
    public static void setNavigationPath(String... pages) {
        navigationStack.clear();
        for (String page : pages) {
            navigationStack.add(page);
        }
    }
    
    /**
     * Add a page to the navigation path
     */
    public static void pushNavigation(String page) {
        navigationStack.add(page);
    }
    
    /**
     * Remove the last page from navigation path
     */
    public static void popNavigation() {
        if (navigationStack.size() > 0) {
            navigationStack.remove(navigationStack.size() - 1);
        }
    }
    
    /**
     * Clear the navigation stack
     */
    public static void clearNavigation() {
        navigationStack.clear();
    }
    
    /**
     * Get the current navigation breadcrumb
     */
    public static String getNavigationBreadcrumb() {
        if (navigationStack.isEmpty()) {
            return "Home";
        }
        
        StringBuilder breadcrumb = new StringBuilder();
        for (int i = 0; i < navigationStack.size(); i++) {
            if (i > 0) {
                breadcrumb.append(" > ");
            }
            breadcrumb.append(navigationStack.get(i));
        }
        return breadcrumb.toString();
    }
    
    /**
     * Display navigation header with breadcrumb
     */
    public static void showNavigationHeader() {
        System.out.println("Navigation: " + getNavigationBreadcrumb());
        System.out.println("=".repeat(60));
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
        clearScreen();
        showNavigationHeader();
        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("    " + menuTitle.toUpperCase());
        System.out.println("=".repeat(50));
    }
    
    /**
     * Display a formatted section header
     */
    public static void showSectionHeader(String sectionTitle) {
        System.out.println();
        System.out.println("-".repeat(40));
        System.out.println("  " + sectionTitle);
        System.out.println("-".repeat(40));
    }
    
    /**
     * Pause and wait for user to continue
     */
    public static void pauseForUser() {
        System.out.println();
        System.out.println("Type 'exit' to continue:");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to continue:");
        }
    }
}
