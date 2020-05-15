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
        MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
        BasicDBObject query = new BasicDBObject("_id",new ObjectId(id));
        ArrayList<Document> DBPersonalPages = personalPages.find(query).into(new ArrayList<>());
        if(!DBPersonalPages.isEmpty()){
            Document personalPage = DBPersonalPages.get(0);
            return Optional.ofNullable(convertPersonalPageDocument(personalPage));
        }
        else
            return null;
    }

    @Override
    public List<PersonalPage> getAll() throws ClassNotFoundException {
        MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
        ArrayList<Document> DBPersonalPages = personalPages.find().into(new ArrayList<>());
        ArrayList<PersonalPage> allPersonalPages = new ArrayList<>();
        for (Document dbPersonalPage : DBPersonalPages)
            allPersonalPages.add(convertPersonalPageDocument(dbPersonalPage));
        return allPersonalPages;
    }

    @Override
    public void save(PersonalPage personalPage) {
        MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
        String personalPageJson = gson.toJson(personalPage);
        Document newPersonalPage = new Document();
        Document newUserJson = Document.parse(personalPageJson);
        newPersonalPage.put("json",newUserJson);
        newPersonalPage.put("type",personalPage.getClass().toString());
        personalPages.insertOne(newPersonalPage);
        personalPage.set_id(newPersonalPage.get("_id").toString());
    }

    @Override
    public void update(PersonalPage personalPage) {
        MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
        Bson filter = eq("_id", new ObjectId(personalPage.get_id()));
        Bson change = set("json", Document.parse(gson.toJson(personalPage)));
        personalPages.updateOne(filter, change);
    }

    @Override
    public void delete(PersonalPage pp) {
        MongoCollection<Document> personalPages = mongoConnection.getPersonalPages();
        MongoCollection<Document> archivePersonalPages = mongoConnection.getArchivePersonalPages();

        //remove document to archive
        Document personalPage = personalPages.find(eq("_id", new ObjectId(pp.get_id()))).first();
        if(personalPage!=null){
            archivePersonalPages.insertOne(personalPage);
        }
        // delete from users
        personalPages.deleteOne(new Document("_id", new ObjectId(pp.get_id())));
    }


    public PersonalPage checkFollow(Fan fan, PersonalPage personalPage ) throws ClassNotFoundException {
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
    }



    // ======== Help Methods =========
    private PersonalPage convertPersonalPageDocument(Document dbPersonalPage) throws ClassNotFoundException {
        String type1 = dbPersonalPage.getString("type");
        String type = type1.substring(6);
        Document json = dbPersonalPage.get("json", Document.class);
        Class userClass = Class.forName(type);
        Object id = dbPersonalPage.get("_id");
        PersonalPage personalPage = (PersonalPage) gson.fromJson(json.toJson(settings), userClass);
        personalPage.set_id(id.toString());
        return personalPage;

    }

}
