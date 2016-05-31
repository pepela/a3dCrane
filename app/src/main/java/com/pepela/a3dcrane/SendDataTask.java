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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

/**
 * Created by giorg_000 on 27.04.2016.
 */
public class SendDataTask extends AsyncTask<Double, Void, Void> {

	private int mPort;
	InetAddress inet_addr;
	DatagramSocket socket;

	public String getIp() {
		return inet_addr.toString();
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
	protected Void doInBackground(Double... params) {
		double x = params[0];
		double y = params[1];
		double z = params[2];


		Log.i("UDP send task", "Sending data");

		ByteBuffer bf = ByteBuffer.allocate(24);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.putDouble(x);
		bf.putDouble(y);
		bf.putDouble(z);
		byte[] sendBuffer = bf.array();

		DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, inet_addr, mPort);
		try {
			socket = new DatagramSocket();
			socket.send(packet);
			Log.i("UDP send task", String.format("x = %f, y = %f, z = %f", x, y, z));
		} catch (IOException e) {
			Log.e("UDP send task", e.getMessage());
		}

		return null;
	}
}
