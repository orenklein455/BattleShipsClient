package oren.battleships;

import oren.battleships.model.GameRoom;

public class MyApplication {

    private static MyApplication instance;
    private GameRoom[] myAllRooms ;

    public static MyApplication getInstance()
    {
        if (instance == null)
        {
            instance = new MyApplication();
        }
        return instance;
    }

    public MyApplication()
    {
        this.setMyAllRooms(new GameRoom[3]);
        myAllRooms[0] = new GameRoom("Room0");
        myAllRooms[1] = new GameRoom("Room1");
        myAllRooms[2] = new GameRoom("Room2");
    }

    public GameRoom[] getMyAllRooms() {
        return myAllRooms;
    }
    public void setMyAllRooms(GameRoom[] myAllRooms) {
        this.myAllRooms = myAllRooms;
    }
}
