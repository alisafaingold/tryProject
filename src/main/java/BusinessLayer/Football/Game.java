package BusinessLayer.Football;

import BusinessLayer.Enum.FieldType;
import BusinessLayer.Enum.RefereeRole;
import BusinessLayer.SystemFeatures.EventLog;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.Referee;
import DB.SeasonDao;
import DB.TeamDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Game {

    private String season;
    private String field;
    private String homeTeam;
    private String awayTeam;
    private long gameDate;
    private HashSet<String> events;
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
        this.season = season.get_id();
        this.homeTeam = homeTeam.get_id();
        this.awayTeam = awayTeam.get_id();
        this.events = new HashSet<>();
        for (Field f :homeTeam.getFields() ) {
            if(f.getFieldType() == FieldType.Tournament){
                this.field=f.get_id();
                break;
                //ToDo what if team have no tournament fields?
            }
        }
    }

    public Season getSeason() {
        //TODO ??
        return (Season) SeasonDao.getInstance().get(season).get();
    }

    public Team getHomeTeam() {
        //TODO ??
        return (Team) TeamDao.getInstance().get(homeTeam).get();
    }

    public Team getAwayTeam() {
        //TODO ??
        return (Team) TeamDao.getInstance().get(awayTeam).get();
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

    public List<String > getFansObserverID() {
        return fansObserverID;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setAwayTeam(String awayTeam) {
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

    public boolean addEvent(String eventID){
        events.add(eventID);
        return true;
    }

    public HashSet<String> getEvents() {
        return events;
    }
}

