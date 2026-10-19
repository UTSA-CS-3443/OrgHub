package orghub.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** A volunteer event: only overrides the type-specific behavior of {@link Event}. */
public class VolunteerEvent extends Event {

    public VolunteerEvent(int id, String title, LocalDate date, LocalTime time,
                   String location, String description, String organizer) {
        super(id, title, date, time, location, description, organizer);
    }

    @Override
    public EventType getType() {
        return EventType.VOLUNTEER;
    }

    @Override
    public String getReminder() {
        return "Wear comfortable clothes; volunteers should arrive a few minutes early.";
    }
}
