package dao;

import adt.ListADT;
import adt.ArrayList;
import entity.Patient;
import java.io.*;

/**
 * PatientDAO handles file persistence for Patient records.
 * Author: [Your Name]
 */
public class PatientDAO {
    private static final String FILE_PATH = "data/patients.txt";

    // Load all patients into a ListADT
    public ListADT<Patient> loadPatients() {
        ListADT<Patient> patients = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String id = parts[0];
                    String name = parts[1];
                    int age = Integer.parseInt(parts[2]);
                    String condition = parts[3];
                    patients.add(new Patient(id, name, age, condition));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading patients: " + e.getMessage());
        }
        return patients;
    }

    // Save all patients back to file
    public void savePatients(ListADT<Patient> patients) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i < patients.size(); i++) {
                Patient p = patients.get(i);
                String line = p.getId() + "," + p.getName() + "," + p.getAge() + "," + p.getCondition();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving patients: " + e.getMessage());
        }
    }
}
