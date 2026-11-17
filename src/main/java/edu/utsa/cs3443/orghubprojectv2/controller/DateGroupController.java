package edu.utsa.cs3443.orghubprojectv2.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for date-group.fxml: one date heading plus the event cards for that day.
 * The overview loads one of these per distinct date.
 */
public class DateGroupController {
    @FXML private Label dateHeadingLabel;
    @FXML private Label eventCountLabel;
    @FXML private VBox cardsBox;

    /**
     * Sets the heading text and the event count.
     *
     * @param heading the date text, e.g. "Sunday, November 8"
     * @param eventCount how many events fall on that day
     */
    public void setGroup(String heading, int eventCount) {
        dateHeadingLabel.setText(heading);
        eventCountLabel.setText(eventCount == 1 ? "1 event" : eventCount + " events");
    }

    /**
     * Adds an event card under the heading.
     *
     * @param card a loaded event-card.fxml
     */
    public void addCard(Node card) {
        cardsBox.getChildren().add(card);
    }
}
