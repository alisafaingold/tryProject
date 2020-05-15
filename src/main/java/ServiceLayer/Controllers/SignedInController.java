package ServiceLayer.Controllers;

import BusinessLayer.Football.League;
import CrossCutting.Utils;
import DB.*;
import BusinessLayer.Enum.UserStatus;
import BusinessLayer.Users.*;
import org.apache.commons.validator.routines.EmailValidator;
import BusinessLayer.Enum.UserStatus;
import BusinessLayer.Users.SignedUser;
import CrossCutting.Utils;
import DB.UserDao;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SignedInController {
    private UserDao userDao;

    public SignedInController() {
        this.userDao = UserDao.getInstance();
    }

    //Use Case 2.2
    public boolean singUp (String email, String password, String fName, String lName) throws Exception {
        UserDao userDao = new UserDao();
        if(password == null || password.length()<6){
            throw new Exception("Not long enough");
        }
        SignedUser byEmail = userDao.getByEmail(email);
        if(byEmail!=null){
            throw new Exception("Not unique user name");
        }

        boolean valid = EmailValidator.getInstance().isValid(email);
        if(!valid){
            throw new Exception("Not valid email");
        }
        try {
            String hashPassword = Utils.sha256(password);
            Fan newUser = new Fan(email,hashPassword, fName, lName, email);
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
    public boolean signIn (String username, String password) throws Exception {
        if(username== null || password == null || username.length()<4 || password.length()<6){
            throw new Exception("Couldn't be that credentials");
        }
        try{
            String hashPassword = Utils.sha256(password);
            SignedUser user = userDao.getByEmail(username);
            if(user!=null){
                if(user.getPassword()!=hashPassword){
                    throw new Exception("Wrong credentials");
                }
            }
            else {
                throw new Exception("Wrong credentials");
            }
            user.changeStatus(UserStatus.LogIn);
            userDao.update(user);
        } catch (Exception e) {
          //  e.printStackTrace();
        }
        return true;
    }

    //Use Case 3.1
    public boolean logOut (SignedUser signedUser) throws Exception {
        if(signedUser.getStatus().equals(UserStatus.LogOut))
            return false;
        signedUser.changeStatus(UserStatus.LogOut);
        userDao.update(signedUser);
        return true;
    }


    //Use Case 4.1 5.1 10.1
    public boolean updateDetails(SignedUser signedUser, HashMap<String, String> valuesToUpdate) throws Exception {
        for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key.toLowerCase()) {
                case "password":
                    if(value.length() >= 6) {
                        String hashPassword = Utils.sha256(value);
                        signedUser.setPassword(hashPassword);
                        break;
                    }
                    else{
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
    }

    //Use Case 2.5
    public static HashMap<String, HashSet<Object>> search(User user, String searchInput) throws ClassNotFoundException {
        UserDao userDao = new UserDao();
        LeagueDao leagueDao = new LeagueDao();
        SeasonDao seasonDao = new SeasonDao();
        PersonalPageDao personalPageDao = new PersonalPageDao();

        if(user instanceof Fan){
           Fan f=((Fan) user);
           f.addToMySearches(System.currentTimeMillis(),searchInput);
            userDao.update(f);
        }
        HashMap<String, HashSet<Object>> returned = new HashMap<>();
        String[] searchArray = searchInput.split(" ");

        returned.put("League",new HashSet<>());
        returned.put("Personal Pages",new HashSet<>());

        HashSet searchPP = personalPageDao.search(searchArray);
        returned.get("Personal Pages").addAll(searchPP);

        for (String s : searchArray) {
            League leagueByName = leagueDao.getLeagueByName(s);
            if(leagueByName!=null){
                returned.get("League").add(leagueByName);
            }
        }

        return returned;
    }


}
