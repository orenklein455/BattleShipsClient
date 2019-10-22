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
    public void makeToastText(final int textIndex){
        String text = getString(textIndex);
        makeToastText(text);
    }
    public void makeToastText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        text,
                        Toast.LENGTH_LONG).show();
            }
        });
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
            APIConsumer apiConsumer = new APIConsumer(getString(R.string.protocol), getString(R.string.server_ip), getString(R.string.server_port));

            try {

                Boolean success = apiConsumer.signUp(parameters);

                if (success) {
                    makeToastText(R.string.user_created);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    makeToastText(R.string.user_already_exists);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Couldn't get valid response from server.");
                makeToastText("Couldn't get json from server. Check LogCat for possible errors!");
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}