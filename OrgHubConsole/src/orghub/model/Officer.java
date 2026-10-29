package orghub.model;

/** An organization officer (president, VP, secretary, event coordinator...). Full event access. */
public class Officer extends User {

    public Officer(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
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
