package oren.battleships.model;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {
    private List<Game> games = new ArrayList<Game>();

    public List<Game> getGames() {
        return games;
    }

    public void addGame(Game game) {
       games.add(game);
    }
}
