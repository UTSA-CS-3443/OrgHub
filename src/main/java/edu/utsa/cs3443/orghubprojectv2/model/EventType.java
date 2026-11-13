package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * The valid event categories. Used to declare an event's type and to validate user input.
 */
public enum EventType {
    MEETING("Meeting"),
    WORKSHOP("Workshop"),
    SOCIAL("Social"),
    VOLUNTEER("Volunteer");

    private final String label;

    /**
     * Creates a new EventType.
     *
     * @param label the name shown to the user and saved in events.csv
     */
    EventType(String label) {
        this.label = label;
    }

    /**
     * Returns the label.
     *
     * @return the name shown to the user and saved in events.csv
     */
    public String getLabel() {
        return label;
    }

    /**
     * Finds the type for a name, ignoring case.
     *
     * @param text the type name (Meeting, Workshop, Social or Volunteer)
     * @return the matching type, or null if the text is not a valid type
     */
    public static EventType fromString(String text) {
        for (EventType t : values()) {
            if (t.label.equalsIgnoreCase(text)) {
                return t;
            }
        }
        return null;
    }
}
