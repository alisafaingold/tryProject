package BusinessLayer.SystemFeatures;

import BusinessLayer.Users.Coach;
import BusinessLayer.Users.Footballer;
import BusinessLayer.Users.SignedUser;
import BusinessLayer.Football.Field;
import BusinessLayer.Football.Team;
import DB.SystemController;

import java.util.HashSet;


public class TeamPersonalPage extends PersonalPage {
    String coachName="";
    String teamFootballerMembers="";
    String teamFields="";
    String records="";
    String Games="";

    public TeamPersonalPage(SignedUser user, Team team) {
        super(user);

        for (Field field : team.getFields()) {
            teamFields = "Filed Name: "+ field.getName() +", Field Location: "+ field.getLocation() +", Field Type: "+field.getFieldType()+
                    ", Number Of seats: "+field.getSeats() + "\n";
        }
        pageName = team.getTeamName();
        //Logger
        SystemController.logger.info("Creation | New Personal Page for Team have been created have been defined; Owner NAME: " + user.getFirstName()+" "+user.getLastName() +
                "; Personal Page ID: " + this.get_id() + "; Team ID:" + team.get_id());
    }

    public void setTeamCoach(HashSet<Coach> coaches){
        for (Coach teamCoach : coaches) {
            coachName += teamCoach.getCoachPosition() + ": "+ teamCoach.getFirstName() +" " + teamCoach.getLastName()+"\n";
        }
    }

    public void setTeamFootballers(HashSet<Footballer> footballers){
        for (Footballer teamFootballer : footballers) {
            teamFootballerMembers += teamFootballer.getFootballerPosition() +": " + teamFootballer.getFirstName()  +" " + teamFootballer.getLastName()+"\n";
        }
    }

    @Override
    public String toString() {
        String string = super.toString();
//        string += coachName +" " + teamFootballerMembers +" " + teamFields + " "+ records + " " + Games;
        return string;
    }

    // ========= Getter and Setters =============

    public String getCoachName() {
        return coachName;
    }

    public String getTeamFootballerMembers() {
        return teamFootballerMembers;
    }

    public String getTeamFields() {
        return teamFields;
    }

    public String getRecords() {
        return records;
    }

    public String getGames() {
        return Games;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public void setTeamFootballerMembers(String teamFootballerMembers) {
        this.teamFootballerMembers = teamFootballerMembers;
    }

    public void setTeamFields(String teamFields) {
        this.teamFields = teamFields;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public void setGames(String games) {
        Games = games;
    }
}
