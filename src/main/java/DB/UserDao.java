package DB;

import BusinessLayer.Users.Referee;
import BusinessLayer.Users.SignedUser;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class UserDao<T> implements Dao<SignedUser> {
    private static volatile UserDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;

    private UserDao() {
        try {
            mongoConnection = MongoConnection.getInstance();
            gson = new Gson();
            settings = JsonWriterSettings.builder()
                    .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                    .build();
        } catch (Exception e) {
            System.out.println("constructor eror!!!");
        }
    }

    public static UserDao getInstance() {
        if (instance == null) {
            synchronized (UserDao.class) {
                if (instance == null) {
                    instance = new UserDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<SignedUser> get(String id) throws ClassNotFoundException {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
            ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
            if(DBusers.isEmpty())
                return Optional.empty();
            return Optional.ofNullable(convertUserDocument(DBusers.get(0)));
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }


    }

    public HashSet<SignedUser> getAll(HashSet<String> ids) throws ClassNotFoundException {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            HashSet<SignedUser> allUsers = new HashSet<>();

            for (String id : ids) {
                BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
                ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
                if (!DBusers.isEmpty()) {
                    Document user = DBusers.get(0);
                    allUsers.add(convertUserDocument(user));
                }
            }
            return allUsers;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    @Override
    public List<SignedUser> getAll() throws ClassNotFoundException {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            ArrayList<Document> dbUsers = users.find().into(new ArrayList<>());
            ArrayList<SignedUser> allUsers = new ArrayList<>();
            for (Document dbUser : dbUsers)
                allUsers.add(convertUserDocument(dbUser));
            return allUsers;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }


    }

    @Override
    public void save(SignedUser signedUser) {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            String userJson = gson.toJson(signedUser);
            Document newUser = new Document();
            Document newUserJson = Document.parse(userJson);
            newUser.put("json", newUserJson);
            newUser.put("type", signedUser.getClass().toString().substring(6));
            users.insertOne(newUser);
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    @Override
    public void update(SignedUser signedUser) {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            Bson filter = eq("_id", new ObjectId(signedUser.get_id()));
            Bson change = set("json", Document.parse(gson.toJson(signedUser)));
            users.updateOne(filter, change);
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    @Override
    public void delete(SignedUser signedUser) {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            MongoCollection<Document> archiveUsers = mongoConnection.getArchiveUsers();
            //remove document to archive
            Document user = users.find(eq("_id", new ObjectId(signedUser.get_id()))).first();
            if (user != null) {
                archiveUsers.insertOne(user);
            }
            // delete from users
            users.deleteOne(new Document("_id", new ObjectId(signedUser.get_id())));
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    public SignedUser getByEmail(String email) throws ClassNotFoundException {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            BasicDBObject query = new BasicDBObject("json.email", email);
            ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
            if (!DBusers.isEmpty()) {
                Document user = DBusers.get(0);
                return convertUserDocument(user);
            } else
                return null;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }


    // ======== Help Methods =========
    private SignedUser convertUserDocument(Document dbUser) throws ClassNotFoundException {
        try {
            String type = dbUser.getString("type");
            Document json = dbUser.get("json", Document.class);
            Class userClass = Class.forName(type);
            Object id = dbUser.get("_id");
            SignedUser signedUser = (SignedUser) gson.fromJson(json.toJson(settings), userClass);
            signedUser.set_id(id.toString());
            return signedUser;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }


    //Validation Function
    public ArrayList<SignedUser> getById(String id) throws ClassNotFoundException {
        try {
            MongoCollection<Document> users = mongoConnection.getUsers();
            BasicDBObject query = new BasicDBObject("json.id", id);
            ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
            ArrayList<SignedUser> allUsersWithName = new ArrayList<>();
            for (Document dbUser : DBusers)
                allUsersWithName.add(convertUserDocument(dbUser));

            return allUsersWithName;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    public ArrayList<Referee> getRefereesThatFitToTraining(int numTraining) throws ClassNotFoundException {
         try {
             MongoCollection<Document> users = mongoConnection.getUsers();
             ArrayList<Referee> allReferees = new ArrayList<>();
             BasicDBList and = new BasicDBList();
             and.add(new BasicDBObject("type", "Referee"));
             and.add(new BasicDBObject("json.refereeTraining", numTraining));
             BasicDBObject query = new BasicDBObject("$and", and);
             List<Document> into = users.find(query).into(new ArrayList<>());
             if (!into.isEmpty()) {
                 for (Document document : into) {
                     allReferees.add((Referee) convertUserDocument(document));
                 }
             } else {
                 return null;
             }
             return allReferees;
         } catch (Exception e) {
             //TODO what we should return
             return null;
         }
    }


}











