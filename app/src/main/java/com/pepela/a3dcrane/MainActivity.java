package com.pepela.a3dcrane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements CraneView.OnCranePositionChangeEventListener {

    CraneView craneView;

    private Receiver udpReceiveBroadcastReceiver;
    private IntentFilter filter;
    private Intent serviceIntent;

    private Button mSetButton;
    private EditText mXEditText;
    private EditText mYEditText;
    private EditText mZEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        craneView = (CraneView) findViewById(R.id.crane);

        mXEditText = (EditText) findViewById(R.id.mainEditTextX);
        mYEditText = (EditText) findViewById(R.id.mainEditTextY);
        mZEditText = (EditText) findViewById(R.id.mainEditTextZ);

        mSetButton = (Button) findViewById(R.id.mainButtonSet);

        mSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean itsOk = true;

                String xStr = mXEditText.getText().toString();
                String yStr = mYEditText.getText().toString();
                String zStr = mZEditText.getText().toString();

                if (TextUtils.isEmpty(xStr)) {
                    mXEditText.setError("Required");
                    itsOk = false;
                } else if (TextUtils.isEmpty(yStr)) {
                    mYEditText.setError("Required");
                    itsOk = false;
                } else if (TextUtils.isEmpty(zStr)) {
                    mZEditText.setError("Required");
                    itsOk = false;
                }
                if (!itsOk)
                    return;


                float x = Float.valueOf(xStr);
                float y = Float.valueOf(yStr);
                float z = Float.valueOf(zStr);

                craneView.setPosition(x, y, z);

                SendDataTask sdt = new SendDataTask();
                sdt.setIp("192.168.0.5");
                sdt.setPort(25000);
                sdt.execute(x, y, z);
            }
        });


        mXEditText.setText(String.format("%.2f", craneView.getXInCm()));
        mYEditText.setText(String.format("%.2f", craneView.getYInCm()));
        mZEditText.setText(String.format("%.2f", craneView.getZInCm()));


        serviceIntent = new Intent(this, MatlabConnection.class);
        serviceIntent.putExtra(MatlabConnection.PORT_NUMBER, 25001);
        startService(serviceIntent);

        udpReceiveBroadcastReceiver = new Receiver();
        filter = new IntentFilter(MatlabConnection.UDP_BROADCAST);

    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            float x = arg1.getFloatExtra(MatlabConnection.INTENT_DATA_X, 10);
            float y = arg1.getFloatExtra(MatlabConnection.INTENT_DATA_Y, 10);
            float z = arg1.getFloatExtra(MatlabConnection.INTENT_DATA_Z, 10);

            mXEditText.setText(String.format("%.2f", craneView.getXInCm()));
            mYEditText.setText(String.format("%.2f", craneView.getYInCm()));
            mZEditText.setText(String.format("%.2f", craneView.getZInCm()));

            craneView.setPosition(x, y, z);
            //Toast.makeText(arg0, url, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(udpReceiveBroadcastReceiver, filter);
        craneView.setCranePositionChangeEventListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(serviceIntent);
        this.unregisterReceiver(udpReceiveBroadcastReceiver);
    }

    @Override
    public void onPositionChangeEvent(float x, float y, float z) {
        //Toast.makeText(getApplicationContext(), String.format("x: %s  y: %s  z: %s", x, y, z), Toast.LENGTH_SHORT).show();

        mXEditText.setText(String.format("%.2f", x));
        mYEditText.setText(String.format("%.2f", y));
        mZEditText.setText(String.format("%.2f", z));

        SendDataTask sendDataTask = new SendDataTask();
        sendDataTask.setIp("192.168.0.5");
        sendDataTask.setPort(25000);
        sendDataTask.execute(x, y, z);
    }


}
