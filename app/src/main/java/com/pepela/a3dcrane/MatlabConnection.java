package com.pepela.a3dcrane;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MatlabConnection extends Service {
    public static String UDP_BROADCAST = "UDPBroadcast";
    public static String INTENT_DATA_X = "com.pepela.a3dcrane.intent_data.x";
    public static String INTENT_DATA_Y = "com.pepela.a3dcrane.intent_data.y";
    public static String INTENT_DATA_Z = "com.pepela.a3dcrane.intent_data.z";
    public static String PORT_NUMBER = "com.pepela.a3dcrane.intent_data.port_number";

    private int portNumber;

    private Thread UDPBroadcastThread;
    private Boolean shouldRestartSocketListen = true;


    DatagramSocket socket;

    private void listenAndWaitAndThrowIntent() throws Exception {

        byte[] recvBuf = new byte[3];
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(portNumber);
            socket.setBroadcast(true);
        }
        //socket.setSoTimeout(1000);
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        Log.e("UDP", "Waiting for UDP broadcast");
        socket.receive(packet);

        String senderIP = packet.getAddress().getHostAddress();
        //String message = new String(packet.getData()).trim();

        byte[] arr = packet.getData();
        float x = (float) arr[0];
        float y = (float) arr[1];
        float z = (float) arr[2];

        Log.e("UDP", "Got UDB broadcast from " + senderIP + String.format("x = %f, y = %f, z = %f", x, y, z));

        broadcastIntent(x, y, z);
        socket.close();
    }

    private void broadcastIntent(float x, float y, float z) {
        Log.e("Broadcast", "broadcasting intent");
        Intent intent = new Intent(UDP_BROADCAST);
        intent.putExtra(INTENT_DATA_X, x);
        intent.putExtra(INTENT_DATA_Y, y);
        intent.putExtra(INTENT_DATA_Z, z);
        sendBroadcast(intent);
    }


    void startListenForUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (shouldRestartSocketListen) {
                        listenAndWaitAndThrowIntent();
                    }
                    //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                } catch (Exception e) {
                    Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }


    public void stopListen() {
        shouldRestartSocketListen = false;
        if (socket != null)
            socket.close();
    }


    @Override
    public void onDestroy() {
        stopListen();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        portNumber = intent.getIntExtra(PORT_NUMBER, 2501);
        shouldRestartSocketListen = true;
        startListenForUDPBroadcast();
        Log.i("UDP", "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}