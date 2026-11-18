package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.OrgHubApp;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controller for shell-view.fxml: the sidebar, the user chip, the organization switcher and the
 * Events page that is included in the middle. Holds no data itself.
 */
public class ShellController {
    @FXML private Label userInitialsLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button userMenuButton;
    @FXML private ComboBox<User> organizationComboBox;

    // the controller of the included events-view.fxml (fx:include fx:id="eventsView")
    @FXML private EventsViewController eventsViewController;

    private OrgHubApp app;

    // who is logged in, and in which organization (one User row = one organization)
    private User currentUser;

    /**
     * Fills the shell for a user who just signed in (or came back from another screen).
     *
     * @param app the running application
     * @param user the signed-in user (one organization membership)
     */
    public void init(OrgHubApp app, User user) {
        this.app = app;
        this.currentUser = user;

        // the switcher lists the organizations this person belongs to
        List<User> memberships = app.getMemberManager().findByEmail(user.getEmail());
        organizationComboBox.setItems(FXCollections.observableArrayList(memberships));
        organizationComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User membership) {
                return membership == null ? "" : app.getOrgManager().getName(membership.getOrgId());
            }

            @Override
            public User fromString(String text) {
                return null;
            }
        });
        organizationComboBox.setValue(user);
        organizationComboBox.setOnAction(e -> {
            User chosen = organizationComboBox.getValue();
            if (chosen != null) {
                currentUser = chosen;
                showUser();
                eventsViewController.setUser(chosen);
            }
        });

        showUser();
        eventsViewController.init(app, user);
    }

    /**
     * "Overview" in the sidebar.
     */
    @FXML
    private void onOverviewClick() {
        app.showOverview(currentUser);
    }

    /**
     * The "..." button next to the user's name: a small menu with Sign out.
     */
    @FXML
    private void onUserMenuClick() {
        MenuItem signOut = new MenuItem("Sign out");
        signOut.setOnAction(e -> app.showLogin());
        new ContextMenu(signOut).show(userMenuButton, Side.TOP, 0, 0);
    }

    /**
     * Updates the user chip for the current organization.
     */
    private void showUser() {
        String name = currentUser.getName();
        userNameLabel.setText(name);
        userRoleLabel.setText(currentUser.isAdmin() ? "Organization admin"
                : currentUser.isOfficer() ? "Organization officer" : "Member");

        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1);
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1);
        }
        userInitialsLabel.setText(initials.toUpperCase());
    }
}
