package ServiceLayer.Controllers;

import BusinessLayer.Enum.RefereeTraining;
import BusinessLayer.Football.League;
import BusinessLayer.Football.Season;
import BusinessLayer.Football.Team;
import BusinessLayer.SeasonPolicies.AssignPolicy;
import BusinessLayer.SeasonPolicies.ScoreComputingPolicy;
import BusinessLayer.Users.AssociationRepresentative;
import BusinessLayer.Users.Referee;
import BusinessLayer.Users.SignedUser;
import CrossCutting.Utils;
import DB.*;
import ExternalServices.ExternalServices;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.*;

import static DB.SystemController.userNameUser;

public class AssociationRepresentativeController {
    private LeagueDao leagueDao;
    private SeasonDao seasonDao;
    private UserDao userDao;
    private TeamDao teamDao;

    public AssociationRepresentativeController() {
        leagueDao = LeagueDao.getInstance();
        seasonDao = SeasonDao.getInstance();
        userDao = UserDao.getInstance();
        teamDao = TeamDao.getInstance();
    }

    //Use Case 9.1
    public boolean defineNewLeague(String associationRepresentativeID, String leagueName, RefereeTraining refereeTraining) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        if(optional.isPresent()){
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            League leagueByName = leagueDao.getLeagueByName(leagueName);
            if (leagueByName!=null)
                throw new Exception("This league name is already exist");

            League newLeague = new League(leagueName, associationRepresentative, refereeTraining);
            leagueDao.save(newLeague);
            //Logger
            SystemController.logger.info("Creation | New league have been defined; Association Representative ID: " + associationRepresentative.get_id() +
                    "; League ID: " + newLeague.get_id() + "; League Name:" + newLeague.getLeagueName());

            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //Use Case 9.2
    public boolean addSeasonToLeague(String associationRepresentativeID, String leagueID, int year, long startDate) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = leagueDao.get(leagueID);
        if(optional.isPresent() && optional1.isPresent()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            League league = (League) optional1.get();
            int length = String.valueOf(year).length();
            if (length != 4 || year <= 2000 || year >= 2022) {
                throw new Exception("Not Valid Year");
            }

            List leagueSeasonByYear = seasonDao.getLeagueSeasonByYear(league.get_id(), year);
            if (!leagueSeasonByYear.isEmpty()) {
                throw new Exception("There is season in this year for this league");
            }

            Season season = new Season(year, startDate);
            season.setLeague(league);
            seasonDao.save(season);
            //Logger
            SystemController.logger.info("Creation | New Season have been add to league; season ID: " + season.get_id() +
                    "; League ID: " + league.get_id() + "; Association Representative ID:" + associationRepresentative.get_id());
            return true;

        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 9.3.1
    public boolean appointReferee(String associationRepresentativeID, int id, String fName, String lName, String email, RefereeTraining refereeTraining) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        if(optional.isPresent()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            int length = String.valueOf(id).length();
            String password = String.valueOf(id);
            boolean valid = EmailValidator.getInstance().isValid(email);

            if (length != 9)
                throw new Exception("Not Valid ID");
            if (!valid)
                throw new Exception("Not Valid Email");

            if (userDao.getByEmail(email) != null) {
                throw new Exception("This Email is already in use in The System");
            }
            if (userDao.getById(id + "") != null) {
                throw new Exception("This ID Exist In The System");
            }

            boolean send = ExternalServices.sendInviteToTheSystem(email, email, password, associationRepresentative.getFirstName() + " " + associationRepresentative.getLastName());
            if (!send)
                throw new Exception("Have been problem with send the email");

            String hashPassword = Utils.sha256(password);
            Referee newReferee = new Referee(email, hashPassword, id, fName, lName, email, refereeTraining);
            userDao.save(newReferee);
            //Logger
            SystemController.logger.info("Creation | New Referee have been appoint; referee ID: " + newReferee.get_id() +
                    "; Association Representative ID:" + associationRepresentative.get_id());

            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 9.3.2
    //TODO need to find replacer referee
    public boolean removeReferee(String associationRepresentativeID, String refereeID) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = userDao.get(refereeID);
        if(optional.isPresent() && optional1.isPresent()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            Referee referee = (Referee) optional1.get();
//        SignedUser remove = userNameUser.remove(referee.getFirstName() + "_" + referee.getLastName());
            SignedUser remove = userNameUser.remove(referee.getEmail());
            userDao.delete(referee);
            referee.deleteUser();
            //Logger
            SystemController.logger.info("Deletion | Referee have been remove from the system; Referee ID: " + referee.getId() +
                    "; Association Representative ID:" + associationRepresentative.get_id());
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //Use Case 9.4 A
    public Set<Referee> getAllRefereeThatCanBeForLeague(String leagueID) throws Exception {
        Optional optional = leagueDao.get(leagueID);
        if(optional.isPresent()){
            League league = (League) optional.get();
            int numTraining = league.getMinRefereeTrainingRequired().getNumVal();
            ArrayList refereesThatFitToTraining = userDao.getRefereesThatFitToTraining(numTraining);
            return (Set<Referee>) refereesThatFitToTraining;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //Use Case 9.4 B
    public boolean setRefereeToSeason(String associationRepresentativeID, String seasonID, HashSet<String> refereesID) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = seasonDao.get(seasonID);
        HashSet<Referee> referees = userDao.getAll(refereesID);
        if(optional.isPresent() && optional1.isPresent() && referees.size()==refereesID.size() ) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            Season season = (Season) optional1.get();
            for (Referee referee : referees) {
                season.addReferee(referee.getRefereeTraining(), referee);
                //Logger
                SystemController.logger.info("Linking | New Referee have been appoint to season; SeasonID: " + season.get_id() + "; Referee ID: " + referee.get_id() +
                        "; Association Representative ID:" + associationRepresentative.get_id());
            }
            seasonDao.update(season);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //UseCase 9.5
    //TODO - what to do with the scoreComputingPolicy -it's not an object in the DB
    public boolean setScoreComputingPolicy(String associationRepresentativeID, String seasonID, ScoreComputingPolicy scoreComputingPolicy) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = seasonDao.get(seasonID);
        if(optional.isPresent() && optional1.isPresent()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            Season season = (Season) optional1.get();
            long currentTime = System.currentTimeMillis();
            if (season.getStartDate() <= currentTime)
                throw new Exception("Season already started");

            season.setScorePolicy(scoreComputingPolicy);

            //Logger
            SystemController.logger.info("Creation | New Score Computing have been set to season; SeasonID: " + season.get_id() + "; Score Computing Name: " + scoreComputingPolicy.getName() +
                    "; Association Representative ID:" + associationRepresentative.get_id());
            seasonDao.update(season);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //UseCase 9.6
    //TODO - what to do with the AssignPolicy -it's not an object in the DB
    public boolean setAssignPolicy(String associationRepresentativeID, String seasonID, AssignPolicy assignPolicy) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = seasonDao.get(seasonID);
        if(optional.isPresent() && optional1.isPresent()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            Season season = (Season) optional1.get();
            long currentTime = System.currentTimeMillis();
            if (season.getStartDate() <= currentTime)
                throw new Exception("Season already started");
            season.setAssignPolicy(assignPolicy);

            //Logger
            SystemController.logger.info("Creation | New Assign Policy have been set to season; SeasonID: " + season.get_id() + "; Assign Policy Name: " + assignPolicy.getName() +
                    "; Association Representative ID:" + associationRepresentative.get_id());
            seasonDao.update(season);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //UseCase 9.7* TBD
    public boolean assignGames(String associationRepresentativeID, String seasonID) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = seasonDao.get(seasonID);
        if(optional.isPresent() && optional1.isPresent()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            Season season = (Season) optional1.get();
            if (!(season.getGames().size() == 0))
                throw new Exception("Seasons games already assigned");

            season.setSeasonGames(season.getAssignPolicy().assignSeasonGames(season));

            //Logger
            SystemController.logger.info("Creation | New Games have been assign to season; SeasonID: " + season.get_id() + "; Assign Policy Name: " + season.getAssignPolicy().getName() +
                    "; Association Representative ID:" + associationRepresentative.get_id());
            seasonDao.update(season);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    public boolean setSeasonsTeams(String associationRepresentativeID, String seasonID ,HashSet<String> seasonsTeamsID) throws Exception {
        Optional optional = userDao.get(associationRepresentativeID);
        Optional optional1 = seasonDao.get(seasonID);
        HashSet seasonsTeams = teamDao.getAll(seasonsTeamsID);

        if(optional.isPresent() && optional1.isPresent() && seasonsTeamsID.size()==seasonsTeams.size()) {
            AssociationRepresentative associationRepresentative = (AssociationRepresentative) optional.get();
            Season season = (Season) optional1.get();
            if (!(season.getSeasonsTeams().size() == 0))
                throw new Exception("Seasons teams already assigned");

            season.setSeasonTeams(seasonsTeams);

            //Logger
            SystemController.logger.info("Creation | " + seasonsTeams.size() + " New Teams have been assign to season; SeasonID: " + season.get_id() +
                    "; Association Representative ID:" + associationRepresentative.get_id());
            seasonDao.update(season);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }


}