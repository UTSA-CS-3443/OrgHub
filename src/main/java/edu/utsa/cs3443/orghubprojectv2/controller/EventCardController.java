package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Controls one event card: shows the event and reports clicks on
 * details, edit, and delete.
 */
public class EventCardController {

    @FXML private Label monthLabel;
    @FXML private Label dayLabel;
    @FXML private Label typeTagLabel;
    @FXML private Label dateLabel;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label timeLabel;
    @FXML private Label locationLabel;
    @FXML private HBox locationBox;
    @FXML private Button detailsButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Event event;
    private Consumer<Event> onDetails;
    private Consumer<Event> onEdit;
    private Consumer<Event> onDelete;

    /**
     * Fills the card; edit and delete show only if canManage is true.
     */
    public void setEvent(Event event, boolean canManage, Consumer<Event> onDetails,
                         Consumer<Event> onEdit, Consumer<Event> onDelete) {
        this.event = event;
        this.onDetails = onDetails;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        LocalDate date = parseDate(event.getDate());
        if (date == null) {
            monthLabel.setText("");
            dayLabel.setText("--");
            dateLabel.setText(event.getDate().toUpperCase());
        } else {
            monthLabel.setText(date.format(DateTimeFormatter.ofPattern("MMM", Locale.US)).toUpperCase());
            dayLabel.setText(String.valueOf(date.getDayOfMonth()));
            dateLabel.setText(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)).toUpperCase());
        }
        typeTagLabel.setText(event.getType().toUpperCase());
        titleLabel.setText(event.getTitle());
        descriptionLabel.setText(event.getDescription());
        timeLabel.setText(event.getTime());
        locationLabel.setText(event.getLocation());
        boolean hasLocation = !event.getLocation().trim().isEmpty();
        locationBox.setVisible(hasLocation);
        locationBox.setManaged(hasLocation);
        editButton.setVisible(canManage);
        editButton.setManaged(canManage);
        deleteButton.setVisible(canManage);
        deleteButton.setManaged(canManage);
    }

    /**
     * Returns the date, or null if the text is not YYYY-MM-DD.
     */
    static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }

    @FXML
    protected void onDetailsClick() {
        onDetails.accept(event);
    }

    @FXML
    protected void onEditClick() {
        onEdit.accept(event);
    }

    @FXML
    protected void onDeleteClick() {
        onDelete.accept(event);
    }
}
