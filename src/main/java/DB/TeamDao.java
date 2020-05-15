package DB;

import BusinessLayer.Football.Team;
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

public class TeamDao<T> implements Dao<Team> {
    private static volatile TeamDao instance = null;
    private MongoConnection mongoConnection = MongoConnection.getInstance();
    private Gson gson = new Gson();
    private JsonWriterSettings settings;

    private TeamDao() {
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

    public static TeamDao getInstance() {
        if (instance == null) {
            synchronized(TeamDao.class) {
                if (instance == null) {
                    instance = new TeamDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Optional<Team> get(String id) {
        try {
            MongoCollection<Document> teams = mongoConnection.getTeams();
            BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
            ArrayList<Document> dbTeams = teams.find(query).into(new ArrayList<>());
            if (dbTeams.isEmpty())
                return Optional.empty();
            else {
                Team team = convertTeamDocument(dbTeams.get(0));
                return Optional.ofNullable(team);
            }
        }
        catch(Exception e){
            //TODO what we should return
            return Optional.empty();
        }
    }


    private Team convertTeamDocument(Document dbTeam) {
        return (Team) gson.fromJson(dbTeam.toJson(settings), Team.class);
    }

    @Override
    public List<Team> getAll() {
        try {
            MongoCollection<Document> teams = mongoConnection.getTeams();
            ArrayList<Document> dbTeams = teams.find().into(new ArrayList<>());
            ArrayList<Team> allTeams = new ArrayList<>();
            for (Document dbTeam : dbTeams) {
                allTeams.add(convertTeamDocument(dbTeam));
            }
            return allTeams;
        }
        catch(Exception e){
            //TODO what we should return
            return null;
        }
    }

    @Override
    public void save(Team team) throws SQLException {
        try {
            MongoCollection<Document> teams = mongoConnection.getTeams();
            String s = gson.toJson(team);
            Document newTeamJson = Document.parse(s);
            teams.insertOne(newTeamJson);
            String id = ((ObjectId) newTeamJson.get("_id")).toString();
            team.set_id(id);
        }
        catch(Exception e){
            //TODO what we should return
        }
    }

    @Override
    public void update(Team team) {
        try {
            MongoCollection<Document> teams = mongoConnection.getTeams();
            Document newTeamJson = Document.parse(gson.toJson(team));
            Bson filter = eq("_id", new ObjectId(team.get_id()));
            for (Map.Entry<String, Object> pair : newTeamJson.entrySet()) {
                if (pair.getKey() != "_id") {
                    Bson change = set(pair.getKey(), pair.getValue());
                    teams.updateOne(filter, change);
                }
            }
        }
        catch(Exception e){
            //TODO what we should return
        }
    }

    @Override
    public void delete(Team team) {
        MongoCollection<Document> teams = mongoConnection.getTeams();
        MongoCollection<Document> archiveTeams = mongoConnection.getArchiveTeams();
        //remove document to archive
        Document teamDoc = teams.find(eq("_id", new ObjectId(team.get_id()))).first();
        if(teamDoc!=null){
            archiveTeams.insertOne(teamDoc);
        }
        teams.deleteOne(new Document("_id", new ObjectId(team.get_id())));
    }

    public List<Team> getTeams(List<String> teamsIDs) {
        try {
            MongoCollection<Document> teams = mongoConnection.getTeams();
            ArrayList<Team> allTeams = new ArrayList<>();
            BasicDBObject query;
            for (String teamID : teamsIDs) {
                query = new BasicDBObject("_id", new ObjectId(teamID));
                List<Document> into = teams.find(query).into(new ArrayList<>());
                for (Document document : into) {
                    allTeams.add(convertTeamDocument(document));
                }
            }
            return allTeams;
        }
        catch(Exception e){
            //TODO what we should return
            return null;
        }
    }

    public Optional<Team> getByTeamName(String teamName) {
        try {
            MongoCollection<Document> teams = mongoConnection.getTeams();
            BasicDBObject query = new BasicDBObject("teamName", teamName);
            ArrayList<Document> dbTeams = teams.find(query).into(new ArrayList<>());
            if (dbTeams.isEmpty())
                return Optional.empty();
            else {
                Team team = convertTeamDocument(dbTeams.get(0));
                return Optional.ofNullable(team);
            }
        }
        catch(Exception e){
            //TODO what we should return
            return Optional.empty();
        }
    }


}
