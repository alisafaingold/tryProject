package ServiceLayer.Controllers;

import BusinessLayer.Users.*;
import CrossCutting.Utils;
import DB.SystemController;
import BusinessLayer.Enum.CoachPosition;
import BusinessLayer.Enum.FootballerPosition;
import BusinessLayer.Enum.TeamManagerPermissions;
import BusinessLayer.Enum.TeamState;
import ExternalServices.ExternalServices;
import BusinessLayer.Football.Field;
import BusinessLayer.Football.FinanceActivity;
import BusinessLayer.Football.Team;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TeamOwnerController {
    //Wasn't in UC
    public static Footballer signUpNewFootballer(ManagementUser teamOwner, String firstName, String lastName, String email,
                                                 FootballerPosition footballerPosition, Team team) throws Exception {
        if (teamOwner instanceof Owner) {

            boolean valid = EmailValidator.getInstance().isValid(email);
            if (!valid)
                throw new Exception("Not valid Email");

            String username = email;
            String password = lastName + "_" + firstName + "_123";

            if (SystemController.userNameUser.containsKey(username))
                throw new Exception("This user name already exist in the system");

            //TODO Send Email

            String hashPassword = Utils.sha256(password);

            Footballer footballer = new Footballer(username, hashPassword, firstName, lastName, email, footballerPosition);
            SystemController.userNameUser.put(username, footballer);

            addMemberToTeam(teamOwner, team, footballer);
            return footballer;
        } else
            throw new Exception("The user doesn't have permissions for this one");

    }

    public static Coach signUpNewCoach(ManagementUser teamOwner, String firstName, String lastName, String email,
                                       CoachPosition coachPosition, Team team) throws Exception {
        if (teamOwner instanceof Owner) {
            boolean valid = EmailValidator.getInstance().isValid(email);
            if (!valid)
                throw new Exception("Not valid Email");

            String username = email;
            String password = lastName + "_" + firstName + "_123";

            if (SystemController.userNameUser.containsKey(username))
                throw new Exception("This user already exist in the system");

            //TODO Send Email

            String hashPassword = Utils.sha256(password);

            Coach coach = new Coach(username, hashPassword, firstName, lastName, email, coachPosition);
            SystemController.userNameUser.put(username, coach);

            addMemberToTeam(teamOwner, team, coach);
            return coach;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    public static boolean changePermissionsForTeamManager(ManagementUser managementUser, TeamManager teamManager,
                                                          EnumMap<TeamManagerPermissions, Boolean> changes) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.EditPermissions))) {
            if (teamManager == managementUser)
                throw new Exception("User can't edit his own Permissions");
            teamManager.changePermissions(changes);
        }
        return true;
    }


    //UC 6.1
    public static Team addNewTeamToSystem(ManagementUser teamOwner, String teamName) throws Exception {
        if (teamOwner instanceof Owner) {
            if (SystemController.systemTeams.stream().anyMatch(team -> team.getTeamName().equals(teamName)))
                throw new Exception("There is already team with the exact name");
            Team newTeam = new Team(teamName, TeamState.notActive, teamOwner);
            boolean newTeamAuthorization = ExternalServices.getNewTeamAuthorization(newTeam);
            if (!newTeamAuthorization)
                throw new Exception("There is already team with the exact name");
            newTeam.setStatus(TeamState.active);
            newTeam.addTeamMember(null, teamOwner);
            return newTeam;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.1.1
    public static boolean addFieldToTeam(ManagementUser managementUser, Team team, Field field) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.AddAsset))) {
            team.addField(field);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    public static boolean addMemberToTeam(ManagementUser managementUser, Team team, TeamUser teamUser) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.AddAsset))) {
            team.addTeamMember(managementUser, teamUser);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.1.2
    public static boolean removeFieldFromTeam(ManagementUser managementUser, Team team, Field field) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.RemoveAsset))) {
            team.removeField(field);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    public static boolean removeMemberFromTeam(ManagementUser managementUser, Team team, TeamUser teamUser) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.AddAsset))) {
            team.removeTeamMember(managementUser, teamUser);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.1.3
    public static boolean editAssetOfTeam(ManagementUser managementUser, Asset asset, HashMap<String, String> changes) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.EditAsset))) {
            return asset.editAsset(changes);
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.2
    public static boolean addUserAsTeamOwner(ManagementUser addingOwner, Team team, SignedUser newOwner) throws Exception {
        if (addingOwner instanceof Owner || (addingOwner instanceof TeamManager && ((TeamManager) addingOwner).hasPermission(TeamManagerPermissions.AddOwner))) {
            HashSet<Owner> teamOwners = team.getTeamOwners();
            if (teamOwners.stream().anyMatch(owner -> owner == newOwner))
                throw new Exception("The user is already defined as team owner");
            if (newOwner instanceof Owner) {
                team.addTeamMember(addingOwner, newOwner);
                addingOwner.addOwner(team, (Owner) newOwner);
            } else {
                Owner owner = new Owner(newOwner);
                team.addTeamMember(addingOwner, owner);
                addingOwner.addOwner(team, (Owner) owner);
                SystemController.userNameUser.put(owner.getUserName(),owner);
            }
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.3
    public static boolean removeTeamOwner(ManagementUser removingOwner, Team team, Owner ownerToRemove) throws Exception {
        if (removingOwner instanceof Owner || (removingOwner instanceof TeamManager && ((TeamManager) removingOwner).hasPermission(TeamManagerPermissions.RemoveOwner))) {
            HashSet<Owner> teamOwners = team.getTeamOwners();
            if (teamOwners.stream().anyMatch(owner -> owner == ownerToRemove)) {
                team.removeTeamMember(ownerToRemove);
                removingOwner.removeOwner(team, ownerToRemove);
                validateAndRemoveManagementUser(ownerToRemove);
                //TODO send alerts to the removed
            } else {
                throw new Exception("The select user is not team Owner");
            }
        } else
            throw new Exception("The user doesn't have permissions for this one");
        return true;
    }

    private static void validateAndRemoveManagementUser(ManagementUser ownerToRemove) throws Exception {
        HashMap<Team, HashSet<Owner>> assignedOwners = ownerToRemove.getAssignedOwners();
        HashMap<Team, HashSet<TeamManager>> assignedTeamManagers = ownerToRemove.getAssignedTeamManagers();
        for (Map.Entry<Team, HashSet<Owner>> teamHashSetEntry : assignedOwners.entrySet()) {
            for (Owner owner : teamHashSetEntry.getValue()) {
                removeTeamOwner(ownerToRemove, teamHashSetEntry.getKey(), owner);
            }
        }
        for (Map.Entry<Team, HashSet<TeamManager>> teamHashSetEntry : assignedTeamManagers.entrySet()) {
            for (TeamManager teamManager : teamHashSetEntry.getValue()) {
                removeTeamManager(ownerToRemove, teamHashSetEntry.getKey(), teamManager);
            }
        }
        if (ownerToRemove.getTeams().size() == 0) {
            //No more Owner
            SignedUser additionalRole = ownerToRemove.getAdditionalRole();
            ownerToRemove.deleteUser();
            if (additionalRole != null) {
                SystemController.userNameUser.put(additionalRole.getUserName(), additionalRole);
            } else {
                SystemController.archiveUsers.put(ownerToRemove.getUserName(), ownerToRemove);
                SystemController.userNameUser.remove(ownerToRemove);
            }
        }
    }

    //UC 6.4
    public static TeamManager signUpNewTeamManager(ManagementUser addingOwner, String firstName, String lastName, String email, Team team) throws Exception {
        if (addingOwner instanceof Owner || (addingOwner instanceof TeamManager && ((TeamManager) addingOwner).hasPermission(TeamManagerPermissions.AddManager))) {
            boolean valid = EmailValidator.getInstance().isValid(email);
            if (!valid)
                throw new Exception("Not valid Email");

            String username = email;
            String password = lastName + "_" + firstName + "_123";

            if (SystemController.userNameUser.containsKey(username))
                throw new Exception("This user already exist in the system");

            //TODO Send Email

            String hashPassword = Utils.sha256(password);

            TeamManager teamManager = new TeamManager(username, hashPassword, firstName, lastName, email);
            SystemController.userNameUser.put(username, teamManager);

            addMemberToTeam(addingOwner, team, teamManager);
            addingOwner.addTeamManager(team, teamManager);

            //TODO this user need to be changed to Owner
            return teamManager;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.5
    public static boolean removeTeamManager(ManagementUser removingOwner, Team team, TeamManager managerToRemove) throws Exception {
        if (removingOwner instanceof Owner || (removingOwner instanceof TeamManager && ((TeamManager) removingOwner).hasPermission(TeamManagerPermissions.RemoveManager))) {
            HashSet<TeamManager> teamManagers = team.getTeamManagers();
            if (teamManagers.stream().anyMatch(teamManager -> teamManager == managerToRemove)) {
                team.removeTeamMember(managerToRemove);
                removingOwner.removeTeamManager(team, managerToRemove);
                validateAndRemoveManagementUser(managerToRemove);
                //TODO send alerts to the removed
            } else {
                throw new Exception("The select user is not team manager");
            }
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.6.1
    public static boolean closeTeam(ManagementUser managementUser, Team team) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.CloseTeam))) {
            if (team.getState() == TeamState.active) {
                team.setStatus(TeamState.notActive);
                //Todo send alerts
            } else {
                throw new Exception("This team is already closed");
            }
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

    //UC 6.6.2
    public static boolean openTeam(ManagementUser managementUser, Team team) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.OpenTeam))) {
            TeamState state = team.getState();
            if (state == TeamState.notActive) {
                team.setStatus(TeamState.active);
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
    public static boolean addFinanceAction(ManagementUser managementUser, Team team, String kind, double amount, String description, long date,
                                           ManagementUser reporter) throws Exception {
        if (managementUser instanceof Owner || (managementUser instanceof TeamManager && ((TeamManager) managementUser).hasPermission(TeamManagerPermissions.EditAsset))) {
            FinanceActivity financeActivity = new FinanceActivity(kind, amount, description, date, reporter);
            team.addFinanceActivity(financeActivity);
            return true;
        } else
            throw new Exception("The user doesn't have permissions for this one");
    }

}
