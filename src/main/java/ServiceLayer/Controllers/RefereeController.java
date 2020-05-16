package ServiceLayer.Controllers;

import BusinessLayer.Enum.EventType;
import BusinessLayer.Enum.RefereeRole;
import BusinessLayer.Football.Event;
import BusinessLayer.Football.Game;
import BusinessLayer.Users.Referee;
import DB.EventDao;
import DB.GamesDao;
import DB.UserDao;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RefereeController {
    private GamesDao gamesDao;
    private EventDao eventDao;
    private UserDao userDao;

    public RefereeController() {
        this.gamesDao = GamesDao.getInstance();
        this.eventDao = EventDao.getInstance();
        this.userDao = UserDao.getInstance();
    }


    //Use Case 10.2
    public HashMap<Game, RefereeRole> showRefereeAssignedGames(String refereeID) throws Exception {
        Optional optional = userDao.get(refereeID);
        if(optional.isPresent()){
            Referee referee = (Referee) optional.get();
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
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //Use Case 10.3 A
    public HashSet<Game> getCurrentGames(String refereeID) throws Exception {
        Optional optional = userDao.get(refereeID);
        if(optional.isPresent()) {
            Referee referee = (Referee) optional.get();
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
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 10.3 B
    public boolean addEventToCurrentGame(String refereeID, String gameID, EventType eventType, int eventMinute, String description) throws Exception {
        Optional optional = userDao.get(refereeID);
        Optional optional1 = gamesDao.get(gameID);
        if(optional.isPresent() && optional1.isPresent()){
            Referee referee = (Referee) optional.get();
            Game game = (Game) optional1.get();
            Event event = new Event(eventType, eventMinute, description, referee);
            eventDao.save(event);
            game.addEvent(event.get_id());
            gamesDao.update(game);
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //Use Case 10.4.1 A
    public HashSet<Game> getGamesForEdit(String refereeID) throws Exception {
        Optional optional = userDao.get(refereeID);
        if(optional.isPresent()) {
            Referee referee = (Referee) optional.get();
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
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 10.4.1 B
    public HashSet<Event> getGamesEventsForEdit(String refereeID, String gameID) throws Exception {
        Optional optional = userDao.get(refereeID);
        Optional optional1 = gamesDao.get(gameID);
        if(optional.isPresent() && optional1.isPresent()) {
            Referee referee = (Referee) optional.get();
            Game game = (Game) optional1.get();
            HashSet<String> events = game.getEvents();
            return eventDao.getAll(events);
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 10.4.1 C
    public boolean editGameEvent(String refereeID, String gameID, String eventID, HashMap<String, String> valuesToUpdate) throws Exception {
        Optional optional = userDao.get(refereeID);
        Optional optional1 = gamesDao.get(gameID);
        Optional optional2 = eventDao.get(eventID);
        if(optional.isPresent() && optional1.isPresent() && optional2.isPresent()) {
            Referee referee = (Referee) optional.get();
            Game game = (Game) optional1.get();
            Event event = (Event) optional2.get();
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
            eventDao.update(event);
            Game gameByEvent = gamesDao.getGameByEvent(event);
            if (gameByEvent != null) {
                gameByEvent.addEvent(event.get_id());
                gamesDao.update(game);
            }
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //Use Case 10.4.2
    public HashSet<Event> createGameReport(String refereeID, String gameID) throws Exception {
        Optional optional = userDao.get(refereeID);
        Optional optional1 = gamesDao.get(gameID);
        if(optional.isPresent() && optional1.isPresent()) {
            Referee referee = (Referee) optional.get();
            Game game = (Game) optional1.get();
            if (!(game.getMainRefereeID().equals(referee))) {
                throw new Exception("Not Main Referee Of This Game");
            }
            HashSet all = eventDao.getAll(game.getEvents());
            return all;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

}
