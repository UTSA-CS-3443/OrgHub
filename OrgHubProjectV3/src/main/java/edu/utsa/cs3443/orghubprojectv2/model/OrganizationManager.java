package edu.utsa.cs3443.orghubprojectv2.model;

import java.io.*;
import java.util.*;

/**
 * Loads organizations from orginizations.txt (id,name,email) and saves new ones.
 */
public class OrganizationManager {
    private List<Organization> organizations = new ArrayList<>();
    private String filePath;
    private boolean loadFailed = false;

    /**
     * Creates a new OrganizationManager.
     *
     * @param filePath the path of the data file
     */
    public OrganizationManager(String filePath){
        this.filePath = filePath;
        File file =  new File(filePath);
        if (!file.exists()) {
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine()) != null){
                if(line.trim().isEmpty()){
                    continue;
                }
                String[] p = line.split(",", -1);
                organizations.add(new Organization(Integer.parseInt(p[0]), p[1], p[2]));
            }
        }
        catch(IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            loadFailed = true;
            System.out.println("Error loading organizations: " + e.getMessage());
        }
    }

    /**
     * True when the file loaded fine but holds no organizations (triggers first-organization setup).
     *
     * @return true when the file loaded fine but holds no organizations (triggers first-organization setup), otherwise false
     */
    public boolean isEmpty() {
        return organizations.isEmpty() && !loadFailed;
    }

    /**
     * Adds an organization with the next free id, saves the file and returns the new id.
     *
     * @param name the person's name
     * @param email the email address
     * @return the integer value
     */
    public int addOrganization(String name, String email) {
        int max = 0;
        for (Organization o : organizations) {
            if (o.getId() > max) {
                max = o.getId();
            }
        }
        organizations.add(new Organization(max + 1, name, email));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Organization o : organizations) {
                bw.write(o.getId() + "," + o.getName() + "," + o.getContactEmail() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving organizations: " + e.getMessage());
        }
        return max + 1;
    }

    /**
     * Finds an organization by id, or null.
     *
     * @param id the id
     * @return the result, or null if not found
     */
    public Organization findById (int id){
        for (Organization o : organizations) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }

    /**
     * Organization name for an id, or "Unknown".
     *
     * @param id the id
     * @return the text value
     */
    public String getName(int id){
        Organization o = findById(id);
        return o == null ? "Unknown" : o.getName();
    }

    /**
     * All organizations.
     *
     * @return the matching list (empty if none)
     */
    public List<Organization> getAll(){
        return organizations;
    }
}
