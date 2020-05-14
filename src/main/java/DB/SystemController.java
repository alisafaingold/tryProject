package DB;

import ServiceLayer.Controllers.PersonalPageSystem;
import BusinessLayer.Football.League;
import BusinessLayer.Football.Season;
import BusinessLayer.Football.Team;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.Referee;
import BusinessLayer.Users.SignedUser;
import BusinessLayer.Users.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemController {
    public static HashMap<String, SignedUser> userNameUser= new HashMap<>();
    public static HashMap<String, League> leagueNameLeagues = new HashMap<>();
    public static HashSet<Team> systemTeams = new HashSet<>();
    public static HashSet<Team> archivedTeams = new HashSet<>();
    public static HashMap<String, SignedUser> archiveUsers= new HashMap<>();
    public static final Logger logger = LogManager.getLogger(SystemController.class.getName());


    //Use Case 2.5
        public static HashMap<String, HashSet<Object>> search(User user, String searchInput){
        if(user instanceof Fan){
            ((Fan) user).addToMySearches(System.currentTimeMillis(),searchInput);
        }
        HashMap<String, HashSet<Object>> returned = new HashMap<>();
//        String[] searchArray = searchInput.split(" ");
//
//        returned.put("League",new HashSet<>());
//        returned.put("Season",new HashSet<>());
//        for (League league : leagueNameLeagues.values()) {
//            for (String searchWord : searchArray) {
//                if(league.toString().contains(searchWord)){
//                    returned.get("League").add(league);
//                }
//                for (Season season : league.getLeaguesSeasons().values()) {
//                    if(season.toString().contains(searchWord)){
//                        returned.get("Season").add(season);
//                    }
//                }
//            }
//        }
//        returned.put("Footballer",new HashSet<>());
//        returned.put("Coach",new HashSet<>());
//        returned.put("Team",new HashSet<>());
//
//        returned.get("Footballer").addAll(PersonalPageSystem.searchInputFootballer(searchArray));
//        returned.get("Coach").addAll(PersonalPageSystem.searchInputCoach(searchArray));
//        returned.get("Team").addAll(PersonalPageSystem.searchInputTeam(searchArray));
        return returned;
    }






}
