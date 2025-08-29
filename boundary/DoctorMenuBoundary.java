package boundary;

import java.util.Scanner;
import control.DoctorRecordControl;
import control.ConsultationControl;
import control.DoctorScheduleControl;
import control.DoctorMenuControl;
import entity.Doctor;
import boundary.ConsultationMenuBoundary;

/**
 * Boundary class for doctor menu interface with login functionality
 * @author Your Name
 */
public class DoctorMenuBoundary {
    private Scanner sc;
    private DoctorRecordControl doctorRecordControl;
    private ConsultationControl consultationControl;
    private DoctorScheduleControl scheduleControl;
    private DoctorMenuControl doctorMenuControl;
    private Doctor currentDoctor;

    public DoctorMenuBoundary(DoctorRecordControl doctorRecordControl, ConsultationControl sharedConsultationControl) {
        this.sc = new Scanner(System.in);
        this.doctorRecordControl = doctorRecordControl;
        this.consultationControl = sharedConsultationControl; // Use shared instance
        this.scheduleControl = new DoctorScheduleControl();
        this.doctorMenuControl = new DoctorMenuControl(doctorRecordControl, consultationControl, scheduleControl);
        this.currentDoctor = null;
    }

    public void mainMenu() {
        while (true) {
            if (currentDoctor == null) {
                // Not logged in - show login menu
                boolean shouldReturnToMain = showLoginMenu();
                if (shouldReturnToMain) {
                    return; // Return to main clinic menu
                }
            } else {
                // Logged in - show doctor dashboard
                showDoctorDashboard();
            }
        }
    }

