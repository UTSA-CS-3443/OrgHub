package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * Standard role: can view events and members but cannot change anything.
 */
public class RegularMember extends Member{
    /**
     * Creates a new RegularMember.
     *
     * @param id the id
     * @param name the person's name
     * @param email the email address
     * @param orgId the id of the organization
     */
    public RegularMember(int id, String name, String email, int orgId) {
        super(id, name, email, orgId);
    }

    /**
     * Returns the role.
     *
     * @return the role
     */
    @Override
    public String getRole() { return "Member"; }

    /**
     * Returns whether this officer.
     *
     * @return true if the condition holds, otherwise false
     */
    @Override
    public boolean isOfficer() { return false; }
}
