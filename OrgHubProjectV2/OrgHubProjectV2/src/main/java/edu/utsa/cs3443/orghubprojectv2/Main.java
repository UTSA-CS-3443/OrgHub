package edu.utsa.cs3443.orghubprojectv2;

import edu.utsa.cs3443.orghubprojectv2.controller.ConsoleUI;
import edu.utsa.cs3443.orghubprojectv2.model.*;

public class Main {
    public static void main(String[] args){
        String datafile = "data/events.csv";
        EventFileHandler fileHandler = new EventFileHandler(datafile);
        EventManager events  = new EventManager(fileHandler);

        MemberManager members = new MemberManager(new MemberFileHandler("data/members.csv"));
        OrganizationManager orgs = new OrganizationManager("data/orginizations.txt");

        ConsoleUI ui = new ConsoleUI(events, members, orgs);
        ui.run();
    }
}
