package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Controller for event-tile.fxml: one event as a card on the Events page.
 * (event-card.fxml / EventCardController are the overview's row version of an event.)
 */
public class EventTileController {
    @FXML private Label monthLabel;
    @FXML private Label dayLabel;
    @FXML private Label typeTagLabel;
    @FXML private Label dateLabel;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label timeLabel;
    @FXML private HBox locationBox;
    @FXML private Label locationLabel;
    @FXML private Button detailsButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Event event;
    private EventsViewController page;

    /**
     * Fills the card from an event.
     *
     * @param event the event to show
     * @param canEdit true for officers, who also see the edit and delete buttons
     * @param page the Events page that opens the dialogs
     */
    public void setEvent(Event event, boolean canEdit, EventsViewController page) {
        this.event = event;
        this.page = page;

        typeTagLabel.setText(event.getType().toUpperCase());
        titleLabel.setText(event.getTitle());
        descriptionLabel.setText(event.getDescription());
        timeLabel.setText(event.getTime());
        locationLabel.setText(event.getLocation());
        locationBox.setVisible(!event.getLocation().isEmpty());
        locationBox.setManaged(!event.getLocation().isEmpty());

        try {
            LocalDate date = LocalDate.parse(event.getDate());
            monthLabel.setText(date.format(DateTimeFormatter.ofPattern("MMM", Locale.US)).toUpperCase());
            dayLabel.setText(String.valueOf(date.getDayOfMonth()));
            dateLabel.setText(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)).toUpperCase());
        } catch (DateTimeParseException ex) {
            monthLabel.setText("");
            dayLabel.setText("?");
            dateLabel.setText(event.getDate());
        }

        editButton.setVisible(canEdit);
        editButton.setManaged(canEdit);
        deleteButton.setVisible(canEdit);
        deleteButton.setManaged(canEdit);
    }

    /**
     * "View details" button.
     */
    @FXML
    private void onDetailsClick() {
        page.showDetails(event);
    }

    /**
     * Pencil button (officers only).
     */
    @FXML
    private void onEditClick() {
        page.editEvent(event);
    }

    /**
     * Trash button (officers only).
     */
    @FXML
    private void onDeleteClick() {
        page.deleteEvent(event);
    }
}
