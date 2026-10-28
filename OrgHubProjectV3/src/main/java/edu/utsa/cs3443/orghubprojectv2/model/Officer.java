package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * Officer role: can create, update and delete events and add, edit or remove members.
 * Cannot assign roles.
 */
public class Officer extends Member{
    /**
     * Creates a new Officer.
     *
     * @param id the id
     * @param name the person's name
     * @param email the email address
     * @param orgId the id of the organization
     */
    public Officer(int id, String name, String email, int orgId){
        super(id, name, email, orgId);
    }

    /**
     * Returns the role.
     *
     * @return the role
     */
    @Override
    public String getRole() {
        return "Officer";
    }

    /**
     * Returns whether this officer.
     *
     * @return true if the condition holds, otherwise false
     */
    @Override
    public boolean isOfficer() {
        return true;
    }
}
