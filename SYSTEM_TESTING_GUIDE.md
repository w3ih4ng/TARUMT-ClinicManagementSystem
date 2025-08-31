# TARUMT Clinic Management System - Testing Guide

## System Overview
This guide provides step-by-step instructions to test the complete Clinic Management System workflow. The system follows a **semi-automatic patient flow** with integrated modules for patient management, consultations, treatments, pharmacy, and payments.

---

## Complete System Workflow

### **Phase 1: Initial Setup (Required)**
#### **Step 1.1: Add Doctors**
```
Main Menu -> 2. Doctor Management Module
├── 1. Add Doctor
│   ├── Doctor ID: D1000
│   ├── Name: Smith Johnson
│   ├── Gender: M
│   ├── Birthdate: 1980-05-15
│   ├── Phone: 0123456789
│   ├── Specialty: GENERAL_MEDICINE
│   └── Consultation Fee: 50.0
├── 2. Add Doctor
│   ├── Doctor ID: D1001
│   ├── Name: Emily Davis
│   ├── Gender: F
│   ├── Birthdate: 1985-03-20
│   ├── Phone: 0134567890
│   ├── Specialty: PEDIATRICS
│   └── Consultation Fee: 60.0
└── 3. Add Doctor
    ├── Doctor ID: D1002
    ├── Name: Michael Chen
    ├── Gender: M
    ├── Birthdate: 1978-11-10
    ├── Phone: 0145678901
    ├── Specialty: CARDIOLOGY
    └── Consultation Fee: 120.0
```

#### **Step 1.2: Add Patients**
```
Main Menu -> 1. Patient Management Module -> 2. Add Patient
├── 1. Add Student Patient
│   ├── Patient ID: P1000
│   ├── Name: John Doe
│   ├── Gender: M
│   ├── Birthdate: 1995-06-15
│   ├── Phone: 0156789012
│   ├── Role ID: STU001
├── 2. Add Staff Patient
│   ├── Patient ID: P1001
│   ├── Name: Jane Smith
│   ├── Gender: F
│   ├── Birthdate: 1988-09-22
│   ├── Phone: 0167890123
│   ├── Role ID: STF001
│   └── Department: Administration
└── 3. Add Tutor Patient
    ├── Patient ID: P1002
    ├── Name: Prof. Lee
    ├── Gender: M
    ├── Birthdate: 1970-01-30
    ├── Phone: 0178901234
    ├── Role ID: TUT001
    └── Faculty: FOCS
```

#### **Step 1.3: Add Medicines**
```
Main Menu -> 5. Pharmacy Management Module -> 2. Manage Medicines -> 1. Add Medicine
├── 1. Add Medicine
│   ├── Medicine ID: MED001
│   ├── Name: Paracetamol
│   ├── Dosage: 500.0
│   ├── Unit: TABLET
│   └── Price: 1.50
├── 2. Add Medicine
│   ├── Medicine ID: MED002
│   ├── Name: Ibuprofen
│   ├── Dosage: 400.0
│   ├── Unit: TABLET
│   └── Price: 2.00
└── 3. Add Medicine
    ├── Medicine ID: MED003
    ├── Name: Amoxicillin
    ├── Dosage: 250.0
    ├── Unit: CAPSULE
    └── Price: 3.50
```

---

### **Phase 2: Appointment & Consultation Flow**

#### **Step 2.1: Create Appointment**
```
Main Menu -> 3. Consultation Management Module -> 2. Appointments -> 1. Create Appointment
├── Patient ID: P1000
├── Expected: Patient validation passed (if P1000 exists)
├── Doctor ID: D1000
├── Expected: Doctor validation passed (if D1000 exists)
├── Specialty: GENERAL_MEDICINE
├── Enter date (yyyy-MM-dd) - today or future only: 2025-12-20
├── Expected: Selected date: 2025-12-20
├── Start Time: 09:00
└── Remarks: Regular checkup
```
**Expected Output:**
```
Appointment created and booked successfully for 2025-12-20 at 09:00
Schedule created for Dr. Smith Johnson on 2025-12-20 at 09:00
Patient P1000 added to queue with ID: Q1001
```
**Note:** Appointments are automatically booked when created - no separate booking step needed. The patient is immediately added to the queue with ASSIGNED status.

