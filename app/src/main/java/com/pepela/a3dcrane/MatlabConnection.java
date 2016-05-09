package com.pepela.a3dcrane;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MatlabConnection extends Service {
    public static String UDP_BROADCAST = "UDPBroadcast";
    public static String INTENT_DATA_X = "a3dcrane.intent_data.x";
    public static String INTENT_DATA_Y = "a3dcrane.intent_data.y";
    public static String INTENT_DATA_Z = "a3dcrane.intent_data.z";
    public static String PORT_NUMBER = "a3dcrane.intent_data.port_number";


    private int mPortNumber;

    private Thread UDPBroadcastThread;
    private Boolean shouldRestartSocketListen = true;


    DatagramSocket socket;


    private void listenAndWaitAndThrowIntent() throws Exception {

        byte[] recvBuf = new byte[24];
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(mPortNumber);
            socket.setBroadcast(true);
        }
        //socket.setSoTimeout(1000);
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

        DoubleBuffer db = ByteBuffer.wrap(recvBuf).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer();

        Log.i("UDP", "Waiting for UDP broadcast");
        socket.receive(packet);

        //db.limit(packet.getLength()/Double.SIZE);

        String senderIP = packet.getAddress().getHostAddress();

        double x = db.get(0);
        double y = db.get(1);
        double z = db.get(2);

        Log.i("UDP", "Got UDB broadcast from " + senderIP + String.format("x = %f, y = %f, z = %f", x, y, z));

        broadcastIntent(x, y, z);
        socket.close();
    }

    private void broadcastIntent(double x, double y, double z) {
        Log.i("Broadcast", "broadcasting intent");
        Intent intent = new Intent(UDP_BROADCAST);
        intent.putExtra(INTENT_DATA_X, x);
        intent.putExtra(INTENT_DATA_Y, y);
        intent.putExtra(INTENT_DATA_Z, z);
        sendBroadcast(intent);
    }

    private byte[] toByteArray(double[] doubleArray) {
        int times = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[doubleArray.length * times];
        for (int i = 0; i < doubleArray.length; i++) {
            ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
        }

        return bytes;
    }

    private double[] toDoubleArray(byte[] byteArray) {
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).getDouble();
        }
        return doubles;
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
        mPortNumber = intent.getIntExtra(PORT_NUMBER, 25001);
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