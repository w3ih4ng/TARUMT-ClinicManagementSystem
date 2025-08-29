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

    public DoctorMenuBoundary(DoctorRecordControl doctorRecordControl, ConsultationMenuBoundary consultationBoundary) {
        this.sc = new Scanner(System.in);
        this.doctorRecordControl = doctorRecordControl;
        this.consultationControl = new ConsultationControl();
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
            System.out.println("\n" + "=".repeat(50));
            System.out.println("    DR. " + currentDoctor.getName().toUpperCase() + " - DASHBOARD");
            System.out.println("    Specialty: " + currentDoctor.getSpecialty() + " | ID: " + currentDoctor.getDoctorId());
            System.out.println("=".repeat(50));
            System.out.println("1. My Today's Schedule");
            System.out.println("2. My Pending Consultations");
            System.out.println("3. Complete Consultation");
            System.out.println("4. My Consultation History");
            System.out.println("5. My Profile");
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
                    showMyConsultationHistory();
                    break;
                case "5":
                    showMyProfile();
                    break;
                case "0":
                    logout();
                    return; // This will exit the dashboard and return to main menu
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private void showMySchedule() {
        System.out.println("\n--- My Today's Schedule ---");
        System.out.println("Doctor: " + currentDoctor.getName());
        System.out.println("Date: " + doctorMenuControl.getCurrentDate());
        System.out.println("-".repeat(40));
        
        // Get today's schedule for this doctor using control
        adt.ListInterface<entity.DoctorSchedule> todaysSchedules = doctorMenuControl.getTodaysSchedulesForDoctor(currentDoctor.getDoctorId());
        
        if (todaysSchedules.isEmpty()) {
            System.out.println("No appointments scheduled for today.");
        } else {
            System.out.println("Time | Patient ID | Status");
            System.out.println("-".repeat(30));
            for (int i = 0; i < todaysSchedules.size(); i++) {
                entity.DoctorSchedule schedule = todaysSchedules.get(i);
                System.out.printf("%-5s | %-11s | %s%n", 
                    schedule.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                    schedule.getPatientId(),
                    schedule.getConsultationId() != null ? "BOOKED" : "AVAILABLE");
            }
        }
        
        System.out.println("-".repeat(40));
        System.out.println("Press Enter to continue...");
        sc.nextLine();
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
        System.out.println("Press Enter to continue...");
        sc.nextLine();
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
                
                // Mark consultation as completed using control
                boolean success = doctorMenuControl.completeConsultationForDoctor(consultation.getConsultationId(), currentDoctor.getDoctorId());
                
                if (success) {
                    System.out.println("✅ Consultation completed successfully!");
                } else {
                    System.out.println("❌ Failed to complete consultation.");
                }
            } else {
                System.out.println("❌ Invalid consultation number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
        
        System.out.println("Press Enter to continue...");
        sc.nextLine();
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
        System.out.println("Press Enter to continue...");
        sc.nextLine();
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
}