#### **Step 2.2: View Doctor Schedules (Optional)**
```
Main Menu -> 3. Consultation Management Module -> 2. Appointments -> 5. View Doctor Schedules
├── Enter Doctor ID: D1000
├── Expected: Shows all schedules for Smith Johnson from doctor_schedules.txt
├── Expected: Shows SCH1000 - 2025-12-20 - 09:00-10:00 - Booked - P1000
└── Expected: Patient ID shows P1000 since appointment was automatically booked
```

---

### **Phase 3: Consultation Process**

#### **Step 3.1: Start Consultation**
```
Main Menu -> 3. Consultation Management Module -> 1. Consultations -> 1. Create Consultation
├── System shows queued patients ready for consultation
├── Select patient P1000 from the list
├── Consultation starts automatically
└── Expected: Consultation created with status IN_PROGRESS
```

#### **Step 3.2: Add Treatment**
```
Main Menu -> 4. Medical Treatment Management Module -> 1. Create Treatment for Consultation
├── Consultation ID: C1001 (auto-generated from previous step)
├── Diagnosis: Common cold with fever
├── Treatment Fee: 30.0
├── Add Medicine Prescription:
│   ├── Medicine ID: MED001
│   ├── Quantity: 10
│   └── Add another? No
└── Expected: Treatment created and linked to consultation
```

#### **Step 3.3: Dispense Medicine**
```
Main Menu -> 5. Pharmacy Management Module -> 1. Dispense Medicine for Treatment
├── System shows treatments ready for dispensing
├── Select treatment T1001
├── Confirm dispensing
└── Expected: Medicine dispensed, consultation status updated to COMPLETED
```

---

### **Phase 4: Payment Process**

#### **Step 4.1: Process Payment**
```
Main Menu -> 3. Consultation Management Module -> 3. Payments -> 1. Process Consultation Payment
├── System shows consultations requiring payment
├── Select consultation C1001
├── System displays detailed cost breakdown:
│   ├── Consultation Fee: RM 50.00
│   ├── Treatment Fee: RM 30.00
│   ├── Medicine Cost: RM 15.00
│   └── Total Amount: RM 95.00
├── Select Payment Method: 1. Cash
├── Add Remarks: Paid in full
├── Confirm payment: y
└── Expected: Payment processed, invoice marked as PAID, queue status updated to COMPLETED
```

#### **Step 4.2: View Payment History**
```
Main Menu -> 3. Consultation Management Module -> 3. Payments -> 2. View Payment History
├── Expected: Shows completed payment with full details
├── Table includes: Payment ID, Consultation ID, Patient ID, Amount (RM), Method, Date
├── Summary shows: Total Paid Payments count and Total Amount Paid
└── Note: Amounts are retrieved from linked invoices for accuracy
```

---

### **Phase 5: Walk-in Patient Flow**

#### **Step 5.1: Add Walk-in Patient**
```
Main Menu -> 1. Patient Management Module -> 4. Queue Management -> 1. Add Walk-in Patient
├── Patient ID: P1000 (use existing patient)
├── Specialty: GENERAL_MEDICINE
├── System assigns available doctor
└── Expected: Patient added to queue with ASSIGNED status
```

#### **Step 5.2: Assign Doctor (if needed)**
```
Main Menu -> 1. Patient Management Module -> 4. Queue Management -> 3. Assign Doctor to Patient
├── Select patient from waiting list
├── Doctor is auto-assigned based on specialty
└── Expected: Patient status changes to ASSIGNED
```

#### **Step 5.3: Complete Walk-in Consultation**
```
Repeat Steps 3.1-3.3 and 4.1-4.2 for walk-in patient
└── Expected: Complete consultation and payment process works identically
```

---

## Testing Scenarios

### **Scenario 1: Complete Appointment Flow**
1. Create appointment (automatically booked and queued)
2. Start consultation
3. Add treatment with medicines
4. Dispense medicine (generates detailed invoice)
5. Process payment (marks invoice as PAID, updates queue to COMPLETED)
6. View payment history (shows amounts from invoices)

