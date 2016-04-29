package com.pepela.a3dcrane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity implements CraneView.OnCranePositionChangeEventListener {

    CraneView craneView;

    final Handler mHandler = new Handler();
    private Receiver udpReceiveBroadcastReceiver;
    private IntentFilter filter;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        craneView = (CraneView) findViewById(R.id.crane);

        serviceIntent = new Intent(this, MatlabConnection.class);
        serviceIntent.putExtra(MatlabConnection.PORT_NUMBER, 25001);
        startService(serviceIntent);

        udpReceiveBroadcastReceiver = new Receiver();
        filter = new IntentFilter(MatlabConnection.UDP_BROADCAST);

    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String url = arg1.getStringExtra(MatlabConnection.INTENT_DATA);
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
    public void onPositionChangeEvent(int x, int y, int z) {
        //Toast.makeText(getApplicationContext(), String.format("x: %s  y: %s  z: %s", x, y, z), Toast.LENGTH_SHORT).show();
        //startCommunication(x, y);
        SendDataTask sdt = new SendDataTask();
        sdt.execute("1","2");
    }

    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };


    protected void startCommunication(int x, int y) {

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                try {
                    DatagramSocket clientSocket = null;
                    if (clientSocket == null) {
                        clientSocket = new DatagramSocket(null);
                        clientSocket.setReuseAddress(true);
                        clientSocket.setBroadcast(true);
                        clientSocket.bind(new InetSocketAddress(25001));
                    }
                    byte[] receivedData = new byte[16];
                    while (true) {
                        DatagramPacket recv_packet = new DatagramPacket(receivedData, receivedData.length);
                        Log.d("UDP", "S: Receiving...");
                        clientSocket.receive(recv_packet);

                        byte[] ada = recv_packet.getData();

                        String rec_str = new String(recv_packet.getData());
                        Log.d("UDP", "Received: " + rec_str);

                        InetAddress ipAddress = recv_packet.getAddress();
                        int port = recv_packet.getPort();
                        Log.d("UDP", "IPAddress : " + ipAddress.toString());
                        Log.d("UDP", " Port : " + Integer.toString(port));
                    }
                } catch (Exception e) {
                    Log.e("UDP", "S: Error", e);
                }
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private void updateResultsInUi() {
        //Toast.makeText(getApplicationContext(), "ok we are here", Toast.LENGTH_SHORT).show();
    }


}
