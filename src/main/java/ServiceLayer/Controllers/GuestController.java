package ServiceLayer.Controllers;

import BusinessLayer.Enum.UserStatus;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.SignedUser;
import CrossCutting.Utils;
import DB.ComplaintDao;
import DB.SystemController;
import DB.UserDao;
import org.apache.commons.validator.routines.EmailValidator;

public class GuestController {
    private UserDao userDao;

    public GuestController() {
        this.userDao = UserDao.getInstance();
    }

   //Use Case 2.2
    public boolean singUp (String email, String password, String fName, String lName) throws Exception {
        if(password == null || password.length()<6){
            throw new Exception("Not long enough");
        }
        SignedUser byEmail = userDao.getByEmail(email);
        if(byEmail!=null){
            throw new Exception("Not unique user name");
        }

}

