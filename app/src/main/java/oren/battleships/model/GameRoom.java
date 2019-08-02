package oren.battleships.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GameRoom {

   // public final int MAX_OBSERVERS = 3;

   // @SerializedName("name")
    private String name;
 //   @SerializedName("game")
    private Game game;
  //  @SerializedName("observers")
  //  private ArrayList<Player> observers;
  //  @SerializedName("currentState")
    private GameRoomState currentState;
    public enum GameRoomState{EMPTY, NOT_EMPTY, FULL}


    //name getter & setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //game getter & setter
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    //players getter & setter
//    public ArrayList<Player> getObservers() {
//        return observers;
//    }
//    public void setObservers(ArrayList<Player> players) {
//        this.observers = players;
//    }

    //currentState getter & setter
    public GameRoomState getCurrentState() {
        return currentState;
    }
    public void setCurrentState()
    {
        if (game.getPlayers().isEmpty()) currentState = GameRoomState.EMPTY;
        else if (game.getPlayers().size() == 2) currentState = GameRoomState.FULL;
        else currentState = GameRoomState.NOT_EMPTY;
    }

    public GameRoom(String name)
    {
        this.setName(name);
        this.game = new Game();
      //  observers = new ArrayList<Player>();
        this.setCurrentState();
    }
}
