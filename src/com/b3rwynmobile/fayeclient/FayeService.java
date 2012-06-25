package com.b3rwynmobile.fayeclient;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class FayeService extends Service {

	final private String		TAG			= getClass().getSimpleName();

	final private static String	FAYE_HOST	= "ws://push01.cloudsdale.org";
	final private static String	FAYE_PORT	= "80";
	final private static String	authToken	= "e854ebd38d63042f210214f95b5281b8934b359821cade18e52549e3788ef713";
	final private static String	mainChannel	= "/notifications";

	FayeClient					fayeClient;

	@Override
	public IBinder onBind(Intent intent) {
		startFaye();
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopFaye();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		startFaye();
	}

	/**
	 * Public section
	 */
	private void startFaye() {
		fayeClient = new FayeClient(FAYE_HOST + ":" + FAYE_PORT + "/faye",
				authToken, mainChannel);
		fayeClient.setListener(fayeClientListener);
		fayeClient.connectToServer();
		Toast.makeText(getApplicationContext(), "FayeService Started!",
				Toast.LENGTH_LONG).show();
	}

	public void stopFaye() {
		if (fayeClient.isWebSocketConnected())
			fayeClient.disconnectFromServer();
		Toast.makeText(getApplicationContext(), "FayeService Stopped!",
				Toast.LENGTH_LONG).show();
	}

	/**
	 * FayeClientListener
	 */
	FayeClientListener	fayeClientListener	= new FayeClientListener() {

												@Override
												public void messageReceieved(
														FayeClient fc,
														String msg) {
													Log.i(TAG,
															"MESSAGE FROM SERVER: "
																	+ msg);
													Toast.makeText(
															getApplicationContext(),
															"Message from server: "
																	+ msg,
															Toast.LENGTH_LONG)
															.show();
												}

												@Override
												public void disconnectedFromServer(
														FayeClient fc) {
													Log.i(TAG,
															"DISCONNECTED FROM SERVER");
													Toast.makeText(
															getApplicationContext(),
															"Disconnected from faye server",
															Toast.LENGTH_LONG)
															.show();
													fc.connectToServer(); // reconnect
																			// when
																			// disconnect
												}

												@Override
												public void connectedToServer(
														FayeClient fc) {
													Log.i(TAG,
															"CONNECTED TO SERVER");
													fc.subscribeToChannel(mainChannel);
													Toast.makeText(
															getApplicationContext(),
															"Connected to faye server on channel: "
																	+ mainChannel,
															Toast.LENGTH_LONG)
															.show();
												}
											};

	/**
	 * Hnadler class to handle messages coming in to the service because Fuck
	 * Tim WhatsHisAss for making the service useless
	 * 
	 * @author Jamison Greeley (atomicrat2552@gmail.com)
	 */
	private class FayeServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
		}

	}
}
