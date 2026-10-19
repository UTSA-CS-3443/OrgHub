package orghub.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** A workshop event: only overrides the type-specific behavior of {@link Event}. */
public class WorkshopEvent extends Event {

    public WorkshopEvent(int id, String title, LocalDate date, LocalTime time,
                   String location, String description, String organizer) {
        super(id, title, date, time, location, description, organizer);
    }

    @Override
    public EventType getType() {
        return EventType.WORKSHOP;
    }

    @Override
    public String getReminder() {
        return "Bring a laptop or notebook so you can follow along.";
    }
}
