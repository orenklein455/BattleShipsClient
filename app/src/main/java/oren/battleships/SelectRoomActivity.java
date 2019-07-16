package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SelectRoomActivity extends AppCompatActivity {

    private TextView txtUserDisplay;
    private TextView txtScoreDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
//        txtUserDisplay = (TextView)findViewById(R.id.userNameDisplay);
//        txtUserDisplay.setText(MyApplication.getInstance().getCurrentPlayer().getUserName());
//
//        txtScoreDisplay = (TextView)findViewById(R.id.scoreDisplay);
//        txtScoreDisplay.setText(MyApplication.getInstance().getCurrentPlayer().getScore());
    }

    public void join1Pressed(View view)
    {
        Intent intent = new Intent(SelectRoomActivity.this, InGameActivity.class);
        //intent.putExtra("ROOM_NUMBER",0);
        startActivity(intent);
    }


}
