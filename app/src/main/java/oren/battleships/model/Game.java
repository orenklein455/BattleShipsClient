package oren.battleships.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public enum GameState{ACTIVE, CREATING ,GAME_OVER}

    public final int NUM_PLAYERS = 2;
    @SerializedName("state")
    private GameState state;
    @SerializedName("players")
    private ArrayList<Player> players;
    @SerializedName("playerTurn")
    private int playerTurn;
    @SerializedName("board")
    private Board board;
    @SerializedName("opponent_board")
    private Board opponent_board;
    @SerializedName("place_ships")
    private int place_ships;

    public Game()
    {
        board = new Board(5);
        opponent_board = new Board(5);
        state = GameState.CREATING;
        players = new ArrayList<Player>();
        playerTurn = 0;
        place_ships = 0;
    }

    public void setPlacedShips(int num) {
        this.place_ships = num;
    }
    public int getPlacedShips() {
        return place_ships;
    }

    //state getter & setter
    public GameState getState() {
        return state;
    }
    public void setState(GameState state) {
        this.state = state;
    }

    //players getter
    public ArrayList<Player> getPlayers() {
        return players;
    }

    //get specific player
    public Player getPlayer(int playerIndex) throws Exception {
        if ((playerIndex >= NUM_PLAYERS) || (playerIndex < 0)) {
            throw new Exception("Invalid player index: " + playerIndex);
        }
        return players.get(playerIndex);
    }
    //add specific player
    public void addPlayer(Player player) throws Exception {
        if (players.size() >= NUM_PLAYERS) {
            throw new Exception("Too many players in game");
        }
        players.add(player);
    }

    //playerTurn getter & setter
    public int getPlayerTurn() {
        return playerTurn;
    }
    public void setNextPlayerTurn () {
        playerTurn = (playerTurn++)%NUM_PLAYERS;
    }

    //board getter & setter
    public Board getBoard() { return board; }
    public void setBoard(Board board) {
        this.board = board;
    }

    //opponent_board getter & setter
    public Board getOpponentBoard() { return opponent_board; }
    public void setOpponentBoard(Board board) {
        this.opponent_board = opponent_board;
    }

}
