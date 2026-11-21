package edu.utsa.cs3443.orghubprojectv2;

import javafx.application.Application;

/**
 * Entry point for the JavaFX version of OrgHub. It is kept separate from OrgHubApp so the app
 * also starts from an IDE (a main class that extends Application needs JavaFX on the module path).
 * Run from the project root so the relative data/ folder can be found.
 * The console version is still available through Main.
 */
public class OrgHubLauncher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Application.launch(OrgHubApp.class, args);
    }
}
