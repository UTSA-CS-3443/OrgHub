package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * Base class for an event that belongs to one organization (one row of events.csv).
 * Subclasses only differ in their event type.
 */
public abstract class Event {
    private int id;
    private int orgId;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private String organizer;

    /**
     * Creates a new for.
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
    public Event(int id, int orgId, String title, String date, String time,
                 String location, String description, String organizer) {
        this.id = id;
        this.orgId = orgId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.organizer = organizer;
    }

    /**
     * Event type name: Meeting, Workshop, Social or Volunteer.
     *
     * @return the text value
     */
    public abstract String getEventType();

    /**
     * Returns the id.
     *
     * @return the id
     */
    public int getId() { return id; }
    /**
     * Returns the org id.
     *
     * @return the org id
     */
    public int getOrgId() { return orgId; }
    /**
     * Returns the title.
     *
     * @return the title
     */
    public String getTitle() { return title; }
    /**
     * Returns the date.
     *
     * @return the date
     */
    public String getDate() { return date; }
    /**
     * Returns the time.
     *
     * @return the time
     */
    public String getTime() { return time; }
    /**
     * Returns the location.
     *
     * @return the location
     */
    public String getLocation() { return location; }
    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription() { return description; }
    /**
     * Returns the organizer.
     *
     * @return the organizer
     */
    public String getOrganizer() { return organizer; }

    /**
     * Sets the title.
     *
     * @param title the event title
     */
    public void setTitle(String title) { this.title = title; }
    /**
     * Sets the date.
     *
     * @param date the date in YYYY-MM-DD format
     */
    public void setDate(String date) { this.date = date; }
    /**
     * Sets the time.
     *
     * @param time the time, e.g. 6:00 PM
     */
    public void setTime(String time) { this.time = time; }
    /**
     * Sets the location.
     *
     * @param location the event location
     */
    public void setLocation(String location) { this.location = location; }
    /**
     * Sets the description.
     *
     * @param description the event description
     */
    public void setDescription(String description) { this.description = description; }
    /**
     * Sets the organizer.
     *
     * @param organizer the name of the organizer
     */
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    /**
     * One line of events.csv: id,orgId,title,date,time,location,description,type,organizer.
     *
     * @return the text value
     */
    public String toCsv(){
        return id + "," + orgId + "," + title + "," + date + "," + time + "," + location + ","
                + description + "," + getEventType() + "," + organizer;
    }

    /**
     * Returns a readable one-line description for display.
     *
     * @return the text value
     */
    @Override
    public String toString(){
        return "[" + id + "] " + title + " (" + getEventType() + ") - " + date + " " + time
                + " @ " + location + "\n     " + description + " | Organizer: " + organizer;
    }



}
