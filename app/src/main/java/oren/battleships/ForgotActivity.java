package oren.battleships;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotActivity extends AppCompatActivity {

    private EditText textMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        textMail = (EditText)findViewById(R.id.mail_txt);

    }

    public boolean Validate() {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(textMail.getText().toString()).matches()) {
            Toast.makeText(this, R.string.incorrect_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void SendPressed(View view)
    {
        if (!Validate())
            return;

        Toast.makeText(this, R.string.details_sent, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ForgotActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

