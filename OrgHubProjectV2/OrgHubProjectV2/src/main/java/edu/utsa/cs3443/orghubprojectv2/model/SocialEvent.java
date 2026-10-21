package edu.utsa.cs3443.orghubprojectv2.model;

public class SocialEvent extends Event{

    public SocialEvent(int id, int orgId, String title, String date, String time,
                       String location, String description, String organizer) {
        super(id, orgId, title, date, time, location, description, organizer);
    }

    @Override
    public String getEventType() {
        return "Social";
    }
}
