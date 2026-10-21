package orghub.data;

import orghub.model.Event;
import orghub.model.EventType;

import java.nio.file.Path;
import java.util.List;

/** Stores events in events.csv: id,title,date,time,location,description,type,organizer. */
public class CsvEventRepository extends CsvRepository<Event> {

    public CsvEventRepository(Path file) {
        super(file);
    }

    @Override
    protected String[] header() {
        return new String[]{"id", "title", "date", "time", "location", "description", "type", "organizer"};
    }

    @Override
    protected Event fromFields(List<String> f) {
        if (f.size() != 8) {
            throw new IllegalArgumentException("expected 8 columns but found " + f.size());
        }
        return EventType.fromLabel(f.get(6)).create(
                Integer.parseInt(f.get(0).trim()), f.get(1),
                Event.parseDate(f.get(2)), Event.parseTime(f.get(3)),
                f.get(4), f.get(5), f.get(7));
    }

    @Override
    protected String[] toFields(Event event) {
        return event.toCsvFields();
    }
}
