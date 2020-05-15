package ServiceLayer.Controllers;

import BusinessLayer.Enum.UserStatus;
import BusinessLayer.Football.League;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.SignedUser;
import BusinessLayer.Users.User;
import CrossCutting.Utils;
import DB.*;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class SignedInController {
    private UserDao userDao;
    private PersonalPageDao personalPageDao;
    private LeagueDao leagueDao;
    private static int sessionID = 10000;
    private HashMap<Integer, String> sessionIDToDBID;
    private HashMap<Integer, String> sessionIDToType;


    public SignedInController() {
        this.userDao = UserDao.getInstance();
        this.personalPageDao = PersonalPageDao.getInstance();
        this.leagueDao = LeagueDao.getInstance();
        sessionIDToDBID = new HashMap<>();
        sessionIDToType = new HashMap<>();
    }

    // For service
    public String get_ID(int sessionID, String type ) throws Exception {
        if(sessionIDToType.get(sessionID).equals(type))
            return sessionIDToDBID.get(sessionID);
        else
            throw new Exception("type not match");
    }

    //Use Case 2.2
    public boolean singUp(String email, String password, String fName, String lName) throws Exception {
        if (password == null || password.length() < 6) {
            throw new Exception("Not long enough");
        }
        SignedUser byEmail = userDao.getByEmail(email);
        if (byEmail != null) {
            throw new Exception("Not unique user name");
        }

        boolean valid = EmailValidator.getInstance().isValid(email);
        if (!valid) {
            throw new Exception("Not valid email");
        }
        try {
            String hashPassword = Utils.sha256(password);
            Fan newUser = new Fan(email, hashPassword, fName, lName, email);
            newUser.changeStatus(UserStatus.LogIn);
            userDao.save(newUser);
            //Logger
            SystemController.logger.info("Creation | New Fan sing up to the system; user ID: " + newUser.get_id());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return true;
    }

    //Use Case 2.3
    public int signIn(String username, String password) throws Exception {
        try {
            if (username == null || password == null || username.length() < 4 || password.length() < 6) {
                throw new Exception("Couldn't be that credentials");
            }
            String hashPassword = Utils.sha256(password);
            SignedUser user = userDao.getByEmail(username);
            if (user != null) {
                if (user.getPassword() != hashPassword) {
                    throw new Exception("Wrong credentials");
                }
            } else {
                throw new Exception("Wrong credentials");
            }
            user.changeStatus(UserStatus.LogIn);
            userDao.update(user);
            sessionIDToDBID.put(sessionID, user.get_id());
            sessionIDToType.put(sessionID, user.getClass().toString().substring(6));
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        return sessionID++;
    }

    //Use Case 3.1
    public boolean logOut(String signedUserID) throws Exception {
        Optional optional = userDao.get(signedUserID);
        if (optional.isPresent()) {
            SignedUser signedUser = (SignedUser) optional.get();
            if (signedUser.getStatus().equals(UserStatus.LogOut))
                return false;
            signedUser.changeStatus(UserStatus.LogOut);
            userDao.update(signedUser);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }


    //Use Case 4.1 5.1 10.1
    public boolean updateDetails(String signedUserID, HashMap<String, String> valuesToUpdate) throws Exception {
        Optional optional = userDao.get(signedUserID);
        if (optional.isPresent()) {
            SignedUser signedUser = (SignedUser) optional.get();
            for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                switch (key.toLowerCase()) {
                    case "password":
                        if (value.length() >= 6) {
                            String hashPassword = Utils.sha256(value);
                            signedUser.setPassword(hashPassword);
                            break;
                        } else {
                            throw new Exception("Password not long enough");
                        }
                    case "first name":
                        signedUser.setFirstName(value);
                        break;
                    case "last name":
                        signedUser.setLastName(value);
                        break;
                }
            }
            userDao.update(signedUser);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 2.5
    public HashMap<String, HashSet<Object>> search(String userID, String searchInput) throws Exception {
        Optional optional = userDao.get(userID);
        if (optional.isPresent()) {
            User user = (User) optional.get();
            if (user instanceof Fan) {
                Fan f = ((Fan) user);
                f.addToMySearches(System.currentTimeMillis(), searchInput);
                userDao.update(f);
            }
            HashMap<String, HashSet<Object>> returned = new HashMap<>();
            String[] searchArray = searchInput.split(" ");

            returned.put("League", new HashSet<>());
            returned.put("Personal Pages", new HashSet<>());

            HashSet searchPP = personalPageDao.search(searchArray);
            returned.get("Personal Pages").addAll(searchPP);

            for (String s : searchArray) {
                League leagueByName = leagueDao.getLeagueByName(s);
                if (leagueByName != null) {
                    returned.get("League").add(leagueByName);
                }
            }
            return returned;
        } else {
            throw new Exception("Wrong IDS");
        }
    }


}
