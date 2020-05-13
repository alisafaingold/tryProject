package ServiceLayer.Controllers;

import CrossCutting.Utils;
import DB.*;
import BusinessLayer.Enum.ComplaintStatus;
import BusinessLayer.Football.Game;
import BusinessLayer.SystemFeatures.Complaint;
import BusinessLayer.Users.Fan;
import BusinessLayer.SystemFeatures.PersonalPage;

import java.util.HashMap;
import java.util.Map;


public class FanController {


    // ============== Follow ============
    //Use Case 3.2
    public boolean follow(Fan fan, PersonalPage personalPage) throws ClassNotFoundException {
        PersonalPageDao personalPageDao = new PersonalPageDao();
        PersonalPage personalPageDB = personalPageDao.checkFollow(fan, personalPage);
        if(personalPage!=null){
            personalPage.removeFans(fan);
            personalPageDao.update(personalPage);
            return false;
        } else {
            personalPage.addFan(fan);
            personalPageDao.update(personalPage);
            return true;
        }
    }

    // ============ Subscribe ===========
    //Use Case 3.3 - observer
    public boolean subscribe(Fan fan, Game game) {
        GamesDao gamesDao  = new GamesDao();
        Game gameDB = gamesDao.checkObserver(fan, game);
        if(gameDB!=null){
            game.removeObserver(fan);
            gamesDao.update(game);
            return false;
        } else {
            game.attachObserver(fan);
            gamesDao.update(game);
            return true;
        }
    }

    // ============ Complaint ===========
    //Use Case 3.4
    public boolean createComplaint(Fan fan, String description) {
        if (description.length() <= 0) {
            return false;
        }
        Complaint complaint = new Complaint(fan, description);
        ComplaintSystemController.addComplaint(complaint);
        return true;

    }

    public boolean closeComplaint(Fan fan, Complaint complaint) {
        complaint.setStatus(ComplaintStatus.Closed);
        ComplaintSystemController.moveToClose(complaint);
        return true;
    }

    // ============ Search History ==============
    //Use Case 3.5
    public Map<String, Long> mySearchHistory(Fan fan, long fromDate, long toDate) throws Exception {
        UserDao userDao = new UserDao();
        if (fromDate < fan.getSignedUpDate() || fromDate > toDate) {
            throw new Exception("Wrong Dates");
        }
        Map<String, Long> searchHistory = SystemController.getSearchHistory(fan, fromDate, toDate);
        if (searchHistory == null || searchHistory.size() == 0) {
            throw new Exception("No Search History");
        }
        userDao.update(fan);
        return searchHistory;
    }

    // ========= Update ==============
    //Use Case 3.6
    public boolean updateDetails(Fan fan, HashMap<String, String> valuesToUpdate) {
        UserDao userDao = new UserDao();
        for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
            switch (entry.getKey().toLowerCase()) {
                case "firstname":
                    fan.setFirstName(entry.getValue());
                    break;
                case "lastname":
                    fan.setLastName(entry.getValue());
                    break;
                case "password":
                    String hashPassword = Utils.sha256(entry.getValue());
                    fan.setPassword(hashPassword);
                    break;
            }
        }
        userDao.update(fan);
        return true;
    }


}
