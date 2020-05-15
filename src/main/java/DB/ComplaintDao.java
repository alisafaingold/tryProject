package DB;

import BusinessLayer.SystemFeatures.Complaint;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class ComplaintDao implements Dao<Complaint>{
    private static volatile ComplaintDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;
    private ComplaintDao() {
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

    public static ComplaintDao getInstance() {
        if (instance == null) {
            synchronized(ComplaintDao.class) {
                if (instance == null) {
                    instance = new ComplaintDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Complaint> get(String id)  {
        MongoCollection<Document> complaints = mongoConnection.getComplaints();
        BasicDBObject query = new BasicDBObject("_id",new ObjectId(id));
        ArrayList<Document> dbComplaints = complaints.find(query).into(new ArrayList<>());
        if(dbComplaints.isEmpty())
            return Optional.empty();
        else {
            Complaint complaint = convertComplaint(dbComplaints.get(0));
            return Optional.ofNullable(complaint);
        }
    }

    @Override
    public List<Complaint> getAll() {
        MongoCollection<Document> complaints = mongoConnection.getComplaints();
        ArrayList<Document> dbComplaints = complaints.find().into(new ArrayList<>());
        ArrayList<Complaint> allComplaints = new ArrayList<>();
        for (Document dbComplaint : dbComplaints) {
            allComplaints.add(convertComplaint(dbComplaint));
        }
        return allComplaints;
    }

    @Override
    public void save(Complaint complaint)  {
        MongoCollection<Document> complaints = mongoConnection.getComplaints();
        String complaintJson = gson.toJson(complaint);
        Document newComplaintJson = Document.parse(complaintJson);
        complaints.insertOne(newComplaintJson);
        String id = ((ObjectId) newComplaintJson.get("_id")).toString();
        complaint.set_id(id);

    }

    @Override
    public void update(Complaint complaint) {
        MongoCollection<Document> complaints = mongoConnection.getComplaints();
        Document newEventJson = Document.parse(gson.toJson(complaint));
        Bson filter = eq("_id", new ObjectId(complaint.get_id()));
        for (Map.Entry<String, Object> pair : newEventJson.entrySet()) {
            if(pair.getKey()!="_id") {
                Bson change = set(pair.getKey(), pair.getValue());
                complaints.updateOne(filter, change);
            }
        }
    }

    @Override
    public void delete(Complaint c) {
        MongoCollection<Document> complaints = mongoConnection.getComplaints();
        MongoCollection<Document> archiveComplaints = mongoConnection.getArchiveComplaints();
        //remove document to archive
        Document complaint = complaints.find(eq("_id", new ObjectId(c.get_id()))).first();
        if(complaint!=null){
            archiveComplaints.insertOne(complaint);
        }
        // delete from users
        complaints.deleteOne(new Document("_id", new ObjectId(c.get_id())));
    }


    // ======== Help Methods =========
    private Complaint convertComplaint(Document complaint) {
        return (Complaint) gson.fromJson(complaint.toJson(settings),Complaint.class);
    }


    // ========= For Classes Getters =============

    public List<Complaint> getComplaints(List<String> complaintsID) throws ClassNotFoundException {
        MongoCollection<Document> complaints = mongoConnection.getComplaints();
        ArrayList<Complaint> allUsers = new ArrayList<>();
        for (String complaintID : complaintsID) {
            BasicDBObject query = new BasicDBObject("_id",new ObjectId(complaintID));
            ArrayList<Document> DBcomplaints = complaints.find(query).into(new ArrayList<>());
            Document complaint = DBcomplaints.get(0);
            allUsers.add(convertComplaint(complaint));
        }
        return allUsers;
    }

}
