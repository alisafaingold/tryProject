package ServiceLayer.Controllers;

import BusinessLayer.Users.SignedUser;
import DB.UserDao;

public class GuestController {
    private UserDao userDao;

    public GuestController() {
        this.userDao = UserDao.getInstance();
    }


}

