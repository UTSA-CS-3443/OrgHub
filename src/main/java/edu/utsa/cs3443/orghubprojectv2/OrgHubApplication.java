package edu.utsa.cs3443.orghubprojectv2;

import edu.utsa.cs3443.orghubprojectv2.controller.ShellController;
import edu.utsa.cs3443.orghubprojectv2.model.EventFileHandler;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.MemberFileHandler;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.Organization;
import edu.utsa.cs3443.orghubprojectv2.model.OrganizationManager;
import edu.utsa.cs3443.orghubprojectv2.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Launches the OrgHub GUI: loads the data files and shows the Events page.
 * Run from the project root; pass --email=someone@example.edu to sign in as them.
 */
public class OrgHubApplication extends Application {

    /**
     * Loads the data, builds the screen, and signs in a user.
     */
    @Override
    public void start(Stage stage) throws IOException {
        if (!new File("data").isDirectory()) {
            new Alert(Alert.AlertType.ERROR, "Could not find the data folder. Run the program from the project root: "
                    + new File("").getAbsolutePath()).showAndWait();
            return;
        }
        EventManager events = new EventManager(new EventFileHandler("data/events.csv"));
        MemberManager members = new MemberManager(new MemberFileHandler("data/members.csv"));
        OrganizationManager organizations = new OrganizationManager("data/orginizations.txt");

        FXMLLoader loader = new FXMLLoader(OrgHubApplication.class.getResource("layouts/shell-view.fxml"));
        Scene scene = new Scene(loader.load());
        ShellController controller = loader.getController();
        controller.setData(events, members, organizations);
        controller.signIn(pickEmail(members, organizations));

        stage.setTitle("OrgHub");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    /**
     * Uses the --email parameter if given, otherwise the first
     * officer or admin found, otherwise the first member found.
     */
    private String pickEmail(MemberManager members, OrganizationManager organizations) {
        String given = getParameters().getNamed().get("email");
        if (given != null && !given.trim().isEmpty()) {
            return given.trim();
        }
        String first = null;
        for (Organization organization : organizations.getAll()) {
            for (User user : members.getMembersForOrg(organization.getId())) {
                if (user.isOfficer()) {
                    return user.getEmail();
                }
                if (first == null) {
                    first = user.getEmail();
                }
            }
        }
        return first;
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
