package DB;

import BusinessLayer.Football.League;
import BusinessLayer.Football.Season;
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

public class LeagueDao<T> implements Dao<League> {
    MongoConnection mongoConnection = MongoConnection.getInstance();
    Gson gson = new Gson();
    JsonWriterSettings settings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .objectIdConverter((value, writer) -> writer.writeString(value.toString()))
            .build();


    @Override
    public Optional<League> get(String id) {
        try {
            MongoCollection<Document> leagues = mongoConnection.getLeagues();
            BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
            ArrayList<Document> dbLeagues = leagues.find(query).into(new ArrayList<>());
            if (dbLeagues.isEmpty())
                return Optional.empty();
            else {
                League league = convertLeagueDocument(dbLeagues.get(0));
                return Optional.ofNullable(league);
            }
        } catch (Exception e) {
            //TODO what we should return
            return Optional.empty();
        }
    }

    @Override
    public List<League> getAll() {
        try {
            MongoCollection<Document> leagues = mongoConnection.getLeagues();
            ArrayList<Document> dbLeagues = leagues.find().into(new ArrayList<>());
            ArrayList<League> allLeagues = new ArrayList<>();
            for (Document dbSeason : dbLeagues) {
                allLeagues.add(convertLeagueDocument(dbSeason));
            }
            return allLeagues;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    @Override
    public void save(League league) throws SQLException {
        try {
            MongoCollection<Document> leagues = mongoConnection.getLeagues();
            String s = gson.toJson(league);
            Document newSeasonJson = Document.parse(s);
            leagues.insertOne(newSeasonJson);
            String id = ((ObjectId) newSeasonJson.get("_id")).toString();
            league.set_id(id);
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void update(League league) {
        try {
            MongoCollection<Document> leagues = mongoConnection.getLeagues();
            Document newTeamJson = Document.parse(gson.toJson(league));
            Bson filter = eq("_id", new ObjectId(league.get_id()));
            for (Map.Entry<String, Object> pair : newTeamJson.entrySet()) {
                if (pair.getKey() != "_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    leagues.updateOne(filter, change);
                }
            }
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void delete(League league) {
        MongoCollection<Document> leagues = mongoConnection.getLeagues();
        leagues.deleteOne(new Document("_id", new ObjectId(league.get_id())));
    }

    public League getLeagueByName(String name){
        MongoCollection<Document> leagues = mongoConnection.getLeagues();
        BasicDBObject query = new BasicDBObject("leagueName",leagues);
        ArrayList<Document> DBusers = leagues.find(query).into(new ArrayList<>());
        if(!DBusers.isEmpty()){
            Document league = DBusers.get(0);
            return convertLeagueDocument(league);
        }
        else
            return null;

    }

    private League convertLeagueDocument(Document dbLeague) {
        return gson.fromJson(dbLeague.toJson(settings), League.class);
    }
}
