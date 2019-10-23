package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class SelectRoomActivity extends AppCompatActivity {

    private TextView txtScoreDisplay;
    private int room;
    private String user, user_score;
    private String TAG = SelectRoomActivity.class.getSimpleName();
    private ArrayList<String> room_state;
    private ArrayList<String> room_name;
    private ArrayList<Map<String, String>> top_users;
    private TextView[] room_stat_disp, room_name_disp, score_disp, place_disp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);

        room_state = new ArrayList<>();
        room_name = new ArrayList<>();
        int room_quantity = 3;
        room_stat_disp = new TextView[room_quantity];
        room_name_disp = new TextView[room_quantity];
        int top_users_quantity = 3;
        score_disp = new TextView[room_quantity];
        place_disp = new TextView[room_quantity];

        user = getIntent().getStringExtra("USER");
        TextView txtUserDisplay = findViewById(R.id.userNameDisplay);
        txtUserDisplay.setText(user);

        txtScoreDisplay = findViewById(R.id.scoreDisplay);

        for (int i = 0; i < room_quantity; i++) {
            int roomNameViewID = getResources().getIdentifier("roomName_" + i, "id", getPackageName());
            int roomStatViewID = getResources().getIdentifier("roomStatus_" + i, "id", getPackageName());
            room_name_disp[i] = findViewById(roomNameViewID);
            room_stat_disp[i] = findViewById(roomStatViewID);
        }

        for (int i = 0; i < top_users_quantity; i++) {
            int topUserViewID = getResources().getIdentifier("topUser_" + i, "id", getPackageName());
            int topScoreViewID = getResources().getIdentifier("topScore_" + i, "id", getPackageName());
            place_disp[i] = findViewById(topUserViewID);
            score_disp[i] = findViewById(topScoreViewID);
        }

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
            String jsonStr = null;
            try {
                jsonStr = apiConsumer.joinRoom(user, room);
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
