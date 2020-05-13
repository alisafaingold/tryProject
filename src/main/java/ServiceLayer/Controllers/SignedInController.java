package ServiceLayer.Controllers;

import BusinessLayer.Users.User;
import CrossCutting.Utils;
import DB.SystemController;
import BusinessLayer.Enum.UserStatus;
import BusinessLayer.Users.SignedUser;
import DB.UserDao;

import java.util.HashMap;
import java.util.Map;

public class SignedInController {

    //Use Case 2.3
    public boolean signIn (String username, String password) throws Exception {
        UserDao userDao = new UserDao();
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
        UserDao userDao = new UserDao();
        userDao.update(signedUser);
        return true;
    }


    //Use Case 4.1 5.1 10.1
    public static boolean updateDetails(SignedUser signedUser, HashMap<String, String> valuesToUpdate) throws Exception {
        UserDao userDao = new UserDao();
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

}
