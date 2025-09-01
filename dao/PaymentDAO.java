package dao;

import entity.Payment;
import adt.HashMapInterface;
import adt.HashMapADT;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for Payment persistence
 * @author Your Name
 */
public class PaymentDAO {
    private static final String FILE_NAME = "data/payments.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static int paymentCounter = 1001; // Start from PAY1001

    private static void ensureFile() {
        try {
            File file = new File(FILE_NAME);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error ensuring payments file: " + e.getMessage());
        }
    }

    public static void savePayments(HashMapInterface<String, Payment> paymentMap) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Write header comment
            pw.println("# PaymentID|InvoiceID|ConsultationID|PatientID|PaymentMethod|PaymentStatus|PaymentDate|Remarks|isDeleted");
            
            for (int i = 0; i < paymentMap.keySet().size(); i++) {
                String key = paymentMap.keySet().get(i);
                Payment payment = paymentMap.get(key);
                if (payment != null && !payment.isDeleted())
                    pw.println(toFileString(payment));
            }
        } catch (IOException e) {
            System.out.println("Error saving payments: " + e.getMessage());
        }
    }

    public static HashMapInterface<String, Payment> loadPayments() {
        ensureFile();
        HashMapInterface<String, Payment> map = new HashMapADT<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Payment payment = fromFileString(line);
                if (payment != null)
                    map.put(payment.getPaymentId(), payment);
            }
        } catch (IOException e) {
            System.out.println("Error loading payments: " + e.getMessage());
        }

        // Initialize counter from existing data
        initCounterFromMap(map);

        return map;
    }

    private static String toFileString(Payment payment) {
        return String.join("|",
                payment.getPaymentId(),
                payment.getInvoiceId(),
                payment.getConsultationId(),
                payment.getPatientId(),
                payment.getPaymentMethod().name(),
                payment.getStatus().name(),
                payment.getPaymentDate() != null ? payment.getPaymentDate().format(formatter) : "",
                payment.getRemarks(),
                Boolean.toString(payment.isDeleted())
        );
    }

    private static Payment fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 9) {
                throw new IllegalArgumentException("Expected 9 columns, got " + parts.length);
            }
            String paymentId = parts[0];
            String invoiceId = parts[1];
            String consultationId = parts[2];
            String patientId = parts[3];
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(parts[4]);
            Payment.PaymentStatus status = Payment.PaymentStatus.valueOf(parts[5]);
            LocalDateTime paymentDate = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6], formatter);
            String remarks = parts[7];
            boolean deleted = Boolean.parseBoolean(parts[8]);

            Payment payment = new Payment(paymentId, invoiceId, consultationId, patientId, paymentMethod);
            payment.setStatus(status);
            payment.setPaymentDate(paymentDate);
            payment.setRemarks(remarks);

            if (deleted) payment.delete();

            return payment;
        } catch (Exception e) {
            System.out.println("Error parsing payment line: " + line + " -> " + e.getMessage());
            return null;
        }
    }

    public static String generatePaymentId() {
        return "PAY" + (paymentCounter++);
    }

    private static void initCounterFromMap(HashMapInterface<String, Payment> map) {
        int max = 1000; // Start from PAY1001
        for (String key : map.keySet()) {
            Payment payment = map.get(key);
            if (payment != null) {
                String id = payment.getPaymentId();
                try {
                    int num = Integer.parseInt(id.substring(3)); // Remove 'PAY' prefix
                    if (num > max) max = num;
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        paymentCounter = max + 1;
    }
}
