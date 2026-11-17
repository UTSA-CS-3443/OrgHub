package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Controller for event-card.fxml: shows one event. The overview loads one of these per event.
 */
public class EventCardController {
    // accent bar color, tag background and tag text per organization (picked by organization id)
    private static final String[][] ORG_COLORS = {
        {"#1a6b52", "#dcebe6", "#1a6b52"},
        {"#4f7a8a", "#dfe8ec", "#3f6b7c"},
        {"#b9803a", "#f3e5cf", "#8a5a1c"},
        {"#7a5c8e", "#e9e1ef", "#5d4470"}
    };

    @FXML private Region cardAccent;
    @FXML private Label monthLabel;
    @FXML private Label dayLabel;
    @FXML private Label orgTagLabel;
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label timeLabel;
    @FXML private Label locationLabel;

    /**
     * Fills the card from an event.
     *
     * @param event the event to show
     * @param orgName the name of the event's organization
     */
    public void setEvent(Event event, String orgName) {
        titleLabel.setText(event.getTitle());
        typeLabel.setText(event.getType());
        timeLabel.setText(event.getTime());
        locationLabel.setText(event.getLocation());
        orgTagLabel.setText(orgName.toUpperCase());

        try {
            LocalDate date = LocalDate.parse(event.getDate());
            monthLabel.setText(date.format(DateTimeFormatter.ofPattern("MMM", Locale.US)).toUpperCase());
            dayLabel.setText(String.valueOf(date.getDayOfMonth()));
        } catch (DateTimeParseException ex) {
            monthLabel.setText("");
            dayLabel.setText("?");
        }

        String[] colors = ORG_COLORS[Math.floorMod(event.getOrgId() - 1, ORG_COLORS.length)];
        cardAccent.setStyle("-fx-background-color: " + colors[0] + ";");
        orgTagLabel.setStyle("-fx-background-color: " + colors[1] + "; -fx-text-fill: " + colors[2] + ";");
    }
}
