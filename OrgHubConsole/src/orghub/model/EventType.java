package orghub.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The four kinds of events OrgHub supports. The enum doubles as a small factory:
 * each constant knows which Event subclass to build, so no other class needs a
 * switch on the type.
 */
public enum EventType {
    MEETING("Meeting"), WORKSHOP("Workshop"), SOCIAL("Social"), VOLUNTEER("Volunteer");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Converts text from the CSV file (case-insensitive) to an EventType. */
    public static EventType fromLabel(String text) {
        for (EventType t : values()) {
            if (t.label.equalsIgnoreCase(text.trim())) return t;
        }
        throw new IllegalArgumentException("Unknown event type: " + text);
    }

    /** Builds the correct Event subclass for this type. */
    public Event create(int id, String title, LocalDate date, LocalTime time,
                        String location, String description, String organizer) {
        return switch (this) {
            case MEETING   -> new MeetingEvent(id, title, date, time, location, description, organizer);
            case WORKSHOP  -> new WorkshopEvent(id, title, date, time, location, description, organizer);
            case SOCIAL    -> new SocialEvent(id, title, date, time, location, description, organizer);
            case VOLUNTEER -> new VolunteerEvent(id, title, date, time, location, description, organizer);
        };
    }
}
