package oren.battleships;

import java.util.ArrayList;
import java.util.HashMap;

import oren.battleships.model.GameRoom;
import oren.battleships.model.Player;

public class MyApplication {

    private static MyApplication instance;
    private  GameRoom[] myAllRooms ;
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
        players = new HashMap<String,Player>();
        players.put("orenklein455",new Player("orenklein455", "12345", "orenklein455@gmail.com"));
        players.put("shimi",new Player("shimi", "123456", "shimi@gmail.com"));
        this.setMyAllRooms(new GameRoom[3]);
    }

    //myAllrooms getter&setter
    public GameRoom[] getMyAllRooms() {
        return myAllRooms;
    }
    public void setMyAllRooms(GameRoom[] myAllRooms) {
        this.myAllRooms = myAllRooms;
    }
}
