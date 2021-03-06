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
	public static final String UDP_BROADCAST = "UDPBroadcast";

	public static final String INTENT_DATA_X = "a3dcrane.intent_data.x";
	public static final String INTENT_DATA_Y = "a3dcrane.intent_data.y";
	public static final String INTENT_DATA_Z = "a3dcrane.intent_data.z";
	public static final String INTENT_DATA_X_ANGLE = "a3dcrane.intent_data.xAngle";
	public static final String INTENT_DATA_Y_ANGLE = "a3dcrane.intent_data.yAngle";

	public static final String PORT_NUMBER = "a3dcrane.intent_data.port_number";


	private int mPortNumber;

	private Thread UDPBroadcastThread;
	private Boolean shouldRestartSocketListen = true;


	DatagramSocket socket;


	private void listenAndWaitAndThrowIntent() throws Exception {

		byte[] recvBuf = new byte[40];
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
		double xAngle = db.get(3);
		double yAngle = db.get(4);

		Log.i("UDP", String.format("Got UDB broadcast from: %s x = %f, y = %f, z = %f, xAngle = %f, yAngle = %f", senderIP, x, y, z, xAngle, yAngle));

		broadcastIntent(x, y, z, xAngle, yAngle);
		socket.close();
	}

	private void broadcastIntent(double x, double y, double z, double xAngle, double yAngle) {
		Log.i("Broadcast", "broadcasting intent");
		Intent intent = new Intent(UDP_BROADCAST);
		intent.putExtra(INTENT_DATA_X, x);
		intent.putExtra(INTENT_DATA_Y, y);
		intent.putExtra(INTENT_DATA_Z, z);
		intent.putExtra(INTENT_DATA_X_ANGLE, xAngle);
		intent.putExtra(INTENT_DATA_Y_ANGLE, yAngle);
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
					Log.e("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
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