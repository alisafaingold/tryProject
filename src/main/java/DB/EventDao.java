package DB;

import BusinessLayer.Football.Event;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class EventDao<T> implements Dao<Event> {
    private static volatile EventDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;
    private EventDao() {
        try {
            mongoConnection = MongoConnection.getInstance();
            gson = new Gson();
            settings = JsonWriterSettings.builder()
                    .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                    .objectIdConverter((value, writer) -> writer.writeString(value.toString()))
                    .build();
        }
        catch (Exception e) {
            System.out.println("constructor eror!!!");
        }
    }

    public static EventDao getInstance() {
        if (instance == null) {
            synchronized(EventDao.class) {
                if (instance == null) {
                    instance = new EventDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Event> get(String id)  {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            BasicDBObject query = new BasicDBObject("_id",new ObjectId(id));
            ArrayList<Document> dbEvents = events.find(query).into(new ArrayList<>());
            if(dbEvents.isEmpty())
                return Optional.empty();
            else {
                Event event = convertEvent(dbEvents.get(0));
                return Optional.ofNullable(event);
            }
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    @Override
    public List<Event> getAll() {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            ArrayList<Document> dbEvents = events.find().into(new ArrayList<>());
            ArrayList<Event> allEvents = new ArrayList<>();
            for (Document dbEvent : dbEvents) {
                allEvents.add(convertEvent(dbEvent));
            }
            return allEvents;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    public HashSet<Event> getAll(HashSet<String> ids) throws ClassNotFoundException {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            HashSet<Event> allEvents = new HashSet<>();

            for (String id : ids) {
                BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
                ArrayList<Document> DBevents = events.find(query).into(new ArrayList<>());
                if (!DBevents.isEmpty()) {
                    Document user = DBevents.get(0);
                    allEvents.add(convertEvent(user));
                }
            }
            return allEvents;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    @Override
    public void save(Event event)  {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            String eventJson = gson.toJson(event);
            Document newEventJson = Document.parse(eventJson);
            events.insertOne(newEventJson);
            String id = ((ObjectId) newEventJson.get("_id")).toString();
            event.set_id(id);

        } catch (Exception e) {
            //TODO what we should return
        }

    }

    @Override
    public void update(Event event) {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            Document newEventJson = Document.parse(gson.toJson(event));
            Bson filter = eq("_id", new ObjectId(event.get_id()));
            for (Map.Entry<String, Object> pair : newEventJson.entrySet()) {
                if(pair.getKey()!="_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    events.updateOne(filter, change);
                }
            }
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    @Override
    public void delete(Event event) {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            events.deleteOne(new Document("_id", new ObjectId(event.get_id())));
        } catch (Exception e) {
            //TODO what we should return
        }

    }


    // ======== Help Methods =========
    private Event convertEvent(Document event) {
        return (Event) gson.fromJson(event.toJson(settings),Event.class);
    }


    // ========= For Classes Getters =============

    public List<Event> getEvents(List<String> eventsID) throws ClassNotFoundException {
        try {
            MongoCollection<Document> events = mongoConnection.getEvents();
            ArrayList<Event> allEvents = new ArrayList<>();
            for (String eventID : eventsID) {
                BasicDBObject query = new BasicDBObject("_id",new ObjectId(eventID));
                ArrayList<Document> DBComplaint = events.find(query).into(new ArrayList<>());
                Document complaint = DBComplaint.get(0);
                allEvents.add(convertEvent(complaint));
            }
            return allEvents;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }


}
