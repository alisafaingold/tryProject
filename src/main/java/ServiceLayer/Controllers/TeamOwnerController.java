package ServiceLayer.Controllers;

import BusinessLayer.Enum.CoachPosition;
import BusinessLayer.Enum.FootballerPosition;
import BusinessLayer.Enum.TeamManagerPermissions;
import BusinessLayer.Enum.TeamState;
import BusinessLayer.Football.Field;
import BusinessLayer.Football.FinanceActivity;
import BusinessLayer.Football.Team;
import BusinessLayer.Users.*;
import CrossCutting.Utils;
import DB.ComplaintDao;
import DB.FieldDao;
import DB.TeamDao;
import DB.UserDao;
import ExternalServices.ExternalServices;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.EnumMap;
import java.util.HashMap;

public class TeamOwnerController {
    private UserDao userDao;
    private TeamDao teamDao;
    private ComplaintDao complaintDao;
    private FieldDao fieldDao;

    public TeamOwnerController() {
        userDao = UserDao.getInstance();
        teamDao = TeamDao.getInstance();
        complaintDao = ComplaintDao.getInstance();
        fieldDao = FieldDao.getInstance();
    }

    //Wasn't in UC
    public Footballer signUpNewFootballer(ManagementUser teamOwner, String firstName, String lastName, String email,
                                                 FootballerPosition footballerPosition, Team team) throws Exception {
        if (teamOwner instanceof Owner) {

            boolean valid = EmailValidator.getInstance().isValid(email);
            if (!valid)
                throw new Exception("Not valid Email");

            String username = email;
            String password = lastName + "_" + firstName + "_123";

            SignedUser byEmail = userDao.getByEmail(email);
            if (byEmail != null)
                throw new Exception("This user name already exist in the system");

            //TODO Send Email

            String hashPassword = Utils.sha256(password);

            Footballer footballer = new Footballer(username, hashPassword, firstName, lastName, email, footballerPosition);
            userDao.save(footballer);
//            SystemController.userNameUser.put(username, footballer);

            addMemberToTeam(teamOwner, team, footballer);
            userDao.update(teamOwner);
            return footballer;
        } else
            throw new Exception("The user doesn't have permissions for this one");

    }

