package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import oren.battleships.model.Player;

public class MainActivity extends AppCompatActivity {

    private EditText txtUserN;
    private EditText txtPassword;
    private Button buttonSignIn;
    private Button btnSignUp;
    private Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // activity's previously saved state
        super.onCreate(savedInstanceState);   //call the super class to complete the activity creation
        setContentView(R.layout.activity_main); //set the UI layout as defined in the XML file

        txtUserN = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        buttonSignIn = (Button)findViewById(R.id.btnSignIN);
        btnSignUp = (Button)findViewById(R.id.btnSignUP);
        btnForgotPassword = (Button)findViewById(R.id.btnForgetPassword);
    }

//    public boolean Validate()
//    {
////        if (TextUtils.isEmpty(txtUserN.getText().toString()) || TextUtils.isEmpty(txtPassword.getText().toString()))
////        {
////            Toast.makeText(this,R.string.enter_user_pass,Toast.LENGTH_SHORT).show();
////            return false;
////        }
////
////        if (!(MyApplication.getInstance().getPlayers().containsKey(txtUserN.getText().toString())))
////        {
////            Toast.makeText(this,R.string.user_doesnt_exist,Toast.LENGTH_SHORT).show();
////            return false;
////        }
////
////        if (!(MyApplication.getInstance().getPlayers().get(txtUserN.getText().toString()).getPassword().equals(txtPassword.getText().toString())))
////        {
////            Toast.makeText(this,R.string.wrong_password,Toast.LENGTH_SHORT).show();
////            return false;
////        }
//
//        return true;
//    }

    public void signInPressed(View view)
    {
//        if (!Validate())
//            return;

      //  MyApplication.getInstance().setCurrentPlayer(MyApplication.getInstance().getPlayers().get(txtUserN.getText().toString()));
        Intent intent = new Intent(MainActivity.this, SelectRoomActivity.class);
        startActivity(intent);
    }

    public void signUpPressed(View view)
    {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void forgotPressed(View view)
    {
        Intent intent = new Intent(MainActivity.this, ForgotActivity.class);
        startActivity(intent);
    }
}



//        Toast.makeText(this,txtUserName.getText().toString(),Toast.LENGTH_SHORT);
//txtUserName.getText().toString() - contains the text value of inputed username
//      sign in button pressed
//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });