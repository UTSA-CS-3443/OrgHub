package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.Event;
import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.Member;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.OrganizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private EventManager eventManager;
    private MemberManager memberManager;
    private OrganizationManager orgManager;
    private Scanner scan;

    // who is logged in, and in which organization (one Member row = one organization)
    private Member currentUser;

    public ConsoleUI(EventManager eventManager, MemberManager memberManager, OrganizationManager orgManager) {
        this.eventManager = eventManager;
        this.memberManager = memberManager;
        this.orgManager = orgManager;
        this.scan = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("\n=== OrgHub ===");
            System.out.println("1) Log in");
            System.out.println("0) Exit");
            String choice = ask("Choose: ");

            if (choice.equals("1")) {
                currentUser = login();
                if (currentUser != null) {
                    mainMenu();
                }
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private Member login() {
        String email = ask("Email: ");
        List<Member> memberships = memberManager.findByEmail(email);
        if (memberships.isEmpty()) {
            System.out.println("No account found for that email.");
            return null;
        }
        return chooseMembership(memberships);
    }

    private Member chooseMembership(List<Member> memberships) {
        if (memberships.size() == 1) {
            return memberships.get(0);
        }
        System.out.println("Choose an organization:");
        for (int i = 0; i < memberships.size(); i++) {
            Member m = memberships.get(i);
            System.out.println((i + 1) + ") " + orgManager.getName(m.getOrgId()) + " - " + m.getRole());
        }
        int pick = askId("Number: ");
        if (pick < 1 || pick > memberships.size()) {
            System.out.println("Invalid choice.");
            return null;
        }
        return memberships.get(pick - 1);
    }

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
                case "0": loggedIn = false; currentUser = null; System.out.println("Signed out."); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private boolean officerOnly() {
        if (!currentUser.isOfficer()) {
            System.out.println("Only officers can do that.");
            return false;
        }
        return true;
    }


    private String ask(String prompt) {
        System.out.print(prompt);
        return scan.nextLine().trim();
    }

    private int askId(String prompt) {
        try {
            return Integer.parseInt(ask(prompt));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void printEvents(List<Event> list) {
        if (list.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        for (Event e : list) {
            System.out.println(e);
        }
    }

    private void showOverview() {
        List<Integer> orgIds = new ArrayList<>();
        for (Member m : memberManager.findByEmail(currentUser.getEmail())) {
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

    private void viewEvents() {
        String type = ask("Filter by type (Meeting/Workshop/Social/Volunteer, blank = all): ");
        String keyword = ask("Search title/location/organizer (blank = none): ");
        printEvents(eventManager.searchEvents(currentUser.getOrgId(), type, keyword));
    }

    private void viewMembers() {
        List<Member> list = memberManager.getMembersForOrg(currentUser.getOrgId());
        int officers = 0;
        for (Member m : list) {
            System.out.println(m);
            if (m.isOfficer()) {
                officers++;
            }
        }
        System.out.println(list.size() + " members, " + officers + " officers");
    }

    private void switchOrganization() {
        List<Member> memberships = memberManager.findByEmail(currentUser.getEmail());
        if (memberships.size() == 1) {
            System.out.println("You only belong to one organization.");
            return;
        }
        Member chosen = chooseMembership(memberships);
        if (chosen != null) {
            currentUser = chosen;
        }
    }

    private void createEvent() {
        String type = ask("Type (Meeting/Workshop/Social/Volunteer): ");
        String title = ask("Title: ");
        String date = ask("Date (YYYY-MM-DD): ");
        String time = ask("Time (e.g. 6:00 PM): ");
        String location = ask("Location: ");
        String description = ask("Description: ");

        eventManager.createEvent(currentUser.getOrgId(), type, title, date, time,
                location, description, currentUser.getName());
        System.out.println("Event created.");
    }

    private void updateEvent() {
        printEvents(eventManager.getEventsForOrg(currentUser.getOrgId()));
        int id = askId("ID of event to update: ");

        System.out.println("(leave a field blank to keep it)");
        String title = ask("New title: ");
        String date = ask("New date (YYYY-MM-DD): ");
        String time = ask("New time: ");
        String location = ask("New location: ");
        String description = ask("New description: ");

        if (eventManager.updateEvent(id, currentUser.getOrgId(), title, date, time, location, description)) {
            System.out.println("Event updated.");
        } else {
            System.out.println("Event not found.");
        }
    }

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

    private void addMember() {
        String name = ask("Name: ");
        String email = ask("Email: ");
        String role = ask("Role (Member/Officer): ");
        if (memberManager.addMember(currentUser.getOrgId(), name, email, role)) {
            System.out.println("Member added.");
        } else {
            System.out.println("That email is already in this organization.");
        }
    }

    private void removeMember() {
        viewMembers();
        int id = askId("ID of member to remove: ");
        if (id == currentUser.getId()) {
            System.out.println("You can't remove yourself.");
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
}