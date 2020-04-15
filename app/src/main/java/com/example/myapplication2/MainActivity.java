package com.example.myapplication2;

import android.app.Activity;
import android.Manifest;
import android.app.Activity;
import android.app.AppComponentFactory;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    //Bluetooth control
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    // UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // MAC-address of Bluetooth Hc-05
    private static String address = "98:D3:71:FD:51:47";


    //SMS message variables
    String etmessage = "Intruder has Entered the Base";
    //Destination telephone number
    String etTelNr = "7543675392";
    int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    int stop_value = 1;

    //Count down Variables
    private TextView CountdownText;
    private Button countdownbutton;
    private Button Countdownstop;
    private Button next_button;


    private CountDownTimer countDownTimer;
    //2 Minutes in Millisecond
    private long timeLeftMillis = 120000;
    private boolean timeRunning;

    //Computer vision images
    ImageView img;

    protected void onCreate(Bundle savedInstanceState){

        //Connects Layout xml file to MainActivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_layout);

        Countdownstop = findViewById(R.id.Stop);
        CountdownText = findViewById(R.id.timertext);
        countdownbutton = findViewById(R.id.Start);
        next_button = findViewById(R.id.button2);


        //img.setBackgroundResource(R.drawable.ic_launcher_background);
        //ImageView image = (ImageView) findViewById(R.id.imageView);
        //Bitmap bMap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Screenshots/bground.jpg");
        //img.setImageBitmap(bMap);



        //Create Bluetooth Adapter and Check if Bluetooth is enabled
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();


        //ImageView mImageView;
        //mImageView = (ImageView) findViewById(R.id.imageView);
        //mImageView.setImageBitmap(BitmapFactory.decodeFile("/My Files/Internal storage/DCIM/Screenshots/bground.jpg"));

        //File imgFile = new File("/storage/emulated/0/DCIM/Screenshots/index.jpgeg");

        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        //ImageView myImage = (ImageView) findViewById(R.id.imageView);

        //myImage.setImageBitmap(myBitmap)

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });

        //Controls Start Button
        countdownbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send 1 value through Bluetooth
                sendData("1");
                //Toast.makeText(getBaseContext(), "Alarm is On", Toast.LENGTH_SHORT).show();

                //Intent intent = getIntent();
                //etTelNr = intent.getStringExtra("number");

                if(etTelNr.isEmpty()){
                    etTelNr = "7543675392";
                }
                //start and stop Timer
                startStop();




                if(stop_value > 0) {
                    //Programming to send Sms message when start button is clicked
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                {Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    } else {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(etTelNr, null, etmessage, null, null);
                        stop_value = stop_value - 1;
                    }
                }
            }
        });

        //Controls Stop Button
        Countdownstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeRunning)
                {
                    //Send 0 value through Bluetooth
                    sendData("0");
                   // Toast.makeText(getBaseContext(), "Alarm is Off", Toast.LENGTH_SHORT).show();

                    //Calls function that stops Timer when Stop Button is clicked
                    stopTimer();

                }
            }
        });
    }
    public void sendMessage(View view){
        Intent intent = new Intent(this, setup_sms.class);
        startActivity(intent);
    }
    public void getMessagevalue(View view){
        Intent intent = getIntent();
        etTelNr = intent.getStringExtra("name");
    }
    public void startStop(){
        if (timeRunning)
        {
            stopTimer();
        }
        else{
            startTimer();
        }
    }
    public void startTimer()
    {
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftMillis = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        timeRunning = true;
    }
    public void stopTimer(){
        countDownTimer.cancel();
        timeRunning=false;
    }

    //Builds Timer
    public void updateTimer()
    {
        //Create minute and seconds variables
        int minutes =(int) timeLeftMillis/60000;
        int secondes = (int) timeLeftMillis % 60000/ 1000;

        //Construct output to textview on layout xml
        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if(secondes < 10) timeLeftText +="0";
        timeLeftText += secondes;
        CountdownText.setText(timeLeftText);

        //If Time in Milliseconds send Zero through Bluetooth
        if (timeLeftMillis == 0) {
            sendData("0");
        }

    }

    //Creates Bluetooth Socket
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Error", "Could not create Connection: Check Bluetooth module " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Error", "Could not  close socket: Check Alarm" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Error", "Could Send message: Check Alarm" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Error", "Failed to flush output stream: Check Alarm" + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Error", "Failed to close socket: Check Alarm" + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Error", "Bluetooth is not supported on this device");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
            errorExit("Fatal Error", msg);
        }
    }
}