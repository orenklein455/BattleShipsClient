package oren.battleships.model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public final int NUM_PLAYERS = 2;

    public enum GameState{ACTIVE, CREATING ,GAME_OVER}

    private GameState state;
    private List<Player> players = new ArrayList<Player>();
    private int playerTurn;
    private Board board;

    public Game()
    {
        this.board = new Board(5);
        this.players = new ArrayList<Player>();
        this.state = GameState.CREATING;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Player getPlayer(int playerIndex) throws Exception {
        if ((playerIndex > NUM_PLAYERS) || (playerIndex < 0)) {
            throw new Exception("Invalid player index: " + playerIndex);
        }

        return players.get(playerIndex);
    }

    public void addPlayer(Player player) throws Exception {
        if (players.size() >= NUM_PLAYERS) {
            throw new Exception("Too many players in game");
        }
        players.add(player);
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setNextPlayerTurn () {
        playerTurn = (playerTurn++)%NUM_PLAYERS;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
