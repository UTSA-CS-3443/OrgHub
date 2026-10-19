package orghub.model;

/** An organization officer (president, VP, secretary, event coordinator...). Full event access. */
public class Officer extends User {

    public Officer(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "Officer";
    }

    @Override
    public boolean canManageEvents() {
        return true;
    }
}
