package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.EventType;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller for event-form.fxml: the Create event / Edit event window. The JavaFX counterpart of
 * MainScreenController's createEvent() and updateEvent(), with the same checks. Holds no data
 * itself; it calls EventManager.
 */
public class EventFormController {
    @FXML private Label headingLabel;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField locationField;
    @FXML private TextField organizerField;
    @FXML private TextArea descriptionArea;
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private OrgHubApp app;
    private User currentUser;
    private Event editing;     // the event being edited, or null when creating

    /**
     * Runs after the FXML loads: fills the type and time dropdowns.
     */
    @FXML
    private void initialize() {
        List<String> types = new ArrayList<>();
        for (EventType type : EventType.values()) {
            types.add(type.getLabel());
        }
        typeComboBox.setItems(FXCollections.observableArrayList(types));

        // every half hour from 7:00 AM to 10:00 PM, in the same "6:00 PM" format the data files use
        List<String> times = new ArrayList<>();
        for (LocalTime t = LocalTime.of(7, 0); !t.isAfter(LocalTime.of(22, 0)); t = t.plusMinutes(30)) {
            times.add(t.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US)));
        }
        timeComboBox.setItems(FXCollections.observableArrayList(times));
    }

    /**
     * Opens the form empty, for a new event in the user's organization.
     *
     * @param app the running application
     * @param user the signed-in officer
     */
    public void setCreate(OrgHubApp app, User user) {
        this.app = app;
        this.currentUser = user;
        this.editing = null;
        headingLabel.setText("Create event");
        organizerField.setText(user.getName());
    }

    /**
     * Opens the form filled with an existing event. The type and organizer can't be changed
     * (EventManager.updateEvent doesn't update them), so those two fields are locked.
     *
     * @param app the running application
     * @param user the signed-in officer
     * @param event the event to edit
     */
    public void setEdit(OrgHubApp app, User user, Event event) {
        this.app = app;
        this.currentUser = user;
        this.editing = event;
        headingLabel.setText("Edit event");

        titleField.setText(event.getTitle());
        typeComboBox.setValue(event.getType());
        typeComboBox.setDisable(true);
        try {
            datePicker.setValue(LocalDate.parse(event.getDate()));
        } catch (DateTimeParseException ex) {
            datePicker.setValue(null);
        }
        if (!event.getTime().isEmpty() && !timeComboBox.getItems().contains(event.getTime())) {
            timeComboBox.getItems().add(event.getTime());   // a time that isn't on the half-hour list
        }
        timeComboBox.setValue(event.getTime().isEmpty() ? null : event.getTime());
        locationField.setText(event.getLocation());
        organizerField.setText(event.getOrganizer());
        organizerField.setDisable(true);
        descriptionArea.setText(event.getDescription());
    }

    /**
     * Save button. Validates the form, then creates or updates the event.
     */
    @FXML
    private void onSaveClick() {
        if (!currentUser.isOfficer()) {
            showError("Only officers can do that.");
            return;
        }
        String title = clean(titleField.getText());
        String type = typeComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = timeComboBox.getValue() == null ? "" : timeComboBox.getValue();

        if (title.isEmpty()) {
            showError("A title is required.");
            return;
        }
        if (type == null || !EventManager.isValidType(type)) {
            showError("Type must be Meeting, Workshop, Social or Volunteer.");
            return;
        }
        if (date == null) {
            showError("Choose a date.");
            return;
        }
        String location = clean(locationField.getText());
        String description = clean(descriptionArea.getText());

        if (editing == null) {
            String organizer = clean(organizerField.getText());
            app.getEventManager().createEvent(currentUser.getOrgId(), type, title, date.toString(), time,
                    location, description, organizer.isEmpty() ? currentUser.getName() : organizer);
        } else if (!app.getEventManager().updateEvent(editing.getId(), editing.getOrgId(), title,
                date.toString(), time, location, description)) {
            showError("Event not found.");
            return;
        }
        close();
    }

    /**
     * Cancel button.
     */
    @FXML
    private void onCancelClick() {
        close();
    }

    /**
     * Trims text and turns line breaks and commas into spaces, because the data files are
     * comma-separated with one event per line.
     *
     * @param text the text from a field
     * @return the cleaned text
     */
    private String clean(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s*[\\r\\n]+\\s*", " ").replace(",", " ");
    }

    /**
     * Shows a message inside the form.
     *
     * @param message the text to show
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /**
     * Closes this window.
     */
    private void close() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
