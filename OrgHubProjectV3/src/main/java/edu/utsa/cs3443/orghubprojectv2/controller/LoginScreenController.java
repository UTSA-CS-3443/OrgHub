package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Optional;

/**
 * Controller for login-screen.fxml. The JavaFX counterpart of LoginController's login():
 * email + password, with a first-login password for people an officer added.
 * Holds no data itself; it calls MemberManager.
 */
public class LoginScreenController {
    // the sample account in data/members.csv (Clara Villatoro, password 123456)
    private static final String DEMO_EMAIL = "clara@example.edu";
    private static final String DEMO_PASSWORD = "123456";

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button togglePasswordButton;
    @FXML private Button signInButton;
    @FXML private Hyperlink demoLink;
    @FXML private Hyperlink createAccountLink;

    private OrgHubApp app;
    private TextField visiblePasswordField;

    /**
     * Runs after the FXML loads. The layout only has a masked field, so this adds a plain-text
     * copy on top of it (kept in sync) for the Show/Hide button.
     */
    @FXML
    private void initialize() {
        visiblePasswordField = new TextField();
        visiblePasswordField.getStyleClass().add("input");
        visiblePasswordField.setPromptText(passwordField.getPromptText());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visiblePasswordField.setVisible(false);
        // index 1 = above the masked field, below the Show/Hide button
        ((Pane) passwordField.getParent()).getChildren().add(1, visiblePasswordField);
    }

    /**
     * Gives the controller access to the managers and screen switching.
     *
     * @param app the running application
     */
    public void init(OrgHubApp app) {
        this.app = app;
    }

    /**
     * Show/Hide button next to the password.
     */
    @FXML
    private void handleTogglePassword() {
        boolean show = !visiblePasswordField.isVisible();
        visiblePasswordField.setVisible(show);
        passwordField.setVisible(!show);
        togglePasswordButton.setText(show ? "Hide" : "Show");
        (show ? visiblePasswordField : passwordField).requestFocus();
    }

    /**
     * Sign in button. Checks the email and password and opens the overview.
     * People in several organizations start in the first one and switch from the overview.
     */
    @FXML
    private void handleSignIn() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        MemberManager members = app.getMemberManager();

        if (email.isEmpty()) {
            showError("Enter your email address.");
            return;
        }
        List<User> memberships = members.findByEmail(email);
        if (memberships.isEmpty()) {
            showError("No account found for that email. Choose 'Create an account' to sign up.");
            return;
        }
        if (!members.hasPassword(email)) {
            // account was added by an officer (or predates passwords): set it on first login
            if (!setFirstPassword(email, password)) {
                return;
            }
        } else if (!members.checkPassword(email, password)) {
            showError("Incorrect password.");
            return;
        }
        app.showOverview(memberships.get(0));
    }

    /**
     * "Use demo account" link: fills in the sample account and signs in.
     */
    @FXML
    private void handleDemo() {
        emailField.setText(DEMO_EMAIL);
        passwordField.setText(DEMO_PASSWORD);
        handleSignIn();
    }

    /**
     * "Create an account" link.
     */
    @FXML
    private void handleCreateAccount() {
        app.showRegister();
    }

    /**
     * First login for an account with no password yet: the typed password must be 6+ characters
     * and be confirmed in a small dialog.
     *
     * @param email the account's email
     * @param password the password typed in the field
     * @return true if the password was saved, otherwise false
     */
    private boolean setFirstPassword(String email, String password) {
        if (password.length() < 6) {
            showError("Welcome! This is your first login, so choose a password of at least 6 characters, "
                    + "then press Sign in.");
            return false;
        }
        PasswordField confirm = new PasswordField();
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create your password");
        dialog.setHeaderText("This is your first login. Re-enter the password to confirm it.");
        dialog.getDialogPane().setContent(confirm);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> button == ButtonType.OK ? confirm.getText() : null);

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return false;
        }
        if (!password.equals(result.get())) {
            showError("Passwords do not match.");
            return false;
        }
        app.getMemberManager().setPassword(email, password);
        return true;
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
