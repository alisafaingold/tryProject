package DB;

import BusinessLayer.Users.SignedUser;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class CURDOperations{
    MongoConnection mongoConnection = MongoConnection.getInstance();
    Gson gson = new Gson();
    JsonWriterSettings settings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();

    public String createNewUser(SignedUser signedUser){
        try{
            MongoCollection<Document> users = mongoConnection.getUsers();
            String s = gson.toJson(signedUser);
            Document newUser = new Document();
            Document newUserJson = Document.parse(s);
            newUser.put("json",newUserJson);
            newUser.put("type",signedUser.getClass().toString());
            users.insertOne(newUser);
            return ((ObjectId) newUser.get("_id")).toString();
        } catch (Error e){
            return null;
        }


    }

    public SignedUser getUser(String userName, String password) throws ClassNotFoundException {
        try{
            MongoCollection<Document> users = mongoConnection.getUsers();
            BasicDBObject query = new BasicDBObject("json.userName",userName);
            ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
            //If the userName is right
            if(!DBusers.isEmpty()){
                Document user = DBusers.get(0);
                String type = user.getString("type");
                Document json = user.get("json", Document.class);
                Class userClass = Class.forName(type);
                SignedUser userObject = (SignedUser)gson.fromJson(json.toJson(settings), userClass);
                return userObject;
            }
            else {
                return null;
            }
        } catch (Error e){
            return null;
        }

    }

    public SignedUser getUser(String userName) throws ClassNotFoundException {
        MongoCollection<Document> users = mongoConnection.getUsers();
        BasicDBObject query = new BasicDBObject("json.userName",userName);
        ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
        //If the userName is right
        if(!DBusers.isEmpty()){
            Document user = DBusers.get(0);
            String type = user.getString("type");
            Document json = user.get("json", Document.class);
            Class userClass = Class.forName(type);
            SignedUser userObject = (SignedUser)gson.fromJson(json.toJson(settings), userClass);
            return userObject;
        }
        else {
            return null;
        }
    }



    public boolean userNameExists(String userName){
        MongoCollection<Document> users = mongoConnection.getUsers();
        BasicDBObject query = new BasicDBObject("json.userName",userName);
        ArrayList<Document> DBusers = users.find(query).into(new ArrayList<>());
        return !DBusers.isEmpty();
    }


}
