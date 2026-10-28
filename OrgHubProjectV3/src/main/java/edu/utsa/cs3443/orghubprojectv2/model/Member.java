package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * A person's membership in ONE organization (one row of members.csv).
 * The same email can appear once per organization, each with its own role.
 * Subclasses (RegularMember, Officer, Admin) supply the role and permissions.
 */
public abstract class Member {

    private int id;
    private String name;
    private String email;
    private int orgId;
    private String passwordHash = "";

    /**
     * Creates a new Member.
     *
     * @param id the id
     * @param name the person's name
     * @param email the email address
     * @param orgId the id of the organization
     */
    public Member(int id, String name, String email, int orgId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.orgId = orgId;
    }

    /**
     * Role name saved to the data file and shown in the UI (Member, Officer or Admin).
     *
     * @return the text value
     */
    public abstract String getRole();
    /**
     * True if this role may manage events and members (Officers and Admins).
     *
     * @return true if this role may manage events and members (Officers and Admins), otherwise false
     */
    public abstract boolean isOfficer();
    /**
     * True only for Admins, who alone may assign or change roles.
     *
     * @return true only for Admins, who alone may assign or change roles, otherwise false
     */
    public boolean isAdmin() { return false; }

    /**
     * Returns the id.
     *
     * @return the id
     */
    public int getId() { return id; }
    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() { return name; }
    /**
     * Returns the email.
     *
     * @return the email
     */
    public String getEmail() { return email; }
    /**
     * Returns the org id.
     *
     * @return the org id
     */
    public int getOrgId() { return orgId; }
    /**
     * SHA-256 hash of the password; empty until the person sets one on first login.
     *
     * @return the text value
     */
    public String getPasswordHash() { return passwordHash; }
    /**
     * Sets the password hash.
     *
     * @param hash the password hash
     */
    public void setPasswordHash(String hash) { this.passwordHash = hash; }

    /**
     * One line of members.csv: id,name,email,orgId,role,passwordHash.
     *
     * @return the text value
     */
    public String toCsv() {
        return id + "," + name + "," + email + "," + orgId + "," + getRole() + "," + passwordHash;
    }

    /**
     * Returns a readable one-line description for display.
     *
     * @return the text value
     */
    @Override
    public String toString() {
        return "[" + id + "] " + name + " <" + email + "> - " + getRole();
    }
}
