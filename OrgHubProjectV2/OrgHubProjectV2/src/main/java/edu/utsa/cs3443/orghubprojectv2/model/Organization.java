package edu.utsa.cs3443.orghubprojectv2.model;

public class Organization {
    private int id;
    private String name;
    private String contactEmail;

    public Organization(int id, String name, String contactEmail) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getContactEmail() {
        return contactEmail;
    }

    @Override
    public String toString() {
        return name + " (" + contactEmail + ")";

    }
}
