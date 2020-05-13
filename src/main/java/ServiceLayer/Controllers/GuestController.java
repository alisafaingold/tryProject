package ServiceLayer.Controllers;

import BusinessLayer.Users.SignedUser;
import CrossCutting.Utils;
import DB.SystemController;
import BusinessLayer.Enum.UserStatus;
import BusinessLayer.Users.Fan;
import DB.UserDao;
import org.apache.commons.validator.routines.EmailValidator;

public class GuestController {

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

}

