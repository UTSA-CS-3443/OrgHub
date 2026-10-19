package orghub.model;

/** A regular organization member. Read-only access to events. */
public class Member extends User {

    public Member(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "Member";
    }

    @Override
    public boolean canManageEvents() {
        return false;
    }
}
