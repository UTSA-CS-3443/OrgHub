package orghub.data;

import orghub.model.User;

import java.nio.file.Path;
import java.util.List;

/** Stores people in members.csv: id,name,email,role. */
public class CsvUserRepository extends CsvRepository<User> {

    public CsvUserRepository(Path file) {
        super(file);
    }

    @Override
    protected String[] header() {
        return new String[]{"id", "name", "email", "role"};
    }

    @Override
    protected User fromFields(List<String> f) {
        if (f.size() != 4) {
            throw new IllegalArgumentException("expected 4 columns but found " + f.size());
        }
        return User.create(Integer.parseInt(f.get(0).trim()), f.get(1), f.get(2), f.get(3));
    }

    @Override
    protected String[] toFields(User user) {
        return user.toCsvFields();
    }
}
