package ServiceLayer.Controllers;

import BusinessLayer.SystemFeatures.PersonalPage;
import BusinessLayer.SystemFeatures.TeamMemberPersonalPage;
import BusinessLayer.SystemFeatures.TeamPersonalPage;
import BusinessLayer.Users.*;
import DB.PersonalPageDao;
import DB.SystemController;
import BusinessLayer.Football.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PersonalPageSystem {

    // ========== Add Personal Page to System ==========


    public static boolean createNewPersonalPage(ManagementUser managementUser, Team team){
        PersonalPageDao personalPageDao = new PersonalPageDao();
        TeamPersonalPage teamPersonalPage = new TeamPersonalPage(managementUser,team);
        personalPageDao.save(teamPersonalPage);
        return true;
    }


    public static boolean createNewPersonalPage(TeamUser tu){
        PersonalPageDao personalPageDao = new PersonalPageDao();
        TeamMemberPersonalPage teamMemberPersonalPage = new TeamMemberPersonalPage(tu);
        personalPageDao.save(teamMemberPersonalPage);
        return true;
    }

    // ==========  Archive Personal Page ==========
    public static boolean moveToArchive(PersonalPage personalPage) {
        PersonalPageDao personalPageDao = new PersonalPageDao();
        personalPageDao.delete(personalPage);
        //Logger
        SystemController.logger.info("Deletion | Personal Page have been move to archive; Personal Page ID: " + personalPage.get_id() +
                "; Owner ID: " + personalPage.getPageOwnerID());
        return true;
    }

    // ========== Update Personal Page ==========
    //Use Case 4.1 5.1
    public boolean updatePersonalPage(PersonalPage personalPage, HashMap<String,String> valuesToUpdate) {
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
        PersonalPageDao personalPageDao = new PersonalPageDao();
        personalPageDao.update(personalPage);
        return true;
    }

    //Use Case 4.2 5.2
    public boolean addContentToPersonalPage(PersonalPage personalPage, HashMap<String,String> valuesToUpdate){
        if(personalPage instanceof TeamMemberPersonalPage){
            for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
                ((TeamMemberPersonalPage) personalPage).setContent(entry.getKey()+": "+ entry.getValue()+"\n");
            }
            PersonalPageDao personalPageDao = new PersonalPageDao();
            personalPageDao.update(personalPage);
            return true;
        }
        return false;
    }

}
