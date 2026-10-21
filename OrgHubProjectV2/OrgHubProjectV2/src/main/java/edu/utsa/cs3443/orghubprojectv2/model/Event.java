package edu.utsa.cs3443.orghubprojectv2.model;

public abstract class Event {
    private int id;
    private int orgId;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private String organizer;

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

    public abstract String getEventType();

    public int getId() { return id; }
    public int getOrgId() { return orgId; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getOrganizer() { return organizer; }

    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String toCsv(){
        return id + "," + orgId + "," + title + "," + date + "," + time + "," + location + ","
                + description + "," + getEventType() + "," + organizer;
    }

    @Override
    public String toString(){
        return "[" + id + "] " + title + " (" + getEventType() + ") - " + date + " " + time
                + " @ " + location + "\n     " + description + " | Organizer: " + organizer;
    }



}
