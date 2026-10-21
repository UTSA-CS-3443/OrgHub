package orghub.ui;

import orghub.controller.EventController;
import orghub.controller.UserController;
import orghub.model.Event;
import orghub.model.EventType;
import orghub.model.Organization;
import orghub.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * The presentation layer. It only prints menus and reads input; every rule
 * (validation, permissions, saving) is delegated to the controllers and model.
 * Each screen here matches one screen from the proposal: Dashboard, View, Create,
 * Update and Delete.
 */
public class ConsoleUI {

    private static final String LINE = "=".repeat(78);

    private final Scanner in = new Scanner(System.in);
    private final EventController events;
    private final UserController users;
    private final Organization organization;
    private User currentUser;

    public ConsoleUI(EventController events, UserController users, Organization organization) {
        this.events = events;
        this.users = users;
        this.organization = organization;
    }

    // ============================== main loop ==============================

    public void run() {
        System.out.println(LINE);
        System.out.println(" OrgHub - " + organization.getName());
        System.out.println(LINE);
        while (login()) {
            if (dashboardLoop()) break;      // true means "exit the program"
        }
        System.out.println("Goodbye!");
    }

    /** @return false if the user chose to quit instead of logging in */
    private boolean login() {
        System.out.println("\nAccounts:");
        for (User u : users.getAllUsers()) System.out.println("  " + u);
        while (true) {
            String email = ask("Log in with your email (blank to quit)", "");
            if (email.isBlank()) return false;
            Optional<User> user = users.findByEmail(email);
            if (user.isPresent()) {
                currentUser = user.get();
                return true;
            }
            System.out.println("No account with that email.");
        }
    }

    // ============================== Dashboard ==============================

    /** @return true if the user chose Exit, false if they chose Switch user */
    private boolean dashboardLoop() {
        while (true) {
            showDashboard();
            switch (askInt("Choose an option", 0, 7)) {
                case 1: viewEvents(events.getAllEvents(), "All events"); break;
                case 2: searchEvents(); break;
                case 3: if (allowed()) eventForm(null); break;
                case 4: if (allowed()) updateEvent(); break;
                case 5: if (allowed()) deleteEvent(); break;
                case 6: showMembers(); break;
                case 7: return false;
                default: return true;
            }
        }
    }

    private void showDashboard() {
        System.out.println("\n" + LINE);
        System.out.println(" DASHBOARD - " + organization.getName() + "   |   " + currentUser.getName()
                + " (" + currentUser.getRole() + ")");
        System.out.println(LINE);
        StringBuilder counts = new StringBuilder();
        for (Map.Entry<EventType, Integer> c : events.countByType().entrySet()) {
            counts.append(c.getKey().getLabel()).append(": ").append(c.getValue()).append("   ");
        }
        System.out.println(" Total events: " + events.getEventCount() + "   (" + counts.toString().trim() + ")");
        System.out.println(" Next up:");
        List<Event> next = events.getUpcoming(3);
        if (next.isEmpty()) System.out.println("   (no upcoming events)");
        for (Event e : next) System.out.println("   " + e);
        System.out.println();
        System.out.println(" 1. View events");
        System.out.println(" 2. Search / filter events");
        System.out.println(" 3. Create event" + officerTag());
        System.out.println(" 4. Update event" + officerTag());
        System.out.println(" 5. Delete event" + officerTag());
        System.out.println(" 6. View members");
        System.out.println(" 7. Switch user");
        System.out.println(" 0. Exit");
    }

    private String officerTag() {
        return currentUser.canManageEvents() ? "" : "  (officers only)";
    }

    /** Permission check lives in the model (User.canManageEvents); the UI just reports it. */
    private boolean allowed() {
        if (currentUser.canManageEvents()) return true;
        System.out.println("Only officers can do that. Members have view-only access.");
        return false;
    }

    // ============================== View / Search ==============================

    private void viewEvents(List<Event> list, String heading) {
        printTable(list, heading);
        if (list.isEmpty()) return;
        String id = ask("Enter an event ID for details (blank to go back)", "");
        if (id.isBlank()) return;
        Optional<Event> e = lookup(id);
        if (e.isPresent()) printDetails(e.get());
        else System.out.println("No event with that ID.");
    }

    private void searchEvents() {
        String keyword = ask("Keyword in title/location/organizer (blank for any)", "");
        System.out.println("Type filter: 0. Any");
        EventType[] types = EventType.values();
        for (int i = 0; i < types.length; i++) System.out.println("  " + (i + 1) + ". " + types[i].getLabel());
        int choice = askInt("Choose type", 0, types.length);
        EventType type = choice == 0 ? null : types[choice - 1];
        viewEvents(events.search(keyword, type), "Search results");
    }

