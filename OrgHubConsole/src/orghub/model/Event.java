package orghub.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Abstract base class for every organization event (inheritance + abstraction).
 *
 * Fields are private (encapsulation). Setters validate their input, and the
 * constructor reuses those setters, so an Event can never hold blank/invalid data.
 * Subclasses only supply the parts that differ: {@link #getType()} and
 * {@link #getReminder()} (polymorphism).
 */
public abstract class Event implements Comparable<Event> {

    /** Display/storage format for times, e.g. "6:00 PM"; also accepts "6:00 pm" as input. */
    private static final DateTimeFormatter TIME_12H = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("h:mm a").toFormatter(Locale.US);
    /** Accepts 24-hour "18:00" as input. */
    private static final DateTimeFormatter TIME_24H = DateTimeFormatter.ofPattern("H:mm");

    private final int id;
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String description;
    private String organizer;

    protected Event(int id, String title, LocalDate date, LocalTime time,
                    String location, String description, String organizer) {
        this.id = id;
        setTitle(title);
        setDate(date);
        setTime(time);
        setLocation(location);
        setDescription(description);
        setOrganizer(organizer);
    }

    // ---- behavior that differs per event type (polymorphism) ----

    public abstract EventType getType();

    /** A short type-specific note shown on the event details screen. */
    public abstract String getReminder();

    // ---- getters ----

    public int getId()             { return id; }
    public String getTitle()       { return title; }
    public LocalDate getDate()     { return date; }
    public LocalTime getTime()     { return time; }
    public String getLocation()    { return location; }
    public String getDescription() { return description; }
    public String getOrganizer()   { return organizer; }

    /** The time formatted the same way it is stored in events.csv. */
    public String getTimeText()    { return time.format(TIME_12H); }

    // ---- validating setters (final so the constructor can safely call them) ----

    public final void setTitle(String title)             { this.title = required(title, "Title"); }
    public final void setLocation(String location)       { this.location = required(location, "Location"); }
    public final void setDescription(String description) { this.description = required(description, "Description"); }
    public final void setOrganizer(String organizer)     { this.organizer = required(organizer, "Organizer"); }

    public final void setDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date is required.");
        this.date = date;
    }

    public final void setTime(LocalTime time) {
        if (time == null) throw new IllegalArgumentException("Time is required.");
        this.time = time;
    }

    /** Trims, collapses line breaks (CSV is one line per row) and rejects blanks. */
    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank.");
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    // ---- parsing helpers shared by the controller and the CSV repository ----

    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text == null ? "" : text.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be a real date in yyyy-MM-dd format (e.g. 2026-11-04).");
        }
    }

    public static LocalTime parseTime(String text) {
        String t = text == null ? "" : text.trim();
        try {
            return LocalTime.parse(t, TIME_12H);
        } catch (DateTimeParseException ignored) {
            // fall through and try 24-hour format
        }
        try {
            return LocalTime.parse(t, TIME_24H);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Time must look like 6:00 PM or 18:00.");
        }
    }

    // ---- output ----

    /** Field order matches the header of events.csv. */
    public String[] toCsvFields() {
        return new String[]{String.valueOf(id), title, date.toString(), getTimeText(),
                location, description, getType().getLabel(), organizer};
    }

    /** Orders events chronologically (date, then time, then id). */
    @Override
    public int compareTo(Event other) {
        int byDate = date.compareTo(other.date);
        if (byDate != 0) return byDate;
        int byTime = time.compareTo(other.time);
        return byTime != 0 ? byTime : Integer.compare(id, other.id);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s %s @ %s (%s)", id, title, date, getTimeText(), location, getType().getLabel());
    }
}
