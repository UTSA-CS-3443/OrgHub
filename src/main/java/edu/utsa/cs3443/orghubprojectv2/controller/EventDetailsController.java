package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Controller for event-details.fxml: a read-only window with everything about one event,
 * including its type-specific reminder.
 */
public class EventDetailsController {
    @FXML private Label typeTagLabel;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label locationLabel;
    @FXML private Label organizerLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label reminderLabel;
    @FXML private Button closeButton;

    /**
     * Fills the window from an event.
     *
     * @param event the event to show
     */
    public void setEvent(Event event) {
        typeTagLabel.setText(event.getType().toUpperCase());
        titleLabel.setText(event.getTitle());
        timeLabel.setText(event.getTime());
        locationLabel.setText(event.getLocation());
        organizerLabel.setText(event.getOrganizer());
        descriptionLabel.setText(event.getDescription());
        reminderLabel.setText(event.getReminder());
        try {
            dateLabel.setText(LocalDate.parse(event.getDate())
                    .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US)));
        } catch (DateTimeParseException ex) {
            dateLabel.setText(event.getDate());
        }
    }

    /**
     * Close button.
     */
    @FXML
    private void onCloseClick() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
