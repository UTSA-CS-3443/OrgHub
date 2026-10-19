package orghub.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** A social event: only overrides the type-specific behavior of {@link Event}. */
public class SocialEvent extends Event {

    public SocialEvent(int id, String title, LocalDate date, LocalTime time,
                   String location, String description, String organizer) {
        super(id, title, date, time, location, description, organizer);
    }

    @Override
    public EventType getType() {
        return EventType.SOCIAL;
    }

    @Override
    public String getReminder() {
        return "Casual event - bring a friend and meet other members.";
    }
}
