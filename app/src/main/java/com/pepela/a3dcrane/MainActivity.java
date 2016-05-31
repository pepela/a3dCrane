package com.pepela.a3dcrane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements CraneView.OnCranePositionChangeEventListener {

	private CraneView craneView;
	private CraneAngleView craneAngleView;

	private Receiver udpReceiveBroadcastReceiver;
	private IntentFilter filter;
	private Intent serviceIntent;

	private Button mSetButton;
	private Button mHomeButton;
	private Button mCenterButton;


	private EditText mXEditText;
	private EditText mYEditText;
	private EditText mZEditText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		craneView = (CraneView) findViewById(R.id.crane);
		craneAngleView = (CraneAngleView) findViewById(R.id.craneAngleView);

		mXEditText = (EditText) findViewById(R.id.mainEditTextX);
		mYEditText = (EditText) findViewById(R.id.mainEditTextY);
		mZEditText = (EditText) findViewById(R.id.mainEditTextZ);

		mSetButton = (Button) findViewById(R.id.mainButtonSet);
		try {
			mHomeButton = (Button) findViewById(R.id.mainButtonHome);
			mCenterButton = (Button) findViewById(R.id.mainButtonCenter);
		} catch (Exception e) {
			Log.i("Main", "Landscape mode has no extra buttons");
		}

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

				double x = Double.valueOf(xStr);
				double y = Double.valueOf(yStr);
				double z = Double.valueOf(zStr);
				craneView.setPosition(x, y, z);

				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String ip = sharedPreferences.getString(getString(R.string.pref_ip_key), getString(R.string.pref_ip_default));
				int port = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_sending_port_key), "88"));

				SendDataTask sdt = new SendDataTask();
				sdt.setIp(ip);
				sdt.setPort(port);
				sdt.execute(x / 100, y / 100, z / 100);
			}
		});


		if (mCenterButton != null)
			mCenterButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					double x = 15;
					double y = 15;
					double z = 25;

					craneView.setPosition(x, y, z);

					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String ip = sharedPreferences.getString(getString(R.string.pref_ip_key), getString(R.string.pref_ip_default));
					int port = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_sending_port_key), "2500"));

					SendDataTask sdt = new SendDataTask();
					sdt.setIp(ip);
					sdt.setPort(port);
					sdt.execute(x / 100, y / 100, z / 100);
				}
			});

		if (mHomeButton != null)
			mHomeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					double x = 0;
					double y = 0;
					double z = 0;

					craneView.setPosition(x, y, z);

					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String ip = sharedPreferences.getString(getString(R.string.pref_ip_key), getString(R.string.pref_ip_default));
					int port = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_sending_port_key), "2500"));

					SendDataTask sdt = new SendDataTask();
					sdt.setIp(ip);
					sdt.setPort(port);
					sdt.execute(x, y, z);
				}
			});

		mXEditText.setText(String.format("%.2f", 0.0)); //craneView.getXInCm()));
		mYEditText.setText(String.format("%.2f", 0.0)); //craneView.getYInCm()));
		mZEditText.setText(String.format("%.2f", 0.0)); //craneView.getZInCm()));

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int port = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_receiving_port_key), "25001"));

		serviceIntent = new Intent(this, MatlabConnection.class);
		serviceIntent.putExtra(MatlabConnection.PORT_NUMBER, port);
		startService(serviceIntent);

		udpReceiveBroadcastReceiver = new Receiver();
		filter = new IntentFilter(MatlabConnection.UDP_BROADCAST);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingsIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			double x = arg1.getDoubleExtra(MatlabConnection.INTENT_DATA_X, 10);
			double y = arg1.getDoubleExtra(MatlabConnection.INTENT_DATA_Y, 10);
			double z = arg1.getDoubleExtra(MatlabConnection.INTENT_DATA_Z, 10);
			double xAngle = arg1.getDoubleExtra(MatlabConnection.INTENT_DATA_X_ANGLE, 0);
			double yAngle = arg1.getDoubleExtra(MatlabConnection.INTENT_DATA_Y_ANGLE, 0);

			craneView.setShadowPosition(x * 100, y * 100, z * 100);
			craneAngleView.setCraneAngle(xAngle, yAngle);
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
	public void onPositionChangeEvent(double x, double y, double z) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String ip = sharedPreferences.getString(getString(R.string.pref_ip_key), getString(R.string.pref_ip_default));
		int port = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_sending_port_key), "88"));

		SendDataTask sendDataTask = new SendDataTask();
		sendDataTask.setIp(ip);
		sendDataTask.setPort(port);
		sendDataTask.execute(x / 100, y / 100, z / 100);
	}


}