    private void printTable(List<Event> list, String heading) {
        System.out.println("\n--- " + heading + " (" + list.size() + ") ---");
        if (list.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        String row = "%-4s %-11s %-9s %-26s %-24s %-9s%n";
        System.out.printf(row, "ID", "Date", "Time", "Title", "Location", "Type");
        for (Event e : list) {
            System.out.printf(row, e.getId(), e.getDate(), e.getTimeText(),
                    cut(e.getTitle(), 26), cut(e.getLocation(), 24), e.getType().getLabel());
        }
    }

    private void printDetails(Event e) {
        System.out.println("\n--- Event #" + e.getId() + " ---");
        System.out.println("Title:       " + e.getTitle());
        System.out.println("Type:        " + e.getType().getLabel());
        System.out.println("Date/Time:   " + e.getDate() + " at " + e.getTimeText());
        System.out.println("Location:    " + e.getLocation());
        System.out.println("Organizer:   " + e.getOrganizer());
        System.out.println("Description: " + e.getDescription());
        System.out.println("Note:        " + e.getReminder());
    }

    private void showMembers() {
        System.out.println("\n--- Members (" + users.getAllUsers().size() + ") --- contact: " + organization.getContactEmail());
        for (User u : users.getAllUsers()) System.out.println("  " + u);
    }

    // ============================== Create / Update ==============================

    private void updateEvent() {
        Event target = selectEvent("update");
        if (target != null) eventForm(target);
    }

    /**
     * Shared form for Create (existing == null) and Update. When updating, the
     * current value is shown in [brackets]; pressing Enter keeps it. If validation
     * fails the user can retry with their previous answers pre-filled.
     */
    private void eventForm(Event existing) {
        String[] labels = {"Title", "Date (yyyy-MM-dd)", "Time (e.g. 6:00 PM)", "Location", "Description", "Organizer"};
        String[] v = existing == null
                ? new String[]{"", "", "", "", "", ""}
                : new String[]{existing.getTitle(), existing.getDate().toString(), existing.getTimeText(),
                existing.getLocation(), existing.getDescription(), existing.getOrganizer()};
        EventType type = existing == null ? null : existing.getType();

        System.out.println("\n--- " + (existing == null ? "Create event" : "Update event #" + existing.getId()) + " ---");
        while (true) {
            for (int i = 0; i < labels.length; i++) v[i] = ask(labels[i], v[i]);
            type = askType(type);
            try {
                Event saved = existing == null
                        ? events.createEvent(v[0], v[1], v[2], v[3], v[4], type, v[5])
                        : events.updateEvent(existing.getId(), v[0], v[1], v[2], v[3], v[4], type, v[5]);
                System.out.println("Saved: " + saved);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                if (!confirm("Try again?")) return;
            } catch (IOException e) {
                System.out.println("Could not save to file: " + e.getMessage());
                return;
            }
        }
    }

    private EventType askType(EventType current) {
        EventType[] types = EventType.values();
        for (int i = 0; i < types.length; i++) System.out.println("  " + (i + 1) + ". " + types[i].getLabel());
        while (true) {
            String prompt = current == null ? "Event type (1-" + types.length + ")" : "Event type [" + current.getLabel() + "] (1-" + types.length + ")";
            String text = ask(prompt, "");
            if (text.isBlank() && current != null) return current;
            try {
                int n = Integer.parseInt(text.trim());
                if (n >= 1 && n <= types.length) return types[n - 1];
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Enter a number from 1 to " + types.length + ".");
        }
    }

    // ============================== Delete ==============================

    private void deleteEvent() {
        Event target = selectEvent("delete");
        if (target == null) return;
        printDetails(target);
        if (!confirm("Permanently delete \"" + target.getTitle() + "\"?")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        try {
            System.out.println(events.deleteEvent(target.getId()) ? "Event deleted." : "Event was already gone.");
        } catch (IOException e) {
            System.out.println("Could not save to file: " + e.getMessage());
        }
    }

    // ============================== input helpers ==============================

    /** Lists events and asks for an ID; returns null if cancelled or not found. */
    private Event selectEvent(String action) {
        List<Event> all = events.getAllEvents();
        printTable(all, "Choose an event to " + action);
        if (all.isEmpty()) return null;
        String id = ask("Event ID (blank to cancel)", "");
        if (id.isBlank()) return null;
        Optional<Event> e = lookup(id);
        if (e.isEmpty()) System.out.println("No event with that ID.");
        return e.orElse(null);
    }

    /** Turns typed text into an event lookup; non-numbers simply find nothing. */
    private Optional<Event> lookup(String idText) {
        try {
            return events.findById(Integer.parseInt(idText.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** Prompts "label [default]: " and returns the typed text, or the default if blank. */
    private String ask(String label, String defaultValue) {
        System.out.print(label + (defaultValue.isEmpty() ? "" : " [" + defaultValue + "]") + ": ");
        if (!in.hasNextLine()) {          // input closed (e.g. piped input ended)
            System.out.println();
            System.exit(0);
        }
        String line = in.nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }

    private int askInt(String label, int min, int max) {
        while (true) {
            String text = ask(label + " (" + min + "-" + max + ")", "");
            try {
                int n = Integer.parseInt(text);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    private boolean confirm(String question) {
        while (true) {
            String a = ask(question + " (y/n)", "").toLowerCase();
            if (a.equals("y") || a.equals("yes")) return true;
            if (a.equals("n") || a.equals("no")) return false;
        }
    }

    private static String cut(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
