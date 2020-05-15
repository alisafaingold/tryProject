package BusinessLayer.Users;

import BusinessLayer.Enum.CoachPosition;
import CrossCutting.Utils;
import ServiceLayer.Controllers.PersonalPageSystem;

import java.util.HashMap;
import java.util.Map;

public class Coach extends TeamUser implements Asset {
    private CoachPosition coachPosition;

    public Coach(String username, String password, String firstName, String lastName, String email, CoachPosition coachPosition) {
        super(username, password, firstName, lastName, email);
        this.coachPosition = coachPosition;
        PersonalPageSystem personalPageSystem = new PersonalPageSystem();
        personalPageSystem.createNewPersonalPage(this);
    }

    public CoachPosition getCoachPosition() {
        return coachPosition;
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

    @Override
    public boolean deleteUser() throws Exception {
//        PersonalPageSystem.moveToArchive(myPersonalPage);
//        for (Team team : this.teams.keySet()) {
//            team.removeTeamMember(this);
//        }
//        SystemController.archiveUsers.put(this.getUserName(),this);
//        SystemController.userNameUser.remove(this);
        return true;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += coachPosition.toString();
        return string;
    }
}
