package edu.utsa.cs3443.orghubprojectv2.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Holds every Member in memory and keeps members.csv in sync after each change.
 * Also handles password hashing and checking. Passwords are shared by email across organizations.
 */
public class MemberManager {
    private List<Member> members;
    private MemberFileHandler fileHandler;

    /**
     * Creates a new MemberManager.
     *
     * @param fileHandler reads and writes the data file
     */
    public MemberManager(MemberFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.members = fileHandler.load();
    }

    /**
     * Factory: creates the right Member subclass for a role name (defaults to RegularMember).
     *
     * @param role the role name (Member, Officer or Admin)
     * @param id the id
     * @param name the person's name
     * @param email the email address
     * @param orgId the id of the organization
     * @return a new object of the matching subclass
     */
    public static Member buildMember(String role, int id, String name, String email, int orgId) {
        if (role.equalsIgnoreCase("admin")) {
            return new Admin(id, name, email, orgId);
        }
        if (role.equalsIgnoreCase("officer")) {
            return new Officer(id, name, email, orgId);
        }
        return new RegularMember(id, name, email, orgId);
    }

    /**
     * Next free member id (highest existing id + 1).
     *
     * @return the next unused id
     */
    private int nextId() {
        int max = 0;
        for (Member m : members) {
            if (m.getId() > max) {
                max = m.getId();
            }
        }
        return max + 1;
    }

    /**
     * Returns the SHA-256 hash of a password as a hex string. Plain passwords are never stored.
     *
     * @param password the plain-text password
     * @return the text value
     */
    public static String hash(String password) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // one password per email, shared by all of that person's organizations
    /**
     * True if this email has already set a password.
     *
     * @param email the email address
     * @return true if this email has already set a password, otherwise false
     */
    public boolean hasPassword(String email) {
        for (Member m : findByEmail(email)) {
            if (!m.getPasswordHash().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the password matches the stored hash for this email.
     *
     * @param email the email address
     * @param password the plain-text password
     * @return true if the password matches the stored hash for this email, otherwise false
     */
    public boolean checkPassword(String email, String password) {
        String h = hash(password);
        for (Member m : findByEmail(email)) {
            if (m.getPasswordHash().equals(h)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves a password hash on every membership row that uses this email.
     *
     * @param email the email address
     * @param password the plain-text password
     */
    public void setPassword(String email, String password) {
        String h = hash(password);
        for (Member m : findByEmail(email)) {
            m.setPasswordHash(h);
        }
        fileHandler.save(members);
    }

    /**
     * Finds a member by id within one organization, or null.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @return the result, or null if not found
     */
    public Member findById(int id, int orgId) {
        for (Member m : members) {
            if (m.getId() == id && m.getOrgId() == orgId) {
                return m;
            }
        }
        return null;
    }

    /**
     * True when there are no accounts at all (triggers first-Admin setup).
     *
     * @return true when there are no accounts at all (triggers first-Admin setup), otherwise false
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * All memberships (one per organization) for an email, ignoring case.
     *
     * @param email the email address
     * @return the matching list (empty if none)
     */
    public List<Member> findByEmail(String email) {
        List<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getEmail().equalsIgnoreCase(email)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * All members of one organization.
     *
     * @param orgId the id of the organization
     * @return the matching list (empty if none)
     */
    public List<Member> getMembersForOrg(int orgId) {
        List<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getOrgId() == orgId) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Adds a member to an organization. Returns false if the email is already in it.
     *
     * @param orgId the id of the organization
     * @param name the person's name
     * @param email the email address
     * @param role the role name (Member, Officer or Admin)
     * @return true if the change was made, otherwise false
     */
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

    /**
     * Changes a member's name and/or role (blank keeps the old value) and saves.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @param name the person's name
     * @param role the role name (Member, Officer or Admin)
     * @return true if the change was made, otherwise false
     */
    public boolean updateMember(int id, int orgId, String name, String role) {
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            if (m.getId() == id && m.getOrgId() == orgId) {
                String newName = name.isEmpty() ? m.getName() : name;
                String newRole = role.isEmpty() ? m.getRole() : role;
                Member updated = buildMember(newRole, id, newName, m.getEmail(), orgId);
                updated.setPasswordHash(m.getPasswordHash());
                members.set(i, updated);
                fileHandler.save(members);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a member from one organization and saves.
     *
     * @param id the id
     * @param orgId the id of the organization
     * @return true if the change was made, otherwise false
     */
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
