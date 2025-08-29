# 👥 **PATIENT MANAGEMENT MODULE DEMO**

## 🎯 **Module Overview:**
Complete patient lifecycle management with registration, queue management, and comprehensive reporting.

---

## 📋 **Step-by-Step Demo:**

### **1. Start System & Navigate to Patient Management**
```bash
java main.ClinicManagementSystem
# Choose: 1 (Staff Portal)
# Choose: 1 (Patient Management)
```

### **2. Patient Registration (Choice: 1 → 1)**
**Test all 3 patient types:**

#### **Student Registration:**
```
Name: Alice Wong
Gender (M/F): F
Birthdate (yyyy-MM-dd): 2003-09-15
Phone (digits only): 0123456789
Role:
1. Student
2. Tutor  
3. Staff
Choice: 1
Student ID: S2024001
Faculty: FOCS
```
**Expected:** Patient ID: P1003

#### **Tutor Registration:**
```
Name: Dr. Bob Lee
Gender (M/F): M
Birthdate (yyyy-MM-dd): 1980-05-20
Phone (digits only): 0187654321
Role Choice: 2
Tutor ID: T001
Faculty: FICT
```
**Expected:** Patient ID: P1004

#### **Staff Registration:**
```
Name: Carol Smith
Gender (M/F): F
Birthdate (yyyy-MM-dd): 1990-12-03
Phone (digits only): 0156789012
Role Choice: 3
Staff ID: ST002
Department: HR Department
```
**Expected:** Patient ID: P1005

### **3. View All Patients (Choice: 1 → 2)**
**Expected Display:**
```
Navigation: Home > Staff Portal > Staff Menu > Patient Management > Patient Details > View All Patients
==================================================
    PATIENT LIST
==================================================

+---------+------------+--------+------------+----------+-------+----------+----------+----------+
|PatientID| Name       | Gender | Birthdate  | Phone    | Role  | Role ID  | Faculty  | Dept     |
+---------+------------+--------+------------+----------+-------+----------+----------+----------+
| P1000   | John Doe   | M      | 2000-05-20 | 01112345 | Student| S2000001 | -       | -        |
| P1001   | Mary Smith | F      | 1990-08-12 | 01198765 | Tutor  | T001    | FOCS     | -        |
| P1002   | Alex John  | M      | 1985-11-30 | 01155555 | Staff  | ST001   | -        | IT       |
| P1003   | Alice Wong | F      | 2003-09-15 | 01234567 | Student| S2024001| -        | -        |
+---------+------------+--------+------------+----------+-------+----------+----------+----------+

Options:
1. Filter    2. Search    3. Sort    4. Reset    0. Back
```

### **4. Patient Queue Management (Choice: 1 → 2)**

#### **Add Walk-in Patient:**
```
Choose: 1 (Add walk-in patient)
Enter Patient ID: P1003
Select Specialty:
1. GENERAL_PRACTICE
2. CARDIOLOGY  
3. DERMATOLOGY
4. PEDIATRICS
Choice: 4 (PEDIATRICS)
```
**Expected:** Patient added to queue successfully!

#### **View Current Queue:**
```
Choose: 3 (View current queue)
```
**Expected Display:**
```
==================================================
    CURRENT PATIENT QUEUE
==================================================
Queue Position | Patient ID | Patient Name | Type    | Specialty
1              | P1003      | Alice Wong   | Walk-in | PEDIATRICS
```

#### **Call Next Patient:**
```
Choose: 4 (Call next patient)
```
**Expected:** Auto-assignment if only one pediatric doctor available

### **5. Update Patient (Choice: 1 → 3)**
```
Enter Patient ID to update: P1003
Update Name (current: Alice Wong): Alice Wong Mei
# Continue with other fields or press Enter to keep current
```

### **6. Reports Generation**

#### **Report 1: Patients by Role (Choice: 1 → Reports)**
**Expected Output:**
```
--- Patients by Role ---
Students: 2
Tutors: 1  
Staff: 2
Total Active Patients: 5
```

#### **Report 2: Queue Size Analysis**
**Expected Output:**
```
--- Queue Size Report ---
Current Queue Size: 1
By Specialty:
- PEDIATRICS: 1
- GENERAL_PRACTICE: 0
- CARDIOLOGY: 0
- DERMATOLOGY: 0
```

---

## 🔍 **Key Features Demonstrated:**

### ✅ **Core Functionality:**
- **Multi-type Patient Registration**: Student, Tutor, Staff with specific fields
- **Queue Management**: Walk-in and appointment handling
- **Patient Lifecycle**: Create, view, update, delete, restore
- **Advanced Filtering**: Search, sort, filter by multiple criteria

### ✅ **ADT Usage:**
- **ArrayList**: Patient lists, queue management
- **HashMapADT**: Patient storage with ID keys
- **Generics**: Type-safe collections throughout

### ✅ **ECB Architecture:**
- **Boundary**: PatientManagementBoundary, PatientQueueBoundary
- **Control**: PatientRecordControl, PatientQueueControl  
- **Entity**: Patient hierarchy (Student, Tutor, Staff)

### ✅ **Reports (2+ Required):**
1. **Patients by Role Summary**
2. **Queue Size Analysis**

---

## 🧪 **Expected Results:**
- **Professional Navigation**: Breadcrumb showing current location
- **Clean Interface**: Consistent headers and formatting
- **Data Persistence**: All changes saved to files
- **Validation**: Robust input validation for all fields
- **Real-time Updates**: Queue changes reflect immediately

**Module Status: ✅ FULLY FUNCTIONAL**
