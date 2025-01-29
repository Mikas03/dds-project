package de.luh.vss.chat.common;

public enum PriorityLevel {
    HIGH(3), NORMAL(2), LOW(1);

    private final int level;

    // Konstruktor für PriorityLevel mit einem Integer-Wert
    PriorityLevel(int level) {
        this.level = level;
    }

    // Getter-Methode für den Level
    public int getLevel() {
        return level;
    }

    // Methode, um die Priorität als String zu parsen
    public static PriorityLevel fromString(String priority) {
        switch (priority.toUpperCase()) {
            case "HIGH":
                return HIGH;
            case "NORMAL":
                return NORMAL;
            case "LOW":
                return LOW;
            default:
                throw new IllegalArgumentException("Unknown priority: " + priority);
        }
    }
}
