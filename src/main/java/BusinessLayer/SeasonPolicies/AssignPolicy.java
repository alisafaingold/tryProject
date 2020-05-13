package BusinessLayer.SeasonPolicies;

import BusinessLayer.Football.Game;
import BusinessLayer.Football.Season;

import java.util.HashMap;
import java.util.HashSet;

public  interface AssignPolicy {

    public String getName();

    public HashMap<Integer,HashSet<Game>> assignSeasonGames(Season season);

}
