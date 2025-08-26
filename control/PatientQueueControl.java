package control;

import adt.*;
import entity.*;
import dao.*;

public class PatientQueueControl {
    private ListInterface<PatientQueueEntry> queue;
    private int nextQueueNo = 1;
    private HashMapInterface<String, Patient> patientMap;

    public PatientQueueControl() {
        this.patientMap = PatientDAO.loadPatients();
        queue = new ArrayList<>(); // or your own ADT list
    }

    public void addToQueue(String patientId) {
        Patient patient = patientMap.get(patientId);

        if (patient == null || patient.isDeleted()) {
            System.out.println("Patient ID not found or deleted.");
            return;
        }

        // check duplicate
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getPatientId().equals(patientId)) {
                System.out.println("Patient is already in the queue.");
                return;
            }
        }

        PatientQueueEntry entry = new PatientQueueEntry(nextQueueNo++, patientId);
        queue.add(entry);
        System.out.println("Patient added to queue: " + entry + " | Name: " + patient.getName());
    }

    public PatientQueueEntry callNext() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return null;
        }
        return queue.remove(0); // FIFO
    }

    public void printQueue() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        System.out.println("\n--- Current Queue ---");
        for (int i = 0; i < queue.size(); i++) {
            PatientQueueEntry entry = queue.get(i);
            Patient p = patientMap.get(entry.getPatientId());
            System.out.println("Queue No: " + entry.getQueueNo() + 
                               " | Patient ID: " + entry.getPatientId() + 
                               " | Name: " + (p != null ? p.getName() : "Unknown"));
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
