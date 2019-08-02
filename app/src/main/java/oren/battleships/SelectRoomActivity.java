package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import oren.battleships.model.GameRoom;
import oren.battleships.model.Player;

public class SelectRoomActivity extends AppCompatActivity {

    private TextView txtUserDisplay,txtScoreDisplay;
    private TextView firstPlaceDisplay, secondPlaceDisplay, thirdPlaceDisplay;
    private TextView firstScoreDisplay, secondScoreDisplay, thirdScoreDisplay;
    private TextView room1StatDisp, room2StatDisp, room3StatDisp, room1NameDisp, room2NameDisp, room3NameDisp;
    public static String user;
    public static String user_score, room1_state, room2_state, room3_state, room1_name, room2_name, room3_name, top_user1, top_user2, top_user3, top_score1, top_score2, top_score3;
    private String TAG = SelectRoomActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        user = getIntent().getStringExtra("USER");
        txtUserDisplay = (TextView)findViewById(R.id.userNameDisplay);
        txtUserDisplay.setText(user);

        txtScoreDisplay = (TextView)findViewById(R.id.scoreDisplay);

        firstPlaceDisplay = (TextView)findViewById(R.id.first_player);
        secondPlaceDisplay = (TextView)findViewById(R.id.second_player);
        thirdPlaceDisplay = (TextView)findViewById(R.id.third_player);

        firstScoreDisplay = (TextView)findViewById(R.id.first_score);
        secondScoreDisplay = (TextView)findViewById(R.id.second_score);
        thirdScoreDisplay = (TextView)findViewById(R.id.third_score);

        room1StatDisp = (TextView)findViewById(R.id.room1Status);
        room2StatDisp = (TextView)findViewById(R.id.room2Status);
        room3StatDisp = (TextView)findViewById(R.id.room3Status);

        room1NameDisp = (TextView)findViewById(R.id.room1Name);
        room2NameDisp = (TextView)findViewById(R.id.room2Name);
        room3NameDisp = (TextView)findViewById(R.id.room3Name);

        //room1NameDisp
        new GetLobbyData().execute();
    }

    private class GetLobbyData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void...args0)  {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url =getString(R.string.http_s) + "://"+ getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/getLobbyData";

            String jsonStr = null;
            try {
                jsonStr = sh.SendPost(url, user);
            } catch (Exception e) { e.printStackTrace(); }

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {

                String data[] = jsonStr.split("_");
                if (data!=null && data.length==13) {
                    user_score = data[0];
                    room1_state = data[1];
                    room2_state = data[2];
                    room3_state = data[3];
                    room1_name = data[4];
                    room2_name = data[5];
                    room3_name = data[6];
                    top_user1 = data[7];
                    top_score1 = data[8];
                    top_user2 = data[9];
                    top_score2 = data[10];
                    top_user3 = data[11];
                    top_score3 = data[12];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtScoreDisplay.setText(user_score);

                            room1StatDisp.setText(room1_state);
                            room2StatDisp.setText(room2_state);
                            room3StatDisp.setText(room3_state);

                            room1NameDisp.setText(room1_name);
                            room2NameDisp.setText(room2_name);
                            room3NameDisp.setText(room3_name);

                            firstPlaceDisplay.setText(top_user1);
                            secondPlaceDisplay.setText(top_user2);
                            thirdPlaceDisplay.setText(top_user3);

                            firstScoreDisplay.setText(top_score1);
                            secondScoreDisplay.setText(top_score2);
                            thirdScoreDisplay.setText(top_score3);
                        }
                    });
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void join1Pressed(View view)
    {
        // ToDo - check room's status in the server
        if (MyApplication.getInstance().getMyAllRooms()[0].getCurrentState() == GameRoom.GameRoomState.FULL) {
            Toast.makeText(this,R.string.full_room,Toast.LENGTH_SHORT).show();
            return;
        }
        // ToDo - add player to the server's room.game.players
        MyApplication.getInstance().getMyAllRooms()[0].getGame().getPlayers().add(new Player(user));
        MyApplication.getInstance().getMyAllRooms()[0].setCurrentState();
        Intent intent = new Intent(SelectRoomActivity.this, InGameActivity.class);
        intent.putExtra("ROOM_NUMBER",0);
        intent.putExtra("USER", user);

        startActivity(intent);
    }

    public void join2Pressed(View view)
    {
        Intent intent = new Intent(SelectRoomActivity.this, InGameActivity.class);
        intent.putExtra("ROOM_NUMBER",1);
        startActivity(intent);
    }

    public void join3Pressed(View view)
    {
        Intent intent = new Intent(SelectRoomActivity.this, InGameActivity.class);
        intent.putExtra("ROOM_NUMBER",2);
        startActivity(intent);
    }


}
