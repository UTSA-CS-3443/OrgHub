package edu.utsa.cs3443.orghubprojectv2.model;

import java.io.*;
import java.util.*;

public class OrganizationManager {
    private List<Organization> organizations = new ArrayList<>();

    public OrganizationManager(String filePath){
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
            System.out.println("Error loading organizations: " + e.getMessage());
        }
    }

    public Organization findById (int id){
        for (Organization o : organizations) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }

    public String getName(int id){
        Organization o = findById(id);
        return o == null ? "Unknown" : o.getName();
    }

    public List<Organization> getAll(){
        return organizations;
    }
}
