package edu.utsa.cs3443.orghubprojectv2;

import edu.utsa.cs3443.orghubprojectv2.controller.LoginScreenController;
import edu.utsa.cs3443.orghubprojectv2.controller.OverviewScreenController;
import edu.utsa.cs3443.orghubprojectv2.controller.RegisterScreenController;
import edu.utsa.cs3443.orghubprojectv2.model.EventFileHandler;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.MemberFileHandler;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.OrganizationManager;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * JavaFX application. Loads the three data files into the manager classes (same as Main does for
 * the console version), then switches between the login, register and overview screens.
 * Each screen's controller is given this object so it can reach the managers and change screens.
 */
public class OrgHubApp extends Application {
    private Stage stage;
    private EventManager eventManager;
    private MemberManager memberManager;
    private OrganizationManager orgManager;

    /**
     * Loads the data and shows the login screen.
     *
     * @param primaryStage the main window
     */
    @Override
    public void start(Stage primaryStage) {
        if (!new File("data").isDirectory()) {
            System.out.println("Could not find the data folder. Run the program from the project root: "
                    + new File("").getAbsolutePath());
            Platform.exit();
            return;
        }
        eventManager = new EventManager(new EventFileHandler("data/events.csv"));
        memberManager = new MemberManager(new MemberFileHandler("data/members.csv"));
        orgManager = new OrganizationManager("data/orginizations.txt");

        stage = primaryStage;
        stage.setTitle("OrgHub - Student Organization Management");
        showLogin();
        stage.show();
    }

    /**
     * Shows the login screen.
     */
    public void showLogin() {
        LoginScreenController controller = swap("login-screen.fxml");
        controller.init(this);
    }

    /**
     * Shows the create-account screen.
     */
    public void showRegister() {
        RegisterScreenController controller = swap("register-screen.fxml");
        controller.init(this);
    }

    /**
     * Shows the overview screen for a signed-in user.
     *
     * @param user the signed-in user (one organization membership)
     */
    public void showOverview(User user) {
        OverviewScreenController controller = swap("overview-screen.fxml");
        controller.init(this, user);
    }

    /**
     * Loads a layout and puts it in the window.
     *
     * @param fxml the file name inside the layouts folder
     * @return the controller that the layout's fx:controller created
     */
    private <T> T swap(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(OrgHubApp.class.getResource("layouts/" + fxml));
            Parent root = loader.load();
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }
            return loader.getController();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + fxml, e);
        }
    }

    /**
     * @return manages events
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @return manages members and passwords
     */
    public MemberManager getMemberManager() {
        return memberManager;
    }

    /**
     * @return manages organizations
     */
    public OrganizationManager getOrgManager() {
        return orgManager;
    }
}
