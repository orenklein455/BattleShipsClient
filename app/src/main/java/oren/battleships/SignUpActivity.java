package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText txtUser, txtPass, txtPass2, txtMail;
    private String TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtUser = (EditText)findViewById(R.id.NewTxtUsername);
        txtPass = (EditText)findViewById(R.id.newTxtPassword);
        txtPass2 = (EditText)findViewById(R.id.txtPassword);
        txtMail = (EditText)findViewById(R.id.txtEmail);
    }

    public boolean Validate() {
        if (TextUtils.isEmpty(txtPass.getText().toString()) || TextUtils.isEmpty(txtPass2.getText().toString()) ||
                TextUtils.isEmpty(txtUser.getText().toString()) || TextUtils.isEmpty(txtMail.getText().toString()))
        {
            Toast.makeText(this,R.string.empty_field_error,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(txtPass.getText().toString().equals(txtPass2.getText().toString())))
        {
            Toast.makeText(this,R.string.passwords_dont_match,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtPass.getText().toString().length() < 5 || txtPass.getText().toString().length() > 8 )
        {
            Toast.makeText(this,R.string.password_length,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtMail.getText().toString()).matches() )
        {
            Toast.makeText(this,R.string.incorrect_email,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void submitPressed(View view)
    {
        if (!Validate())
            return;

        new CreateUser().execute();
    }

    private class CreateUser extends AsyncTask<Void, Void, Void> {
        private String msg;
        private HashMap<String, String> parameters = new HashMap<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            parameters.put("username", txtUser.getText().toString());
            parameters.put("password", txtPass.getText().toString());
        }

        @Override
        protected Void doInBackground(Void...args0)  {
            APIConsumer sh = new APIConsumer();
            // Making a request to url and getting response
            String url =getString(R.string.protocol) + "://"+ getString(R.string.server_ip) + ":" + getString(R.string.server_port) + "/signUp";

            Map jsonResult = null;
            try {
                jsonResult = sh.send_post(url, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonResult);
            if (jsonResult != null) {

                if (jsonResult.get("message").equals("completed")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.user_created,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (jsonResult.get("message").equals("user_exists")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.user_already_exists,
                                    Toast.LENGTH_LONG).show();
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
}