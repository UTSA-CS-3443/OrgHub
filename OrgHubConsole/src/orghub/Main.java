package orghub;

import orghub.controller.EventController;
import orghub.controller.UserController;
import orghub.data.CsvEventRepository;
import orghub.data.CsvUserRepository;
import orghub.model.Organization;
import orghub.ui.ConsoleUI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * OrgHub console prototype - entry point. Wires the layers together:
 * CSV repositories -> controllers -> console UI.
 *
 * Run from the project folder (so "data/" is found), or pass a different data folder
 * as the first argument.
 */
public class Main {

    public static void main(String[] args) {
        Path dataDir = Paths.get(args.length > 0 ? args[0] : "data");
        try {
            CsvEventRepository eventRepo = new CsvEventRepository(dataDir.resolve("events.csv"));
            CsvUserRepository userRepo = new CsvUserRepository(dataDir.resolve("members.csv"));

            EventController events = new EventController(eventRepo);
            UserController users = new UserController(userRepo);
            Organization organization = Organization.load(dataDir.resolve("organization.txt"));

            // Tell the user about any malformed rows that were skipped while loading.
            eventRepo.getWarnings().forEach(w -> System.out.println("Warning: " + w));
            userRepo.getWarnings().forEach(w -> System.out.println("Warning: " + w));

            new ConsoleUI(events, users, organization).run();
        } catch (IOException e) {
            System.out.println("Could not read data files from '" + dataDir + "': " + e.getMessage());
        }
    }
}
