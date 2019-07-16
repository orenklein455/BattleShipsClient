package oren.battleships.model;

public class GameRoom {

    public final int MAX_OBSERVERS = 2;


    private String name;
    private Game game;
    private Player[] observers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player[] getObservers() {
        return observers;
    }

    public void setObservers(Player[] observers) {
        this.observers = observers;
    }

    public GameRoomState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameRoomState currentState) {
        this.currentState = currentState;
    }

    public enum GameRoomState{EMPTY, ALMOST_FULL, FULL}
    private GameRoomState currentState;


    public GameRoom(String name)
    {
        this.setName(name);
        this.setObservers(new Player[3]);
        this.setCurrentState(GameRoomState.EMPTY);
        this.setGame(new Game());
    }
}
