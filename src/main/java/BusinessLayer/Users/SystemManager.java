package BusinessLayer.Users;

import BusinessLayer.Enum.UserStatus;
import DB.SystemController;
import BusinessLayer.SystemFeatures.Complaint;

import java.util.HashSet;

public class SystemManager extends SignedUser {
    private int SystemManagerID;

    //========== Constructor ================

    public SystemManager(String username, String password, String firstName, String lastName, String email) {
        super(username,password,firstName, lastName, email);
    }


    //========== Getters and Setters ================
    public int getSystemManagerID() {
        return SystemManagerID;
    }


    //========= Delete ===========

    @Override
    public boolean deleteUser() throws Exception {
//        SystemController.removeUserFromActiveList(this.userName);
//        this.changeStatus(UserStatus.NotActive);
        return true;
    }


}
