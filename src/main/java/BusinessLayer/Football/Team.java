package BusinessLayer.Football;

import BusinessLayer.Enum.TeamState;
import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.Users.*;
import DB.FieldDao;
import ServiceLayer.Controllers.PersonalPageSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Team {
    private String teamName;
    private TeamState state;
    private Members teamMembers;
    private HashSet<FinanceActivity> financeActivities;
    private String _id;


    PersonalPage teamPersonalPage;

    public Team(String teamName, TeamState state, ManagementUser managementUser) {
        this.teamName = teamName;
        this.state = state;
        this.teamMembers = new Members();
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
        //TODO fields
        List<Field> addedFields = new ArrayList<>();
        FieldDao fieldDao = new FieldDao();
        List teamFields = fieldDao.getTeamFields(this._id);
        for (Field field : fields) {
            if (teamFields.contains(field)) {
                teamFields.removeAll(addedFields);
                throw new Exception("The following field is already related to the team" + field);
            } else {
                teamFields.add(field);
                teamFields.add(field);
            }
        }
        for (Object teamField : teamFields) {
            fieldDao.update((Field) teamField);
        }
        return true;
    }

    public boolean removeField(Field... fields) throws Exception {
        List<Field> removedAssets = new ArrayList<>();
        FieldDao fieldDao = new FieldDao();
        List teamFields = fieldDao.getTeamFields(this._id);
        for (Field field : fields) {
            if (teamFields.contains(field)) {
                removedAssets.add(field);
                teamFields.remove(field);
            } else {
                teamFields.addAll(removedAssets);
                throw new Exception("The following field is not related to the team" + field);
            }
        }
        for (Object teamField : teamFields) {
            fieldDao.update((Field) teamField);
        }
        return true;
    }

//    public HashSet<Footballer> getTeamFootballers() {
//        return this.teamMembers.getFootballers();
//    }
//
//    public HashSet<Coach> getTeamCoaches() {
//        return this.teamMembers.getCoaches();
//    }
//
//    public HashSet<Owner> getTeamOwners() {
//        return this.teamMembers.getOwners();
//    }
//
//    public HashSet<TeamManager> getTeamManagers() {
//        return this.teamMembers.getTeamManagers();
//    }

    public boolean addFinanceActivity(FinanceActivity financeActivity) {
        this.financeActivities.add(financeActivity);
        return true;
    }
    public HashSet<Field> getFields() {
        //TODO FIELD DAO
        HashSet<Field> fields = new HashSet<>();
        FieldDao fieldDao = new FieldDao();
        fields.addAll(fieldDao.getTeamFields(this._id));
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