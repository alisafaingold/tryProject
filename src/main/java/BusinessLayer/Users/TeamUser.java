package BusinessLayer.Users;

import BusinessLayer.Football.Team;

import java.util.HashMap;

public abstract class TeamUser extends SignedUser {
    protected HashMap<Team, ManagementUser> teams;
    protected String assignedManagementId;

    public TeamUser(String username, String password, String firstName, String lastName, String email) {
        super(username,password,firstName,lastName,email);
        teams = new HashMap<>();
    }

    public boolean addTeam(Team team, ManagementUser managementUser){
        teams.put(team, managementUser);
        return true;
    }

    public boolean removeTeam(Team team){
        teams.remove(team);
        return true;
    }

    public HashMap<Team, ManagementUser> getTeams() {
        return teams;
    }

    @Override
    public String toString() {
        String string = super.toString();
        for (Team team : teams.keySet()) {
            string += team.getTeamName();
        }
        return string;

    }
}
