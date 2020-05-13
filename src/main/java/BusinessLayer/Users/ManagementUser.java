package BusinessLayer.Users;

import DB.SystemController;
import ServiceLayer.Controllers.TeamOwnerController;
import BusinessLayer.Football.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class ManagementUser extends TeamUser {
    protected HashMap<Team, HashSet<Owner>> assignedOwners;
    protected HashMap<Team, HashSet<TeamManager>> assignedTeamManagers;
    protected SignedUser additionalRole;

    public ManagementUser(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
        this.assignedOwners = new HashMap<>();
        this.assignedTeamManagers = new HashMap<>();
    }

    public boolean addOwner(Team team, Owner owner) throws Exception {
        HashSet<Owner> owners = this.assignedOwners.get(team);
        if(owners != null){
            if(owners.contains(owner))
                throw new Exception("The user is already defined as team owner");
            owners.add(owner);
            assignedOwners.put(team,owners);
        }
        else{
            HashSet<Owner> myOwners = new HashSet<>();
            myOwners.add(owner);
            assignedOwners.put(team,myOwners);
        }
        return true;
    }

    public boolean removeOwner(Team team, Owner owner) throws Exception {
        HashSet<Owner> owners = this.assignedOwners.get(team);
        if(owners == null || !owners.contains(owner)){
            throw new Exception("The user isn't assigned by this team owner");
        }
        else{
            owners.remove(owner);
            assignedOwners.put(team,owners);
        }
        return true;
    }

    public boolean addTeamManager(Team team, TeamManager teamManager) throws Exception {
        HashSet<TeamManager> teamManagers = this.assignedTeamManagers.get(team);
        if(teamManagers != null){
            if(teamManagers.contains(teamManager))
                throw new Exception("The user is already defined as team manager");
            teamManagers.add(teamManager);
            this.assignedTeamManagers.put(team,teamManagers);
        }
        else{
            HashSet<TeamManager> myTeamManagers = new HashSet<>();
            myTeamManagers.add(teamManager);
            assignedTeamManagers.put(team,myTeamManagers);
        }
        return true;
    }

    public boolean removeTeamManager(Team team, TeamManager teamManager) throws Exception {
        HashSet<TeamManager> teamManagers = this.assignedTeamManagers.get(team);
        if(teamManagers == null || !teamManagers.contains(teamManager)){
            throw new Exception("The user isn't assigned by this team owner");
        }
        else{
            teamManagers.remove(teamManager);
            assignedTeamManagers.put(team,teamManagers);
        }
        return true;
    }

    public HashMap<Team, HashSet<Owner>> getAssignedOwners() {
        return assignedOwners;
    }

    public HashMap<Team, HashSet<TeamManager>> getAssignedTeamManagers() {
        return assignedTeamManagers;
    }

    public SignedUser getAdditionalRole() {
        return additionalRole;
    }

    public String additionalRole(){
        return this.additionalRole.getClass().getSimpleName();
    }

    public void setAdditionalRole(SignedUser additionalRole) throws Exception {
        if (!(additionalRole instanceof Footballer || additionalRole instanceof Coach || additionalRole instanceof TeamManager))
            throw new Exception("Only the following combinations are allowed: teamOwner and (Footballer or Coach or TeamManager)");
        this.additionalRole = additionalRole;
    }

    @Override
    public boolean deleteUser() throws Exception {
        HashMap<Team, ManagementUser> teams = this.getTeams();
        for (Team team : teams.keySet()) {
            if (team.getTeamOwners().size() == 1 && team.getTeamOwners().contains(this)) {
                throw new Exception("Can't remove this user from the system since he is the only team owner of " + team.getTeamName());
            }
        }
        for (Team team : this.teams.keySet()) {
            team.removeTeamMember(this);
        }
        for (Map.Entry<Team, HashSet<Owner>> teamHashSetEntry : assignedOwners.entrySet()) {
            for (Owner owner : teamHashSetEntry.getValue()) {
                TeamOwnerController.removeTeamOwner(this, teamHashSetEntry.getKey(), owner);
            }
        }
        for (Map.Entry<Team, HashSet<TeamManager>> teamHashSetEntry : assignedTeamManagers.entrySet()) {
            for (TeamManager teamManager : teamHashSetEntry.getValue()) {
                TeamOwnerController.removeTeamManager(this, teamHashSetEntry.getKey(), teamManager);
            }
        }
        SystemController.archiveUsers.put(this.getUserName(),this);
        SystemController.userNameUser.remove(this);
        return true;
    }
}
