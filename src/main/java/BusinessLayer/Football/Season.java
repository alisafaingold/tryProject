package BusinessLayer.Football;

import BusinessLayer.Enum.RefereeTraining;
import BusinessLayer.SeasonPolicies.AssignPolicy;
import BusinessLayer.SeasonPolicies.AssignPolicy2;
import BusinessLayer.SeasonPolicies.ScoreComputingPolicy;
import BusinessLayer.SeasonPolicies.ScoreComputingPolicy1;
import BusinessLayer.Users.Referee;

import java.util.HashMap;
import java.util.HashSet;

public class Season {
    private int year;
    long startDate;
    //Connections
    private ScoreComputingPolicy scorePolicy;
    private AssignPolicy assignPolicy;
    private HashMap<RefereeTraining, HashSet<String>> referees;
    private String league;
    private HashSet<String> seasonsTeams;
    private String scoreBoard;
    private String _id;


    public Season(Integer year, long startDate) {
        this.year=year;
        this.startDate = startDate;
        scorePolicy=new ScoreComputingPolicy1();
        assignPolicy= new AssignPolicy2();
        referees = new HashMap<>();
        seasonsTeams = new HashSet<>();
    }

    public boolean addReferee(RefereeTraining refereeTraining, Referee referee){
        if(referees.containsKey(refereeTraining)){
            referees.get(refereeTraining).add(referee.get_id());
        }
        else {
            HashSet<String> thisReferees = new HashSet<>();
            thisReferees.add(referee.get_id());
            referees.put(refereeTraining, thisReferees);
        }
        return true;
    }

    public boolean removeReferee(RefereeTraining refereeTraining, Referee referee){
        HashSet<String> referees = this.referees.get(refereeTraining);
        return referees.remove(referee.get_id());
    }

    public void setSeasonTeams(HashSet<Team> seasonsTeams) throws Exception {
        if(System.currentTimeMillis() > this.startDate){
            throw new Exception("cant change season teams after season start date");
        }
        this.seasonsTeams = null;

        //init new scoreBoard for those teams
        scoreBoard = null;

    }

    @Override
    public String toString() {
        return ""+year;
    }

    // ========== Getters and Setters ============


    public League getLeague() {
        return null;
    }

    public void setLeague(League league) {
        this.league = league.get_id();
    }

    public HashSet<Team> getSeasonsTeams() {
        return null;
    }

    public ScoreComputingPolicy getScorePolicy() {
        return scorePolicy;
    }

    public AssignPolicy getAssignPolicy() {
        return assignPolicy;
    }

    public void setScorePolicy(ScoreComputingPolicy scorePolicy) {
        this.scorePolicy = scorePolicy;
    }

    public void setAssignPolicy(AssignPolicy assignPolicy) {
        this.assignPolicy = assignPolicy;
    }

    public long getStartDate() {
        return startDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public HashMap<Integer,HashSet<Game>> getGames() {
        return null;
    }
    public void setSeasonGames(HashMap<Integer, HashSet<Game>> roundGames) {

    }

    public HashMap<RefereeTraining, HashSet<Referee>> getReferees() {
        return null;
    }

    public ScoreBoard getScoreBoard() {
        return null;
    }
}
