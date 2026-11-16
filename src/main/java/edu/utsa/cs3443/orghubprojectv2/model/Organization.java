package edu.utsa.cs3443.orghubprojectv2.model;

/**
 * An organization (club) with an id, name and contact email.
 */
public class Organization {
    private int id;
    private String name;
    private String contactEmail;

    /**
     * Creates a new Organization.
     *
     * @param id the id
     * @param name the person's name
     * @param contactEmail the organization's contact email
     */
    public Organization(int id, String name, String contactEmail) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
    }

    /**
     * Returns the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * Returns the contact email.
     *
     * @return the contact email
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Returns a readable one-line description for display.
     *
     * @return the text value
     */
    @Override
    public String toString() {
        return name + " (" + contactEmail + ")";

    }
}
