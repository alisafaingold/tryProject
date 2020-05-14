package BusinessLayer;

import BusinessLayer.Enum.RefereeTraining;
import BusinessLayer.Football.Season;
import BusinessLayer.SystemFeatures.TeamMemberPersonalPage;
import BusinessLayer.Users.*;
import CrossCutting.Utils;
import DB.CURDOperations;
import DB.SystemController;
import BusinessLayer.Enum.TeamState;
import ExternalServices.ExternalServices;
import BusinessLayer.Football.Team;
import ServiceLayer.Controllers.*;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static BusinessLayer.Enum.FootballerPosition.Center_Back;

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
        Season season;
        Referee referee1;
        Referee referee2;
        HashSet<Team> teams;
        Owner owner;
        Team team;
        String myDate = "2021/10/29 18:10:45";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = sdf.parse(myDate);
        long millis = date.getTime();
        season = new Season(2021,millis);
        referee1 = new Referee("yoyo","123y",3,"yo","yo","yoyo@gmail.com", RefereeTraining.Medium);
        referee2 = new Referee("toto","123t",4,"to","to","toto@gmail.com",RefereeTraining.Medium);
        teams = new HashSet<>();
        owner = new Owner("google","123","goo","gle","google@gmail.com");
        team = new Team("inbarsTeamRool", TeamState.active,owner);
        teams.add(team);
        season.addReferee(referee1.getRefereeTraining(),referee1);
        season.addReferee(referee2.getRefereeTraining(),referee2);
//        Fan f = new Fan("shachar@gmail.com", "12345654", "shachar", "rumney", "shachar@gmail.com");
        Gson jjs = new Gson();
        String s = "Fan";
        String x = jjs.toJson(season);
        CURDOperations curdOperations = new CURDOperations();
//        curdOperations.save(season);
    }
}




