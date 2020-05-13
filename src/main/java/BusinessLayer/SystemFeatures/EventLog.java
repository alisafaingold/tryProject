package BusinessLayer.SystemFeatures;

import BusinessLayer.Football.Event;
import BusinessLayer.Football.Game;

import java.util.HashSet;

public class EventLog {
    private Game game;
    private HashSet<Event> events;
    private String _id;

    public EventLog(Game game) {
        this.game = game;
        events = new HashSet<>();
    }
     public boolean addEvent(Event event){
         events.add(event);
         return true;
     }

    public HashSet<Event> getEvents() {
        return events;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
