package edu.utsa.cs3443.orghubprojectv2.model;

import java.io.*;
import java.util.*;

/**
 * Reads and writes events.csv (no header row).
 */
public class EventFileHandler {
    private String filePath;

    /**
     * Creates a new EventFileHandler.
     *
     * @param filePath the path of the data file
     */
    public EventFileHandler(String filePath){
        this.filePath = filePath;
    }

    /**
     * Loads all events; returns an empty list if the file is missing.
     *
     * @return the matching list (empty if none)
     */
    public List<Event> load(){
        List<Event> events = new ArrayList<>();
        File file = new File(filePath);

        if(!file.exists()){
            return events;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()){
                    continue;
                }
                String[] p = line.split(",", -1);
                int id = Integer.parseInt(p[0]);
                int orgId = Integer.parseInt(p[1]);
                events.add(EventManager.buildEvent(p[7], id, orgId, p[2], p[3], p[4], p[5], p[6], p[8]));
            }
        }
        catch(IOException | NumberFormatException | ArrayIndexOutOfBoundsException e){
            System.out.println("Error loading events: "+ e.getMessage());
        }
        return events;
    }

    /**
     * Rewrites the whole file from the given list.
     *
     * @param events the list of events
     */
    public void save(List<Event> events){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))){
            for(Event e: events){
                bw.write(e.toCsv()+"\n");
            }
        }
        catch(IOException e){
            System.out.println("Error saving events: "+ e.getMessage());
        }
    }
}
