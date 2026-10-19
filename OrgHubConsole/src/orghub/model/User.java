package orghub.model;

/**
 * Abstract base class for people in the organization. Officers and Members share
 * identity data but differ in what they are allowed to do ({@link #canManageEvents()}).
 */
public abstract class User {

    private final int id;
    private final String name;
    private final String email;

    protected User(int id, String name, String email) {
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Name and email are required.");
        }
        this.id = id;
        this.name = name.trim();
        this.email = email.trim();
    }

    public int getId()       { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }

    /** "Officer" or "Member" - also the value stored in members.csv. */
    public abstract String getRole();

    /** Officers may create, update and delete events; members may only view them. */
    public abstract boolean canManageEvents();

    /** Builds the right subclass from the role text in members.csv. */
    public static User create(int id, String name, String email, String role) {
        String r = role == null ? "" : role.trim();
        if (r.equalsIgnoreCase("Officer")) return new Officer(id, name, email);
        if (r.equalsIgnoreCase("Member"))  return new Member(id, name, email);
        throw new IllegalArgumentException("Unknown role: " + role);
    }

    public String[] toCsvFields() {
        return new String[]{String.valueOf(id), name, email, getRole()};
    }

    @Override
    public String toString() {
        return name + " <" + email + "> (" + getRole() + ")";
    }
}
