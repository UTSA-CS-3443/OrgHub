package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.EventType;
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
import java.util.Locale;

/**
 * Controls the create/edit event form: validates the input and saves
 * it through EventManager.
 */
public class EventFormController {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    @FXML private Label headingLabel;
    @FXML private Label errorLabel;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField locationField;
    @FXML private TextField organizerField;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Event existing;
    private int orgId;
    private EventManager eventManager;

    /**
     * Fills the type list and the time list (every 15 minutes).
     */
    @FXML
    public void initialize() {
        for (EventType type : EventType.values()) {
            typeComboBox.getItems().add(type.getLabel());
        }
        for (int minutes = 0; minutes < 24 * 60; minutes += 15) {
            timeComboBox.getItems().add(LocalTime.of(minutes / 60, minutes % 60).format(TIME_FORMAT));
        }
    }

    /**
     * Prepares the form: blank for a new event, filled in when editing.
     */
    public void setup(Event existing, int orgId, String defaultOrganizer, EventManager eventManager) {
        this.existing = existing;
        this.orgId = orgId;
        this.eventManager = eventManager;
        if (existing == null) {
            headingLabel.setText("Create event");
            organizerField.setText(defaultOrganizer);
            datePicker.setValue(LocalDate.now());
            return;
        }
        headingLabel.setText("Edit event");
        titleField.setText(existing.getTitle());
        typeComboBox.setValue(existing.getType());
        LocalDate date = EventCardController.parseDate(existing.getDate());
        datePicker.setValue(date);
        String time = existing.getTime().trim();
        if (!time.isEmpty() && !timeComboBox.getItems().contains(time)) {
            timeComboBox.getItems().add(time);
        }
        timeComboBox.setValue(time.isEmpty() ? null : time);
        locationField.setText(existing.getLocation());
        organizerField.setText(existing.getOrganizer());
        descriptionArea.setText(existing.getDescription());
    }

    /**
     * Checks the fields, then creates or replaces the event and closes.
     */
    @FXML
    protected void onSaveClick() {
        if (titleField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a title.");
        } else if (typeComboBox.getValue() == null) {
            errorLabel.setText("Please choose a type.");
        } else if (datePicker.getValue() == null) {
            errorLabel.setText("Please pick a date.");
        } else if (timeComboBox.getValue() == null) {
            errorLabel.setText("Please pick a time.");
        } else {
            save();
        }
    }

    @FXML
    protected void onCancelClick() {
        close();
    }

    private void save() {
        String date = datePicker.getValue().toString();
        if (existing == null) {
            eventManager.createEvent(orgId, typeComboBox.getValue(), titleField.getText().trim(), date,
                    timeComboBox.getValue(), locationField.getText().trim(),
                    descriptionArea.getText().trim(), organizerField.getText().trim());
        } else if (!eventManager.replaceEvent(existing.getId(), orgId, typeComboBox.getValue(),
                titleField.getText().trim(), date, timeComboBox.getValue(), locationField.getText().trim(),
                descriptionArea.getText().trim(), organizerField.getText().trim())) {
            errorLabel.setText("That event no longer exists.");
            return;
        }
        close();
    }

    private void close() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
