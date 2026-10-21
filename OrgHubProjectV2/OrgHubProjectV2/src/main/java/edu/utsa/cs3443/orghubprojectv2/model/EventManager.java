package edu.utsa.cs3443.orghubprojectv2.model;

import java.util.*;

public class EventManager {

    private List<Event> events;
    private EventFileHandler fileHandler;

    public EventManager(EventFileHandler fileHandler){
        this.fileHandler = fileHandler;
        this.events = fileHandler.load();
    }

    public static Event buildEvent(String type, int id, int orgId, String title, String date, String time,
                                   String location, String description, String organizer) {
        switch (type.toLowerCase()) {
            case "workshop":
                return new workshopEvent(id, orgId, title, date, time, location, description, organizer);
            case "social":
                return new SocialEvent(id, orgId, title, date, time, location, description, organizer);
            case "volunteer":
                return new volunteerEvent(id, orgId, title, date, time, location, description, organizer);
            default:
                return new meetingEvent(id, orgId, title, date, time, location, description, organizer);
        }
    }

    private int nextId(){
        int max = 0;
        for(Event e : events){
            if(e.getId() > max){
                max = e.getId();
            }
        }
        return max+1;
    }

    public void createEvent(int orgId, String type, String title, String date, String time,
                            String location, String description, String organizer) {
        events.add(buildEvent(type, nextId(), orgId, title, date, time, location, description, organizer));
        fileHandler.save(events);
    }

    public List<Event> getAllEvents(){
        return events;
    }

    public Event findById(int id, int orgId) {
        for (Event e : events) {
            if (e.getId() == id && e.getOrgId() == orgId) {
                return e;
            }
        }
        return null;
    }

    public boolean updateEvent(int id, int orgId, String title, String date, String time,
                               String location, String description) {
        Event e = findById(id, orgId);
        if (e == null) {
            return false;
        }
        if (!title.isEmpty()) { e.setTitle(title); }
        if (!date.isEmpty()) { e.setDate(date); }
        if (!time.isEmpty()) { e.setTime(time); }
        if (!location.isEmpty()) { e.setLocation(location); }
        if (!description.isEmpty()) { e.setDescription(description); }
        fileHandler.save(events);
        return true;
    }

    public boolean deleteEvent(int id, int orgId) {
        Event e = findById(id, orgId);
        if (e == null) {
            return false;
        }
        events.remove(e);
        fileHandler.save(events);
        return true;
    }

    public List<Event> getEventsForOrg(int orgId) {
        return searchEvents(orgId, "", "");
    }

    private void sortByDate(List<Event> list) {
        Collections.sort(list, (a, b) -> a.getDate().compareTo(b.getDate()));
    }

    public List<Event> searchEvents(int orgId, String type, String keyword) {
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (e.getOrgId() != orgId) {
                continue;
            }
            if (!type.isEmpty() && !e.getEventType().equalsIgnoreCase(type)) {
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
