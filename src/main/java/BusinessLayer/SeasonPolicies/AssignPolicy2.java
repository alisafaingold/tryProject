package BusinessLayer.SeasonPolicies;

import BusinessLayer.Football.Game;
import BusinessLayer.Football.Season;
import BusinessLayer.Football.Team;

import java.util.*;

public class AssignPolicy2 implements AssignPolicy {
    String name = "1";

    @Override
    public String getName() {
        return name;
    }

    /**
     * https://stackoverflow.com/questions/6648512/scheduling-algorithm-for-a-round-robin-tournament/6649732#6649732
     */
    @Override
    public HashMap<Integer, HashSet<Game>> assignSeasonGames(Season season) {
        HashMap<Integer, HashSet<Game>> games = new HashMap<>();

        List<Team> teams = new ArrayList<Team>(season.getSeasonsTeams());
        if (teams.size() % 2 != 0) teams.add(null); //add dummy team if odd
        int numOfRounds = teams.size() % 2 == 0 ? (teams.size() - 1) : teams.size();

        for (int i = 1; i <= numOfRounds; i++) { //foreach round
            games.put(i, new HashSet<>());
            games.put(numOfRounds + i, new HashSet<>());

            for (int j = 0; j < teams.size() / 2; j++) {
                Team t1 = teams.get(j);
                Team t2 = teams.get(teams.size() - 1 - j);
                if (t1 != null && t2 != null) {
                    Game g1;
                    Game g2;
                    if (i % 2 == 0) {
                        g1 = new Game(season, t1, t2);
                        g2 = new Game(season, t2, t1);

                    } else {
                        g1 = new Game(season, t2, t1);
                        g2 = new Game(season, t1, t2);

                    }
                    games.get(i).add(g1);
                    games.get(numOfRounds + i).add(g2);
                }

            }
            Team first = teams.remove(0);
            teams.add(0, teams.remove(teams.size() - 1)); //shift left
            teams.add(0, first);
        }

        //add dates to all games
        long roundTime = season.getStartDate();
        for (int i = 1; i <= numOfRounds; i++) {
            for (Game g : games.get(i)) {
                g.setGameDate(roundTime);
            }
            final long MILI_SEC_IN_ONE_WEEK = 604800000;
            roundTime += MILI_SEC_IN_ONE_WEEK;
        }
        printGames(games);
        return games;
    }

    private void printGames(HashMap<Integer, HashSet<Game>> games) {
        for (Map.Entry<Integer, HashSet<Game>> entry : games.entrySet()) {
            System.out.println("========= Round " + entry.getKey() + " ==============");
            for (Game g : entry.getValue()) {
                System.out.println(g.getHomeTeam().getTeamName() + " : " + g.getAwayTeam().getTeamName());
            }
        }
    }
}
