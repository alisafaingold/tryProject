package BusinessLayer.SystemFeatures;

import BusinessLayer.Football.Team;
import BusinessLayer.Users.Coach;
import BusinessLayer.Users.Footballer;
import BusinessLayer.Users.TeamUser;
import DB.SystemController;

public class TeamMemberPersonalPage extends PersonalPage {
    private String birthday="";
    private String history="";
    private String hobbies="";
    private String type="";
    private String role="";
    private String team="";
    private String content="";



    public TeamMemberPersonalPage(TeamUser user) {
        super(user);
        for (Team team1 : user.getTeams().keySet()) {
            team += team1.getTeamName() +"\n";
        }

        if (user instanceof Footballer){
            role = ((Footballer) user).getFootballerPosition().toString();
            type = "Footballer";
        }

        if (user instanceof Coach){
            type = "Coach";
            role = ((Coach) user).getCoachPosition().toString();
        }
        pageName = user.getFirstName() +" "+user.getLastName();
        //Logger
        SystemController.logger.info("Creation | New Personal Page for Team Member have been created have been defined; Owner name: " + user.getFirstName() + " " + user.getLastName() +
                "; Personal Page ID: " + this.get_id());

    }

    // =========== Getters and Setters =============
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getHistory() {
        return history;
    }

    public String getHobbies() {
        return hobbies;
    }

    public String getType() {
        return type;
    }

    public String getRole() {
        return role;
    }

    public String getTeam() {
        return team;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        String string = super.toString();
        string +=  birthday + " " + history + " " + hobbies + " " + type + " "+ role + " "+  team + " " + content;
        return string;
    }
}
