package edu.utsa.cs3443.orghubprojectv2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML
    private TextField organizationNameField;

    @FXML
    private TextField contactEmailField;

    @FXML
    private CheckBox emailNotificationCheckBox;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleSave() {
        String organizationName = organizationNameField.getText().trim();
        String contactEmail = contactEmailField.getText().trim();
        boolean emailNotifications = emailNotificationCheckBox.isSelected();

        // make sure all required feilds are filled in
        if(organizationName.isEmpty() || contactEmail.isEmpty()){
            statusLabel.setText("Please fill in all required fields.");
            return;
        }
        //email validation
        if(!contactEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")){
            statusLabel.setText("Please enter a valid email address.");
            return;
        }
        statusLabel.setText("Settings submitted successfully!");
        System.out.println("Organization: " + organizationName);
        System.out.println("Contact Email: " + contactEmail);
        System.out.println("Email Notifications: " + emailNotifications);

    }
}
