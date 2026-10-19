package orghub.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** A meeting event: only overrides the type-specific behavior of {@link Event}. */
public class MeetingEvent extends Event {

    public MeetingEvent(int id, String title, LocalDate date, LocalTime time,
                   String location, String description, String organizer) {
        super(id, title, date, time, location, description, organizer);
    }

    @Override
    public EventType getType() {
        return EventType.MEETING;
    }

    @Override
    public String getReminder() {
        return "Come prepared with updates or questions for the officers.";
    }
}
