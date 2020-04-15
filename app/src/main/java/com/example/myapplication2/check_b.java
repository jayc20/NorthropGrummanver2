package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class check_b extends AppCompatActivity {

    Button bluetooth;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_bluetooth);


        bluetooth = findViewById(R.id.button3);
        next = findViewById(R.id.button4);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullup_setting(view);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_to_alarm(view);
            }
        });

    }

    public void pullup_setting(View view){

        Intent intentOpenBluetoothSettungs = new Intent();
        intentOpenBluetoothSettungs.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettungs);
    }
    public void send_to_alarm(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
