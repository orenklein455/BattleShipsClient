package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txtUserN;
    private EditText txtPassword;
    private Button buttonSignIn;
    private Button btnSignUp;
    private Button btnForgotPassword;
    private String TAG = MainActivity.class.getSimpleName();
    public static Intent intent;
    public  static String username;

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

    public boolean Validate()
    {
        if (TextUtils.isEmpty(txtUserN.getText().toString()))
        {
            Toast.makeText(this,R.string.enter_user,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(txtPassword.getText().toString()))
        {
            Toast.makeText(this,R.string.enter_password,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void signInPressed(View view)
    {
        if (!Validate())
            return;

        new SignIn().execute();
    }

    public void signUpPressed(View view)
    {
        intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void forgotPressed(View view)
    {
        intent = new Intent(MainActivity.this, ForgotActivity.class);
        startActivity(intent);
    }

    private class SignIn extends AsyncTask<Void, Void, Void> {
        private String msg;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            msg = txtUserN.getText().toString() + "_" + txtPassword.getText().toString();
            username = txtUserN.getText().toString();
        }

        @Override
        protected Void doInBackground(Void...args0)  {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url =getString(R.string.http_s) + "://"+ getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/signIn";

            String jsonStr = null;
            try {
                jsonStr = sh.SendPost(url, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {

                if (jsonStr.equals("user_doesn't_exist")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.user_doesnt_exist,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (jsonStr.equals("wrong_password")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.wrong_password,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (jsonStr.substring(0,9).equals("signed_in")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.signed_in,
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    intent = new Intent(MainActivity.this, SelectRoomActivity.class);
                    intent.putExtra("USER", username);
                    intent.putExtra("SCORE", jsonStr.substring(9));
                    startActivity(intent);
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
}