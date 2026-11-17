package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for overview-screen.fxml. The JavaFX counterpart of MainScreenController's showOverview():
 * shows upcoming events from every organization the user belongs to, grouped by date.
 * Holds no data itself; it reads from EventManager, MemberManager and OrganizationManager.
 */
public class OverviewScreenController {
    @FXML private Label avatarInitialsLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button userMenuButton;
    @FXML private ComboBox<User> organizationSwitcher;
    @FXML private Label greetingLabel;
    @FXML private Label summaryLabel;
    @FXML private VBox eventsContainer;
    @FXML private VBox emptyState;
    @FXML private Button createEventButton;

    private OrgHubApp app;

    // who is logged in, and in which organization (one User row = one organization)
    private User currentUser;

    /**
     * Fills the screen for a user who just signed in.
     *
     * @param app the running application
     * @param user the signed-in user (one organization membership)
     */
    public void init(OrgHubApp app, User user) {
        this.app = app;
        this.currentUser = user;

        // the switcher lists the organizations this person belongs to
        List<User> memberships = app.getMemberManager().findByEmail(user.getEmail());
        organizationSwitcher.setItems(FXCollections.observableArrayList(memberships));
        organizationSwitcher.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User membership) {
                return membership == null ? "" : app.getOrgManager().getName(membership.getOrgId());
            }

            @Override
            public User fromString(String text) {
                return null;
            }
        });
        organizationSwitcher.setValue(user);
        organizationSwitcher.setOnAction(e -> {
            User chosen = organizationSwitcher.getValue();
            if (chosen != null) {
                currentUser = chosen;
                showUser();
            }
        });

        showUser();
        showEvents(memberships);
    }

    /**
     * The "..." button next to the user's name: a small menu with Sign out.
     */
    @FXML
    private void handleUserMenu() {
        MenuItem signOut = new MenuItem("Sign out");
        signOut.setOnAction(e -> app.showLogin());
        new ContextMenu(signOut).show(userMenuButton, Side.TOP, 0, 0);
    }

    /**
     * Updates everything that depends on who is signed in and which organization is current.
     */
    private void showUser() {
        String name = currentUser.getName();
        userNameLabel.setText(name);
        userRoleLabel.setText(currentUser.isAdmin() ? "Organization admin"
                : currentUser.isOfficer() ? "Organization officer" : "Member");
        avatarInitialsLabel.setText(initials(name));
        greetingLabel.setText(greeting() + ", " + firstName(name) + ".");

        // only officers can create events
        createEventButton.setVisible(currentUser.isOfficer());
        createEventButton.setManaged(currentUser.isOfficer());
    }

    /**
     * Builds the date groups and event cards, or shows the empty state when there are no events.
     *
     * @param memberships the user's memberships, one per organization
     */
    private void showEvents(List<User> memberships) {
        List<Integer> orgIds = new ArrayList<>();
        for (User m : memberships) {
            orgIds.add(m.getOrgId());
        }
        List<Event> events = app.getEventManager().getEventsForOrgs(orgIds);
        Collections.sort(events);   // by date, then time

        // group by date, keeping the sorted order
        Map<String, List<Event>> byDate = new LinkedHashMap<>();
        for (Event e : events) {
            List<Event> day = byDate.get(e.getDate());
            if (day == null) {
                day = new ArrayList<>();
                byDate.put(e.getDate(), day);
            }
            day.add(e);
        }

        eventsContainer.getChildren().clear();
        for (Map.Entry<String, List<Event>> day : byDate.entrySet()) {
            eventsContainer.getChildren().add(buildDateGroup(day.getKey(), day.getValue()));
        }

        emptyState.setVisible(events.isEmpty());
        emptyState.setManaged(events.isEmpty());
        summaryLabel.setText(summary(events.size(), memberships.size()));
    }

    /**
     * Loads one date-group.fxml and fills it with that day's event cards.
     *
     * @param date the date in YYYY-MM-DD format
     * @param day the events on that date
     * @return the loaded group
     */
    private Parent buildDateGroup(String date, List<Event> day) {
        FXMLLoader loader = new FXMLLoader(OrgHubApp.class.getResource("layouts/date-group.fxml"));
        Parent group = load(loader);
        DateGroupController controller = loader.getController();
        controller.setGroup(heading(date), day.size());
        for (Event e : day) {
            controller.addCard(buildEventCard(e));
        }
        return group;
    }

    /**
     * Loads one event-card.fxml and fills it from an event.
     *
     * @param event the event to show
     * @return the loaded card
     */
    private Parent buildEventCard(Event event) {
        FXMLLoader loader = new FXMLLoader(OrgHubApp.class.getResource("layouts/event-card.fxml"));
        Parent card = load(loader);
        EventCardController controller = loader.getController();
        controller.setEvent(event, app.getOrgManager().getName(event.getOrgId()));
        return card;
    }

    /**
     * Loads a layout, turning the checked IOException into an unchecked one.
     *
     * @param loader the loader to run
     * @return the loaded root
     */
    private Parent load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + loader.getLocation(), e);
        }
    }

    /**
     * Date heading text such as "Sunday, November 8". Falls back to the raw text if it is not a date.
     *
     * @param date the date in YYYY-MM-DD format
     * @return the heading text
     */
    private static String heading(String date) {
        try {
            return LocalDate.parse(date).format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.US));
        } catch (DateTimeParseException e) {
            return date;
        }
    }

    /**
     * Summary line under the greeting.
     *
     * @param eventCount how many events are listed
     * @param orgCount how many organizations the user belongs to
     * @return the text, e.g. "You have 4 upcoming events across your organizations."
     */
    private static String summary(int eventCount, int orgCount) {
        if (eventCount == 0) {
            return "You have no upcoming events.";
        }
        String text = "You have " + eventCount + " upcoming " + (eventCount == 1 ? "event" : "events");
        return orgCount > 1 ? text + " across your organizations." : text + ".";
    }

    /**
     * @return "Good morning", "Good afternoon" or "Good evening" for the current time
     */
    private static String greeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Good morning";
        }
        return hour < 18 ? "Good afternoon" : "Good evening";
    }

    /**
     * @param name a full name
     * @return the first word of the name
     */
    private static String firstName(String name) {
        return name.trim().split("\\s+")[0];
    }

    /**
     * @param name a full name
     * @return the first letters of the first and last words, e.g. "CV"
     */
    private static String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1);
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1);
        }
        return initials.toUpperCase();
    }
}
