package oren.battleships;

import java.util.ArrayList;
import java.util.HashMap;

import oren.battleships.model.Cell;
import oren.battleships.model.GameRoom;
import oren.battleships.model.Player;

public class MyApplication {

    private static MyApplication instance;
    private GameRoom[] myAllRooms ;
    private Player currentPlayer;
    private HashMap<String,Player> players;


    //currentPlayer getters&setters
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    //players getter
    public HashMap<String, Player> getPlayers() {
        return players;
    }

    //instance getters
    public static MyApplication getInstance()
    {
        if (instance==null)
        {
            instance=new MyApplication();
        }
        return instance;
    }

    public MyApplication()
    {
//        players = new HashMap<String,Player>();
//        players.put("orenklein455",new Player("orenklein455", "12345", "orenklein455@gmail.com"));
//        players.put("shimi",new Player("shimi", "123456", "shimi@gmail.com"));
//        players.put("a",new Player("a", "a", "a@gmail.com"));

        this.setMyAllRooms(new GameRoom[3]);
        myAllRooms[0] = new GameRoom("Room0");
        myAllRooms[1] = new GameRoom("Room1");
        myAllRooms[2] = new GameRoom("Room2");

        //TODO - receive the board state from outside
        myAllRooms[0].getGame().getOpponentBoard().getCells()[0][0].setState(Cell.StateEnum.SHIP_PART);
        myAllRooms[0].getGame().getOpponentBoard().getCells()[0][1].setState(Cell.StateEnum.SHIP_PART);
        myAllRooms[0].getGame().getOpponentBoard().getCells()[0][2].setState(Cell.StateEnum.SHIP_PART);

        myAllRooms[0].getGame().getOpponentBoard().getCells()[3][1].setState(Cell.StateEnum.SHIP_PART);
        myAllRooms[0].getGame().getOpponentBoard().getCells()[3][2].setState(Cell.StateEnum.SHIP_PART);

        myAllRooms[0].getGame().getOpponentBoard().getCells()[4][4].setState(Cell.StateEnum.SHIP_PART);

    }

    //myAllrooms getter&setter
    public GameRoom[] getMyAllRooms() {
        return myAllRooms;
    }
    public void setMyAllRooms(GameRoom[] myAllRooms) {
        this.myAllRooms = myAllRooms;
    }
}
