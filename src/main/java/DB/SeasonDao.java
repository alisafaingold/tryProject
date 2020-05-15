package DB;

import BusinessLayer.Football.Season;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
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

public class SeasonDao<T> implements Dao<Season> {
    private static volatile SeasonDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;

    private SeasonDao() {
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

    public static SeasonDao getInstance() {
        if (instance == null) {
            synchronized(SeasonDao.class) {
                if (instance == null) {
                    instance = new SeasonDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Season> get(String id) {
        try {
            MongoCollection<Document> seasons = mongoConnection.getSeasons();
            BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
            ArrayList<Document> dbSeasons = seasons.find(query).into(new ArrayList<>());
            if (dbSeasons.isEmpty())
                return Optional.empty();
            else {
                Season season = convertSeasonDocument(dbSeasons.get(0));
                return Optional.ofNullable(season);
            }
        } catch (Exception e) {
            //TODO what we should return
            return Optional.empty();
        }
    }

    @Override
    public List<Season> getAll() {
        try {
            MongoCollection<Document> seasons = mongoConnection.getSeasons();
            ArrayList<Document> dbSeasons = seasons.find().into(new ArrayList<>());
            ArrayList<Season> allSeasons = new ArrayList<>();
            for (Document dbSeason : dbSeasons) {
                allSeasons.add(convertSeasonDocument(dbSeason));
            }
            return allSeasons;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    @Override
    public void save(Season season) throws SQLException {
        try {
            MongoCollection<Document> seasons = mongoConnection.getSeasons();
            String s = gson.toJson(season);
            Document newSeasonJson = Document.parse(s);
            seasons.insertOne(newSeasonJson);
            String id = ((ObjectId) newSeasonJson.get("_id")).toString();
            season.set_id(id);
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void update(Season season) {
        try {
            MongoCollection<Document> seasons = mongoConnection.getSeasons();
            Document newTeamJson = Document.parse(gson.toJson(season));
            Bson filter = eq("_id", new ObjectId(season.get_id()));
            for (Map.Entry<String, Object> pair : newTeamJson.entrySet()) {
                if (pair.getKey() != "_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    seasons.updateOne(filter, change);
                }
            }
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void delete(Season season) {
        MongoCollection<Document> seasons = mongoConnection.getSeasons();
        seasons.deleteOne(new Document("_id", new ObjectId(season.get_id())));
    }

    public List<Season> getSeasons(List<String> seasonsIds) {
        try {
            MongoCollection<Document> seasons = mongoConnection.getSeasons();
            ArrayList<Season> allSeasons = new ArrayList<>();
            BasicDBObject query;
            for (String teamID : seasonsIds) {
                query = new BasicDBObject("_id", new ObjectId(teamID));
                List<Document> into = seasons.find(query).into(new ArrayList<>());
                for (Document document : into) {
                    allSeasons.add(convertSeasonDocument(document));
                }
            }
            return allSeasons;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    public List<Season> getRefereeSeasons(String refereeID) {
        try {
            MongoCollection<Document> seasons = mongoConnection.getSeasons();
            ArrayList<Season> allSeasons = new ArrayList<>();
            BasicDBList or = new BasicDBList();
            or.add(new BasicDBObject("referees.Medium", new ObjectId(refereeID)));
            or.add(new BasicDBObject("referees.Expert", new ObjectId(refereeID)));
            or.add(new BasicDBObject("referees.Begginer", new ObjectId(refereeID)));
            BasicDBObject query = new BasicDBObject("$or", or);
            List<Document> into = seasons.find(query).into(new ArrayList<>());
            for (Document document : into) {
                allSeasons.add(convertSeasonDocument(document));
            }
            return allSeasons;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    public List<Season> getLeagueSeasons(String leagueID) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Season> allSeasons = new ArrayList<>();
            BasicDBObject query = new BasicDBObject("league", new ObjectId(leagueID));
            List<Document> into = games.find(query).into(new ArrayList<>());
            for (Document document : into) {
                allSeasons.add(convertSeasonDocument(document));
            }
            return allSeasons;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    public List<Season> getLeagueSeasonByYear(String leagueID, int year) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Season> allSeasons = new ArrayList<>();
            BasicDBList and = new BasicDBList();
            and.add(new BasicDBObject("league", new ObjectId(leagueID)));
            and.add(new BasicDBObject("year", year));
            BasicDBObject query = new BasicDBObject("$and", and);
            List<Document> into = games.find(query).into(new ArrayList<>());
            for (Document document : into) {
                allSeasons.add(convertSeasonDocument(document));
            }
            return allSeasons;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }



    private Season convertSeasonDocument(Document dbSeason) {
        return (Season) gson.fromJson(dbSeason.toJson(settings), Season.class);
    }
}
