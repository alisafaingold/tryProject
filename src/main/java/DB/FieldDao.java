package DB;

import BusinessLayer.Football.Field;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class FieldDao<T> implements Dao<Field> {
    private static volatile FieldDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;

    private FieldDao() {
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

    public static FieldDao getInstance() {
        if (instance == null) {
            synchronized(FieldDao.class) {
                if (instance == null) {
                    instance = new FieldDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Field> get(String id) {
        try {
            MongoCollection<Document> fields = mongoConnection.getFields();
            BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
            ArrayList<Document> dbFields = fields.find(query).into(new ArrayList<>());
            if (dbFields.isEmpty())
                return Optional.empty();
            else {
                Field field = convertFieldDocument(dbFields.get(0));
                return Optional.ofNullable(field);
            }
        } catch (Exception e) {
            //TODO what we should return
            return Optional.empty();
        }
    }

    @Override
    public List<Field> getAll() {
        try {
            MongoCollection<Document> fields = mongoConnection.getFields();
            ArrayList<Document> dbFields = fields.find().into(new ArrayList<>());
            ArrayList<Field> allFields = new ArrayList<>();
            for (Document dbField : dbFields) {
                allFields.add(convertFieldDocument(dbField));
            }
            return allFields;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    @Override
    public void save(Field field) throws SQLException {
        try {
            MongoCollection<Document> fields = mongoConnection.getFields();
            String s = gson.toJson(field);
            Document newSeasonJson = Document.parse(s);
            fields.insertOne(newSeasonJson);
            String id = ((ObjectId) newSeasonJson.get("_id")).toString();
            field.set_id(id);
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void update(Field field) {
        try {
            MongoCollection<Document> fields = mongoConnection.getFields();
            Document newTeamJson = Document.parse(gson.toJson(field));
            Bson filter = eq("_id", new ObjectId(field.get_id()));
            for (Map.Entry<String, Object> pair : newTeamJson.entrySet()) {
                if (pair.getKey() != "_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    fields.updateOne(filter, change);
                }
            }
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void delete(Field field) {
        MongoCollection<Document> fields = mongoConnection.getFields();
        fields.deleteOne(new Document("_id", new ObjectId(field.get_id())));
    }

    private Field convertFieldDocument(Document dbField) {
        return gson.fromJson(dbField.toJson(settings), Field.class);
    }

    public List<Field> getTeamFields(String team) {
        try {
            MongoCollection<Document> fields = mongoConnection.getFields();
            BasicDBObject query = new BasicDBObject("team", team);
            ArrayList<Document> dbFields = fields.find(query).into(new ArrayList<>());
            ArrayList<Field> allFields = new ArrayList<>();
            for (Document dbField : dbFields) {
                allFields.add(convertFieldDocument(dbField));
            }
            return allFields;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }
}
