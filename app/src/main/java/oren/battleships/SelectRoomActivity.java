package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectRoomActivity extends AppCompatActivity {

    private TextView txtUserDisplay,txtScoreDisplay;
    public static int room;
    public static String user;
    public static String user_score;

    private String TAG = SelectRoomActivity.class.getSimpleName();

    public static ArrayList<String> room_state;
    public static ArrayList<String> room_name;
    public static String[] top_user;
    public static String[] top_score;
    public static ArrayList<Map<String, String>> top_users;
    public static TextView[] room_stat_disp;
    public static TextView[] room_name_disp;
    public static TextView[] score_disp;
    public static TextView[] place_disp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        room_state = new ArrayList<>();
        room_name = new ArrayList<>();
        top_user = new String[3];
        top_score = new String[3];
        room_stat_disp = new TextView[3];
        room_name_disp = new TextView[3];
        score_disp = new TextView[3];
        place_disp = new TextView[3];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        user = getIntent().getStringExtra("USER");
        txtUserDisplay = (TextView)findViewById(R.id.userNameDisplay);
        txtUserDisplay.setText(user);

        txtScoreDisplay = (TextView)findViewById(R.id.scoreDisplay);

        place_disp[0] = (TextView)findViewById(R.id.first_player);
        place_disp[1] = (TextView)findViewById(R.id.second_player);
        place_disp[2] = (TextView)findViewById(R.id.third_player);

        score_disp[0] = (TextView)findViewById(R.id.first_score);
        score_disp[1] = (TextView)findViewById(R.id.second_score);
        score_disp[2] = (TextView)findViewById(R.id.third_score);

        room_stat_disp[0] = (TextView)findViewById(R.id.room1Status);
        room_stat_disp[1] = (TextView)findViewById(R.id.room2Status);
        room_stat_disp[2] = (TextView)findViewById(R.id.room3Status);

        room_name_disp[0] = (TextView)findViewById(R.id.room1Name);
        room_name_disp[1] = (TextView)findViewById(R.id.room2Name);
        room_name_disp[2] = (TextView)findViewById(R.id.room3Name);


        new GetLobbyData().execute();

    }

    private class GetLobbyData extends AsyncTask<Void, Void, Void> {
        private Map<String, Object> data;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void...args0)  {
            APIConsumer apiConsumer = new APIConsumer(getString(R.string.protocol), getString(R.string.server_ip), getString(R.string.server_port));

            try {
                data = apiConsumer.getLobbyData(user);

                Log.e(TAG, data.toString());
            } catch (Exception e) { e.printStackTrace(); }

            Log.e(TAG, "Response from url: " + data.toString());
            if (data != null) {
                    Map<String, String> room_data = (Map<String, String>) data.get("rooms");

                    for (Map.Entry<String,String> entry : room_data.entrySet()){
                        room_name.add(entry.getKey());
                        room_state.add(entry.getValue());
                    }
                    user_score = (String)data.get("my_score");
                    top_users = (ArrayList<Map<String, String>>)data.get("top_users");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtScoreDisplay.setText(user_score);
                            int i = 0;
                            for (Map<String,String> user: top_users){
                                place_disp[i].setText(user.get("username"));
                                score_disp[i].setText(user.get("score"));
                                i++;
                            }
                            for (i = 0; i < room_name.size(); i++) {
                                room_stat_disp[i].setText(room_state.get(i));
                                room_name_disp[i].setText(room_name.get(i));

                            }
                        }
                    });

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

    private class joinRoom extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer...args0)  {
            room = args0[0];
            APIConsumer apiConsumer = new APIConsumer(getString(R.string.protocol), getString(R.string.server_ip), getString(R.string.server_port));
            // Making a request to url and getting response
            String url = getString(R.string.protocol) + "://"+ getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/joinRoom";

            String jsonStr = null;
            try {
                jsonStr = apiConsumer.SendPost(url, user + "_" + room);
            } catch (Exception e) { e.printStackTrace(); }
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                if (jsonStr.equals("full")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            room_state.set(room, "Full");
                            room_stat_disp[room].setText(room_state.get(room));
                            Toast.makeText(getApplicationContext(),
                                    "This room is full",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                else if (jsonStr.equals("joined")) {
                    Intent intent = new Intent(SelectRoomActivity.this, InGameActivity.class);
                    intent.putExtra("ROOM_NUMBER",room);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    finish();
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
        new joinRoom().execute(0);
    }

    public void join2Pressed(View view)
    {
        new joinRoom().execute(1);
    }

    public void join3Pressed(View view)
    {
        new joinRoom().execute(2);
    }
}
