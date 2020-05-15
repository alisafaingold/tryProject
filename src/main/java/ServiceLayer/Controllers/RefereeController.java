package ServiceLayer.Controllers;

import BusinessLayer.Enum.EventType;
import BusinessLayer.Enum.RefereeRole;
import BusinessLayer.Football.Event;
import BusinessLayer.Football.Game;
import BusinessLayer.SystemFeatures.EventLog;
import BusinessLayer.Users.Referee;
import DB.GamesDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefereeController {
    private GamesDao gamesDao;

    public RefereeController() {
        this.gamesDao = GamesDao.getInstance();
    }


    //Use Case 10.2
    public HashMap<Game, RefereeRole> showRefereeAssignedGames(Referee referee) {
        HashMap<Game, RefereeRole> relevantGames = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        List refereeGames = gamesDao.getRefereeGames(referee.get_id());
        for (Object game : refereeGames) {
            Game g = (Game) game;
            if (g.getGameDate() >= currentTime) {
                relevantGames.put(g, g.findRefereeRole(referee.get_id()));
            }
        }
        return relevantGames;
    }

    //Use Case 10.3 A
    public HashSet<Game> getCurrentGames(Referee referee) {
        List refereeGames = gamesDao.getRefereeGames(referee.get_id());
        long currentTime = System.currentTimeMillis();
        HashSet<Game> relevantGames = new HashSet<>();
        for (Object game : refereeGames) {
            Game g = (Game) game;
            long gameStart = g.getGameDate();
            long gameEnd = g.getGameDate() + TimeUnit.MINUTES.toMillis(90);
            if (currentTime >= gameStart && currentTime <= gameEnd) {
                relevantGames.add(g);
            }
        }
        return relevantGames;
    }


    //Use Case 10.3 B
    public boolean addEventToCurrentGame(Referee referee, Game game, EventType eventType, int eventMinute, String description) {
        Event event = new Event(eventType, eventMinute, description, referee);
        game.getEventLog().addEvent(event);
        gamesDao.update(game);
        return true;
    }


    //Use Case 10.4.1 A
    public HashSet<Game> getGamesForEdit(Referee referee) {
        List refereeGames = gamesDao.getMainRefereeGames(referee.get_id());
        long currentTime = System.currentTimeMillis();
        HashSet<Game> relevantGames = new HashSet<>();
        for (Object game : refereeGames) {
            Game g = (Game) game;
            long gameStart = g.getGameDate() + TimeUnit.MINUTES.toMillis(90);
            long gameEnd = g.getGameDate() + TimeUnit.MINUTES.toMillis(390);
            if (currentTime >= gameStart && currentTime <= gameEnd) {
                relevantGames.add(g);
            }
        }
        return relevantGames;
    }

    //Use Case 10.4.1 B
    public HashSet<Event> getGamesEventsForEdit(Referee referee, Game game) {
        return game.getEventLog().getEvents();
    }

    //Use Case 10.4.1 C
    public boolean editGameEvent(Referee referee, Game game, Event event, HashMap<String, String> valuesToUpdate) {
        for (Map.Entry<String, String> entry : valuesToUpdate.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key.toLowerCase()) {
                case "description":
                    event.editDescription(value);
                    break;
                case "eventminute":
                    event.editEventMinute(Integer.parseInt(value));
                    break;
            }
        }
        gamesDao.update(game);
        return true;
    }

    //Use Case 10.4.2
    public EventLog createGameReport(Referee referee, Game game) throws Exception {
        if (!(game.getMainRefereeID().equals(referee))) {
            throw new Exception("Not Main Referee Of This Game");
        }
        return game.getEventLog();
    }

}
