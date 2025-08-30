# TARUMT Clinic Management System

A comprehensive healthcare management system built in Java following the Entity-Control-Boundary (ECB) pattern. The system manages patient care from registration through treatment completion with integrated pharmacy and payment modules.

## 🏥 System Overview

The TARUMT Clinic Management System is designed to streamline healthcare operations with a focus on:
- **Patient Management**: Registration, queue management, and record maintenance
- **Doctor Management**: Registration, schedule management, and performance tracking
- **Consultation Management**: Patient assignment, appointment booking, and consultation tracking
- **Treatment Management**: Treatment creation, medicine prescription, and treatment lifecycle
- **Pharmacy Management**: Medicine inventory, stock management, and dispensing

## 🏗️ Architecture

### Design Pattern
- **Entity-Control-Boundary (ECB) Pattern**: Clear separation of concerns
  - **Entity**: Data models and business objects
  - **Control**: Business logic and data processing
  - **Boundary**: User interface and system boundaries

### Custom ADT Implementation
- **Primary ADT**: `HashMapInterface` (HashMapADT) for efficient data storage and retrieval
- **Supporting ADT**: `ListInterface` (ArrayList) for sequential data operations
- **No JCF**: System uses only custom ADT implementations as per requirements

### Module Structure
```
TARUMT Clinic Management System
├── Patient Management Module
├── Doctor Management Module
├── Consultation Management Module
├── Medical Treatment Management Module
└── Pharmacy Management Module
```

## 🔄 Clinical Workflow

The system follows a natural clinical workflow:

1. **Patient Registration** → Patient enters the system
2. **Queue Management** → Patient joins waiting queue
3. **Doctor Assignment** → Patient assigned to available doctor
4. **Consultation Creation** → Consultation record created
5. **Treatment Completion** → Doctor completes consultation with treatment
6. **Medicine Prescription** → Medicines prescribed and added to treatment
7. **Payment Processing** → Treatment fees and medicine costs processed
8. **Pharmacy Dispensing** → Medicines dispensed from inventory

## 📁 Project Structure

```
TARUMT-ClinicManagementSystem/
├── adt/                    # Custom ADT implementations
│   ├── ArrayList.java
│   ├── HashMapADT.java
│   ├── HashMapInterface.java
│   └── ListInterface.java
├── boundary/               # User interface layer
│   ├── ConsultationUI.java
│   ├── DoctorUI.java
│   ├── PatientUI.java
│   ├── PaymentBoundary.java
│   ├── PharmacyUI.java
│   ├── StaffMenuBoundary.java
│   └── TreatmentUI.java
├── control/                # Business logic layer
│   ├── ConsultationController.java
│   ├── DoctorController.java
│   ├── PatientController.java
│   ├── PaymentController.java
│   ├── PharmacyController.java
│   ├── StaffControl.java
│   └── TreatmentController.java
├── dao/                    # Data access layer
│   ├── ConsultationDAO.java
│   ├── DoctorDAO.java
│   ├── DoctorScheduleDAO.java
│   ├── InvoiceDAO.java
│   ├── MedicineDAO.java
│   ├── PatientDAO.java
│   ├── PatientQueueDAO.java
│   ├── PaymentDAO.java
│   ├── StockDAO.java
│   └── TreatmentDAO.java
├── data/                   # Data storage files
│   ├── consultations.txt
│   ├── doctor_schedules.txt
│   ├── doctors.txt
│   ├── invoices.txt
│   ├── medicines.txt
│   ├── patient_queue.txt
│   ├── patients.txt
│   ├── payments.txt
│   ├── queue_history.txt
│   ├── stocks.txt
│   └── treatments.txt
├── entity/                 # Data models
│   ├── Consultation.java
│   ├── Doctor.java
│   ├── DoctorSchedule.java
│   ├── Human.java
│   ├── Invoice.java
│   ├── Medicine.java
│   ├── MedicinePrescribed.java
│   ├── Patient.java
│   ├── PatientQueueEntry.java
│   ├── Payment.java
│   ├── QueueStatus.java
│   ├── QueueType.java
│   ├── Specialty.java
│   ├── Staff.java
│   ├── Stock.java
│   ├── Student.java
│   ├── Treatment.java
│   └── Tutor.java
├── main/                   # Main application entry
│   └── ClinicManagementSystem.java
├── utility/                # Utility classes
│   ├── FilterCriteriaUtil.java
│   └── SystemUtil.java
├── SystemFlowTest.java     # Comprehensive system flow test
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Windows PowerShell (for batch scripts)

### Compilation
```bash
javac -cp . main/ClinicManagementSystem.java
```

### Execution
```bash
java -cp . main.ClinicManagementSystem
```

### Running Tests
```bash
javac SystemFlowTest.java
java SystemFlowTest
```

## 🧪 Testing

The system includes comprehensive testing:

### Automated Test Suite
- **SystemFlowTest.java**: Comprehensive test covering all modules and methods
- **Test Coverage**: All 5 modules and complete workflow
- **Validation**: Data integrity and system performance

### Test Results
```
✓ System Startup and Navigation
✓ Patient Management Module
✓ Doctor Management Module
✓ Consultation Management Module
✓ Treatment Management Module
✓ Pharmacy Management Module
✓ Complete Clinical Workflow
```

## 📊 Features by Module

### 1. Patient Management Module
- Patient registration (Student, Tutor, Staff)
- Queue management (Walk-in, Appointment)
- Patient record maintenance
- Patient search and update
- Queue status tracking

### 2. Doctor Management Module
- Doctor registration and profiles
- Schedule management
- Specialty assignment
- Performance tracking
- Doctor availability management

### 3. Consultation Management Module
- Patient assignment to doctors
- Consultation creation and tracking
- Appointment booking system
- Consultation status management
- Daily consultation reports

### 4. Medical Treatment Management Module
- Treatment creation and management
- Medicine prescription system
- Treatment updates and modifications
- Treatment history tracking
- Performance analysis reports

### 5. Pharmacy Management Module
- Medicine inventory management
- Stock level monitoring
- Medicine dispensing
- Inventory reports
- Stock alerts and notifications

## 🔧 Technical Specifications

### Data Persistence
- **File-based Storage**: Text files for data persistence
- **Custom DAO Layer**: Data Access Objects for each entity
- **Data Validation**: Input validation and data integrity checks

### User Interface
- **Console-based UI**: Clean, intuitive command-line interface
- **Menu-driven Navigation**: Hierarchical menu system
- **User-friendly Prompts**: Clear instructions and error messages

### Data Structures
- **HashMap ADT**: Primary data structure for efficient lookups
- **ArrayList ADT**: Supporting structure for sequential operations
- **Custom Implementations**: No Java Collections Framework usage

## 📈 System Performance

### Optimizations
- Efficient data structures for fast lookups
- Minimal memory footprint
- Optimized file I/O operations
- Streamlined workflow processes

### Scalability
- Modular design for easy expansion
- Extensible entity relationships
- Configurable system parameters

## 🛠️ Maintenance

### Data Management
- Regular data backups recommended
- File integrity monitoring
- Data validation procedures

### System Updates
- Modular update capability
- Backward compatibility maintenance
- Version control integration

## 📝 License

This project is developed for TARUMT academic purposes.

## 👥 Development Team

This system was developed as a group project with each team member responsible for one of the five core modules.

---

**TARUMT Clinic Management System** - Complete healthcare management solution
