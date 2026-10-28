package edu.utsa.cs3443.orghubprojectv2;

import edu.utsa.cs3443.orghubprojectv2.controller.ConsoleUI;
import edu.utsa.cs3443.orghubprojectv2.model.*;

/**
 * Entry point. Loads the three data files (events, members, organizations) into their
 * manager classes and hands them to the console UI.
 * Run from the project root so the relative data/ folder can be found.
 */
public class Main {
    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args){
        if (!new java.io.File("data").isDirectory()) {
            System.out.println("Could not find the data folder. Run the program from the project root: "
                    + new java.io.File("").getAbsolutePath());
            return;
        }
        String datafile = "data/events.csv";
        EventFileHandler fileHandler = new EventFileHandler(datafile);
        EventManager events  = new EventManager(fileHandler);

        MemberManager members = new MemberManager(new MemberFileHandler("data/members.csv"));
        OrganizationManager orgs = new OrganizationManager("data/orginizations.txt");

        ConsoleUI ui = new ConsoleUI(events, members, orgs);
        ui.run();
    }
}
