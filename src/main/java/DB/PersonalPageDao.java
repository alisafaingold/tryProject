package DB;

import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.Users.Fan;
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

public class PersonalPageDao<T> implements Dao<PersonalPage> {
    private static volatile PersonalPageDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;

    private PersonalPageDao() {
        try {
            mongoConnection = MongoConnection.getInstance();
            gson = new Gson();
            settings = JsonWriterSettings.builder()
                    .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                    .build();
        }
        catch (Exception e) {
            System.out.println("constructor eror!!!");
        }
    }

    public static PersonalPageDao getInstance() {
        if (instance == null) {
            synchronized(PersonalPageDao.class) {
                if (instance == null) {
                    instance = new PersonalPageDao();
                }
            }
        }
        return instance;
    }



    @Override
    public Optional<PersonalPage> get(String id) throws ClassNotFoundException {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            BasicDBObject query = new BasicDBObject("_id",new ObjectId(id));
            ArrayList<Document> DBPersonalPages = personalPages.find(query).into(new ArrayList<>());
            if(!DBPersonalPages.isEmpty()){
                Document personalPage = DBPersonalPages.get(0);
                return Optional.ofNullable(convertPersonalPageDocument(personalPage));
            }
            else
                return Optional.empty();
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    @Override
    public List<PersonalPage> getAll() throws ClassNotFoundException {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            ArrayList<Document> DBPersonalPages = personalPages.find().into(new ArrayList<>());
            ArrayList<PersonalPage> allPersonalPages = new ArrayList<>();
            for (Document dbPersonalPage : DBPersonalPages)
                allPersonalPages.add(convertPersonalPageDocument(dbPersonalPage));
            return allPersonalPages;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }

    }

    @Override
    public void save(PersonalPage personalPage) {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            String personalPageJson = gson.toJson(personalPage);
            Document newPersonalPage = new Document();
            Document newUserJson = Document.parse(personalPageJson);
            newPersonalPage.put("json",newUserJson);
            newPersonalPage.put("type",personalPage.getClass().toString().substring(6));
            personalPages.insertOne(newPersonalPage);
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    @Override
    public void update(PersonalPage personalPage) {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            Bson filter = eq("_id", new ObjectId(personalPage.get_id()));
            Bson change = set("json", Document.parse(gson.toJson(personalPage)));
            personalPages.updateOne(filter, change);
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    @Override
    public void delete(PersonalPage pp) {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            MongoCollection<Document> archivePersonalPages = mongoConnection.getArchivePersonalPages();

            //remove document to archive
            Document personalPage = personalPages.find(eq("_id", new ObjectId(pp.get_id()))).first();
            if(personalPage!=null){
                archivePersonalPages.insertOne(personalPage);
            }
            // delete from users
            personalPages.deleteOne(new Document("_id", new ObjectId(pp.get_id())));
        } catch (Exception e) {
            //TODO what we should return
        }

    }

    public void delete(String ppID) {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            MongoCollection<Document> archivePersonalPages = mongoConnection.getArchivePersonalPages();

            //remove document to archive
            Document personalPage = personalPages.find(eq("_id", new ObjectId(ppID))).first();
            if(personalPage!=null){
                archivePersonalPages.insertOne(personalPage);
            }
            // delete from users
            personalPages.deleteOne(new Document("_id", new ObjectId(ppID)));
        } catch (Exception e) {
            //TODO what we should return
        }

    }


    public PersonalPage checkFollow(Fan fan, PersonalPage personalPage ) throws ClassNotFoundException {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            BasicDBList and = new BasicDBList();
            and.add(new BasicDBObject("_id",new ObjectId(personalPage.get_id())));
            and.add(new BasicDBObject("json.fans", new ObjectId(fan.get_id())));
            BasicDBObject query = new BasicDBObject("$and", and);
            ArrayList<Document> DBPersonalPages = personalPages.find(query).into(new ArrayList<>());
            if(!DBPersonalPages.isEmpty()){
                return convertPersonalPageDocument(DBPersonalPages.get(0));
            }
            else {
                return null;
            }
        } catch (Exception e) {
            //TODO what we should return\
            return null;
        }

    }



    // ======== Help Methods =========
    private PersonalPage convertPersonalPageDocument(Document dbPersonalPage) throws ClassNotFoundException {
        try {
            String type = dbPersonalPage.getString("type");
            Document json = dbPersonalPage.get("json", Document.class);
            Class userClass = Class.forName(type);
            Object id = dbPersonalPage.get("_id");
            PersonalPage personalPage = (PersonalPage) gson.fromJson(json.toJson(settings), userClass);
            personalPage.set_id(id.toString());
            return personalPage;
        } catch (Exception e) {
            //TODO what we should return\
            return null;
        }


    }

    public HashSet<PersonalPage> search(String[] searchArray) throws ClassNotFoundException {
        try {
            MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
            HashSet<PersonalPage> allPersonalPage = new HashSet<>();
            BasicDBList or = new BasicDBList();
            for (String s : searchArray) {
                or.add(new BasicDBObject("json.pageName", s));
                or.add(new BasicDBObject("json.team", s));
                or.add(new BasicDBObject("json.teamFootballerMembers", s));
                or.add(new BasicDBObject("json.coachName", s));
                or.add(new BasicDBObject("json.teamFields", s));
                BasicDBObject query = new BasicDBObject("$or", or);
                List<Document> into = personalPages.find(query).into(new ArrayList<>());
                for (Document document : into) {
                    allPersonalPage.add(convertPersonalPageDocument(document));
                }

            }
            return allPersonalPage;
        } catch (Exception e) {
            //TODO what we should return\
            return null;
        }

    }
}
