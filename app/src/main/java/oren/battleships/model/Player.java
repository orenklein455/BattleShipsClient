package oren.battleships.model;

//import com.google.gson.annotations.SerializedName;

public class Player {
//    @SerializedName("userName")
    private String userName;
//    @SerializedName("password")
//    private String password;
//    @SerializedName("email")
//    private String email;
//    @SerializedName("score")
//    private int score;
//    @SerializedName("room")
//    private GameRoom room;
//    @SerializedName("connected")
//    private boolean connected;


    public String getUserName() {
        return userName;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public int getScore() {
//        return score;
//    }
//
//    public GameRoom getRoom() {
//        return room;
//    }
//
//    public boolean isConnected() {
//        return connected;
//    }

//    public Player(String userName, String password, String email) {
    public Player(String userName) {
        this.userName = userName;
//        this.password = password;
//        this.email = email;
//        this.score = 0;
//        connected = false;
    }

//    @Override
//    public boolean equals(Object obj) { //add this method to other objects as well
//        Player p = (Player)obj;
//        if ((this.userName.equals(p.userName)) || (this.email.equals(p.email)))
//                return true;
//        return false;
//    }

}