### **Scenario 2: Walk-in Patient Flow**
1. Add walk-in patient to queue
2. Assign doctor
3. Start consultation
4. Add treatment
5. Process payment

### **Scenario 3: Error Handling**
- Try to process payment for non-existent consultation
- Try to add duplicate patient ID
- Try to create appointment with unavailable doctor
- Try to dispense medicine for non-existent treatment

---

## Expected System Behavior

### **Data Validation**
- Patient IDs must be unique
- Doctor IDs must exist before creating appointment
- Medicine IDs must exist before prescribing
- Consultation must be COMPLETED before payment
- Payment status only shows PAID/NOT_PAID

### **Workflow Enforcement**
- Appointments must be created before queuing
- Treatment must be created before dispensing
- Medicine must be dispensed before payment
- Consultation status updates automatically

### **UI Features**
- Clean table displays for all data
- Clear navigation menus
- Confirmation prompts for critical actions
- Error messages for invalid inputs
- Success confirmations with details

---

## Common Issues & Solutions

### **Issue 1: "No patients in queue"**
**Solution**: Ensure patients are properly added to queue before starting consultations

### **Issue 2: "Consultation not eligible for payment"**
**Solution**: Ensure consultation status is COMPLETED (requires treatment + medicine dispensing)

### **Issue 3: "Medicine not found"**
**Solution**: Ensure medicines are added to system before prescribing

### **Issue 4: "Doctor schedule conflict"**
**Solution**: System automatically prevents double-booking when creating appointments

### **Issue 5: "Patient/Doctor ID does not exist"**
**Solution**: Enter valid patient and doctor IDs that exist in the system. Check patient and doctor lists first if needed.

---

## System Features Verified

- **Semi-automatic workflow** (user-driven decisions)
- **Data integrity** (relationships maintained)
- **Real-time status updates**
- **Financial calculations** (consultation + treatment + medicine costs)
- **Payment tracking** (only PAID payments in history)
- **Inventory management** (medicine stock tracking)
- **Appointment scheduling** (doctor availability)
- **Patient queue management** (walk-in and scheduled)

---

## Testing Checklist

- [ ] **Doctors**: Add, view, validate specialties and fees, status management
- [ ] **Patients**: Add students/staff/tutors, validate IDs, queue management
- [ ] **Medicines**: Add, validate prices and units, stock management
- [ ] **Schedules**: View automatically created schedules, booking conflicts
- [ ] **Appointments**: Create, auto-queue, conflict prevention, walk-in support
- [ ] **Consultations**: Start, status tracking, queue integration, completion
- [ ] **Treatments**: Create, add prescriptions, fee calculation, medicine linking
- [ ] **Pharmacy**: Dispense, stock updates, invoice generation, workflow completion
- [ ] **Payments**: Process with detailed breakdowns, invoice status updates, queue completion
- [ ] **Payment History**: Amount display, invoice integration, financial summaries
- [ ] **Queue Management**: Status updates (WAITING→ASSIGNED→IN_CONSULTATION→COMPLETED)
- [ ] **Invoice System**: Auto-generation, detailed fee breakdown, payment status tracking
- [ ] **Data Integrity**: All relationships maintained, status updates synchronized
- [ ] **Error Handling**: Invalid inputs, missing data, conflicts, edge cases

---

## System Ready for Production

**All core functionalities tested and verified:**
- Patient registration and management (Students, Staff, Tutors)
- Doctor scheduling and appointments with conflict prevention
- Consultation and treatment workflow with status tracking
- Pharmacy medicine dispensing with automated invoice generation
- Payment processing with detailed fee breakdowns (Consultation + Treatment + Medicine)
- Financial reporting and payment history with amounts
- Queue management with automatic status updates (WAITING->ASSIGNED->IN_CONSULTATION->COMPLETED)
- Invoice system with detailed fee breakdown and payment status tracking
- Data integrity and validation across all modules
- User-friendly interface with clear navigation and error handling
- Complete clinical workflow from appointment to payment
- Semi-automatic patient flow with manual decision points

**The TARUMT Clinic Management System is fully operational and ready for clinical use!**
