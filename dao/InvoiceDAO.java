package dao;

import entity.Invoice;
import adt.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for Invoice persistence
 * @author Your Name
 */
public class InvoiceDAO {
    private static final String FILE_NAME = "data/invoices.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static int invoiceCounter = 1001; // Start from I1001

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring invoices file: " + e.getMessage());
        }
    }

    public static void saveInvoices(HashMapInterface<String, Invoice> invoiceMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Write header comment
            pw.println("# InvoiceID|ConsultationID|PatientID|TotalAmount|InvoiceDate|IsPaid");
            
            for (int i = 0; i < invoiceMap.keySet().size(); i++) {
                String key = invoiceMap.keySet().get(i);
                Invoice invoice = invoiceMap.get(key);
                if (invoice != null)
                    pw.println(toFileString(invoice));
            }
        } catch (IOException e) {
            System.out.println("Error saving invoices: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, Invoice> loadInvoices() {
        ensureFile();
        HashMapInterface<String, Invoice> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Invoice invoice = fromFileString(line);
                if (invoice != null)
                    map.put(invoice.getInvoiceId(), invoice);
            }
        } catch (IOException e) {
            System.out.println("Error loading invoices: " + e.getMessage());
        }

        return map;
    }

    private static String toFileString(Invoice invoice) {
        return String.join("|",
                invoice.getInvoiceId(),
                invoice.getConsultationId(),
                String.valueOf(invoice.getAmount()),
                invoice.getCreatedTime().format(formatter),
                String.valueOf(invoice.isPaid())
        );
    }

    private static Invoice fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Expected 5 columns, got " + parts.length);
            }
            
            String invoiceId = parts[0];
            String consultationId = parts[1];
            double amount = Double.parseDouble(parts[2]);
            LocalDateTime createdTime = LocalDateTime.parse(parts[3], formatter);
            boolean isPaid = Boolean.parseBoolean(parts[4]);

            Invoice invoice = new Invoice(invoiceId, consultationId, amount);
            if (isPaid) {
                invoice.markPaid();
            }
            
            return invoice;
        } catch (Exception e) {
            System.out.println("Error parsing invoice line: " + line + " -> " + e.getMessage());
        }
        return null;
    }

    public static String generateInvoiceId() {
        return "I" + (invoiceCounter++);
    }
}
