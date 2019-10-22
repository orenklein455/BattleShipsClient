package oren.battleships.model;

public class GameRoom {

    private String name;
    private Game game;
    private GameRoomState currentState;
    private enum GameRoomState{Empty, Not_empty, Full}

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void resetRoom() {
        currentState = GameRoomState.Empty;

        this.game.resetGame();
        this.game = null;
        this.game = new Game();
    }

    public GameRoomState getCurrentState() {
        return currentState;
    }
    public void setCurrentState()
    {
        if (game.getPlayers().isEmpty()) currentState = GameRoomState.Empty;
        else if (game.getPlayers().size() == 2) currentState = GameRoomState.Full;
        else currentState = GameRoomState.Not_empty;
    }

    public GameRoom(String name)
    {
        this.name = name;
        this.game = new Game();
        this.setCurrentState();
    }
}
