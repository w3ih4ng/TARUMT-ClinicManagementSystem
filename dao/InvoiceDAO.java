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
            pw.println("# InvoiceID|ConsultationID|ConsultationFee|TreatmentFee|MedicineFee|TotalAmount|InvoiceDate|IsPaid");
            
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
                String.valueOf(invoice.getConsultationFee()),
                String.valueOf(invoice.getTreatmentFee()),
                String.valueOf(invoice.getMedicineFee()),
                String.valueOf(invoice.getTotalAmount()),
                invoice.getCreatedTime().format(formatter),
                String.valueOf(invoice.isPaid())
        );
    }

    private static Invoice fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");

            // Handle both old format (5 columns) and new format (8 columns)
            if (parts.length == 5) {
                // Old format: InvoiceID|ConsultationID|TotalAmount|InvoiceDate|IsPaid
                String invoiceId = parts[0];
                String consultationId = parts[1];
                double totalAmount = Double.parseDouble(parts[2]);
                LocalDateTime createdTime = LocalDateTime.parse(parts[3], formatter);
                boolean isPaid = Boolean.parseBoolean(parts[4]);

                // Create invoice with old constructor (backward compatibility)
                Invoice invoice = new Invoice(invoiceId, consultationId, totalAmount);
                if (isPaid) {
                    invoice.markPaid();
                }

                return invoice;
            } else if (parts.length == 8) {
                // New format: InvoiceID|ConsultationID|ConsultationFee|TreatmentFee|MedicineFee|TotalAmount|InvoiceDate|IsPaid
                String invoiceId = parts[0];
                String consultationId = parts[1];
                double consultationFee = Double.parseDouble(parts[2]);
                double treatmentFee = Double.parseDouble(parts[3]);
                double medicineFee = Double.parseDouble(parts[4]);
                double totalAmount = Double.parseDouble(parts[5]);
                LocalDateTime createdTime = LocalDateTime.parse(parts[6], formatter);
                boolean isPaid = Boolean.parseBoolean(parts[7]);

                // Create invoice with new constructor
                Invoice invoice = new Invoice(invoiceId, consultationId, consultationFee, treatmentFee, medicineFee);
                if (isPaid) {
                    invoice.markPaid();
                }

                return invoice;
            } else {
                throw new IllegalArgumentException("Expected 5 or 8 columns, got " + parts.length);
            }
        } catch (Exception e) {
            System.out.println("Error parsing invoice line: " + line + " -> " + e.getMessage());
        }
        return null;
    }

    public static String generateInvoiceId() {
        return "I" + (invoiceCounter++);
    }
}
