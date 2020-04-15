package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class setup_sms extends AppCompatActivity {

    Button next;
    EditText number;
    EditText Message;
    String n_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_sms);

        next = findViewById(R.id.button);

        number = findViewById(R.id.editText7);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n_number = number.getText().toString();
                Intent intent = new Intent(setup_sms.this, MainActivity.class);
                intent.putExtra("number", n_number);
                startActivity(intent);

                check(view);
            }
        });
    }

    public void check(View view){

        Intent intent = new Intent(this, check_b.class);
        startActivity(intent);
    }
}
