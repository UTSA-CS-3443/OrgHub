package edu.utsa.cs3443.orghubprojectv2.model;

public class Officer extends Member{
    public Officer(int id, String name, String email, int orgId){
        super(id, name, email, orgId);
    }

    @Override
    public String getRole() {
        return "Officer";
    }

    @Override
    public boolean isOfficer() {
        return true;
    }
}
