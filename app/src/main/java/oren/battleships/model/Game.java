package oren.battleships.model;

import java.util.ArrayList;

public class Game {

    public enum GameState{ACTIVE, CREATING ,GAME_OVER}

    private final int NUM_PLAYERS = 2;
    private GameState state;
    private ArrayList<Player> players;
    private int playerTurn;
    private Board board;
    private Board opponent_board;
    public Boolean winner_knows;
    public Boolean loser_knows;

    public Game()
    {
        board = new Board(5,"myOne");
        opponent_board = new Board(5, "opponent'sOne");
        state = GameState.CREATING;
        players = new ArrayList<Player>();
        winner_knows = false;
        loser_knows = false;
    }

    public void resetGame() {
        players.clear();
        state = GameState.CREATING;
        board.resetBoard("myOne");
        opponent_board.resetBoard("opponent'sOne");
        winner_knows = false;
        loser_knows = false;
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

//    //playerTurn getter & setter
//    public int getPlayerTurn() {
//        return playerTurn;
//    }
//    public void setNextPlayerTurn () {
//        playerTurn = (playerTurn++)%2;
//    }

    //board getter & setter
    public Board getBoard() { return board; }
    public void setBoard(Board board) {
        this.board = board;
    }

    //opponent_board getter & setter
    public Board getOpponentBoard() { return this.opponent_board; }

    public void setOpponentBoard(Board board) {
        this.opponent_board = board;
    }

}
