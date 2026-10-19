package orghub.model;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Basic organization information, loaded from data/organization.txt. */
public class Organization {

    private final String name;
    private final String contactEmail;

    public Organization(String name, String contactEmail) {
        this.name = name;
        this.contactEmail = contactEmail;
    }

    public String getName()         { return name; }
    public String getContactEmail() { return contactEmail; }

    /** Reads name= and contactEmail= from a key=value text file; falls back to defaults if missing. */
    public static Organization load(Path file) {
        Properties p = new Properties();
        if (Files.exists(file)) {
            try (Reader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                p.load(in);
            } catch (IOException e) {
                // use defaults below
            }
        }
        return new Organization(p.getProperty("name", "My Organization"),
                p.getProperty("contactEmail", "not set"));
    }
}
