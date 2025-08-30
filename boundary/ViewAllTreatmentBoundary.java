package boundary;

import control.TreatmentControl;
import control.ViewTreatmentControl;
import control.PaymentControl;
import control.InvoiceControl;
import entity.*;
import adt.*;
import utility.*;

import java.util.Scanner;

/**
 * Boundary class for viewing all treatment data interface
 * @author Your Name
 */
public class ViewAllTreatmentBoundary {
    private final ViewTreatmentControl viewTreatmentControl;
    private final TreatmentControl treatmentControl;
    private final PaymentControl paymentControl;
    private final InvoiceControl invoiceControl;

    public ViewAllTreatmentBoundary(TreatmentControl treatmentControl, PaymentControl paymentControl, InvoiceControl invoiceControl) {
        this.treatmentControl = treatmentControl;
        this.viewTreatmentControl = new ViewTreatmentControl(treatmentControl, paymentControl, invoiceControl);
        this.paymentControl = paymentControl;
        this.invoiceControl = invoiceControl;
    }

    public void show() {
        HashMapInterface<String, Treatment> baseView = viewTreatmentControl.getTreatmentMap();
        HashMapInterface<String, Treatment> currentMap = baseView;
        ListInterface<Treatment> currentList = viewTreatmentControl.toList(currentMap);

        Scanner sc = new Scanner(System.in);

        while (true) {
            utility.SystemUtil.pushNavigation("View All Treatments");
            utility.SystemUtil.showMenuHeader("Treatment List");
            
            viewTreatmentControl.printTreatmentsFromList(currentList);

            System.out.println("\nOptions:");
            System.out.println("1. Filter");
            System.out.println("2. Search");
            System.out.println("3. Sort");
            System.out.println("4. Reset");
            System.out.println("5. View Details");
            System.out.println("6. Edit Treatment");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    utility.SystemUtil.showSectionHeader("Filter Treatments");
                    currentMap = viewTreatmentControl.handleFilter(sc, currentMap);
                    currentList = viewTreatmentControl.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "2":
                    utility.SystemUtil.showSectionHeader("Search Treatments");
                    currentMap = viewTreatmentControl.handleSearch(sc, currentMap);
                    currentList = viewTreatmentControl.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "3":
                    utility.SystemUtil.showSectionHeader("Sort Treatments");
                    currentList = viewTreatmentControl.handleSort(sc, currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "4":
                    utility.SystemUtil.showSectionHeader("Reset View");
                    currentMap = baseView;
                    currentList = viewTreatmentControl.toList(currentMap);
                    viewTreatmentControl.clearCriteria();
                    System.out.println("View reset to show all treatments.");
                    utility.SystemUtil.pauseForUser();
                    utility.SystemUtil.popNavigation();
                    break;
                case "5":
                    utility.SystemUtil.showSectionHeader("View Treatment Details");
                    viewTreatmentControl.viewTreatmentDetails(sc);
                    utility.SystemUtil.popNavigation();
                    break;
                case "6":
                    utility.SystemUtil.showSectionHeader("Edit Treatment");
                    viewTreatmentControl.editTreatment(sc);
                    // Refresh the view
                    currentMap = viewTreatmentControl.getTreatmentMap();
                    currentList = viewTreatmentControl.toList(currentMap);
                    utility.SystemUtil.popNavigation();
                    break;
                case "0":
                    utility.SystemUtil.popNavigation();
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
                    utility.SystemUtil.pauseForUser();
            }
        }
    }
}
