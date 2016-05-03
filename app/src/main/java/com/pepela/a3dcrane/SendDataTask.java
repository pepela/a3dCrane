package com.pepela.a3dcrane;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
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

    private int mPort;
    InetAddress inet_addr;
    DatagramSocket socket;

    public String getIp() {
        return "1";
    }

    public boolean setIp(String ip) {
        try {
            inet_addr = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            Log.e("Send data task", e.getMessage());
            return false;
        }
        return true;
    }

    public int getPort() {
        return mPort;
    }

    public boolean setPort(int port) {
        if (port > 0 && port <= 65535) {
            this.mPort = port;
            return true;
        }
        return false;
    }


    @Override
    protected Void doInBackground(Float... params) {
        float x = params[0];
        float y = params[1];
        float z = params[2];

        Log.wtf("UDP send task", "Sending data");
        byte[] buffer = new byte[]{(byte) x, (byte) y, (byte) z};
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, mPort);
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
