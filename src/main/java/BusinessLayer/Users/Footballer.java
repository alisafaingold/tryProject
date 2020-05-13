package BusinessLayer.Users;

import ServiceLayer.Controllers.PersonalPageSystem;
import DB.SystemController;
import CrossCutting.Utils;
import BusinessLayer.Enum.FootballerPosition;
import BusinessLayer.Football.Team;
import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.SystemFeatures.TeamMemberPersonalPage;

import java.util.HashMap;
import java.util.Map;

public class Footballer extends TeamUser implements Asset {
    private FootballerPosition footballerPosition;

    public Footballer(String username, String password, String firstName, String lastName, String email, FootballerPosition footballerPosition) {
        super(username, password, firstName, lastName, email);
        this.footballerPosition = footballerPosition;
        PersonalPageSystem.createNewPersonalPage(this);
    }


    @Override
    public boolean editAsset(HashMap<String, String> changes) throws Exception {
        for (Map.Entry<String, String> entry : changes.entrySet()) {
            switch (entry.getKey().toLowerCase()) {
                case "email":
                    this.email = entry.getValue();
                    break;
                case "firstname":
                    this.firstName = entry.getValue();
                    break;
                case "lastname":
                    this.lastName = entry.getValue();
                    break;
                case "password":
                    this.password = Utils.sha256(entry.getValue());
                    break;
            }

        }
        return true;
    }

    //========== Getters and Setters ================
    public FootballerPosition getFootballerPosition() {
        return footballerPosition;
    }

    //========== To String ================
    @Override
    public String toString() {
        String string = super.toString() +" ";
        string += footballerPosition.toString() +" ";
        return string;
    }

    //========== Delete ================
    @Override
    public boolean deleteUser() throws Exception {
//        PersonalPageSystem.moveToArchive(myPersonalPage);
//        for (Team team : this.teams.keySet()) {
//            team.removeTeamMember(this);
//        }
//        SystemController.archiveUsers.put(this.getUserName(), this);
//        SystemController.userNameUser.remove(this);
        return true;
    }


}
