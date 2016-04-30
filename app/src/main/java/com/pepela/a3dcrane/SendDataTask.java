package com.pepela.a3dcrane;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by giorg_000 on 27.04.2016.
 */
public class SendDataTask extends AsyncTask<Float, Void, Void> {

    int PORT = 25000;
    InetAddress inet_addr;
    DatagramSocket socket;

    @Override
    protected Void doInBackground(Float... params) {
        float x = params[0];
        float y = params[1];
        float z = params[2];

        byte[] ip_bytes = new byte[]{(byte) 10, (byte) 60, (byte) 3, (byte) 96};
        try {
            inet_addr = InetAddress.getByAddress(ip_bytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Log.wtf("UDP send task", "Sending data");
        byte[] buffer = new byte[]{(byte) x, (byte) y, (byte) z};
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, PORT);
        try {
            socket = new DatagramSocket();
            socket.send(packet);
            Log.wtf("UDP send task", "it worked idk");
        } catch (IOException e) {
            Log.e("UDP send task", e.getMessage());
        }

        return null;
    }
}
