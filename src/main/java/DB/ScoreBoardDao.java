package DB;

import BusinessLayer.Football.ScoreBoard;
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

public class ScoreBoardDao<T> implements Dao<ScoreBoard> {
    MongoConnection mongoConnection = MongoConnection.getInstance();
    Gson gson = new Gson();
    JsonWriterSettings settings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .objectIdConverter((value, writer) -> writer.writeString(value.toString()))
            .build();

    @Override
    public Optional<ScoreBoard> get(String id) {
        MongoCollection<Document> scoreBoards = mongoConnection.getScoreBoards();
        BasicDBObject query = new BasicDBObject("_id",new ObjectId(id));
        ArrayList<Document> dbScoreBoards = scoreBoards.find(query).into(new ArrayList<>());
        if(dbScoreBoards.isEmpty())
            return Optional.empty();
        else {
            ScoreBoard scoreBoard = convertScoreBoardDocument(dbScoreBoards.get(0));
            return Optional.ofNullable(scoreBoard);
        }
    }

    @Override
    public List<ScoreBoard> getAll() {
        try {
            MongoCollection<Document> scoreBoards = mongoConnection.getScoreBoards();
            ArrayList<Document> dbScoreBoards = scoreBoards.find().into(new ArrayList<>());
            ArrayList<ScoreBoard> allScoreBoards = new ArrayList<>();
            for (Document dbSeason : dbScoreBoards) {
                allScoreBoards.add(convertScoreBoardDocument(dbSeason));
            }
            return allScoreBoards;
        } catch (Exception e) {
            //TODO what we should return
            return null;
        }
    }

    @Override
    public void save(ScoreBoard scoreBoard) throws SQLException {
        try {
            MongoCollection<Document> scoreBoards = mongoConnection.getScoreBoards();
            String s = gson.toJson(scoreBoard);
            Document newScoreBoardJson = Document.parse(s);
            scoreBoards.insertOne(newScoreBoardJson);
            String id = ((ObjectId) newScoreBoardJson.get("_id")).toString();
            scoreBoard.set_id(id);
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void update(ScoreBoard scoreBoard) {
        try {
            MongoCollection<Document> scoreBoards = mongoConnection.getScoreBoards();
            Document newTeamJson = Document.parse(gson.toJson(scoreBoard));
            Bson filter = eq("_id", new ObjectId(scoreBoard.get_id()));
            for (Map.Entry<String, Object> pair : newTeamJson.entrySet()) {
                if (pair.getKey() != "_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    scoreBoards.updateOne(filter, change);
                }
            }
        } catch (Exception e) {
            //TODO what we should return
        }
    }

    @Override
    public void delete(ScoreBoard scoreBoard) {
//        MongoCollection<Document> scoreBoards = mongoConnection.getScoreBoards();
//        scoreBoards.deleteOne(new Document("_id", new ObjectId(scoreBoard.get_id())));
    }

    public List<ScoreBoard> getSeasons(List<String> scoreBoardIDs) {
        try {
            MongoCollection<Document> scoreBoards = mongoConnection.getSeasons();
            ArrayList<ScoreBoard> allScoreBoards = new ArrayList<>();
            BasicDBObject query;
            for (String scoreBoardID : scoreBoardIDs) {
                query = new BasicDBObject("_id", new ObjectId(scoreBoardID));
                List<Document> into = scoreBoards.find(query).into(new ArrayList<>());
                for (Document document : into) {
                    allScoreBoards.add(convertScoreBoardDocument(document));
                }
            }
            return allScoreBoards;
        }
        catch(Exception e){
            //TODO what we should return
            return null;
        }
    }

    private ScoreBoard convertScoreBoardDocument(Document dbScoreBoard) {
        return gson.fromJson(dbScoreBoard.toJson(settings), ScoreBoard.class);
    }
}
