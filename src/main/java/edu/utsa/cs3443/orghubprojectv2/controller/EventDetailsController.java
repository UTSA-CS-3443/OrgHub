package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controls the read-only event details dialog.
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
     * Shows every detail of the event, including its reminder.
     */
    public void setEvent(Event event) {
        LocalDate date = EventCardController.parseDate(event.getDate());
        typeTagLabel.setText(event.getType().toUpperCase());
        titleLabel.setText(event.getTitle());
        dateLabel.setText(date == null ? event.getDate()
                : date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US)));
        timeLabel.setText(event.getTime());
        locationLabel.setText(orElse(event.getLocation(), "To be announced"));
        organizerLabel.setText(orElse(event.getOrganizer(), "Not listed"));
        descriptionLabel.setText(orElse(event.getDescription(), "No description."));
        reminderLabel.setText(event.getReminder());
    }

    private String orElse(String text, String fallback) {
        return text == null || text.trim().isEmpty() ? fallback : text;
    }

    @FXML
    protected void onCloseClick() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
