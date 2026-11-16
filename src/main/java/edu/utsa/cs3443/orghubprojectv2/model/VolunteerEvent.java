package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * Event of type Volunteer.
 */
public class VolunteerEvent extends Event{

    /**
     * Creates a new VolunteerEvent.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @param title the event title
     * @param date the date in YYYY-MM-DD format
     * @param time the time, e.g. 6:00 PM
     * @param location the event location
     * @param description the event description
     * @param organizer the name of the organizer
     */
    public VolunteerEvent(int id, int orgId, String title, String date, String time,
                          String location, String description, String organizer) {
        super(id, orgId, title, date, time, location, description, organizer);
    }

    /**
     * Returns the event type.
     *
     * @return the event type
     */
    @Override
    public String getType() {
        return "Volunteer";
    }

    /**
     * Returns the reminder for this event type.
     *
     * @return the reminder text
     */
    @Override
    public String getReminder() {
        return "Reminder: Volunteer event - wear comfortable clothes.";
    }
}
