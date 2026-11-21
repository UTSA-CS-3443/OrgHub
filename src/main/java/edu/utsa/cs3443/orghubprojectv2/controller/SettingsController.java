package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.Organization;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for settings-view.fxml: edits the current organization's name and contact email.
 * Only officers can change them; members see the values read-only. Included in shell-view.fxml.
 */
public class SettingsController {
    @FXML private TextField organizationNameField;
    @FXML private TextField contactEmailField;
    @FXML private CheckBox emailNotificationCheckBox;
    @FXML private Button saveButton;
    @FXML private Label statusLabel;

    private OrgHubApp app;

    // who is logged in, and in which organization (one User row = one organization)
    private User currentUser;

    // tells the shell to redraw the organization name after a save
    private Runnable onSaved;

    /**
     * Gives the page access to the managers and shows the user's organization.
     *
     * @param app the running application
     * @param user the signed-in user (one organization membership)
     * @param onSaved called after the organization was saved, so other parts of the window can refresh
     */
    public void init(OrgHubApp app, User user, Runnable onSaved) {
        this.app = app;
        this.onSaved = onSaved;
        setUser(user);
    }

    /**
     * Switches to another organization of the same person and reloads the fields.
     *
     * @param user the membership in the chosen organization
     */
    public void setUser(User user) {
        this.currentUser = user;
        Organization org = app.getOrgManager().findById(user.getOrgId());
        if (org != null) {
            organizationNameField.setText(org.getName());
            contactEmailField.setText(org.getContactEmail());
        }

        // only officers can change organization details
        boolean canEdit = user.isOfficer();
        organizationNameField.setEditable(canEdit);
        contactEmailField.setEditable(canEdit);
        saveButton.setDisable(!canEdit);
        showStatus(canEdit ? "" : "Only officers can change organization details.", false);
    }

    /**
     * "Save settings" button: checks the fields, then saves the organization to the data file.
     */
    @FXML
    private void handleSave() {
        String organizationName = organizationNameField.getText().trim();
        String contactEmail = contactEmailField.getText().trim();

        // make sure all required fields are filled in
        if (organizationName.isEmpty() || contactEmail.isEmpty()) {
            showStatus("Please fill in all required fields.", true);
            return;
        }
        // the data file is comma separated, so a comma in the name would break it
        if (organizationName.contains(",")) {
            showStatus("The organization name cannot contain a comma.", true);
            return;
        }
        // email validation
        if (!contactEmail.matches("^[^\\s@,]+@[^\\s@,]+\\.[^\\s@,]+$")) {
            showStatus("Please enter a valid email address.", true);
            return;
        }

        app.getOrgManager().updateOrganization(currentUser.getOrgId(), organizationName, contactEmail);
        showStatus("Settings saved.", false);
        if (onSaved != null) {
            onSaved.run();
        }
    }

    /**
     * Shows a message under the Save button, in red for an error and in green otherwise.
     *
     * @param message the text (empty hides the line)
     * @param error true for an error message
     */
    private void showStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("error-label", "success-label");
        statusLabel.getStyleClass().add(error ? "error-label" : "success-label");
    }
}
