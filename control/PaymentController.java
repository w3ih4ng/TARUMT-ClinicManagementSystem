package control;

import entity.*;
import dao.ConsultationDAO;
import adt.*;
import dao.PaymentDAO;
import dao.InvoiceDAO;
import dao.ConsultationDAO;
import dao.PatientDAO;
import dao.DoctorDAO;





/**
 * Consolidated Payment Controller - combines payment and invoice functionality
 * Handles payment processing, invoice generation, and financial management
 * @author Your Name
 */
public class PaymentController {
    private HashMapInterface<String, Payment> paymentMap;
    private HashMapInterface<String, Invoice> invoiceMap;
    private HashMapInterface<String, Consultation> consultationMap;
    private HashMapInterface<String, Patient> patientMap;
    private HashMapInterface<String, Doctor> doctorMap;

    public PaymentController() {
        this.paymentMap = PaymentDAO.loadPayments();
        this.invoiceMap = InvoiceDAO.loadInvoices();
        this.consultationMap = ConsultationDAO.loadConsultations();
        this.patientMap = PatientDAO.loadPatients();
        this.doctorMap = DoctorDAO.loadDoctors();
    }

    // ==================== PAYMENT OPERATIONS ====================

    /**
     * Process a new payment for an invoice
     */
    public boolean processPayment(String invoiceId, Payment.PaymentMethod paymentMethod, 
                                 String referenceNumber, String notes) {
        Invoice invoice = invoiceMap.get(invoiceId);
        if (invoice == null) {
            System.out.println("Invoice not found: " + invoiceId);
            return false;
        }

        if (invoice.isPaid()) {
            System.out.println("Invoice is already paid: " + invoiceId);
            return false;
        }

        // Create payment record
        String paymentId = PaymentDAO.generatePaymentId();
        Payment payment = new Payment(paymentId, invoiceId, invoice.getConsultationId(),
                                    getPatientIdFromConsultation(invoice.getConsultationId()),
                                    paymentMethod);

        payment.setRemarks(notes);
        payment.markPaid();

        // Save payment
        paymentMap.put(paymentId, payment);
        PaymentDAO.savePayments(paymentMap);

        // Mark invoice as paid
        invoice.markPaid();
        invoiceMap.put(invoiceId, invoice);
        InvoiceDAO.saveInvoices(invoiceMap);

        // Update consultation with payment ID
        updateConsultationPayment(invoice.getConsultationId(), paymentId);

        System.out.println("Payment processed successfully: " + paymentId);
        System.out.println("Amount: RM " + String.format("%.2f", invoice.getAmount()));
        System.out.println("Method: " + paymentMethod);
        return true;
    }

















    


  












    // ==================== HELPER METHODS ====================

    public String getPatientIdFromConsultation(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        return consultation != null ? consultation.getPatientId() : null;
    }

    private void updateConsultationPayment(String consultationId, String paymentId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            consultation.setPayment(paymentId);
            consultationMap.put(consultationId, consultation);
            ConsultationDAO.saveConsultations(consultationMap);
        }
    }



    // ==================== GETTERS ====================

    public HashMapInterface<String, Payment> getPaymentMap() {
        return paymentMap;
    }

    public HashMapInterface<String, Invoice> getInvoiceMap() {
        return invoiceMap;
    }

    public void deleteInvoiceByConsultation(String consultationId) {
        ListInterface<String> toRemove = new ArrayList<>();
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                toRemove.add(key);
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            invoiceMap.remove(toRemove.get(i));
        }
        InvoiceDAO.saveInvoices(invoiceMap);
    }



    public Invoice getInvoiceByConsultation(String consultationId) {
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && invoice.getConsultationId().equals(consultationId)) {
                return invoice;
            }
        }
        return null;
    }
    
    /**
     * Generate invoice for a consultation
     */
    public String generateInvoice(String consultationId, double totalAmount) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return null;
        }

        String invoiceId = InvoiceDAO.generateInvoiceId();
        Invoice invoice = new Invoice(invoiceId, consultationId, totalAmount);

        invoiceMap.put(invoiceId, invoice);
        InvoiceDAO.saveInvoices(invoiceMap);

        System.out.println("Invoice generated successfully: " + invoiceId);
        return invoiceId;
    }






}
