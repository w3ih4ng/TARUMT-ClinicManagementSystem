package control;

import entity.*;
import adt.*;


public class ViewPatientControl {
    private PatientControl patientControl;
    private HashMapInterface<String, Patient> patientMap;

    public ViewPatientControl(PatientControl patientControl) {
        this.patientControl = patientControl;
        this.patientMap = patientControl.getPatientMap();
    }

    public HashMapInterface<String, Patient> getPatientMap() {
        return this.patientMap;
    }

    // --- Filter ---
    public HashMapInterface<String, Patient> filterByRole(HashMapInterface<String, Patient> map, String roleChoice) {
        return map.filter(p -> {
            if (p.isDeleted())
                return false;
            switch (roleChoice) {
                case "1":
                    return p instanceof Student;
                case "2":
                    return p instanceof Tutor;
                case "3":
                    return p instanceof Staff;
                default:
                    return false;
            }
        });
    }

    public HashMapInterface<String, Patient> filterByGender(HashMapInterface<String, Patient> map, String gender) {
        return map.filter(p -> !p.isDeleted() && p.getGender().equalsIgnoreCase(gender));
    }

    public HashMapInterface<String, Patient> filterHideDeleted(HashMapInterface<String, Patient> map) {
        return map.filter(p -> !p.isDeleted());
    }

    // --- Search ---
    public HashMapInterface<String, Patient> searchPatients(HashMapInterface<String, Patient> map, String keyword) {
        String lower = keyword.toLowerCase();
        return map.filter(p -> p.toString().toLowerCase().contains(lower));
    }

    // --- Display wrapper ---
    public void printPatients(HashMapInterface<String, Patient> map) {
        patientControl.printPatientsTable(map.toList()); // convert to list just for printing
    }
}
