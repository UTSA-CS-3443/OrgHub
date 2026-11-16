package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.Organization;
import edu.utsa.cs3443.orghubprojectv2.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controls the Events page: search, type filter, event cards, and the
 * create, edit, details, and delete actions.
 */
public class EventsViewController {

    private static final String LAYOUTS = "/edu/utsa/cs3443/orghubprojectv2/layouts/";

    @FXML private Label organizationCaptionLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label emptyLabel;
    @FXML private Button createEventButton;
    @FXML private TextField searchField;
    @FXML private ToggleGroup typeToggleGroup;
    @FXML private ToggleButton allToggle;
    @FXML private ToggleButton meetingToggle;
    @FXML private ToggleButton workshopToggle;
    @FXML private ToggleButton socialToggle;
    @FXML private ToggleButton volunteerToggle;
    @FXML private FlowPane cardsPane;

    private Organization organization;
    private User user;
    private EventManager eventManager;

    /**
     * Redraws the cards whenever the search text or type filter changes.
     */
    @FXML
    public void initialize() {
        searchField.textProperty().addListener((obs, oldText, newText) -> refresh());
        typeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null && oldToggle != null) {
                oldToggle.setSelected(true);
            } else {
                refresh();
            }
        });
    }

    /**
     * Sets the organization, the signed-in user's membership in it
     * (or null), and the event data, then redraws the page.
     */
    public void setContext(Organization organization, User user, EventManager eventManager) {
        this.organization = organization;
        this.user = user;
        this.eventManager = eventManager;
        boolean canManage = canManage();
        organizationCaptionLabel.setText(organization == null ? "" : organization.getName().toUpperCase());
        subtitleLabel.setText(canManage
                ? "Only events for the selected organization are shown here."
                : "Only events for the selected organization are shown here. Officers can change them.");
        createEventButton.setVisible(canManage);
        createEventButton.setManaged(canManage);
        refresh();
    }

    /**
     * Opens the form to create a new event (officers and admins only).
     */
    @FXML
    protected void onCreateEventClick() {
        openForm(null);
    }

    private boolean canManage() {
        return user != null && user.isOfficer();
    }

    private void refresh() {
        cardsPane.getChildren().clear();
        if (organization == null || eventManager == null) {
            showEmptyMessage("No organizations found yet. Create the first organization and Admin account to get started.");
            return;
        }
        String type = selectedType();
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        List<Event> events = new ArrayList<Event>(eventManager.searchEvents(organization.getId(), type, keyword));
        Collections.sort(events);
        if (events.isEmpty()) {
            boolean filtering = !type.isEmpty() || !keyword.isEmpty();
            showEmptyMessage(filtering ? "No events match your search." : "This organization has no events yet.");
            return;
        }
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);
        for (Event event : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUTS + "event-card.fxml"));
                Parent card = loader.load();
                EventCardController controller = loader.getController();
                controller.setEvent(event, canManage(), e -> showDetails(e), e -> openForm(e), e -> confirmDelete(e));
                cardsPane.getChildren().add(card);
            } catch (IOException | RuntimeException e) {
                System.out.println("Could not show event " + event.getId() + ": " + e.getMessage());
            }
        }
    }

    private void showEmptyMessage(String text) {
        emptyLabel.setText(text);
        emptyLabel.setVisible(true);
        emptyLabel.setManaged(true);
    }

    /**
     * Returns the selected type name, or an empty string for "All".
     */
    private String selectedType() {
        Toggle selected = typeToggleGroup.getSelectedToggle();
        if (selected == null || selected == allToggle) {
            return "";
        }
        return ((ToggleButton) selected).getText();
    }

    private void openForm(Event existing) {
        if (organization == null || !canManage()) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUTS + "event-form.fxml"));
            Parent root = loader.load();
            EventFormController controller = loader.getController();
            controller.setup(existing, organization.getId(), user.getName(), eventManager);
            openDialog(existing == null ? "Create event" : "Edit event", root);
            refresh();
        } catch (IOException | RuntimeException e) {
            showError("The event form could not be opened.");
        }
    }

    private void showDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LAYOUTS + "event-details.fxml"));
            Parent root = loader.load();
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            openDialog("Event details", root);
        } catch (IOException | RuntimeException e) {
            showError("The event details could not be opened.");
        }
    }

    private void confirmDelete(Event event) {
        if (!canManage()) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + event.getTitle() + "\"? This cannot be undone.", ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Delete event");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            if (!eventManager.deleteEvent(event.getId(), event.getOrgId())) {
                showError("That event could not be found.");
            }
            refresh();
        }
    }

    private void openDialog(String title, Parent root) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(cardsPane.getScene().getWindow());
        dialog.setTitle(title);
        dialog.setScene(new Scene(root));
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
