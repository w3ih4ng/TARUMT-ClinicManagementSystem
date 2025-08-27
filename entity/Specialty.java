package entity;

public enum Specialty {
    GENERAL_MEDICINE,
    PEDIATRICS,
    DENTISTRY,
    DERMATOLOGY,
    ENT,
    OPHTHALMOLOGY,
    PSYCHIATRY,
    CARDIOLOGY,
    NEUROLOGY,
    ORTHOPEDICS;

    @Override
    public String toString() {
        // Pretty print with spaces
        return name().replace("_", " ");
    }
}
