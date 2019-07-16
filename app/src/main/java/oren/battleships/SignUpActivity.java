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

import java.util.ArrayList;

import oren.battleships.model.Player;

public class SignUpActivity extends AppCompatActivity {

    private EditText txtUser;
    private EditText txtPass;
    private EditText txtPass2;
    private EditText txtMail;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtUser = (EditText)findViewById(R.id.NewTxtUsername);
        txtPass = (EditText)findViewById(R.id.newTxtPassword);
        txtPass2 = (EditText)findViewById(R.id.txtPassword);
        txtMail = (EditText)findViewById(R.id.txtEmail);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
    }

    public boolean Validate()
    {

        if (TextUtils.isEmpty(txtPass.getText().toString()) || TextUtils.isEmpty(txtPass2.getText().toString()) ||
                TextUtils.isEmpty(txtUser.getText().toString()) || TextUtils.isEmpty(txtMail.getText().toString()))
        {
            Toast.makeText(this,R.string.empty_field_error,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (MyApplication.getInstance().getPlayers().containsKey(txtUser.getText().toString()))
        {
            Toast.makeText(this,R.string.user_already_exists,Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(txtPass.getText().toString().equals(txtPass2)))
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


        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}