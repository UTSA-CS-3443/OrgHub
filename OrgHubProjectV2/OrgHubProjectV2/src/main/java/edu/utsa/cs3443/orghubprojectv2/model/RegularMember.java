package edu.utsa.cs3443.orghubprojectv2.model;

public class RegularMember extends Member{
    public RegularMember(int id, String name, String email, int orgId) {
        super(id, name, email, orgId);
    }

    @Override
    public String getRole() { return "Member"; }

    @Override
    public boolean isOfficer() { return false; }
}
