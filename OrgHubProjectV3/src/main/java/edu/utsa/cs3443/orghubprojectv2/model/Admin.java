package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * Admin role: everything an Officer can do, plus assigning and changing roles.
 * Extends Officer so it inherits the officer permissions.
 */
public class Admin extends Officer {
    /**
     * Creates a new Admin.
     *
     * @param id the id
     * @param name the person's name
     * @param email the email address
     * @param orgId the id of the organization
     */
    public Admin(int id, String name, String email, int orgId) {
        super(id, name, email, orgId);
    }

    /**
     * Returns the role.
     *
     * @return the role
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    /**
     * Returns whether this admin.
     *
     * @return true if the condition holds, otherwise false
     */
    @Override
    public boolean isAdmin() { return true; }
}
