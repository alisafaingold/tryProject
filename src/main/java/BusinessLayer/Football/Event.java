package BusinessLayer.Football;

import BusinessLayer.Enum.EventType;
import BusinessLayer.Users.Referee;

public class Event {
    private EventType eventType;
    private int eventMinute;
    private String description;
    private Referee referee;
    private String _id;

    public Event(EventType eventType, int eventMinute, String description, Referee referee) {
        this.description=description;
        this.eventMinute=eventMinute;
        this.eventType=eventType;
        this.referee=referee;
    }

    public void editEventMinute(int eventMinute) {
        this.eventMinute = eventMinute;
    }

    public void editDescription(String description) {
        this.description = description;
    }

    public int getEventMinute() {
        return eventMinute;
    }

    public String getDescription() {
        return description;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
