package ServiceLayer.Controllers;

import BusinessLayer.Enum.ComplaintStatus;
import BusinessLayer.Football.Game;
import BusinessLayer.Football.Season;
import BusinessLayer.SystemFeatures.Complaint;
import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.Users.Fan;
import CrossCutting.Utils;
import DB.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class FanController {
    private PersonalPageDao personalPageDao;
    private GamesDao gamesDao;
    private ComplaintSystemController complaintSystemController;
    private UserDao userDao;
    private ComplaintDao complaintDao;

    public FanController() {
        personalPageDao = PersonalPageDao.getInstance();
        gamesDao = GamesDao.getInstance();
        userDao = UserDao.getInstance();
        complaintDao = ComplaintDao.getInstance();
        complaintSystemController = new ComplaintSystemController();
    }

    // ============== Follow ============
    //Use Case 3.2
    public boolean follow(String fanID, String personalPageID) throws Exception {
        Optional optional = userDao.get(fanID);
        Optional optional1 = personalPageDao.get(personalPageID);
        if (optional.isPresent() && optional1.isPresent()) {
            Fan fan = (Fan) optional.get();
            PersonalPage personalPage = (PersonalPage) optional1.get();
            PersonalPage personalPageDB = personalPageDao.checkFollow(fan, personalPage);
            if (personalPage != null) {
                personalPage.removeFans(fan);
                personalPageDao.update(personalPage);
                return false;
            } else {
                personalPage.addFan(fan);
                personalPageDao.update(personalPage);
                return true;
            }
        } else {
            throw new Exception("Wrong IDS");
        }
    }


    // ============ Subscribe ===========
    //Use Case 3.3 - observer
    public boolean subscribe(String fanID, String gameID) throws Exception {
        Optional optional = userDao.get(fanID);
        Optional optional1 = gamesDao.get(gameID);
        if (optional.isPresent() && optional1.isPresent()) {
            Fan fan = (Fan) optional.get();
            Game game = (Game) optional1.get();
            Game gameDB = gamesDao.checkObserver(fan, game);
            if (gameDB != null) {
                game.removeObserver(fan);
                gamesDao.update(game);
                return false;
            } else {
                game.attachObserver(fan);
                gamesDao.update(game);
                return true;
            }
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    // ============ Complaint ===========
    //Use Case 3.4
    public boolean createComplaint(String fanID, String description) throws Exception {
        Optional optional = userDao.get(fanID);
        if (optional.isPresent()) {
            Fan fan = (Fan) optional.get();
            if (description.length() <= 0) {
                return false;
            }
            Complaint complaint = new Complaint(fan, description);
            complaintSystemController.addComplaint(complaint);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    public boolean closeComplaint(String fanID, String complaintID) throws Exception {
        Optional optional = userDao.get(fanID);
        if (optional.isPresent()) {
            complaintSystemController.moveToClose(complaintID);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    // ============ Search History ==============
    //Use Case 3.5
    public Map<String, Long> mySearchHistory(String fanID, long fromDate, long toDate) throws Exception {
        Optional optional = userDao.get(fanID);
        if (optional.isPresent()) {
            Fan fan = (Fan) optional.get();

            if (fromDate < fan.getSignedUpDate() || fromDate > toDate) {
                throw new Exception("Wrong Dates");
            }

            HashMap<Long, String> historyDateHashMap = fan.getMySearches();

            if (historyDateHashMap.isEmpty()) {
                throw new Exception("No Search History");
            }

            Map<String, Long> searchHistory =
                    historyDateHashMap.entrySet().stream().filter(s -> s.getKey() < toDate && s.getKey() > fromDate).collect(Collectors.toMap(stringDateEntry -> stringDateEntry.getValue(), stringDateEntry -> stringDateEntry.getKey()));

            if (searchHistory.isEmpty())
                throw new Exception("No Search History");

            return searchHistory;
        } else {
            throw new Exception("Wrong IDS");
        }
    }


    // ========= Update ==============
    //Use Case 3.6
    public boolean updateDetails(String fanID, HashMap<String, String> valuesToUpdate) throws Exception {
        Optional optional = userDao.get(fanID);
        if (optional.isPresent()) {
            Fan fan = (Fan) optional.get();
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
        } else {
            throw new Exception("Wrong IDS");
        }

    }


}
