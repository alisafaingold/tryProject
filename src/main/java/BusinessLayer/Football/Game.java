package BusinessLayer.Football;

import BusinessLayer.Enum.RefereeRole;
import BusinessLayer.SystemFeatures.EventLog;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.Referee;
import BusinessLayer.Enum.FieldType;

import java.util.ArrayList;
import java.util.List;


public class Game {

    private Season season;
    private Field field;
    private Team homeTeam;
    private Team awayTeam;
    private long gameDate;
    private EventLog eventLog;
    private String mainRefereeID;
    private String secondaryReferee1ID;
    private String secondaryReferee2ID;
    private int state;
    private List<String> fansObserverID = new ArrayList();
    private int homeScore=0;
    private int awayScore=0;
    private String _id;

//    //ToDO delete
//    public Game() {
//    }

    public Game(Season season, Team homeTeam, Team awayTeam) {
        this.season = season;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.eventLog = new EventLog(this);
        for (Field f :homeTeam.getFields() ) {
            if(f.getFieldType() == FieldType.Tournament){
                this.field=f;
                break;
                //ToDo what if team have no tournament fields?
            }
        }
    }

    public Season getSeason() {
        return season;
    }

    public Field getField() {
        return field;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public RefereeRole findRefereeRole(String rID){
        if (mainRefereeID==rID)
            return RefereeRole.Main;
        else{
            return RefereeRole.Secondary;
        }

    }

    public String getMainRefereeID() {
        return mainRefereeID;
    }

    public String getSecondaryReferee1ID() {
        return secondaryReferee1ID;
    }

    public String getSecondaryReferee2ID() {
        return secondaryReferee2ID;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyAllObservers();
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setGameDate(long gameDate) {
        this.gameDate = gameDate;
    }

    public void setMainRefereeID(Referee mainRefereeID) {
        this.mainRefereeID = mainRefereeID.get_id();
    }

    public void setSecondaryReferee1ID(Referee secondaryReferee1ID) {
        this.secondaryReferee1ID = secondaryReferee1ID.get_id();
    }

    public void setSecondaryReferee2ID(Referee secondaryReferee2ID) {
        this.secondaryReferee2ID = secondaryReferee2ID.get_id();
    }

    public boolean attachObserver(Fan fan) {
        if (fansObserverID.add(fan.get_id()))
            return true;
        return false;
    }

    public boolean removeObserver(Fan fan) {
        if (fansObserverID.remove(fan.get_id()))
            return true;
        return false;
    }

    public boolean checkObserver(Fan fan) {
        if (fansObserverID.contains(fan))
            return true;
        return false;
    }

    public void notifyAllObservers() {
//        for (Fan fan : fansObserverID) {
//            fan.update();
//        }
    }

    public boolean removeReferee(Referee referee, RefereeRole refereeRole) {
        switch (refereeRole) {
            case Main:
                if (mainRefereeID == referee.get_id()) {
                    mainRefereeID = null;
                    return true;
                }
            case Secondary:
                if (secondaryReferee1ID == referee.get_id()) {
                    secondaryReferee1ID = null;
                    return true;
                } else if (secondaryReferee2ID == referee.get_id()) {
                    secondaryReferee2ID = null;
                    return true;
                }

        }
        return false;
    }

    public long getGameDate() {
        return gameDate;
    }

    public EventLog getEventLog() {
        return eventLog;
    }

    public List<String > getFansObserverID() {
        return fansObserverID;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}

