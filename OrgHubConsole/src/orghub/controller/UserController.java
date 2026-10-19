package orghub.controller;

import orghub.data.Repository;
import orghub.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Looks up organization members and decides who is signed in. */
public class UserController {

    private final List<User> users;

    public UserController(Repository<User> repository) throws IOException {
        this.users = new ArrayList<>(repository.loadAll());
    }

    public List<User> getAllUsers() {
        return Collections.unmodifiableList(users);
    }

    /** Case-insensitive email lookup. */
    public Optional<User> findByEmail(String email) {
        String e = email == null ? "" : email.trim();
        return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(e)).findFirst();
    }
}
