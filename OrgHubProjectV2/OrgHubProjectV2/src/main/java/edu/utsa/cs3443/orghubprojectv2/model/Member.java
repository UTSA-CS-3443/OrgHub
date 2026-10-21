package edu.utsa.cs3443.orghubprojectv2.model;

public abstract class Member {

    private int id;
    private String name;
    private String email;
    private int orgId;

    public Member(int id, String name, String email, int orgId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.orgId = orgId;
    }

    public abstract String getRole();
    public abstract boolean isOfficer();

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getOrgId() { return orgId; }

    public String toCsv() {
        return id + "," + name + "," + email + "," + orgId + "," + getRole();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " <" + email + "> - " + getRole();
    }
}
