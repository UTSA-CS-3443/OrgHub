package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for event-delete.fxml: asks "Delete this event?". It only records the answer;
 * the Events page does the deleting when isConfirmed() is true.
 */
public class EventDeleteController {
    @FXML private Label messageLabel;
    @FXML private Button keepButton;
    @FXML private Button deleteButton;

    private boolean confirmed = false;

    /**
     * Puts the event's title in the message.
     *
     * @param event the event that would be deleted
     */
    public void setEvent(Event event) {
        messageLabel.setText("\"" + event.getTitle() + "\" will be permanently removed. "
                + "Are you sure you want to remove this event?");
    }

    /**
     * @return true if the user chose to delete, false if they kept the event or closed the window
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * "Keep Event" button.
     */
    @FXML
    private void onKeepClick() {
        close();
    }

    /**
     * "Yes, Delete Event" button.
     */
    @FXML
    private void onDeleteClick() {
        confirmed = true;
        close();
    }

    /**
     * Closes this window.
     */
    private void close() {
        ((Stage) keepButton.getScene().getWindow()).close();
    }
}
