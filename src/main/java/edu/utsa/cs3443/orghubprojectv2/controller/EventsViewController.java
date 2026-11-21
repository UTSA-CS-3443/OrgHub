package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Controller for events-view.fxml: the current organization's events as cards, with a type filter,
 * a search box and (for officers) create, edit and delete. The JavaFX counterpart of
 * MainScreenController's view/create/update/delete events. Holds no data itself; it calls EventManager.
 */
public class EventsViewController {
    @FXML private Label organizationCaptionLabel;
    @FXML private Label subtitleLabel;
    @FXML private Button createEventButton;
    @FXML private TextField searchField;
    @FXML private ToggleGroup typeToggleGroup;
    @FXML private ToggleButton allToggle;
    @FXML private Label emptyLabel;
    @FXML private FlowPane cardsPane;

    private OrgHubApp app;

    // who is logged in, and in which organization (one User row = one organization)
    private User currentUser;

    /**
     * Gives the page access to the managers and shows the user's organization.
     *
     * @param app the running application
     * @param user the signed-in user (one organization membership)
     */
    public void init(OrgHubApp app, User user) {
        this.app = app;

        searchField.textProperty().addListener((obs, oldText, newText) -> refresh());
        typeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                oldToggle.setSelected(true);    // clicking the selected filter again keeps it selected
            } else {
                refresh();
            }
        });
        setUser(user);
    }

    /**
     * Switches to another organization of the same person and reloads the cards.
     *
     * @param user the membership in the chosen organization
     */
    public void setUser(User user) {
        this.currentUser = user;
        organizationCaptionLabel.setText(app.getOrgManager().getName(user.getOrgId()).toUpperCase());

        // only officers can create events
        createEventButton.setVisible(user.isOfficer());
        createEventButton.setManaged(user.isOfficer());
        refresh();
    }

    /**
     * "Create event" button: opens the empty form.
     */
    @FXML
    private void onCreateEventClick() {
        if (!currentUser.isOfficer()) {
            return;
        }
        app.openDialog("event-form.fxml", "Create event", cardsPane,
                (EventFormController form) -> form.setCreate(app, currentUser));
        refresh();
    }

    /**
     * Opens the read-only details of an event.
     *
     * @param event the event to show
     */
    public void showDetails(Event event) {
        app.openDialog("event-details.fxml", "Event details", cardsPane,
                (EventDetailsController details) -> details.setEvent(event));
    }

    /**
     * Opens the form filled with an event (officers only).
     *
     * @param event the event to edit
     */
    public void editEvent(Event event) {
        if (!currentUser.isOfficer()) {
            return;
        }
        app.openDialog("event-form.fxml", "Edit event", cardsPane,
                (EventFormController form) -> form.setEdit(app, currentUser, event));
        refresh();
    }

    /**
     * Asks for confirmation, then deletes an event (officers only).
     *
     * @param event the event to delete
     */
    public void deleteEvent(Event event) {
        if (!currentUser.isOfficer()) {
            return;
        }
        EventDeleteController confirm = app.openDialog("event-delete.fxml", "Delete event", cardsPane,
                (EventDeleteController dialog) -> dialog.setEvent(event));
        if (confirm.isConfirmed()) {
            app.getEventManager().deleteEvent(event.getId(), event.getOrgId());
            refresh();
        }
    }

    /**
     * Rebuilds the cards from the current organization, the type filter and the search text.
     */
    private void refresh() {
        String type = typeFilter();
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        List<Event> events = app.getEventManager().searchEvents(currentUser.getOrgId(), type, keyword);

        cardsPane.getChildren().clear();
        for (Event event : events) {
            cardsPane.getChildren().add(buildCard(event));
        }

        emptyLabel.setText(type.isEmpty() && keyword.isEmpty()
                ? "This organization has no events yet." : "No events match your search.");
        emptyLabel.setVisible(events.isEmpty());
        emptyLabel.setManaged(events.isEmpty());
    }

    /**
     * @return the selected type filter, or an empty string for "All"
     */
    private String typeFilter() {
        Toggle selected = typeToggleGroup.getSelectedToggle();
        if (selected == null || selected == allToggle) {
            return "";
        }
        return ((ToggleButton) selected).getText();
    }

    /**
     * Loads one event-tile.fxml and fills it from an event.
     *
     * @param event the event to show
     * @return the loaded card
     */
    private Parent buildCard(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(OrgHubApp.class.getResource("layouts/event-tile.fxml"));
            Parent card = loader.load();
            EventTileController controller = loader.getController();
            controller.setEvent(event, currentUser.isOfficer(), this);
            return card;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load event-tile.fxml", e);
        }
    }
}
