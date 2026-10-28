package edu.utsa.cs3443.orghubprojectv2.model;

import java.io.*;
import java.util.*;

/**
 * Reads and writes members.csv (id,name,email,orgId,role,passwordHash).
 * The password column is optional so older 5-column rows still load.
 */
public class MemberFileHandler {
    private String filePath;

    /**
     * Creates a new MemberFileHandler.
     *
     * @param filePath the path of the data file
     */
    public MemberFileHandler(String filePath){
        this.filePath = filePath;
    }

    /**
     * Loads all members from the file; returns an empty list if the file is missing.
     *
     * @return the matching list (empty if none)
     */
    public List<Member> load(){
        List<Member> members = new ArrayList<>();
        File file =  new File(filePath);
        if(!file.exists()){
            return members;
        }
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line =  br.readLine()) != null){
                if(line.trim().isEmpty()){
                    continue;
                }
                String[] p = line.split(",", -1);
                Member m = MemberManager.buildMember(p[4], Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]));
                if (p.length > 5) {
                    m.setPasswordHash(p[5]);   // blank = no password set yet
                }
                members.add(m);
            }
        }
        catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Rewrites the whole file from the given list.
     *
     * @param members the list of members
     */
    public void save(List<Member> members) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Member m : members) {
                bw.write(m.toCsv() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }
}
