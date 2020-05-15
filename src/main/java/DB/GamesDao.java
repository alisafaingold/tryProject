package DB;

import BusinessLayer.Football.Game;
import BusinessLayer.Users.Fan;
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

public class GamesDao<T> implements Dao<Game> {
    private static volatile GamesDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;

    private GamesDao() {
        try {
            mongoConnection = MongoConnection.getInstance();
            gson = new Gson();
            settings =  JsonWriterSettings.builder()
                    .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                    .objectIdConverter((value, writer) -> writer.writeString(value.toString()))
                    .build();
        }
        catch (Exception e) {
            System.out.println("constructor eror!!!");
        }
    }

    public static GamesDao getInstance() {
        if (instance == null) {
            synchronized(GamesDao.class) {
                if (instance == null) {
                    instance = new GamesDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Game> get(String id) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
            ArrayList<Document> dbGames = games.find(query).into(new ArrayList<>());
            if (dbGames.isEmpty())
                return Optional.empty();
            else {
                Game game = convertGameDocument(dbGames.get(0));
                return Optional.ofNullable(game);
            }
        } catch (Exception e) {
            //TODO what we should return
            return Optional.empty();
        }    }

    @Override
    public List<Game> getAll() {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Document> dbGames = games.find().into(new ArrayList<>());
            ArrayList<Game> allGames = new ArrayList<>();
            for (Document dbSeason : dbGames) {
                allGames.add(convertGameDocument(dbSeason));
            }
            return allGames;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    @Override
    public void save(Game game) throws SQLException {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            String s = gson.toJson(game);
            Document newGameJson = Document.parse(s);
            games.insertOne(newGameJson);
            String id = ((ObjectId) newGameJson.get("_id")).toString();
            game.set_id(id);
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void update(Game game) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            Document newTeamJson = Document.parse(gson.toJson(game));
            Bson filter = eq("_id", new ObjectId(game.get_id()));
            for (Map.Entry<String, Object> pair : newTeamJson.entrySet()) {
                if (pair.getKey() != "_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    games.updateOne(filter, change);
                }
            }
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void delete(Game game) {
        MongoCollection<Document> games = mongoConnection.getGames();
        games.deleteOne(new Document("_id", new ObjectId(game.get_id())));
    }

    public List<Game> getGames(List<String> gamesIDs) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Game> allGames = new ArrayList<>();
            BasicDBObject query;
            for (String gameID : gamesIDs) {
                query = new BasicDBObject("_id", new ObjectId(gameID));
                List<Document> into = games.find(query).into(new ArrayList<>());
                for (Document document : into) {
                    allGames.add(convertGameDocument(document));
                }
            }
            return allGames;
        }
        catch(Exception e){
            //TODO what we should return
            return null;
        }
    }

    public List<Game> getRefereeGames(String refereeID) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Game> allGames = new ArrayList<>();
            BasicDBList or = new BasicDBList();
            or.add(new BasicDBObject("mainReferee", new ObjectId(refereeID)));
            or.add(new BasicDBObject("secondaryReferee1", new ObjectId(refereeID)));
            or.add(new BasicDBObject("secondaryReferee2", new ObjectId(refereeID)));
            BasicDBObject query = new BasicDBObject("$or", or);
            List<Document> into = games.find(query).into(new ArrayList<>());
            for (Document document : into) {
                allGames.add(convertGameDocument(document));
            }
            return allGames;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }


    public List<Game> getMainRefereeGames(String refereeID) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Game> allGames = new ArrayList<>();
            BasicDBObject query =new BasicDBObject ("mainReferee",new ObjectId(refereeID));
            List<Document> into = games.find(query).into(new ArrayList<>());
            for (Document document : into) {
                allGames.add(convertGameDocument(document));
            }
            return allGames;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }



    public List<Game> getFieldGames(String fieldID) {
        try {
            MongoCollection<Document> games = mongoConnection.getGames();
            ArrayList<Game> allGames = new ArrayList<>();
            BasicDBObject query = new BasicDBObject("field", new ObjectId(fieldID));
            List<Document> into = games.find(query).into(new ArrayList<>());
            for (Document document : into) {
                allGames.add(convertGameDocument(document));
            }
            return allGames;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    // For Fan Controller
    public Game checkObserver (Fan fan, Game game){
        try{
            MongoCollection<Document> games = mongoConnection.getGames();
            BasicDBList and = new BasicDBList();
            and.add(new BasicDBObject("_id", new ObjectId(game.get_id())));
            and.add(new BasicDBObject("fansObserver", new ObjectId(fan.get_id())));
            BasicDBObject query = new BasicDBObject("$and", and);
            ArrayList<Document> DBPersonalPages = games.find(query).into(new ArrayList<>());
            if(!DBPersonalPages.isEmpty()){
                return convertGameDocument(DBPersonalPages.get(0));
            }
            else{
                return null;
            }
        } catch (Exception e){
            return null;
        }
    }

    private Game convertGameDocument(Document dbGame) {
        return gson.fromJson(dbGame.toJson(settings), Game.class);
    }
}
