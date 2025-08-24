package control;

public class DoctorControl {

    public void viewQueue() {
        System.out.println("[Doctor: View My Queue - placeholder]");
        // later: show patients assigned to this doctor in queue
    }

    public void startConsultation() {
        System.out.println("[Doctor: Start Consultation - placeholder]");
        // later: pick a patient from queue, create consultation record
    }

    public void recordTreatment() {
        System.out.println("[Doctor: Record Diagnosis & Treatment - placeholder]");
        // later: input diagnosis, treatment plan, save to treatment history
    }

    public void viewTreatmentHistory() {
        System.out.println("[Doctor: View Patient Treatment History - placeholder]");
        // later: lookup patient ID, show past consultations + treatments
    }

    public void manageSchedule() {
        System.out.println("[Doctor: Manage My Schedule - placeholder]");
        // later: add/update availability slots
    }
}
