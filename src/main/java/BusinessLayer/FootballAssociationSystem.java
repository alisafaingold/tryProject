package BusinessLayer;

import BusinessLayer.Enum.TeamState;
import BusinessLayer.Football.Game;
import BusinessLayer.Football.Season;
import BusinessLayer.Football.Team;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.Owner;
import BusinessLayer.Users.SignedUser;
import BusinessLayer.Users.SystemManager;
import CrossCutting.Utils;
import DB.*;
import ExternalServices.ExternalServices;
import ServiceLayer.Controllers.FanController;
import ServiceLayer.Controllers.GuestController;
import ServiceLayer.Controllers.SignedInController;
import org.apache.commons.validator.routines.EmailValidator;

public class FootballAssociationSystem {

    public static boolean initializeSystem(String systemManagerEmail) throws Exception {
        boolean valid = EmailValidator.getInstance().isValid(systemManagerEmail);
        if(!valid){
            throw new Exception("Not valid email");
        }

        SystemManager systemManager = new SystemManager(systemManagerEmail, Utils.sha256("initialPassword"+systemManagerEmail),"system","manager",
                systemManagerEmail);
        SystemController.userNameUser.put(systemManagerEmail, systemManager);

        ExternalServices.sendInviteToTheSystem(systemManagerEmail,systemManagerEmail,"initialPassword"+systemManagerEmail,"System owner");
        ExternalServices.establishConnectionToTaxSystem();
        ExternalServices.establishConnectionToAssociationAccountingSystem();
        return true;
    }

    public static void main(String[] args) throws Exception {

//        Season season;
//        Referee referee1;
//        Referee referee2;
//        HashSet<Team> teams;
//        Owner owner;
//        Team team;
//        Game game;
//        String myDate = "2021/10/29 18:10:45";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = sdf.parse(myDate);
//        long millis = date.getTime();
//        season = new Season(2021,millis);
//        referee1 = new Referee("yoyo","123y",3,"yo","yo","yoyo@gmail.com", RefereeTraining.Medium);
//        referee2 = new Referee("toto","123t",4,"to","to","toto@gmail.com",RefereeTraining.Medium);
//        teams = new HashSet<>();
//        owner = new Owner("google","123","goo","gle","google@gmail.com");
//        team = new Team("inbarsTeamRool", TeamState.active,owner);
//        teams.add(team);
//        season.addReferee(referee1.getRefereeTraining(),referee1);
//        season.addReferee(referee2.getRefereeTraining(),referee2);
        UserDao userDao = new UserDao();
        TeamDao teamDao = new TeamDao();
        SeasonDao seasonDao = new SeasonDao();

        Season season1 = new Season(2020,12042020);
        Owner owner1 = new Owner("ron","123","ron","niceman","ron@gmail.com");
        Owner owner2 = new Owner("dan","123","dan","glizman","dan@gmail.com");
        Team team1= new Team("beitar", TeamState.active,owner1);
        Team team2 = new Team("galil",TeamState.active,owner2);

//
//        teamDao.save(team1);
//        teamDao.save(team2);

        Team t1=null;
        Team t2=null;
        int counter=1;

        for (Object o : teamDao.getAll()) {
            if(counter==1)
                t1= (Team) o;
            if(counter==2)
                t2= (Team) o;
            counter++;
        }

//
//        userDao.save(owner1);
//        userDao.save(owner2);

//        seasonDao.save(season1);
        Owner byEmail2 = (Owner)userDao.getByEmail("ron@gmail.com");
        Owner byEmail1 = (Owner) userDao.getByEmail("dan@gmail.com");



        Game game = new Game(season1,t1,t2);
//        Fan fan = new Fan("fan1","123","the","fan","thefan@gmail.com");
        FanController fanController = new FanController();
        SignedInController signedInController = new SignedInController();
        GuestController guestController = new GuestController();
//        boolean b = guestController.singUp("thefan@gmail.com", "123321", "a", "f");
        SignedUser byEmail = userDao.getByEmail("thefan@gmail.com");
        GamesDao gamesDao = new GamesDao();
        gamesDao.save(game);
        fanController.subscribe((Fan) byEmail,game);


//        Fan f = new Fan("shachar@gmail.com", "12345654", "shachar", "rumney", "shachar@gmail.com");
//        Gson jjs = new Gson();
//        String s = "Fan";
//        String x = jjs.toJson(season);
//
//
//
//        CURDOperations curdOperations = new CURDOperations();
//        curdOperations.save(season);
    }
}




