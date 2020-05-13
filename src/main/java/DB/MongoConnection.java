package DB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnection {
    MongoClient mongoClient;
    MongoDatabase db;

    MongoCollection<Document> archiveComplaints;
    MongoCollection<Document> archivePersonalPages;
    MongoCollection<Document> archiveTeams;
    MongoCollection<Document> archiveUsers;
    MongoCollection<Document> complaints;
    MongoCollection<Document> events;
    MongoCollection<Document> fields;
    MongoCollection<Document> games;
    MongoCollection<Document> leagues;
    MongoCollection<Document> personalPages;
    MongoCollection<Document> scoreBoards;
    MongoCollection<Document> seasons;
    MongoCollection<Document> teams;
    MongoCollection<Document> users;

    private static volatile MongoConnection instance = null;

    private MongoConnection() {
        try {
            SystemController.logger.info("opening conecction to mongo DB...");
            mongoClient = MongoClients.create(
                    "mongodb+srv://projectPrep:Project1820@projectpreparation-dxeej.azure.mongodb.net/test?retryWrites=true&w=majority");
            db = mongoClient.getDatabase("ProjectPrep");
        }
        catch (Exception e) {
            System.out.println("constructor eror!!!!!!!!!!!!");
        }
    }

    public static MongoConnection getInstance() {
        if (instance == null) {
            synchronized(MongoConnection.class) {
                if (instance == null) {
                    instance = new MongoConnection();
                }
            }
        }
        return instance;
    }

    public void close() {
        try {
            mongoClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoCollection<Document> getArchiveComplaints() {
        archiveComplaints = db.getCollection("archiveComplaints");
        return archiveComplaints;
    }

    public MongoCollection<Document> getArchivePersonalPages() {
        archivePersonalPages = db.getCollection("archivePersonalPages");
        return archivePersonalPages;
    }

    public MongoCollection<Document> getArchiveTeams() {
        archiveTeams = db.getCollection("archiveTeams");
        return archiveTeams;
    }

    public MongoCollection<Document> getArchiveUsers() {
        archiveUsers = db.getCollection("archiveUsers");
        return archiveUsers;
    }

    public MongoCollection<Document> getComplaints() {
        complaints = db.getCollection("complaints");
        return complaints;
    }

    public MongoCollection<Document> getEvents() {
        events = db.getCollection("events");
        return events;
    }

    public MongoCollection<Document> getFields() {
        fields = db.getCollection("fields");
        return fields;
    }

    public MongoCollection<Document> getGames() {
        games = db.getCollection("games");
        return games;
    }

    public MongoCollection<Document> getLeagues() {
        leagues = db.getCollection("leagues");
        return leagues;
    }

    public MongoCollection<Document> getPersonalPages() {
        personalPages = db.getCollection("personalPages");
        return personalPages;
    }

    public MongoCollection<Document> getScoreBoards() {
        scoreBoards = db.getCollection("scoreBoards");
        return scoreBoards;
    }

    public MongoCollection<Document> getSeasons() {
        seasons = db.getCollection("seasons");
        return seasons;
    }

    public MongoCollection<Document> getTeams() {
        teams = db.getCollection("teams");
        return teams;
    }

    public MongoCollection<Document> getUsers() {
        users = db.getCollection("users");
        return users;
    }

}
