package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.OrganizationManager;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main screen controller: shows the menu for the signed-in user, reads input and calls the
 * manager classes. Holds no data itself. Permissions: everyone can view; Officers manage
 * events and members; only Admins assign roles.
 */
public class MainScreenController {
    private EventManager eventManager;
    private MemberManager memberManager;
    private OrganizationManager orgManager;
    private Scanner scan;

    // who is logged in, and in which organization (one User row = one organization)
    private User currentUser;

    /**
     * Creates a new MainScreenController.
     *
     * @param eventManager manages events
     * @param memberManager manages members and passwords
     * @param orgManager manages organizations
     */
    public MainScreenController(EventManager eventManager, MemberManager memberManager,
                                OrganizationManager orgManager) {
        this.scan = new Scanner(System.in);
        this.eventManager = eventManager;
        this.memberManager = memberManager;
        this.orgManager = orgManager;
    }

    /**
     * Runs the main menu for a user who just logged in. Returns when they sign out.
     *
     * @param user the signed-in user
     */
    public void run(User user) {
        currentUser = user;
        mainMenu();
    }

    /**
     * Menu for the signed-in user; officer-only options are hidden from regular members.
     */
    private void mainMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== " + orgManager.getName(currentUser.getOrgId()) + " ===");
            System.out.println("Signed in as " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            System.out.println("1) Overview (all my organizations)");
            System.out.println("2) View events");
            System.out.println("3) View members");
            System.out.println("4) Switch organization");
            if (currentUser.isOfficer()) {              // officers only
                System.out.println("5) Create event");
                System.out.println("6) Update event");
                System.out.println("7) Delete event");
                System.out.println("8) Add member");
                System.out.println("9) Remove member");
                System.out.println("10) Edit member");
            }
            System.out.println("0) Sign out");

            String choice = ask("Choose: ");
            switch (choice) {
                case "1": showOverview(); break;
                case "2": viewEvents(); break;
                case "3": viewMembers(); break;
                case "4": switchOrganization(); break;
                case "5": if (officerOnly()) { createEvent(); } break;
                case "6": if (officerOnly()) { updateEvent(); } break;
                case "7": if (officerOnly()) { deleteEvent(); } break;
                case "8": if (officerOnly()) { addMember(); } break;
                case "9": if (officerOnly()) { removeMember(); } break;
                case "10": if (officerOnly()) { editMember(); } break;
                case "0": loggedIn = false; currentUser = null; System.out.println("Signed out."); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Permission check before an officer action.
     *
     * @return true if the change was made, otherwise false
     */
    private boolean officerOnly() {
        if (!currentUser.isOfficer()) {
            System.out.println("Only officers can do that.");
            return false;
        }
        return true;
    }

    /**
     * Checks the date is a real YYYY-MM-DD date.
     *
     * @param date the date in YYYY-MM-DD format
     * @return true if the condition holds, otherwise false
     */
    private boolean validDate(String date) {
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks the role is Member, Officer or Admin.
     *
     * @param role the role name (Member, Officer or Admin)
     * @return true if the condition holds, otherwise false
     */
    private boolean validRole(String role) {
        return role.equalsIgnoreCase("Member") || role.equalsIgnoreCase("Officer") || role.equalsIgnoreCase("Admin");
    }

    /**
     * Prints a list of events.
     *
     * @param list the list to use
     */
    private void printEvents(List<Event> list) {
        if (list.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        for (Event e : list) {
            System.out.println(e);
        }
    }

    /**
     * Shows upcoming events from every organization the user belongs to.
     */
    private void showOverview() {
        List<Integer> orgIds = new ArrayList<>();
        for (User m : memberManager.findByEmail(currentUser.getEmail())) {
            orgIds.add(m.getOrgId());
        }
        List<Event> all = eventManager.getEventsForOrgs(orgIds);
        if (all.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        for (Event e : all) {
            System.out.println("<" + orgManager.getName(e.getOrgId()) + ">");
            System.out.println(e);
        }
    }

    /**
     * Lists the current organization's events with optional type and keyword filters.
     */
    private void viewEvents() {
        String type = ask("Filter by type (Meeting/Workshop/Social/Volunteer, blank = all): ");
        String keyword = ask("Search title/location/organizer (blank = none): ");
        printEvents(eventManager.searchEvents(currentUser.getOrgId(), type, keyword));
    }

    /**
     * Lists the current organization's members with officer counts.
     */
    private void viewMembers() {
        List<User> list = memberManager.getMembersForOrg(currentUser.getOrgId());
        int officers = 0;
        for (User m : list) {
            System.out.println(m);
            if (m.isOfficer()) {
                officers++;
            }
        }
        System.out.println(list.size() + " members, " + officers + " officers");
    }

    /**
     * Switches the active organization for people in more than one.
     */
    private void switchOrganization() {
        List<User> memberships = memberManager.findByEmail(currentUser.getEmail());
        if (memberships.size() == 1) {
            System.out.println("You only belong to one organization.");
            return;
        }
        User chosen = chooseMembership(memberships);
        if (chosen != null) {
            currentUser = chosen;
        }
    }

    /**
     * Officer action: creates an event after validating type, title and date.
     */
    private void createEvent() {
        String type = ask("Type (Meeting/Workshop/Social/Volunteer): ");
        if (!EventManager.isValidType(type)) {
            System.out.println("Type must be Meeting, Workshop, Social or Volunteer.");
            return;
        }
        String title = ask("Title: ");
        if (title.isEmpty()) {
            System.out.println("A title is required.");
            return;
        }
        String date = ask("Date (YYYY-MM-DD): ");
        if (!validDate(date)) {
            System.out.println("Invalid date. Use YYYY-MM-DD.");
            return;
        }
        String time = ask("Time (e.g. 6:00 PM): ");
        String location = ask("Location: ");
        String description = ask("Description: ");

        eventManager.createEvent(currentUser.getOrgId(), type, title, date, time,
                location, description, currentUser.getName());
        System.out.println("Event created.");
    }

    /**
     * Officer action: edits an event; blank fields are kept.
     */
    private void updateEvent() {
        printEvents(eventManager.getEventsForOrg(currentUser.getOrgId()));
        int id = askId("ID of event to update: ");

        System.out.println("(leave a field blank to keep it)");
        String title = ask("New title: ");
        String date = ask("New date (YYYY-MM-DD): ");
        if (!date.isEmpty() && !validDate(date)) {
            System.out.println("Invalid date. Use YYYY-MM-DD.");
            return;
        }
        String time = ask("New time: ");
        String location = ask("New location: ");
        String description = ask("New description: ");

        if (eventManager.updateEvent(id, currentUser.getOrgId(), title, date, time, location, description)) {
            System.out.println("Event updated.");
        } else {
            System.out.println("Event not found.");
        }
    }

    /**
     * Officer action: deletes an event after confirmation.
     */
    private void deleteEvent() {
        printEvents(eventManager.getEventsForOrg(currentUser.getOrgId()));
        int id = askId("ID of event to delete: ");

        String confirm = ask("Would you like to delete this event? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }
        if (eventManager.deleteEvent(id, currentUser.getOrgId())) {
            System.out.println("Event deleted.");
        } else {
            System.out.println("Event not found.");
        }
    }

    /**
     * Officer action: adds a member. Only Admins are asked for a role; officers always add Members.
     */
    private void addMember() {
        String name = ask("Name: ");
        String email = ask("Email: ");
        if (name.isEmpty()) {
            System.out.println("Name is required.");
            return;
        }
        if (!validEmail(email)) {
            System.out.println("Invalid email address. Example: jane.doe@example.edu");
            return;
        }
        String role = "Member";
        if (currentUser.isAdmin()) {            // only admins can assign elevated roles
            role = ask("Role (Member/Officer/Admin): ");
            if (!validRole(role)) {
                System.out.println("Role must be Member, Officer or Admin.");
                return;
            }
        }
        if (memberManager.addMember(currentUser.getOrgId(), name, email, role)) {
            System.out.println("Member added. They will set a password the first time they log in.");
        } else {
            System.out.println("That email is already in this organization.");
        }
    }

    /**
     * Officer action: edits a member's name. Only Admins change roles (never their own),
     * and only Admins may edit an Admin.
     */
    private void editMember() {
        viewMembers();
        int id = askId("ID of member to edit: ");
        User target = memberManager.findById(id, currentUser.getOrgId());
        if (target == null) {
            System.out.println("Member not found.");
            return;
        }
        if (target.isAdmin() && !currentUser.isAdmin()) {
            System.out.println("Only an Admin can edit an Admin.");
            return;
        }
        System.out.println("(leave a field blank to keep it)");
        String name = ask("New name: ");
        String role = "";
        if (currentUser.isAdmin() && id != currentUser.getId()) {   // only admins change roles, never their own
            role = ask("New role (Member/Officer/Admin): ");
            if (!role.isEmpty() && !validRole(role)) {
                System.out.println("Role must be Member, Officer or Admin.");
                return;
            }
        }
        if (memberManager.updateMember(id, currentUser.getOrgId(), name, role)) {
            if (id == currentUser.getId()) {
                currentUser = memberManager.findById(id, currentUser.getOrgId());
            }
            System.out.println("Member updated.");
        } else {
            System.out.println("Member not found.");
        }
    }

    /**
     * Officer action: removes a member. Cannot remove yourself, and only Admins may remove an Admin.
     */
    private void removeMember() {
        viewMembers();
        int id = askId("ID of member to remove: ");
        if (id == currentUser.getId()) {
            System.out.println("You can't remove yourself.");
            return;
        }
        User target = memberManager.findById(id, currentUser.getOrgId());
        if (target != null && target.isAdmin() && !currentUser.isAdmin()) {
            System.out.println("Only an Admin can remove an Admin.");
            return;
        }
        String confirm = ask("Remove this member? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }
        if (memberManager.removeMember(id, currentUser.getOrgId())) {
            System.out.println("Member removed.");
        } else {
            System.out.println("Member not found.");
        }
    }

    /**
     * Prompts and reads a line. Commas are removed because the data files are comma-separated.
     *
     * @param prompt the text shown to the user
     * @return the text value
     */
    private String ask(String prompt) {
        System.out.print(prompt);
        return scan.nextLine().trim().replace(",", " ");
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
     * Reads a whole number, or -1 if the input is not a number.
     *
     * @param prompt the text shown to the user
     * @return the number, or -1 if it is not valid
     */
    private int askId(String prompt) {
        try {
            return Integer.parseInt(ask(prompt));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Lets a person in several organizations pick which one to enter.
     *
     * @param memberships the person's memberships, one per organization
     * @return the result, or null if not found
     */
    private User chooseMembership(List<User> memberships) {
        if (memberships.size() == 1) {
            return memberships.get(0);
        }
        System.out.println("Choose an organization:");
        for (int i = 0; i < memberships.size(); i++) {
            User m = memberships.get(i);
            System.out.println((i + 1) + ") " + orgManager.getName(m.getOrgId()) + " - " + m.getRole());
        }
        int pick = askId("Number: ");
        if (pick < 1 || pick > memberships.size()) {
            System.out.println("Invalid choice.");
            return null;
        }
        return memberships.get(pick - 1);
    }
}
