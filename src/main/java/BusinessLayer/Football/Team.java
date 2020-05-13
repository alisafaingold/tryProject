package BusinessLayer.Football;

import BusinessLayer.Enum.TeamState;
import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.SystemFeatures.TeamPersonalPage;
import BusinessLayer.Users.*;
import ServiceLayer.Controllers.PersonalPageSystem;
import DB.SystemController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Team {
    private String teamName;
    private TeamState state;
    private Members teamMembers;
    private HashSet<Field> fields;
    private HashSet<FinanceActivity> financeActivities;
    private String _id;


    PersonalPage teamPersonalPage;

    public Team(String teamName, TeamState state, ManagementUser managementUser) {
        this.teamName = teamName;
        this.state = state;
        this.teamMembers = new Members();
        this.fields = new HashSet<>();
        this.financeActivities = new HashSet<>();
        PersonalPageSystem.createNewPersonalPage(managementUser,this);
    }


    public boolean addTeamMember(ManagementUser managementUser, SignedUser... signedUsers) throws Exception {
        List<SignedUser> addedUsers = new ArrayList<>();
        for (SignedUser signedUser : signedUsers) {
            if (signedUser instanceof TeamManager || signedUser instanceof Owner || signedUser instanceof Footballer || signedUser instanceof Coach) {
                teamMembers.addMember(signedUser);
                addedUsers.add(signedUser);
                ((TeamUser) signedUser).addTeam(this, managementUser);
            } else {
                for (SignedUser addedUser : addedUsers) {
                    teamMembers.removeMember(addedUser);
                }
                throw new Exception("Team members can be only Footballer, Coach, Owner, TeamManager");
            }
        }
        return true;
    }

    public boolean removeTeamMember(SignedUser... signedUsers) throws Exception {
        List<SignedUser> removedUsers = new ArrayList<>();
        for (SignedUser signedUser : signedUsers) {
            if (signedUser instanceof TeamManager || signedUser instanceof Owner || signedUser instanceof Footballer || signedUser instanceof Coach) {
                teamMembers.removeMember(signedUser);
                removedUsers.add(signedUser);
                ((TeamUser) signedUser).removeTeam(this);
            } else {
                for (SignedUser removed : removedUsers) {
                    teamMembers.addMember(removed);
                }
                throw new Exception("The following user isn't related to this team : " + signedUser);
            }
        }
        return true;
    }

    public boolean addField(Field... fields) throws Exception {
        List<Field> addedFields = new ArrayList<>();
        for (Field field : fields) {
            if (this.fields.contains(field)) {
                this.fields.removeAll(addedFields);
                throw new Exception("The following field is already related to the team" + field);
            } else {
                addedFields.add(field);
                this.fields.add(field);
            }
        }
        return true;
    }

    public boolean removeField(Field... fields) throws Exception {
        List<Field> removedAssets = new ArrayList<>();
        for (Field field : fields) {
            if (this.fields.contains(field)) {
                removedAssets.add(field);
                this.fields.remove(field);
            } else {
                this.fields.addAll(removedAssets);
                throw new Exception("The following field is not related to the team" + field);
            }
        }
        return true;
    }

    public HashSet<Footballer> getTeamFootballers() {
        return this.teamMembers.getFootballers();
    }

    public HashSet<Coach> getTeamCoaches() {
        return this.teamMembers.getCoaches();
    }

    public HashSet<Owner> getTeamOwners() {
        return this.teamMembers.getOwners();
    }

    public HashSet<TeamManager> getTeamManagers() {
        return this.teamMembers.getTeamManagers();
    }

    public boolean addFinanceActivity(FinanceActivity financeActivity) {
        this.financeActivities.add(financeActivity);
        return true;
    }
    public HashSet<Field> getFields() {
        return fields;
    }

    public boolean removeFinanceActivity(FinanceActivity financeActivity) {
        this.financeActivities.remove(financeActivity);
        return true;
    }

    public PersonalPage getTeamPersonalPage() {
        return teamPersonalPage;
    }
    public String getTeamName() {
        return teamName;
    }


    public TeamState getState() {
        return state;
    }

    public boolean setStatus(TeamState newState) {
        this.state = newState;
        return true;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}