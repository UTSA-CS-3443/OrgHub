package edu.utsa.cs3443.orghubprojectv2.model;

import java.io.*;
import java.util.*;

public class MemberFileHandler {
    private String filePath;

    public MemberFileHandler(String filePath){
        this.filePath = filePath;
    }

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
                members.add(MemberManager.buildMember(p[4], Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3])));
            }
        }
        catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
        return members;
    }

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
