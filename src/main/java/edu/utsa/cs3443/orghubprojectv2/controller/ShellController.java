package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.Organization;
import edu.utsa.cs3443.orghubprojectv2.model.OrganizationManager;
import edu.utsa.cs3443.orghubprojectv2.model.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the OrgHub shell: sidebar, organization drop-down, and
 * user chip, and gives the Events page its organization and user.
 */
public class ShellController {

    @FXML private ComboBox<Organization> organizationComboBox;
    @FXML private Button overviewButton;
    @FXML private Button eventsButton;
    @FXML private Button membersButton;
    @FXML private Button settingsButton;
    @FXML private Label userInitialsLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Parent eventsView;
    @FXML private EventsViewController eventsViewController;

    private EventManager eventManager;
    private MemberManager memberManager;
    private OrganizationManager organizationManager;
    private List<User> memberships = new ArrayList<User>();

    /**
     * Shows only the organization's name in the drop-down and reacts
     * when another organization is chosen.
     */
    @FXML
    public void initialize() {
        organizationComboBox.setConverter(new StringConverter<Organization>() {
            @Override
            public String toString(Organization organization) {
                return organization == null ? "" : organization.getName();
            }

            @Override
            public Organization fromString(String text) {
                return null;
            }
        });
        organizationComboBox.valueProperty().addListener((obs, oldOrg, newOrg) -> showOrganization(newOrg));
    }

    /**
     * Gives the controller the loaded data managers.
     */
    public void setData(EventManager eventManager, MemberManager memberManager,
                        OrganizationManager organizationManager) {
        this.eventManager = eventManager;
        this.memberManager = memberManager;
        this.organizationManager = organizationManager;
    }

    /**
     * Signs in the person with this email (null signs in nobody and
     * shows every organization read-only), then shows their first organization.
     */
    public void signIn(String email) {
        memberships = email == null ? new ArrayList<User>() : memberManager.findByEmail(email);
        List<Organization> organizations = new ArrayList<Organization>();
        for (User membership : memberships) {
            Organization organization = organizationManager.findById(membership.getOrgId());
            if (organization != null) {
                organizations.add(organization);
            }
        }
        if (organizations.isEmpty()) {
            organizations.addAll(organizationManager.getAll());
        }
        organizationComboBox.getItems().setAll(organizations);
        if (!organizations.isEmpty()) {
            organizationComboBox.getSelectionModel().selectFirst();
        }
        showOrganization(organizationComboBox.getValue());
    }

    private void showOrganization(Organization organization) {
        User user = null;
        for (User membership : memberships) {
            if (organization != null && membership.getOrgId() == organization.getId()) {
                user = membership;
            }
        }
        userInitialsLabel.setText(user == null ? "?" : initials(user.getName()));
        userNameLabel.setText(user == null ? "Guest" : user.getName());
        userRoleLabel.setText(user == null ? "View only" : "Organization " + user.getRole().toLowerCase());
        eventsViewController.setContext(organization, user, eventManager);
    }

    private String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        String result = parts[0].isEmpty() ? "?" : parts[0].substring(0, 1);
        if (parts.length > 1) {
            result += parts[parts.length - 1].substring(0, 1);
        }
        return result.toUpperCase();
    }
}
