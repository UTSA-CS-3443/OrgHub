package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EventDeleteController {

    @FXML private Label titleLabel;
    @FXML private Label questionDeleteLabel;
    @FXML private Button deleteEventButton;
    @FXML private Button keepEventButton;

    private EventManager eventManager;
    private int id;
    private int orgId;

    public void setup(EventManager eventManager, int id, int orgId) {
        this.eventManager = eventManager;
        this.id = id;
        this.orgId = orgId;
    }

    @FXML
    void deleteEventOnClick(ActionEvent actionEvent) {
        if (eventManager.deleteEvent(id, orgId)) {
            ((Stage) deleteEventButton.getScene().getWindow()).close();
        }
    }

    @FXML
    void keepEventOnClick(ActionEvent actionEvent) {
        ((Stage) keepEventButton.getScene().getWindow()).close();
    }
}
