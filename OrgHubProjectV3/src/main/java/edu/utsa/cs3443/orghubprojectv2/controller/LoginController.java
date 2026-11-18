package edu.utsa.cs3443.orghubprojectv2.controller;

import edu.utsa.cs3443.orghubprojectv2.model.EventManager;
import edu.utsa.cs3443.orghubprojectv2.model.MemberManager;
import edu.utsa.cs3443.orghubprojectv2.model.Organization;
import edu.utsa.cs3443.orghubprojectv2.model.OrganizationManager;
import edu.utsa.cs3443.orghubprojectv2.model.User;

import java.util.List;
import java.util.Scanner;

/**
 * Login controller: first-run setup, the Log in / Create account / Exit menu and password handling.
 * After a successful login it hands the signed-in user to the MainScreenController.
 * Holds no data itself.
 */
public class LoginController {
    private MemberManager memberManager;
    private OrganizationManager orgManager;
    private MainScreenController mainScreen;
    private Scanner scan;

    /**
     * Creates a new LoginController (and the MainScreenController it hands users to).
     *
     * @param eventManager manages events
     * @param memberManager manages members and passwords
     * @param orgManager manages organizations
     */
    public LoginController(EventManager eventManager, MemberManager memberManager, OrganizationManager orgManager) {
        this.scan = new Scanner(System.in);
        this.memberManager = memberManager;
        this.orgManager = orgManager;
        this.mainScreen = new MainScreenController(eventManager, memberManager, orgManager);
    }

    /**
     * Start-up flow. First-run setup (first organization, then first Admin account) must succeed
     * before the login menu appears. Then loops on Log in / Create account / Exit.
     */
    public void run() {
        while (orgManager.isEmpty()) {
            System.out.println("\nNo organizations exist yet. Create the first organization to continue.");
            String orgName = ask("Organization name: ");
            String orgEmail = ask("Organization email: ");
            if (orgName.isEmpty() || !validEmail(orgEmail)) {
                System.out.println("A name and a valid organization email (e.g. club@example.edu) are required.");
                continue;
            }
            orgManager.addOrganization(orgName, orgEmail);
        }
        while (memberManager.isEmpty()) {
            System.out.println("\nNo accounts exist yet. Create the first Admin account to continue.");
            createAccount("Admin");
        }

        while (true) {
            System.out.println("\n=== OrgHub ===");
            System.out.println("1) Log in");
            System.out.println("2) Create account");
            System.out.println("0) Exit");
            String choice = ask("Choose: ");

            if (choice.equals("1")) {
                User user = login();
                if (user != null) {
                    mainScreen.run(user);
                }
            } else if (choice.equals("2")) {
                createAccount("Member");
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Email + password login. A person with no password yet creates one on first login.
     *
     * @return the result, or null if not found
     */
    private User login() {
        String email = ask("Email: ");
        List<User> memberships = memberManager.findByEmail(email);
        if (memberships.isEmpty()) {
            System.out.println("No account found for that email. Choose 'Create account' to sign up.");
            return null;
        }
        if (!memberManager.hasPassword(email)) {
            // account was added by an officer (or predates passwords): set it on first login
            System.out.println("Welcome! This is your first login, so please create a password.");
            String pw = askNewPassword();
            if (pw == null) {
                return null;
            }
            memberManager.setPassword(email, pw);
        } else if (!memberManager.checkPassword(email, askPassword("Password: "))) {
            System.out.println("Incorrect password.");
            return null;
        }
        return chooseMembership(memberships);
    }

    /**
     * Self sign-up. New accounts are always Members, except the first-ever account (Admin).
     *
     * @param role the role name (Member, Officer or Admin)
     */
    private void createAccount(String role) {
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
        System.out.println("Organizations:");
        for (Organization o : orgManager.getAll()) {
            System.out.println(o.getId() + ") " + o.getName());
        }
        int orgId = askId("Organization number: ");
        if (orgManager.findById(orgId) == null) {
            System.out.println("Invalid organization.");
            return;
        }
        // someone who already has a password (from another organization) must use the same one
        String pw;
        if (memberManager.hasPassword(email)) {
            pw = askPassword("You already have an account. Enter your existing password: ");
            if (!memberManager.checkPassword(email, pw)) {
                System.out.println("Incorrect password.");
                return;
            }
        } else {
            pw = askNewPassword();
            if (pw == null) {
                return;
            }
        }
        // new accounts are regular members (an Admin can promote them later),
        // except the very first account, which is the Admin
        if (memberManager.addMember(orgId, name, email, role)) {
            memberManager.setPassword(email, pw);
            System.out.println("Account created. You can now log in.");
        } else {
            System.out.println("That email is already in this organization.");
        }
    }

    /**
     * Prompts and reads a line. Commas are removed because the data files are comma-separated.
     *
     * @param prompt the text shown to the user
     * @return the text value
     */
    private String askPassword(String prompt) {
        java.io.Console console = System.console();
        if (console != null) {
            char[] pw = console.readPassword(prompt);
            return pw == null ? "" : new String(pw);
        }
        System.out.print(prompt);
        return scan.nextLine();
    }

    // asks twice and returns the new password, or null if it was rejected
    /**
     * Prompts and reads a line. Commas are removed because the data files are comma-separated.
     *
     * @return the text value
     */
    private String askNewPassword() {
        String pw = askPassword("Password (6+ characters): ");
        if (pw.length() < 6) {
            System.out.println("Password must be at least 6 characters.");
            return null;
        }
        if (!pw.equals(askPassword("Confirm password: "))) {
            System.out.println("Passwords do not match.");
            return null;
        }
        return pw;
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
