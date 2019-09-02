package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import oren.battleships.model.GameRoom;
import oren.battleships.model.Player;

public class SelectRoomActivity extends AppCompatActivity {

    private TextView txtUserDisplay,txtScoreDisplay;
    public static int room;
    public static String user;
    public static String user_score;

    private String TAG = SelectRoomActivity.class.getSimpleName();

    public static String[] room_state;
    public static String[] room_name;
    public static String[] top_user;
    public static String[] top_score;

    public static TextView[] room_stat_disp;
    public static TextView[] room_name_disp;
    public static TextView[] score_disp;
    public static TextView[] place_disp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        room_state = new String[3];
        room_name = new String[3];
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

//        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
//        exec.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 0, 4000, TimeUnit.MILLISECONDS);
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

                    for (int i = 0; i < 3; i++) {
                        room_state[i] = data[i+1];
                        room_name[i] = data[i+4];
                    }
                    user_score = data[0];
                    top_user[0] = data[7];
                    top_score[0] = data[8];
                    top_user[1] = data[9];
                    top_score[1] = data[10];
                    top_user[2] = data[11];
                    top_score[2] = data[12];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtScoreDisplay.setText(user_score);

                            for (int i = 0; i < 3; i++) {
                                room_stat_disp[i].setText(room_state[i]);
                                room_name_disp[i].setText(room_name[i]);
                                place_disp[i].setText(top_user[i]);
                                score_disp[i].setText(top_score[i]);
                            }
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

    private class joinRoom extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer...args0)  {
            room = args0[0];
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = getString(R.string.http_s) + "://"+ getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/joinRoom";

            String jsonStr = null;
            try {
                jsonStr = sh.SendPost(url, user + "_" + room);
            } catch (Exception e) { e.printStackTrace(); }
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                if (jsonStr.equals("full")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            room_state[room] = "Full";
                            room_stat_disp[room].setText(room_state[room]);
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
