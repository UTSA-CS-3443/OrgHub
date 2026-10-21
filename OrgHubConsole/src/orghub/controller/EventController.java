package orghub.controller;

import orghub.data.Repository;
import orghub.model.Event;
import orghub.model.EventType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Application logic for events (the CRUD operations from the proposal).
 * It validates input, assigns IDs and talks to storage through
 * {@link Repository}. It never prints or reads from the console.
 *
 * Every change is built on a copy of the list and only kept after the file was
 * saved successfully, so memory and file can never disagree.
 */
public class EventController {

    private final Repository<Event> repository;
    private List<Event> events;

    public EventController(Repository<Event> repository) throws IOException {
        this.repository = repository;
        this.events = new ArrayList<>(repository.loadAll());
    }

    // ---------- Read ----------

    /** All events in chronological order. */
    public List<Event> getAllEvents() {
        List<Event> sorted = new ArrayList<>(events);
        Collections.sort(sorted);
        return sorted;
    }

    public Optional<Event> findById(int id) {
        return events.stream().filter(e -> e.getId() == id).findFirst();
    }

    /** Case-insensitive match on title, location or organizer; type may be null for "any". */
    public List<Event> search(String keyword, EventType type) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Event> result = new ArrayList<>();
        for (Event e : getAllEvents()) {
            boolean typeOk = type == null || e.getType() == type;
            String haystack = (e.getTitle() + " " + e.getLocation() + " " + e.getOrganizer()).toLowerCase();
            if (typeOk && haystack.contains(k)) result.add(e);
        }
        return result;
    }

    /** The next events happening today or later. */
    public List<Event> getUpcoming(int limit) {
        LocalDate today = LocalDate.now();
        List<Event> upcoming = new ArrayList<>();
        for (Event e : getAllEvents()) {
            if (!e.getDate().isBefore(today) && upcoming.size() < limit) upcoming.add(e);
        }
        return upcoming;
    }

    public Map<EventType, Integer> countByType() {
        Map<EventType, Integer> counts = new EnumMap<>(EventType.class);
        for (EventType t : EventType.values()) counts.put(t, 0);
        for (Event e : events) counts.merge(e.getType(), 1, Integer::sum);
        return counts;
    }

    public int getEventCount() {
        return events.size();
    }

    // ---------- Create ----------

    /** @throws IllegalArgumentException if any field is invalid */
    public Event createEvent(String title, String date, String time, String location,
                             String description, EventType type, String organizer) throws IOException {
        int newId = events.stream().mapToInt(Event::getId).max().orElse(0) + 1;
        Event created = build(newId, title, date, time, location, description, type, organizer);
        List<Event> next = new ArrayList<>(events);
        next.add(created);
        commit(next);
        return created;
    }

    // ---------- Update ----------

    /** Replaces the event with the given id (its id never changes). */
    public Event updateEvent(int id, String title, String date, String time, String location,
                             String description, EventType type, String organizer) throws IOException {
        if (findById(id).isEmpty()) {
            throw new IllegalArgumentException("No event with ID " + id + ".");
        }
        Event updated = build(id, title, date, time, location, description, type, organizer);
        List<Event> next = new ArrayList<>();
        for (Event e : events) next.add(e.getId() == id ? updated : e);
        commit(next);
        return updated;
    }

    // ---------- Delete ----------

    /** @return true if an event was removed, false if the id did not exist */
    public boolean deleteEvent(int id) throws IOException {
        List<Event> next = new ArrayList<>(events);
        boolean removed = next.removeIf(e -> e.getId() == id);
        if (removed) commit(next);
        return removed;
    }

    // ---------- helpers ----------

    private Event build(int id, String title, String date, String time, String location,
                        String description, EventType type, String organizer) {
        if (type == null) throw new IllegalArgumentException("Event type is required.");
        return type.create(id, title, Event.parseDate(date), Event.parseTime(time),
                location, description, organizer);
    }

    private void commit(List<Event> next) throws IOException {
        repository.saveAll(next);   // if this throws, 'events' is left untouched
        events = next;
    }
}
