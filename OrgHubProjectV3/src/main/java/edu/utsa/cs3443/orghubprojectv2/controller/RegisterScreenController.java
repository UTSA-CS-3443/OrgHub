package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.Organization;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Controller for register-screen.fxml. The JavaFX counterpart of LoginController's createAccount():
 * same checks, and new accounts are Members (the very first account in an empty system is the Admin).
 * Holds no data itself; it calls MemberManager.
 */
public class RegisterScreenController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Organization> organizationCombo;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button createAccountButton;
    @FXML private Hyperlink signInLink;

    private OrgHubApp app;

    /**
     * Gives the controller access to the managers and screen switching, and fills the organization list.
     *
     * @param app the running application
     */
    public void init(OrgHubApp app) {
        this.app = app;
        organizationCombo.setItems(FXCollections.observableArrayList(app.getOrgManager().getAll()));
        // Organization.toString() includes the contact email; the dropdown should show the name only
        organizationCombo.setConverter(new StringConverter<Organization>() {
            @Override
            public String toString(Organization org) {
                return org == null ? "" : org.getName();
            }

            @Override
            public Organization fromString(String text) {
                return null;
            }
        });
    }

    /**
     * Create account button. Validates the form, then adds the member and saves the password.
     */
    @FXML
    private void handleCreateAccount() {
        String name = clean(fullNameField.getText());
        String email = clean(emailField.getText());
        Organization org = organizationCombo.getValue();
        String password = passwordField.getText();
        MemberManager members = app.getMemberManager();

        if (name.isEmpty()) {
            showError("Name is required.");
            return;
        }
        if (!validEmail(email)) {
            showError("Invalid email address. Example: jane.doe@example.edu");
            return;
        }
        if (org == null) {
            showError("Choose an organization.");
            return;
        }
        // someone who already has a password (from another organization) must use the same one
        if (members.hasPassword(email)) {
            if (!members.checkPassword(email, password)) {
                showError("You already have an account. Enter your existing password.");
                return;
            }
        } else if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        } else if (!password.equals(confirmPasswordField.getText())) {
            showError("Passwords do not match.");
            return;
        }
        // new accounts are regular members (an Admin can promote them later),
        // except the very first account, which is the Admin
        String role = members.isEmpty() ? "Admin" : "Member";
        if (members.addMember(org.getId(), name, email, role)) {
            members.setPassword(email, password);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account created. You can now sign in.");
            alert.setHeaderText(null);
            alert.showAndWait();
            app.showLogin();
        } else {
            showError("That email is already in this organization.");
        }
    }

    /**
     * "Sign in" link back to the login screen.
     */
    @FXML
    private void handleSignIn() {
        app.showLogin();
    }

    /**
     * Trims text and removes commas, because the data files are comma-separated.
     *
     * @param text the text from a field
     * @return the cleaned text
     */
    private String clean(String text) {
        return text.trim().replace(",", " ");
    }

    /**
     * Basic email format check.
     *
     * @param email the email address
     * @return true if the condition holds, otherwise false
     */
    private boolean validEmail(String email) {
        return email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }

    /**
     * Shows an error message.
     *
     * @param message the text to show
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
