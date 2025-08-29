package control;

import entity.*;
import adt.*;
import dao.InvoiceDAO;
import dao.ConsultationDAO;
import dao.DoctorDAO;
import dao.PatientDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Control class for invoice management and generation
 * @author Your Name
 */
public class InvoiceControl {
    private HashMapInterface<String, Invoice> invoiceMap;
    private HashMapInterface<String, Consultation> consultationMap;
    private HashMapInterface<String, Doctor> doctorMap;
    private HashMapInterface<String, Patient> patientMap;

    public InvoiceControl() {
        this.invoiceMap = InvoiceDAO.loadInvoices();
        this.consultationMap = ConsultationDAO.loadConsultations();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.patientMap = PatientDAO.loadPatients();
    }

    /**
     * Generate invoice for a consultation
     */
    public Invoice generateInvoice(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            System.out.println("Consultation not found: " + consultationId);
            return null;
        }

        // Check if invoice already exists
        Invoice existingInvoice = getInvoiceByConsultation(consultationId);
        if (existingInvoice != null) {
            System.out.println("Invoice already exists for consultation: " + consultationId);
            return existingInvoice;
        }

        // Calculate total amount
        double totalAmount = calculateConsultationTotal(consultationId);
        
        // Create new invoice
        String invoiceId = InvoiceDAO.generateInvoiceId();
        Invoice invoice = new Invoice(invoiceId, consultationId, totalAmount);
        
        // Save invoice
        invoiceMap.put(invoiceId, invoice);
        InvoiceDAO.saveInvoices(invoiceMap);
        
        System.out.println("Invoice generated successfully: " + invoiceId);
        System.out.println("Amount: RM " + String.format("%.2f", totalAmount));
        
        // Display invoice breakdown
        displayInvoiceBreakdown(consultationId, totalAmount);
        
