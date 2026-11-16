package edu.utsa.cs3443.orghubprojectv2.model;

import java.util.*;

/**
 * Holds every Event in memory and keeps events.csv in sync after each change.
 * All lookups are scoped to an organization so organizations never see each other's events.
 */
public class EventManager {

    private List<Event> events;
    private EventFileHandler fileHandler;

    /**
     * Creates a new EventManager.
     *
     * @param fileHandler reads and writes the data file
     */
    public EventManager(EventFileHandler fileHandler){
        this.fileHandler = fileHandler;
        this.events = fileHandler.load();
    }

    /**
     * Factory: creates the right Event subclass for a type name (defaults to Meeting).
     *
     * @param type the event type (Meeting, Workshop, Social or Volunteer)
     * @param id the id
     * @param orgId the id of the organization
     * @param title the event title
     * @param date the date in YYYY-MM-DD format
     * @param time the time, e.g. 6:00 PM
     * @param location the event location
     * @param description the event description
     * @param organizer the name of the organizer
     * @return a new object of the matching subclass
     */
    public static Event buildEvent(String type, int id, int orgId, String title, String date, String time,
                                   String location, String description, String organizer) {
        EventType eventType = EventType.fromString(type);
        if (eventType == null) {
            eventType = EventType.MEETING;
        }
        switch (eventType) {
            case WORKSHOP:
                return new WorkshopEvent(id, orgId, title, date, time, location, description, organizer);
            case SOCIAL:
                return new SocialEvent(id, orgId, title, date, time, location, description, organizer);
            case VOLUNTEER:
                return new VolunteerEvent(id, orgId, title, date, time, location, description, organizer);
            default:
                return new MeetingEvent(id, orgId, title, date, time, location, description, organizer);
        }
    }

    /**
     * True for Meeting, Workshop, Social or Volunteer (ignoring case).
     *
     * @param type the event type (Meeting, Workshop, Social or Volunteer)
     * @return true for Meeting, Workshop, Social or Volunteer (ignoring case), otherwise false
     */
    public static boolean isValidType(String type) {
        return EventType.fromString(type) != null;
    }

    /**
     * Removes commas from text so it cannot corrupt the comma-separated data file.
     *
     * @param text the text to clean
     * @return the text with every comma replaced by a space
     */
    private static String clean(String text) {
        return text.replace(",", " ");
    }

    /**
     * Next free event id (highest existing id + 1).
     *
     * @return the next unused id
     */
    private int nextId(){
        int max = 0;
        for(Event e : events){
            if(e.getId() > max){
                max = e.getId();
            }
        }
        return max+1;
    }

    /**
     * Creates and saves a new event.
     *
     * @param orgId the id of the organization
     * @param type the event type (Meeting, Workshop, Social or Volunteer)
     * @param title the event title
     * @param date the date in YYYY-MM-DD format
     * @param time the time, e.g. 6:00 PM
     * @param location the event location
     * @param description the event description
     * @param organizer the name of the organizer
     */
    public void createEvent(int orgId, String type, String title, String date, String time,
                            String location, String description, String organizer) {
        events.add(buildEvent(clean(type), nextId(), orgId, clean(title), clean(date), clean(time),
                clean(location), clean(description), clean(organizer)));
        fileHandler.save(events);
    }

    /**
     * Returns the all events.
     *
     * @return the matching list (empty if none)
     */
    public List<Event> getAllEvents(){
        return events;
    }

    /**
     * Finds an event by id within one organization, or null.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @return the result, or null if not found
     */
    public Event findById(int id, int orgId) {
        for (Event e : events) {
            if (e.getId() == id && e.getOrgId() == orgId) {
                return e;
            }
        }
        return null;
    }

    /**
     * Updates only the fields that are not blank, then saves.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @param title the event title
     * @param date the date in YYYY-MM-DD format
     * @param time the time, e.g. 6:00 PM
     * @param location the event location
     * @param description the event description
     * @return true if the change was made, otherwise false
     */
    public boolean updateEvent(int id, int orgId, String title, String date, String time,
                               String location, String description) {
        Event e = findById(id, orgId);
        if (e == null) {
            return false;
        }
        title = clean(title);
        date = clean(date);
        time = clean(time);
        location = clean(location);
        description = clean(description);
        if (!title.isEmpty()) { e.setTitle(title); }
        if (!date.isEmpty()) { e.setDate(date); }
        if (!time.isEmpty()) { e.setTime(time); }
        if (!location.isEmpty()) { e.setLocation(location); }
        if (!description.isEmpty()) { e.setDescription(description); }
        fileHandler.save(events);
        return true;
    }

    /**
     * Replaces an event's details (type and organizer too), keeping its id;
     * unlike updateEvent, blank fields are saved as blank.
     */
    public boolean replaceEvent(int id, int orgId, String type, String title, String date, String time,
                                String location, String description, String organizer) {
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            if (e.getId() == id && e.getOrgId() == orgId) {
                events.set(i, buildEvent(clean(type), id, orgId, clean(title), clean(date), clean(time),
                        clean(location), clean(description), clean(organizer)));
                fileHandler.save(events);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes an event from one organization and saves.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @return true if the change was made, otherwise false
     */
    public boolean deleteEvent(int id, int orgId) {
        Event e = findById(id, orgId);
        if (e == null) {
            return false;
        }
        events.remove(e);
        fileHandler.save(events);
        return true;
    }

    /**
     * All events of one organization, sorted by date.
     *
     * @param orgId the id of the organization
     * @return the matching list (empty if none)
     */
    public List<Event> getEventsForOrg(int orgId) {
        return searchEvents(orgId, "", "");
    }

    /**
     * Sorts events by date (YYYY-MM-DD sorts correctly as text).
     *
     * @param list the list to use
     */
    private void sortByDate(List<Event> list) {
        Collections.sort(list, (a, b) -> a.getDate().compareTo(b.getDate()));
    }

    /**
     * Events of one organization filtered by type and keyword (title, location, organizer).
     *
     * @param orgId the id of the organization
     * @param type the event type (Meeting, Workshop, Social or Volunteer)
     * @param keyword text to look for in the title, location or organizer (blank for none)
     * @return the matching list (empty if none)
     */
    public List<Event> searchEvents(int orgId, String type, String keyword) {
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (e.getOrgId() != orgId) {
                continue;
            }
            if (!type.isEmpty() && !e.getType().equalsIgnoreCase(type)) {
                continue;
            }
            String text = (e.getTitle() + " " + e.getLocation() + " " + e.getOrganizer()).toLowerCase();
            if (!keyword.isEmpty() && !text.contains(keyword.toLowerCase())) {
                continue;
            }
            result.add(e);
        }
        sortByDate(result);
        return result;
    }

    /**
     * All events of one organization, sorted by date.
     *
     * @param orgIds the organization ids to include
     * @return the matching list (empty if none)
     */
    public List<Event> getEventsForOrgs(List<Integer> orgIds) {
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (orgIds.contains(e.getOrgId())) {
                result.add(e);
            }
        }
        sortByDate(result);
        return result;
    }

}
