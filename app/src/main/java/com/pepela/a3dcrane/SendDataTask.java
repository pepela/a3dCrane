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
public class SendDataTask extends AsyncTask<String, Void, Void> {

    String at_cmd = "gaajvi";
    int PORT = 25000;
    InetAddress inet_addr;
    DatagramSocket socket;

    @Override
    protected Void doInBackground(String... params) {
        int x = Integer.parseInt(params[0]);
        int y = Integer.parseInt(params[1]);

        byte[] ip_bytes = new byte[]{(byte) 192, (byte) 168, (byte) 0, (byte) 16};
        try {
            inet_addr = InetAddress.getByAddress(ip_bytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.e("AT command: ", "at_cmd)");
        byte[] buffer = (at_cmd + "\r").getBytes();
        buffer =new byte[]{(byte)1};
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, PORT);
        try {
            socket = new DatagramSocket();
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("UDP send task", "it worked idk");

        return null;
    }
}