        return invoice;
    }

    /**
     * Get invoice by consultation ID
     */
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
     * Get all unpaid invoices
     */
    public ListInterface<Invoice> getUnpaidInvoices() {
        ListInterface<Invoice> unpaidInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && !invoice.isPaid()) {
                unpaidInvoices.add(invoice);
            }
        }
        
        return unpaidInvoices;
    }

    /**
     * Get all paid invoices
     */
    public ListInterface<Invoice> getPaidInvoices() {
        ListInterface<Invoice> paidInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null && invoice.isPaid()) {
                paidInvoices.add(invoice);
            }
        }
        
        return paidInvoices;
    }

    /**
     * Get invoices by patient
     */
    public ListInterface<Invoice> getInvoicesByPatient(String patientId) {
        ListInterface<Invoice> patientInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null) {
                Consultation consultation = consultationMap.get(invoice.getConsultationId());
                if (consultation != null && consultation.getPatientId().equals(patientId)) {
                    patientInvoices.add(invoice);
                }
            }
        }
        
        return patientInvoices;
    }

    /**
     * Get invoices by date range
     */
    public ListInterface<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        ListInterface<Invoice> filteredInvoices = new ArrayList<>();
        
        for (int i = 0; i < invoiceMap.keySet().size(); i++) {
            String key = invoiceMap.keySet().get(i);
            Invoice invoice = invoiceMap.get(key);
            if (invoice != null) {
                LocalDateTime invoiceDate = invoice.getCreatedTime();
                if (invoiceDate.isAfter(startDate) && invoiceDate.isBefore(endDate)) {
                    filteredInvoices.add(invoice);
                }
            }
        }
        
        return filteredInvoices;
    }

    /**
     * Calculate total amount for a consultation
     */
    private double calculateConsultationTotal(String consultationId) {
        double total = 0.0;
        
        // Get consultation
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) return 0.0;
        
        // Add doctor consultation fee
        Doctor doctor = doctorMap.get(consultation.getDoctorId());
        if (doctor != null) {
            total += doctor.getConsultationFee();
        }
        
        // Add treatment fee if exists
        if (consultation.getTreatmentId() != null) {
            try {
                dao.TreatmentDAO treatmentDAO = new dao.TreatmentDAO();
                adt.HashMapInterface<String, entity.Treatment> treatmentMap = treatmentDAO.loadTreatments();
                entity.Treatment treatment = treatmentMap.get(consultation.getTreatmentId());
                
                if (treatment != null) {
                    total += treatment.getTreatmentFee();
                    
                    // Add prescribed medicine costs
                    adt.ListInterface<entity.MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
                    for (int i = 0; i < medicines.size(); i++) {
                        entity.MedicinePrescribed medicine = medicines.get(i);
                        
                        try {
                            // Load medicine details to get price
                            dao.MedicineDAO medicineDAO = new dao.MedicineDAO();
                            adt.HashMapInterface<String, entity.Medicine> medicineMap = medicineDAO.loadMedicines();
                            entity.Medicine medicineDetails = medicineMap.get(medicine.getMedicineId());
                            
                            if (medicineDetails != null) {
                                double medicineCost = medicine.calculateCost(medicineDetails);
                                total += medicineCost;
                            } else {
                                // If medicine not found, add default cost
                                total += 5.0;
                            }
                        } catch (Exception e) {
                            // If we can't get the cost, add default
                            total += 5.0;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not load treatment details for invoice calculation");
            }
        }
        
        return total;
    }

    /**
     * Display detailed breakdown of invoice charges
     */
    private void displayInvoiceBreakdown(String consultationId, double totalAmount) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) return;

        System.out.println("\n=== Invoice Breakdown ===");
        
        // Consultation fee
        Doctor doctor = doctorMap.get(consultation.getDoctorId());
        if (doctor != null) {
            System.out.println("Consultation Fee: RM " + String.format("%.2f", doctor.getConsultationFee()));
        }
        
        // Treatment fee
        if (consultation.getTreatmentId() != null) {
            try {
                dao.TreatmentDAO treatmentDAO = new dao.TreatmentDAO();
                adt.HashMapInterface<String, entity.Treatment> treatmentMap = treatmentDAO.loadTreatments();
                entity.Treatment treatment = treatmentMap.get(consultation.getTreatmentId());
                
                if (treatment != null) {
                    System.out.println("Treatment Fee: RM " + String.format("%.2f", treatment.getTreatmentFee()));
                    
                    // Medicine costs
                    adt.ListInterface<entity.MedicinePrescribed> medicines = treatment.getPrescribedMedicines();
                    if (medicines.size() > 0) {
                        System.out.println("Prescribed Medicines:");
                        
                        dao.MedicineDAO medicineDAO = new dao.MedicineDAO();
                        adt.HashMapInterface<String, entity.Medicine> medicineMap = medicineDAO.loadMedicines();
                        
                        for (int i = 0; i < medicines.size(); i++) {
                            entity.MedicinePrescribed medicine = medicines.get(i);
                            entity.Medicine medicineDetails = medicineMap.get(medicine.getMedicineId());
                            
                            if (medicineDetails != null) {
                                double cost = medicine.calculateCost(medicineDetails);
                                System.out.println("  - " + medicineDetails.getName() + 
                                                 " (" + medicine.getQuantity() + " x RM " + 
                                                 String.format("%.2f", medicineDetails.getPrice()) + ") = RM " + 
                                                 String.format("%.2f", cost));
                            } else {
                                System.out.println("  - Medicine ID " + medicine.getMedicineId() + 
                                                 " (quantity: " + medicine.getQuantity() + ") = RM 5.00");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not load treatment details for breakdown");
            }
        }
        
        System.out.println("Total Amount: RM " + String.format("%.2f", totalAmount));
        System.out.println("========================");
    }

    /**
     * Display invoice details
     */
    public void displayInvoiceDetails(String invoiceId) {
        Invoice invoice = invoiceMap.get(invoiceId);
        if (invoice == null) {
            System.out.println("Invoice not found: " + invoiceId);
            return;
        }

        Consultation consultation = consultationMap.get(invoice.getConsultationId());
        Doctor doctor = consultation != null ? doctorMap.get(consultation.getDoctorId()) : null;
        Patient patient = consultation != null ? patientMap.get(consultation.getPatientId()) : null;

        System.out.println("\n=== Invoice Details ===");
        System.out.println("Invoice ID: " + invoice.getInvoiceId());
        System.out.println("Date: " + invoice.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Status: " + (invoice.isPaid() ? "Paid" : "Unpaid"));
        System.out.println("\nConsultation Details:");
        if (consultation != null) {
            System.out.println("  Consultation ID: " + consultation.getConsultationId());
            if (consultation.getConsultationTime() != null) {
                System.out.println("  Date: " + consultation.getConsultationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                System.out.println("  Time: " + consultation.getConsultationTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }
        
        if (doctor != null) {
            System.out.println("  Doctor: Dr. " + doctor.getName() + " (" + doctor.getSpecialty() + ")");
        }
        
        if (patient != null) {
            System.out.println("  Patient: " + patient.getName() + " (" + patient.getPatientId() + ")");
        }
        
        System.out.println("\nCharges:");
        if (consultation != null && doctor != null) {
            System.out.println("  Consultation Fee: RM " + String.format("%.2f", doctor.getConsultationFee()));
        }
        
        System.out.println("\nTotal Amount: RM " + String.format("%.2f", invoice.getAmount()));
    }

    /**
     * Get invoice map
     */
    public HashMapInterface<String, Invoice> getInvoiceMap() {
        return invoiceMap;
    }

    /**
     * Save invoices
     */
    public void saveInvoices() {
        InvoiceDAO.saveInvoices(invoiceMap);
    }
}