    public Coach signUpNewCoach(ManagementUser teamOwner, String firstName, String lastName, String email,
                                       CoachPosition coachPosition, Team team) throws Exception {
        if (teamOwner instanceof Owner) {
            boolean valid = EmailValidator.getInstance().isValid(email);
            if (!valid)
                throw new Exception("Not valid Email");

            String username = email;
            String password = lastName + "_" + firstName + "_123";

            SignedUser byEmail = userDao.getByEmail(email);
            if (byEmail != null)
                throw new Exception("This user name already exist in the system");

            //TODO Send Email

            String hashPassword = Utils.sha256(password);

            Coach coach = new Coach(username, hashPassword, firstName, lastName, email, coachPosition);
            userDao.save(coach);
            addMemberToTeam(teamOwner, team, coach);
            userDao.update(teamOwner);

            return coach;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    public boolean changePermissionsForTeamManager(ManagementUser managementUser, TeamManager teamManager,
                                                          EnumMap<TeamManagerPermissions, Boolean> changes) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.EditPermissions))) {
            if (teamManager == managementUser)
                throw new Exception("User can't edit his own Permissions");
            teamManager.changePermissions(changes);
            userDao.update(teamManager);
        }
        return true;
    }


    //UC 6.1
    public Team addNewTeamToSystem(ManagementUser teamOwner, String teamName) throws Exception {
        if (teamOwner instanceof Owner) {
            if (teamDao.getByTeamName(teamName).isPresent())
                throw new Exception("There is already team with the exact name");
            Team newTeam = new Team(teamName, TeamState.notActive, teamOwner);
            boolean newTeamAuthorization = ExternalServices.getNewTeamAuthorization(newTeam);
            if (!newTeamAuthorization)
                throw new Exception("There is already team with the exact name");
            newTeam.setStatus(TeamState.active);
            newTeam.addTeamMember(null, teamOwner);
            teamDao.save(newTeam);
            userDao.update(teamOwner);
            return newTeam;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.1.1
    public boolean addFieldToTeam(ManagementUser managementUser, Team team, Field field) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.AddAsset))) {
            team.addField(field);
            teamDao.update(team);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    public boolean addMemberToTeam(ManagementUser managementUser, Team team, TeamUser teamUser) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.AddAsset))) {
            team.addTeamMember(managementUser, teamUser);
            teamDao.update(team);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.1.2
    public boolean removeFieldFromTeam(ManagementUser managementUser, Team team, Field field) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.RemoveAsset))) {
            team.removeField(field);
            teamDao.update(team);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    public boolean removeMemberFromTeam(ManagementUser managementUser, Team team, TeamUser teamUser) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.AddAsset))) {
            team.removeTeamMember(managementUser, teamUser);
            teamDao.update(team);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //Maybe we shouldn't change
    //UC 6.1.3
    public boolean editAssetOfTeam(ManagementUser managementUser, Asset asset, HashMap<String, String> changes) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.EditAsset))) {
            asset.editAsset(changes);
            if(asset instanceof Field)
                fieldDao.update((Field) asset);
            else if (asset instanceof SignedUser)
                userDao.update((SignedUser) asset);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //Maybe remove
    //UC 6.2
//    public static boolean addUserAsTeamOwner(ManagementUser addingOwner, Team team, SignedUser newOwner) throws Exception {
//        if (addingOwner instanceof Owner || (addingOwner instanceof TeamManager && ((TeamManager) addingOwner).hasPermission(TeamManagerPermissions.AddOwner))) {
//            HashSet<Owner> teamOwners = team.getTeamOwners();
//            if (teamOwners.stream().anyMatch(owner -> owner == newOwner))
//                throw new Exception("The user is already defined as team owner");
//            if (newOwner instanceof Owner) {
//                team.addTeamMember(addingOwner, newOwner);
//                addingOwner.addOwner(team, (Owner) newOwner);
//            } else {
//                Owner owner = new Owner(newOwner);
//                owner.set_id(newOwner.get_id());
//                team.addTeamMember(addingOwner, owner);
//                addingOwner.addOwner(team, owner);
//                userDao.update(owner);
//                userDao.update(addingOwner);
//
//            }
//            return true;
//        } else
//            throw new Exception("The user doesn't have permissions for this one");
//    }

    //Maybe Remove
//    //UC 6.3
//    public static boolean removeTeamOwner(ManagementUser removingOwner, Team team, Owner ownerToRemove) throws Exception {
//        if (removingOwner instanceof Owner || (removingOwner instanceof TeamManager && ((TeamManager) removingOwner).hasPermission(TeamManagerPermissions.RemoveOwner))) {
//            HashSet<Owner> teamOwners = team.getTeamOwners();
//            if (teamOwners.stream().anyMatch(owner -> owner == ownerToRemove)) {
//                team.removeTeamMember(ownerToRemove);
//                removingOwner.removeOwner(team, ownerToRemove);
//                validateAndRemoveManagementUser(ownerToRemove);
//                //TODO send alerts to the removed
//            } else {
//                throw new Exception("The select user is not team Owner");
//            }
//        } else
//            throw new Exception("The user doesn't have permissions for this one");
//        return true;
//    }

//    private static void validateAndRemoveManagementUser(ManagementUser ownerToRemove) throws Exception {
//        HashMap<Team, HashSet<Owner>> assignedOwners = ownerToRemove.getAssignedOwners();
//        HashMap<Team, HashSet<TeamManager>> assignedTeamManagers = ownerToRemove.getAssignedTeamManagers();
//        for (Map.Entry<Team, HashSet<Owner>> teamHashSetEntry : assignedOwners.entrySet()) {
//            for (Owner owner : teamHashSetEntry.getValue()) {
//                removeTeamOwner(ownerToRemove, teamHashSetEntry.getKey(), owner);
//            }
//        }
//        for (Map.Entry<Team, HashSet<TeamManager>> teamHashSetEntry : assignedTeamManagers.entrySet()) {
//            for (TeamManager teamManager : teamHashSetEntry.getValue()) {
//                removeTeamManager(ownerToRemove, teamHashSetEntry.getKey(), teamManager);
//            }
//        }
//        if (ownerToRemove.getTeams().size() == 0) {
//            //No more Owner
//            SignedUser additionalRole = ownerToRemove.getAdditionalRole();
//            ownerToRemove.deleteUser();
//            if (additionalRole != null) {
//                SystemController.userNameUser.put(additionalRole.getUserName(), additionalRole);
//            } else {
//                SystemController.archiveUsers.put(ownerToRemove.getUserName(), ownerToRemove);
//                SystemController.userNameUser.remove(ownerToRemove);
//            }
//        }
//    }

    //UC 6.4
    public TeamManager signUpNewTeamManager(ManagementUser addingOwner, String firstName, String lastName, String email, Team team) throws Exception {
        if (addingOwner instanceof Owner || (addingOwner instanceof TeamManager && ((TeamManager) addingOwner).hasPermission(TeamManagerPermissions.AddManager))) {
            boolean valid = EmailValidator.getInstance().isValid(email);
            if (!valid)
                throw new Exception("Not valid Email");

            String username = email;
            String password = lastName + "_" + firstName + "_123";

            SignedUser byEmail = userDao.getByEmail(email);
            if (byEmail != null)
                throw new Exception("This user name already exist in the system");

            //TODO Send Email

            String hashPassword = Utils.sha256(password);

            TeamManager teamManager = new TeamManager(username, hashPassword, firstName, lastName, email);

            addMemberToTeam(addingOwner, team, teamManager);
//            addingOwner.addTeamManager(team, teamManager);

            userDao.save(teamManager);
            addMemberToTeam(addingOwner, team, teamManager);
            userDao.update(addingOwner);
            //TODO this user need to be changed to Owner
            return teamManager;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

//    //UC 6.5
//    public static boolean removeTeamManager(ManagementUser removingOwner, Team team, TeamManager managerToRemove) throws Exception {
//        if (removingOwner instanceof Owner || (removingOwner instanceof TeamManager && ((TeamManager) removingOwner).hasPermission(TeamManagerPermissions.RemoveManager))) {
//            HashSet<TeamManager> teamManagers = team.getTeamManagers();
//            if (teamManagers.stream().anyMatch(teamManager -> teamManager == managerToRemove)) {
//                team.removeTeamMember(managerToRemove);
//                removingOwner.removeTeamManager(team, managerToRemove);
//                validateAndRemoveManagementUser(managerToRemove);
//                //TODO send alerts to the removed
//            } else {
//                throw new Exception("The select user is not team manager");
//            }
//            return true;
//        } else
//            throw new Exception("The user doesn't have permissions for this one");
//    }

    //UC 6.6.1
    public boolean closeTeam(ManagementUser managementUser, Team team) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.CloseTeam))) {
            if (team.getState() == TeamState.active) {
                team.setStatus(TeamState.notActive);
                teamDao.update(team);
                //Todo send alerts
            } else {
                throw new Exception("This team is already closed");
            }
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.6.2
    public boolean openTeam(ManagementUser managementUser, Team team) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.OpenTeam))) {
            TeamState state = team.getState();
            if (state == TeamState.notActive) {
                team.setStatus(TeamState.active);
                teamDao.update(team);
                //Todo send alerts
            } else {
                if (state == TeamState.active)
                    throw new Exception("This team is already active");
                else if (state == TeamState.permanentlyClosed)
                    throw new Exception("This team is permanently closed, please contact the system manager");
            }
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.7
    public boolean addFinanceAction(ManagementUser managementUser, Team team, String kind, double amount, String description, long date,
                                           ManagementUser reporter) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.EditAsset))) {
            FinanceActivity financeActivity = new FinanceActivity(kind, amount, description, date, reporter);
            team.addFinanceActivity(financeActivity);
            teamDao.update(team);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

}
