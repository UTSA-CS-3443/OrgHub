package edu.utsa.cs3443.orghubprojectv2.model;

import java.util.*;

public class MemberManager {
    private List<Member> members;
    private MemberFileHandler fileHandler;

    public MemberManager(MemberFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.members = fileHandler.load();
    }

    public static Member buildMember(String role, int id, String name, String email, int orgId) {
        if (role.equalsIgnoreCase("officer")) {
            return new Officer(id, name, email, orgId);
        }
        return new RegularMember(id, name, email, orgId);
    }

    private int nextId() {
        int max = 0;
        for (Member m : members) {
            if (m.getId() > max) {
                max = m.getId();
            }
        }
        return max + 1;
    }

    public List<Member> findByEmail(String email) {
        List<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getEmail().equalsIgnoreCase(email)) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Member> getMembersForOrg(int orgId) {
        List<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getOrgId() == orgId) {
                result.add(m);
            }
        }
        return result;
    }

    public boolean addMember(int orgId, String name, String email, String role) {
        for (Member m : members) {
            if (m.getOrgId() == orgId && m.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        members.add(buildMember(role, nextId(), name, email, orgId));
        fileHandler.save(members);
        return true;
    }

    public boolean removeMember(int id, int orgId) {
        for (Member m : members) {
            if (m.getId() == id && m.getOrgId() == orgId) {
                members.remove(m);
                fileHandler.save(members);
                return true;
            }
        }
        return false;
    }
}
