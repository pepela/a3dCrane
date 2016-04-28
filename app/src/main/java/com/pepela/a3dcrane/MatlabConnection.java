package com.pepela.a3dcrane;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class MatlabConnection extends Service {
    public static String UDP_BROADCAST = "UDPBroadcast";
    public static String INTENT_DATA = "com.pepela.a3dcrane.intent_data.message";
    public static String PORT_NUMBER = "com.pepela.a3dcrane.intent_data.port_number";

    private int portNumber;

    private Thread UDPBroadcastThread;
    private Boolean shouldRestartSocketListen = true;


    private LocalBroadcastManager broadcaster;

    DatagramSocket socket;

    private void listenAndWaitAndThrowIntent() throws Exception {
        Thread.sleep(10000);
        broadcastIntent("didi ylee");
//        byte[] recvBuf = new byte[15000];
//        if (socket == null || socket.isClosed()) {
//            socket = new DatagramSocket(port);
//            socket.setBroadcast(true);
//        }
//        //socket.setSoTimeout(1000);
//        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
//        Log.e("UDP", "Waiting for UDP broadcast");
//        socket.receive(packet);
//
//        String senderIP = packet.getAddress().getHostAddress();
//        String message = new String(packet.getData()).trim();
//
//        Log.e("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);
//
//        broadcastIntent(senderIP, message);
//        socket.close();
    }

    private void broadcastIntent(String message) {
        Log.e("Broadcast", "broadcasting intent");
        Intent intent = new Intent(UDP_BROADCAST);
        intent.putExtra(INTENT_DATA, message);
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
        portNumber = intent.getIntExtra(PORT_NUMBER, 2500);
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