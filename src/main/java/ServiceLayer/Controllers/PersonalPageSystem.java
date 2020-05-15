package ServiceLayer.Controllers;

import BusinessLayer.Football.Team;
import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.SystemFeatures.TeamMemberPersonalPage;
import BusinessLayer.SystemFeatures.TeamPersonalPage;
import BusinessLayer.Users.Coach;
import BusinessLayer.Users.Footballer;
import BusinessLayer.Users.ManagementUser;
import BusinessLayer.Users.TeamUser;
import DB.PersonalPageDao;
import DB.SystemController;
import DB.TeamDao;
import DB.UserDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class PersonalPageSystem {
    private PersonalPageDao personalPageDao;
    private UserDao userDao;
    private TeamDao teamDao;

    public PersonalPageSystem() {
        personalPageDao = PersonalPageDao.getInstance();
        userDao = UserDao.getInstance();
        teamDao = TeamDao.getInstance();

    }
    // ========== Add Personal Page to System ==========


    public boolean createNewPersonalPage(ManagementUser managementUser, Team team) throws ClassNotFoundException {
        TeamPersonalPage teamPersonalPage = new TeamPersonalPage(managementUser, team);
        HashSet<Footballer> fotFootballers = new HashSet<>();
        HashSet<Coach> coaches = new HashSet<>();
        HashSet allFootballer = userDao.getAll(fotFootballers);
        HashSet allCoach = userDao.getAll(coaches);
        teamPersonalPage.setTeamCoach(allCoach);
        teamPersonalPage.setTeamFootballers(allFootballer);
        personalPageDao.save(teamPersonalPage);
        return true;

    }


    public boolean createNewPersonalPage(TeamUser tu) {

        TeamMemberPersonalPage teamMemberPersonalPage = new TeamMemberPersonalPage(tu);
        personalPageDao.save(teamMemberPersonalPage);
        return true;
    }


    // ==========  Archive Personal Page ==========
    public boolean moveToArchive(String personalPageID) throws Exception {
        try {
            personalPageDao.delete(personalPageID);
            //Logger
            SystemController.logger.info("Deletion | Personal Page have been move to archive; Personal Page ID: " + personalPageID);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    // ========== Update Personal Page ==========
    //Use Case 4.1 5.1
    public boolean updatePersonalPage(String personalPageID, HashMap<String, String> valuesToUpdate) throws Exception {
        Optional optional = personalPageDao.get(personalPageID);
        if (optional.isPresent()) {
            PersonalPage personalPage = (PersonalPage) optional.get();
            if (personalPage instanceof TeamMemberPersonalPage) {
                for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
                    switch (entry.getKey()) {
                        case "birthday":
                            ((TeamMemberPersonalPage) personalPage).setBirthday(entry.getValue());
                            break;
                        case "history":
                            ((TeamMemberPersonalPage) personalPage).setHistory(entry.getValue());
                            break;
                        case "hobbies":
                            ((TeamMemberPersonalPage) personalPage).setHobbies(entry.getValue());
                            break;
                        case "role":
                            ((TeamMemberPersonalPage) personalPage).setRole(entry.getValue());
                            break;
                        case "team":
                            ((TeamMemberPersonalPage) personalPage).setTeam(entry.getValue());
                            break;
                    }
                }
            } else {
                for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
                    switch (entry.getKey()) {
                        case "coachName":
                            ((TeamPersonalPage) personalPage).setCoachName(entry.getValue());
                            break;
                        case "teamFootballerMembers":
                            ((TeamPersonalPage) personalPage).setTeamFootballerMembers(entry.getValue());
                            break;
                        case "teamFields":
                            ((TeamPersonalPage) personalPage).setTeamFields(entry.getValue());
                            break;
                        case "records":
                            ((TeamPersonalPage) personalPage).setRecords(entry.getValue());
                            break;
                        case "Games":
                            ((TeamPersonalPage) personalPage).setGames(entry.getValue());
                            break;
                    }
                }
            }
            personalPageDao.update(personalPage);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //Use Case 4.2 5.2
    public boolean addContentToPersonalPage(String personalPageID, HashMap<String, String> valuesToUpdate) throws Exception {
        Optional optional = personalPageDao.get(personalPageID);
        if (optional.isPresent()) {
            PersonalPage personalPage = (PersonalPage) optional.get();
            if (personalPage instanceof TeamMemberPersonalPage) {
                for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
                    ((TeamMemberPersonalPage) personalPage).setContent(entry.getKey() + ": " + entry.getValue() + "\n");
                }
                personalPageDao.update(personalPage);
                return true;
            }
            return false;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

}
