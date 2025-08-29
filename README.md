# 🏥 TARUMT Clinic Management System

## 📋 Project Information
- **Course**: Data Structures and Algorithms
- **Institution**: TAR UMT (Tunku Abdul Rahman University of Management and Technology)
- **Language**: Java
- **IDE**: NetBeans 18
- **JDK**: Java 20
- **Build System**: Apache Ant

## 🚀 Quick Start

### **NetBeans 18 Setup:**
1. **Open Project**: File → Open Project → Select this folder
2. **Build Project**: Right-click project → Build (or F11)
3. **Run Project**: Right-click project → Run (or F6)

### **Command Line (Alternative):**
```bash
# Compile and run
ant run

# Build JAR file
ant jar

# Clean build
ant clean

# Development mode (clean + compile + run)
ant dev
```

## 🏗️ Project Structure
```
TARUMT-ClinicManagementSystem/
├── adt/                    # Custom Collection ADTs
├── boundary/               # User Interface Layer
├── control/                # Business Logic Layer  
├── dao/                    # Data Access Objects
├── data/                   # Data Storage Files
├── entity/                 # Data Models
├── main/                   # Application Entry Point
├── utility/                # Utility Classes
├── nbproject/              # NetBeans Configuration
├── build.xml              # Ant Build Configuration
└── README.md              # This file
```

## 🎯 Key Features
- **Custom Collection ADTs**: ArrayList<T> and HashMapADT<K,V>
- **ECB Architecture**: Entity-Control-Boundary pattern
- **Professional Navigation**: Breadcrumb system throughout
- **Healthcare Workflow**: Complete patient lifecycle management
- **Data Persistence**: File-based storage with DAO pattern

## 📚 Modules
1. **Patient Management** - Patient registration and record management
2. **Doctor Management** - Doctor profiles and scheduling  
3. **Consultation Management** - Appointment booking and consultations
4. **Medical Treatment Management** - Treatment records and prescriptions
5. **Pharmacy Management** - Medicine inventory and stock control

## 🧪 Testing
Follow the comprehensive test guide in `COMPLETE_TEST_GUIDE.md` for complete system testing.

## 🛠️ Development
- **Java Version**: 20
- **Encoding**: UTF-8
- **No External Libraries**: Pure Java implementation
- **No Java Collections Framework**: Custom ADT implementation only

## 📞 Support
For technical issues or questions, refer to the demo guides in the project root directory.

---
**TARUMT Clinic Management System - Professional Healthcare Solution** 🏥✨