    private boolean showLoginMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("    DOCTOR LOGIN");
        System.out.println("=".repeat(40));
        System.out.println("1. Login with Doctor ID");
        System.out.println("2. View Available Doctors");
        System.out.println("0. Back to Main Menu");
        System.out.println("-".repeat(40));
        System.out.print("Enter your choice: ");

        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                loginDoctor();
                return false; // Stay in doctor menu
            case "2":
                showAvailableDoctors();
                return false; // Stay in doctor menu
            case "0":
                return true; // Return to main clinic menu
            default:
                System.out.println("Invalid choice. Please try again.");
                return false; // Stay in doctor menu
        }
    }

    private void loginDoctor() {
        System.out.println("\n--- Doctor Login ---");
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine().trim().toUpperCase();

        if (doctorId.isEmpty()) {
            System.out.println("Doctor ID cannot be empty.");
            return;
        }

        // Get doctor from the map
        Doctor doctor = doctorMenuControl.getDoctorById(doctorId);
        
        if (doctor == null) {
            System.out.println("Doctor not found. Please check your Doctor ID.");
            return;
        }

        if (!doctorMenuControl.isDoctorValid(doctor)) {
            System.out.println("This doctor account has been deactivated.");
            return;
        }

        // Login successful
        currentDoctor = doctor;
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    WELCOME, DR. " + doctor.getName().toUpperCase());
        System.out.println("    Specialty: " + doctor.getSpecialty());
        System.out.println("    Doctor ID: " + doctor.getDoctorId());
        System.out.println("=".repeat(50));
    }

    private void showAvailableDoctors() {
        System.out.println("\n--- Available Doctors ---");
        adt.ListInterface<entity.Doctor> doctors = doctorMenuControl.getAllDoctors();
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors found in the system.");
            return;
        }

        System.out.println("Doctor ID | Name | Specialty");
        System.out.println("-".repeat(40));
        for (int i = 0; i < doctors.size(); i++) {
            entity.Doctor doctor = doctors.get(i);
            if (!doctor.isDeleted()) {
                System.out.printf("%-10s | %-20s | %-15s%n", 
                    doctor.getDoctorId(), 
                    doctor.getName(), 
                    doctor.getSpecialty());
            }
        }
        System.out.println("-".repeat(40));
    }

    private void showDoctorDashboard() {
        while (true) {
            utility.SystemUtil.setNavigationPath("Home", "Doctor Portal", "Dr. " + currentDoctor.getName());
            utility.SystemUtil.showMenuHeader("Dr. " + currentDoctor.getName().toUpperCase() + " - Dashboard");
            
            System.out.println("Specialty: " + currentDoctor.getSpecialty() + " | ID: " + currentDoctor.getDoctorId());
            System.out.println();
            System.out.println("1. My Today's Schedule");
            System.out.println("2. My Pending Consultations");
            System.out.println("3. Complete Consultation");
            System.out.println("4. Edit Completed Consultation");
            System.out.println("5. My Consultation History");
            System.out.println("6. My Profile");
            System.out.println("0. Logout");
            System.out.println("-".repeat(50));
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    showMySchedule();
                    break;
                case "2":
                    showMyPendingConsultations();
                    break;
                                 case "3":
                     completeMyConsultation();
                     break;
                 case "4":
                     editCompletedConsultation();
                     break;
                 case "5":
                     showMyConsultationHistory();
                     break;
                 case "6":
                     showMyProfile();
                     break;
                case "0":
                    logout();
                    return; // This will exit the dashboard and return to main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showMySchedule() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    " + currentDoctor.getName().toUpperCase() + " - WEEKLY TIMETABLE");
        System.out.println("                        Specialty: " + currentDoctor.getSpecialty());
        System.out.println("=".repeat(80));
        
        // Show timetable view
        showWeeklyTimetable();
        
        System.out.println("=".repeat(80));
        System.out.println("Legend: [BOOKED] = Appointment booked | [-----] = Available | [TODAY] = Today");
        System.out.println("Type 'exit' to return to menu:");
        while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to return to menu:");
        }
    }
    
    private void showWeeklyTimetable() {
        // Get current date and calculate week range
        java.time.LocalDate today = java.time.LocalDate.now();
        // For demo purposes, let's show the week containing Aug 30, 2025
        java.time.LocalDate demoDate = java.time.LocalDate.of(2025, 8, 30);
        java.time.LocalDate startOfWeek = demoDate.with(java.time.DayOfWeek.MONDAY);
        
        // Define time slots
        String[] timeSlots = {
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
        };
        
        // Print header with time slots
        System.out.printf("%-12s", "Day/Time");
        for (String time : timeSlots) {
            System.out.printf("| %-9s", time);
        }
        System.out.println("|");
        
        // Print separator
        System.out.print("+");
        System.out.print("-".repeat(11));
        for (int i = 0; i < timeSlots.length; i++) {
            System.out.print("+---------");
        }
        System.out.println("+");
        
        // Print each day of the week
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            java.time.LocalDate currentDate = startOfWeek.plusDays(dayOffset);
            String dayName = currentDate.getDayOfWeek().toString().substring(0, 3);
            String dateStr = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"));
            
            // Highlight demo date (Aug 30)
            if (currentDate.equals(demoDate)) {
                System.out.printf("%-12s", dayName + " " + dateStr + "*");
            } else {
                System.out.printf("%-12s", dayName + " " + dateStr);
            }
            
            // Get appointments for this date
            adt.ListInterface<entity.DoctorSchedule> daySchedules = doctorMenuControl.getSchedulesForDate(currentDoctor.getDoctorId(), currentDate);
            
            // Print each time slot for this day
            for (String timeSlot : timeSlots) {
                java.time.LocalTime slotTime = java.time.LocalTime.parse(timeSlot);
                entity.DoctorSchedule appointment = findAppointmentAtTime(daySchedules, slotTime);
                
                if (appointment != null) {
                    // Truncate patient ID for display
                    String patientId = appointment.getPatientId();
                    if (patientId.length() > 7) {
                        patientId = patientId.substring(0, 7);
                    }
                    System.out.printf("| %-9s", "[" + patientId + "]");
                } else {
                    System.out.printf("| %-9s", "[-----]");
                }
            }
            System.out.println("|");
        }
        
        // Print bottom separator
        System.out.print("+");
        System.out.print("-".repeat(11));
        for (int i = 0; i < timeSlots.length; i++) {
            System.out.print("+---------");
        }
        System.out.println("+");
    }
    
    private entity.DoctorSchedule findAppointmentAtTime(adt.ListInterface<entity.DoctorSchedule> schedules, java.time.LocalTime time) {
        for (int i = 0; i < schedules.size(); i++) {
            entity.DoctorSchedule schedule = schedules.get(i);
            if (schedule.getStartTime().equals(time)) {
                return schedule;
            }
        }
        return null;
    }

    private void showMyPendingConsultations() {
        System.out.println("\n--- My Pending Consultations ---");
        System.out.println("Doctor: " + currentDoctor.getName());
        System.out.println("-".repeat(40));
        
        // Get pending consultations for this doctor using control
        adt.ListInterface<entity.Consultation> pendingConsultations = doctorMenuControl.getPendingConsultationsForDoctor(currentDoctor.getDoctorId());
        
        if (pendingConsultations.isEmpty()) {
            System.out.println("No pending consultations found.");
        } else {
            System.out.println("Consultation ID | Patient ID | Date | Time | Status");
            System.out.println("-".repeat(55));
            for (int i = 0; i < pendingConsultations.size(); i++) {
                entity.Consultation consultation = pendingConsultations.get(i);
                System.out.printf("%-15s | %-15s | %s | %s | %s%n",
                    consultation.getConsultationId(),
                    consultation.getPatientId(),
                    doctorMenuControl.formatDate(consultation.getConsultationTime()),
                    doctorMenuControl.formatTime(consultation.getConsultationTime()),
                    consultation.getStatus());
            }
        }
        
        System.out.println("-".repeat(40));
        System.out.println("Type 'exit' to return to menu:");
        while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to return to menu:");
        }
    }

    private void completeMyConsultation() {
        System.out.println("\n--- Complete Consultation ---");
        System.out.println("Doctor: " + currentDoctor.getName());
        System.out.println("-".repeat(40));
        
        // Get pending consultations for this doctor using control
        adt.ListInterface<entity.Consultation> pendingConsultations = doctorMenuControl.getPendingConsultationsForDoctor(currentDoctor.getDoctorId());
        
        if (pendingConsultations.isEmpty()) {
            System.out.println("No pending consultations to complete.");
            System.out.println("Press Enter to continue...");
            sc.nextLine();
            return;
        }
        
        // Show pending consultations
        System.out.println("Pending Consultations:");
        System.out.println("No. | Consultation ID | Patient ID | Date | Time");
        System.out.println("-".repeat(50));
        for (int i = 0; i < pendingConsultations.size(); i++) {
            entity.Consultation consultation = pendingConsultations.get(i);
            System.out.printf("%-3d | %-15s | %-11s | %s | %s%n",
                i + 1,
                consultation.getConsultationId(),
                consultation.getPatientId(),
                doctorMenuControl.formatDate(consultation.getConsultationTime()),
                doctorMenuControl.formatTime(consultation.getConsultationTime()));
        }
        
        System.out.print("\nEnter consultation number to complete (0 to cancel): ");
        String choice = sc.nextLine().trim();
        
        try {
            int index = Integer.parseInt(choice);
            if (index == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            if (index > 0 && index <= pendingConsultations.size()) {
                entity.Consultation consultation = pendingConsultations.get(index - 1);
                System.out.println("\nCompleting consultation: " + consultation.getConsultationId());
                System.out.println("Patient ID: " + consultation.getPatientId());
                
                // Collect treatment details
                if (collectTreatmentDetails(consultation)) {
                    System.out.println("Consultation completed successfully!");
                } else {
                    System.out.println("Failed to complete consultation.");
                }
            } else {
                System.out.println("Invalid consultation number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        
        System.out.println("Type 'exit' to return to menu:");
        while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to return to menu:");
        }
    }

    private void showMyConsultationHistory() {
        System.out.println("\n--- My Consultation History ---");
        System.out.println("Doctor: " + currentDoctor.getName());
        System.out.println("-".repeat(40));
        
        // Get all consultations for this doctor using control
        adt.ListInterface<entity.Consultation> consultations = doctorMenuControl.getAllConsultationsForDoctor(currentDoctor.getDoctorId());
        
        if (consultations.isEmpty()) {
            System.out.println("No consultation history found.");
        } else {
            System.out.println("Consultation ID | Patient ID | Date | Time | Status");
            System.out.println("-".repeat(55));
            for (int i = 0; i < consultations.size(); i++) {
                entity.Consultation consultation = consultations.get(i);
                System.out.printf("%-15s | %-15s | %s | %s | %s%n",
                    consultation.getConsultationId(),
                    consultation.getPatientId(),
                    doctorMenuControl.formatDate(consultation.getConsultationTime()),
                    doctorMenuControl.formatTime(consultation.getConsultationTime()),
                    consultation.getStatus());
            }
        }
        
        System.out.println("-".repeat(40));
        System.out.println("Type 'exit' to return to menu:");
        while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to return to menu:");
        }
    }

    private void showMyProfile() {
        System.out.println("\n--- My Profile ---");
        System.out.println("Doctor ID: " + currentDoctor.getDoctorId());
        System.out.println("Name: Dr. " + currentDoctor.getName());
        System.out.println("Gender: " + currentDoctor.getGender());
        System.out.println("Birthdate: " + currentDoctor.getBirthdate());
        System.out.println("Phone: " + currentDoctor.getPhoneNumber());
        System.out.println("Specialty: " + currentDoctor.getSpecialty());
        System.out.println("Consultation Fee: RM " + String.format("%.2f", currentDoctor.getConsultationFee()));
    }

    private void logout() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    LOGGING OUT");
        System.out.println("    Goodbye, Dr. " + currentDoctor.getName());
        System.out.println("=".repeat(50));
        currentDoctor = null;
    }
    
    /**
     * Collect treatment details and complete consultation
     */
    private boolean collectTreatmentDetails(entity.Consultation consultation) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    TREATMENT DETAILS");
        System.out.println("=".repeat(50));
        
        // Get diagnosis
        System.out.print("Enter diagnosis: ");
        String diagnosis = sc.nextLine().trim();
        
        if (diagnosis.isEmpty()) {
            System.out.println("Diagnosis cannot be empty.");
            return false;
        }
        
        // Get treatment fee
        System.out.print("Enter treatment fee (RM): ");
        String feeStr = sc.nextLine().trim();
        double treatmentFee;
        
        try {
            treatmentFee = Double.parseDouble(feeStr);
            if (treatmentFee < 0) {
                System.out.println("Treatment fee cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid treatment fee format.");
            return false;
        }
        
        // Collect medicine prescriptions
        adt.ListInterface<entity.MedicinePrescribed> medicines = collectMedicinePrescriptions();
        
        // Show treatment summary and confirm
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    TREATMENT SUMMARY");
        System.out.println("=".repeat(50));
        System.out.println("Diagnosis: " + diagnosis);
        System.out.println("Treatment Fee: RM " + String.format("%.2f", treatmentFee));
        System.out.println("Medicines Prescribed: " + medicines.size());
        for (int i = 0; i < medicines.size(); i++) {
            entity.MedicinePrescribed med = medicines.get(i);
            System.out.println("  - " + med.getMedicineId() + " x" + med.getQuantity());
        }
        
        // Ask for confirmation or options
        System.out.println("\nOptions:");
        System.out.println("1. Complete consultation with this treatment");
        System.out.println("2. Edit treatment details");
        System.out.println("3. Start over (clear all details)");
        System.out.println("0. Cancel and return to menu");
        
        System.out.print("Enter choice: ");
        String choice = sc.nextLine().trim();
        
        switch (choice) {
            case "1":
                // Complete the consultation with treatment details
                return doctorMenuControl.completeConsultationWithTreatment(
                    consultation.getConsultationId(), 
                    currentDoctor.getDoctorId(),
                    diagnosis,
                    treatmentFee,
                    medicines
                );
            case "2":
                // Edit treatment details
                System.out.println("Editing treatment details...");
                return editTreatmentDetails(consultation, diagnosis, treatmentFee, medicines);
            case "3":
                // Start over
                System.out.println("Clearing treatment details. Starting over...");
                return collectTreatmentDetails(consultation); // Recursive call to restart
            case "0":
                System.out.println("Cancelled. Returning to menu.");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return collectTreatmentDetails(consultation); // Recursive call to restart
        }
    }
    
    /**
     * Collect medicine prescriptions
     */
    private adt.ListInterface<entity.MedicinePrescribed> collectMedicinePrescriptions() {
        adt.ListInterface<entity.MedicinePrescribed> medicines = new adt.ArrayList<>();
        
        System.out.println("\n--- Medicine Prescriptions ---");
        System.out.println("Enter medicine prescriptions (press Enter with empty medicine ID to finish):");
        
        while (true) {
            System.out.print("Medicine ID: ");
            String medicineId = sc.nextLine().trim();
            
            if (medicineId.isEmpty()) {
                break; // Finished adding medicines
            }
            
            System.out.print("Quantity: ");
            String quantityStr = sc.nextLine().trim();
            
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }
                
                System.out.print("Instructions: ");
                String instructions = sc.nextLine().trim();
                
                if (instructions.isEmpty()) {
                    instructions = "Take as directed";
                }
                
                // Create medicine prescribed entry (instructions are stored in treatment record)
                entity.MedicinePrescribed medicine = new entity.MedicinePrescribed(medicineId, quantity);
                medicines.add(medicine);
                
                System.out.println("Medicine added: " + medicineId + " x" + quantity + " - " + instructions);
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity format.");
            }
        }
        
                 System.out.println("Total medicines prescribed: " + medicines.size());
         return medicines;
     }
     
     /**
      * Edit treatment details before completion
      */
     private boolean editTreatmentDetails(entity.Consultation consultation, String currentDiagnosis, double currentFee, adt.ListInterface<entity.MedicinePrescribed> currentMedicines) {
         System.out.println("\n" + "=".repeat(50));
         System.out.println("    EDIT TREATMENT DETAILS");
         System.out.println("=".repeat(50));
         
         while (true) {
             System.out.println("\nCurrent Treatment Details:");
             System.out.println("Diagnosis: " + currentDiagnosis);
             System.out.println("Treatment Fee: RM " + String.format("%.2f", currentFee));
             System.out.println("Medicines: " + currentMedicines.size() + " items");
             
             System.out.println("\nEdit Options:");
             System.out.println("1. Change diagnosis");
             System.out.println("2. Change treatment fee");
             System.out.println("3. Edit medicines (add/remove/change quantity)");
             System.out.println("4. Review and confirm");
             System.out.println("0. Cancel editing");
             
             System.out.print("Enter choice: ");
             String choice = sc.nextLine().trim();
             
             switch (choice) {
                 case "1":
                     System.out.print("New diagnosis: ");
                     currentDiagnosis = sc.nextLine().trim();
                     if (currentDiagnosis.isEmpty()) {
                         System.out.println("Diagnosis cannot be empty. Keeping current diagnosis.");
                         currentDiagnosis = "Common cold"; // Default fallback
                     }
                     System.out.println("Diagnosis updated to: " + currentDiagnosis);
                     break;
                     
                 case "2":
                     System.out.print("New treatment fee (RM): ");
                     String feeStr = sc.nextLine().trim();
                     try {
                         double newFee = Double.parseDouble(feeStr);
                         if (newFee >= 0) {
                             currentFee = newFee;
                             System.out.println("Treatment fee updated to: RM " + String.format("%.2f", currentFee));
                         } else {
                             System.out.println("Fee cannot be negative. Keeping current fee.");
                         }
                     } catch (NumberFormatException e) {
                         System.out.println("Invalid fee format. Keeping current fee.");
                     }
                     break;
                     
                 case "3":
                     currentMedicines = editMedicinePrescriptions(currentMedicines);
                     break;
                     
                 case "4":
                     // Show final summary and confirm
                     System.out.println("\n" + "=".repeat(50));
                     System.out.println("    FINAL TREATMENT SUMMARY");
                     System.out.println("=".repeat(50));
                     System.out.println("Diagnosis: " + currentDiagnosis);
                     System.out.println("Treatment Fee: RM " + String.format("%.2f", currentFee));
                     System.out.println("Medicines Prescribed: " + currentMedicines.size());
                     for (int i = 0; i < currentMedicines.size(); i++) {
                         entity.MedicinePrescribed med = currentMedicines.get(i);
                         System.out.println("  - " + med.getMedicineId() + " x" + med.getQuantity());
                     }
                     
                     System.out.print("\nConfirm this treatment? (y/n): ");
                     String confirm = sc.nextLine().trim().toLowerCase();
                     if (confirm.equals("y") || confirm.equals("yes")) {
                         return doctorMenuControl.completeConsultationWithTreatment(
                             consultation.getConsultationId(),
                             currentDoctor.getDoctorId(),
                             currentDiagnosis,
                             currentFee,
                             currentMedicines
                         );
                     } else {
                         System.out.println("Treatment not confirmed. Continue editing.");
                     }
                     break;
                     
                 case "0":
                     System.out.println("Editing cancelled. Returning to menu.");
                     return false;
                     
                 default:
                     System.out.println("Invalid choice. Please try again.");
             }
         }
     }
     
     /**
      * Edit medicine prescriptions
      */
     private adt.ListInterface<entity.MedicinePrescribed> editMedicinePrescriptions(adt.ListInterface<entity.MedicinePrescribed> currentMedicines) {
         while (true) {
             System.out.println("\n--- Current Medicines ---");
             if (currentMedicines.isEmpty()) {
                 System.out.println("No medicines currently prescribed.");
             } else {
                 for (int i = 0; i < currentMedicines.size(); i++) {
                     entity.MedicinePrescribed med = currentMedicines.get(i);
                     System.out.println((i + 1) + ". " + med.getMedicineId() + " x" + med.getQuantity());
                 }
             }
             
             System.out.println("\nMedicine Edit Options:");
             System.out.println("1. Add new medicine");
             System.out.println("2. Remove medicine");
             System.out.println("3. Change quantity");
             System.out.println("4. Done editing medicines");
             
             System.out.print("Enter choice: ");
             String choice = sc.nextLine().trim();
             
             switch (choice) {
                 case "1":
                     // Add new medicine
                     System.out.print("Medicine ID: ");
                     String medicineId = sc.nextLine().trim();
                     if (!medicineId.isEmpty()) {
                         System.out.print("Quantity: ");
                         String quantityStr = sc.nextLine().trim();
                         try {
                             int quantity = Integer.parseInt(quantityStr);
                             if (quantity > 0) {
                                 entity.MedicinePrescribed newMed = new entity.MedicinePrescribed(medicineId, quantity);
                                 currentMedicines.add(newMed);
                                 System.out.println("Added: " + medicineId + " x" + quantity);
                             } else {
                                 System.out.println("Quantity must be positive.");
                             }
                         } catch (NumberFormatException e) {
                             System.out.println("Invalid quantity format.");
                         }
                     }
                     break;
                     
                 case "2":
                     // Remove medicine
                     if (currentMedicines.isEmpty()) {
                         System.out.println("No medicines to remove.");
                         break;
                     }
                     System.out.print("Enter medicine number to remove: ");
                     try {
                         int index = Integer.parseInt(sc.nextLine().trim());
                         if (index > 0 && index <= currentMedicines.size()) {
                             entity.MedicinePrescribed removed = currentMedicines.remove(index - 1);
                             System.out.println("Removed: " + removed.getMedicineId() + " x" + removed.getQuantity());
                         } else {
                             System.out.println("Invalid medicine number.");
                         }
                     } catch (NumberFormatException e) {
                         System.out.println("Invalid number format.");
                     }
                     break;
                     
                 case "3":
                     // Change quantity
                     if (currentMedicines.isEmpty()) {
                         System.out.println("No medicines to modify.");
                         break;
                     }
                     System.out.print("Enter medicine number to modify: ");
                     try {
                         int index = Integer.parseInt(sc.nextLine().trim());
                         if (index > 0 && index <= currentMedicines.size()) {
                             entity.MedicinePrescribed med = currentMedicines.get(index - 1);
                             System.out.print("New quantity for " + med.getMedicineId() + ": ");
                             String newQtyStr = sc.nextLine().trim();
                             try {
                                 int newQty = Integer.parseInt(newQtyStr);
                                 if (newQty > 0) {
                                     // Create new medicine with updated quantity
                                     entity.MedicinePrescribed updatedMed = new entity.MedicinePrescribed(med.getMedicineId(), newQty);
                                     currentMedicines.set(index - 1, updatedMed);
                                     System.out.println("Updated: " + med.getMedicineId() + " x" + newQty);
                                 } else {
                                     System.out.println("Quantity must be positive.");
                                 }
                             } catch (NumberFormatException e) {
                                 System.out.println("Invalid quantity format.");
                             }
                         } else {
                             System.out.println("Invalid medicine number.");
                         }
                     } catch (NumberFormatException e) {
                         System.out.println("Invalid number format.");
                     }
                     break;
                     
                 case "4":
                     System.out.println("Medicine editing completed.");
                     return currentMedicines;
                     
                 default:
                     System.out.println("Invalid choice. Please try again.");
             }
         }
     }
     
     /**
      * Edit completed consultation (before payment)
      */
     private void editCompletedConsultation() {
         System.out.println("\n--- Edit Completed Consultation ---");
         System.out.println("Doctor: " + currentDoctor.getName());
         System.out.println("-".repeat(40));
         
         // Get completed consultations for this doctor (filter from all consultations)
         adt.ListInterface<entity.Consultation> allConsultations = doctorMenuControl.getAllConsultationsForDoctor(currentDoctor.getDoctorId());
         adt.ListInterface<entity.Consultation> completedConsultations = new adt.ArrayList<>();
         
         for (int i = 0; i < allConsultations.size(); i++) {
             entity.Consultation consultation = allConsultations.get(i);
             if (consultation.getStatus().equals("COMPLETED")) {
                 completedConsultations.add(consultation);
             }
         }
         
         if (completedConsultations.isEmpty()) {
             System.out.println("No completed consultations found.");
             System.out.println("Type 'exit' to return to menu:");
             while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
                 System.out.println("Type 'exit' to return to menu:");
             }
             return;
         }
         
         // Show completed consultations
         System.out.println("Completed Consultations:");
         System.out.println("No. | Consultation ID | Patient ID | Date | Treatment ID");
         System.out.println("-".repeat(60));
         for (int i = 0; i < completedConsultations.size(); i++) {
             entity.Consultation consultation = completedConsultations.get(i);
             System.out.printf("%-3d | %-15s | %-11s | %s | %s%n",
                 i + 1,
                 consultation.getConsultationId(),
                 consultation.getPatientId(),
                 doctorMenuControl.formatDate(consultation.getConsultationTime()),
                 consultation.getTreatmentId() != null ? consultation.getTreatmentId() : "N/A");
         }
         
         System.out.print("\nEnter consultation number to edit (0 to cancel): ");
         String choice = sc.nextLine().trim();
         
         try {
             int index = Integer.parseInt(choice);
             if (index == 0) {
                 System.out.println("Operation cancelled.");
                 return;
             }
             
             if (index > 0 && index <= completedConsultations.size()) {
                 entity.Consultation consultation = completedConsultations.get(index - 1);
                 System.out.println("\nEditing consultation: " + consultation.getConsultationId());
                 System.out.println("Patient ID: " + consultation.getPatientId());
                 
                 // Check if payment has been made
                 if (isConsultationPaid(consultation.getConsultationId())) {
                     System.out.println("Cannot edit: Payment has already been made for this consultation.");
                     System.out.println("Type 'exit' to return to menu:");
                     while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
                         System.out.println("Type 'exit' to return to menu:");
                     }
                     return;
                 }
                 
                 // Allow editing of treatment
                 editExistingTreatment(consultation);
                 
             } else {
                 System.out.println("Invalid consultation number.");
             }
         } catch (NumberFormatException e) {
             System.out.println("Please enter a valid number.");
         }
         
         System.out.println("Type 'exit' to return to menu:");
         while (!sc.nextLine().trim().equalsIgnoreCase("exit")) {
             System.out.println("Type 'exit' to return to menu:");
         }
     }
     
     /**
      * Check if consultation has been paid
      */
     private boolean isConsultationPaid(String consultationId) {
         // Check if there's a paid invoice for this consultation
         control.InvoiceControl invoiceControl = new control.InvoiceControl();
         entity.Invoice invoice = invoiceControl.getInvoiceByConsultation(consultationId);
         return invoice != null && invoice.isPaid();
     }
     
     /**
      * Edit existing treatment for completed consultation
      */
     private void editExistingTreatment(entity.Consultation consultation) {
         System.out.println("\n--- Edit Existing Treatment ---");
         System.out.println("Consultation: " + consultation.getConsultationId());
         
         // Load current treatment details
         control.TreatmentControl treatmentControl = new control.TreatmentControl();
         entity.Treatment currentTreatment = treatmentControl.getTreatmentById(consultation.getTreatmentId());
         
         if (currentTreatment == null) {
             System.out.println("No treatment found for this consultation.");
             return;
         }
         
         System.out.println("Current Treatment:");
         System.out.println("Diagnosis: " + currentTreatment.getDescription());
         System.out.println("Treatment Fee: RM " + String.format("%.2f", currentTreatment.getTreatmentFee()));
         System.out.println("Medicines: " + currentTreatment.getPrescribedMedicines().size() + " items");
         
         System.out.println("\nEdit Options:");
         System.out.println("1. Change diagnosis");
         System.out.println("2. Change treatment fee");
         System.out.println("3. Edit medicines");
         System.out.println("4. Save changes");
         System.out.println("0. Cancel editing");
         
         System.out.print("Enter choice: ");
         String choice = sc.nextLine().trim();
         
         switch (choice) {
                              case "1":
                     System.out.print("New diagnosis: ");
                     String newDiagnosis = sc.nextLine().trim();
                     if (!newDiagnosis.isEmpty()) {
                         currentTreatment.setDescription(newDiagnosis);
                         System.out.println("Diagnosis updated.");
                     }
                     break;
                 
             case "2":
                 System.out.print("New treatment fee (RM): ");
                 String feeStr = sc.nextLine().trim();
                 try {
                     double newFee = Double.parseDouble(feeStr);
                     if (newFee >= 0) {
                         currentTreatment.setTreatmentFee(newFee);
                         System.out.println("Treatment fee updated.");
                     }
                 } catch (NumberFormatException e) {
                     System.out.println("Invalid fee format.");
                 }
                 break;
                 
                              case "3":
                     // Edit medicines (clear and rebuild)
                     adt.ListInterface<entity.MedicinePrescribed> newMedicines = editMedicinePrescriptions(new adt.ArrayList<>());
                     // Clear existing medicines and add new ones
                     while (currentTreatment.getPrescribedMedicines().size() > 0) {
                         currentTreatment.getPrescribedMedicines().remove(0);
                     }
                     for (int i = 0; i < newMedicines.size(); i++) {
                         currentTreatment.addPrescribedMedicine(newMedicines.get(i));
                     }
                     break;
                     
                 case "4":
                     // Save changes and regenerate invoice
                     treatmentControl.saveTreatments();
                     System.out.println("Treatment updated successfully!");
                     System.out.println("Invoice will be regenerated with new amounts.");
                     
                     // Regenerate invoice by deleting old one and creating new one
                     control.InvoiceControl invoiceControl = new control.InvoiceControl();
                     invoiceControl.deleteInvoiceByConsultation(consultation.getConsultationId());
                     
                     // Generate new invoice through consultation completion
                     control.ConsultationControl consultationControl = new control.ConsultationControl();
                     consultationControl.completeConsultationWithTreatment(
                         consultation.getConsultationId(),
                         currentDoctor.getDoctorId(),
                         currentTreatment.getDescription(),
                         currentTreatment.getTreatmentFee(),
                         currentTreatment.getPrescribedMedicines()
                     );
                     break;
                 
             case "0":
                 System.out.println("Editing cancelled.");
                 break;
                 
             default:
                 System.out.println("Invalid choice.");
         }
     }
 }
