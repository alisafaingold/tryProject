package BusinessLayer.Football;

import BusinessLayer.Enum.FieldType;
import BusinessLayer.Users.Asset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Field implements Asset {
    private String _id;
    private int seats;
    private String location;
    private String name;
    private FieldType fieldType;

    public Field(int seats, String location, String name, FieldType fieldType) {
        this.seats = seats;
        this.location = location;
        this.name = name;
        this.fieldType = fieldType;
    }

    @Override
    public boolean editAsset(HashMap<String, String> changes) throws Exception {
        for (Map.Entry<String, String> entry : changes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key.toLowerCase()) {
                case "seats":
                    if(seats >= 0) {
                        this.seats = Integer.valueOf(value);
                    }
                    else{
                        throw new Exception("Seats must be non negative number");
                    }
                case "location":
                    this.location = value;
                    break;
                case "name":
                    this.name = value;
                    break;
                case "fieldType":
                    this.fieldType = FieldType.valueOf(value);
                    break;
            }
        }
        return true;
    }

    public int getSeats() {
        return seats;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public HashSet<Game> getGames() {
        return null;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}
