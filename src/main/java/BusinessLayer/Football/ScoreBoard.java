package BusinessLayer.Football;

import java.util.*;

public class ScoreBoard {
    int numOfTeams;
    Season season;
    HashMap<Team, TeamScores> board;

    public ScoreBoard(Season s) {
        this.season = s;
        numOfTeams = season.getSeasonsTeams().size();
        board = new HashMap<>();
        for (Team t : s.getSeasonsTeams()) {
            board.put(t, new TeamScores(t));
        }
    }

    public boolean updateScoreBoard(Game g) {
        TeamScores home = board.get(g.getHomeTeam());
        TeamScores away = board.get(g.getAwayTeam());
        int homeScore = g.getHomeScore();
        int awayScore = g.getAwayScore();

        //update number of goals
        home.numOfScoredGoals += homeScore;
        home.numOfReceivedGoals += awayScore;
        away.numOfScoredGoals += awayScore;
        away.numOfReceivedGoals += (homeScore);

        //update num of games
        home.numOfGames++;
        away.numOfGames++;

        //update points
        int winPoints = season.getScorePolicy().getWinPoints();
        int tiePoints = season.getScorePolicy().getTiePoints();
        int losePoints = season.getScorePolicy().getLosePoints();

        if (homeScore > awayScore) {
            home.numOfWins++;
            home.TotalPoints += winPoints;

            away.numOfLoses++;
            away.TotalPoints += losePoints;
        } else if (awayScore > homeScore) {
            away.numOfWins++;
            away.TotalPoints += winPoints;

            home.numOfLoses++;
            home.TotalPoints += losePoints;
        } else if (awayScore == homeScore) {
            away.numOfTies++;
            away.TotalPoints += tiePoints;

            home.numOfTies++;
            home.TotalPoints += tiePoints;
        }
        return true;
    }

    public List<TeamScores> getBoard() {
        List<TeamScores> ans = new ArrayList<>(board.values());
        Collections.sort(ans, Comparator.comparing(TeamScores::getTotalPoints));
        return ans;
    }

    public static class TeamScores {
        Team team;
        int numOfGames;
        int numOfWins;
        int numOfTies;
        int numOfLoses;
        int numOfScoredGoals;
        int numOfReceivedGoals;
        int TotalPoints;

        public TeamScores(Team t) {
            this.team = t;
        }

        public Integer getTotalPoints() {
            return TotalPoints;
        }


//        public static Comparator<TeamScores> compareByTotalPoints = (TeamScores o1, TeamScores o2) ->
//                o1.getTotalPoints().compareTo(o2.getTotalPoints() );
//    }
    }
}


