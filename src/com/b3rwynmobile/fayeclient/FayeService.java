package com.b3rwynmobile.fayeclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service class to run Faye. Provides a singleton method to get the running
 * instance.
 * 
 * @author Jamison Greeley (atomicrat2552@gmail.com)
 */
public class FayeService extends Service {

	// Debug tag
	@SuppressWarnings("unused")
	private final String		TAG				= "FayeService";

	// String constants
	final private static String	FAYE_HOST		= "ws://YOUR_SERVICE_URL";
	final private static String	FAYE_PORT		= "5556";
	final private static String	AUTH_TOKEN		= "SECRET_TOKEN";
	final private static String	INITIAL_CHANNEL	= "/notifications";

	// Stored static instance of the service
	private static FayeService	instance;

	// Data objects
	private FayeClient			faye;
	private FayeListener		fayeListener;

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;
	}

	/**
	 * Returns the Binder to interact with Faye. This is the prefered method to
	 * run the service, and starting from an Intent is not currently supported
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO return the Message binder
		// TODO start Faye
		return null;
	}

	/**
	 * Stops Faye when the Service is being destroyed by the OS
	 */
	@Override
	public void onDestroy() {
		stopFaye();
		super.onDestroy();
	}

	/**
	 * Returns a pointer to the currently running service
	 * 
	 * @return The instance of the service that running
	 */
	public static FayeService getInstance() {
		return instance;
	}

	/**
	 * Get the service's FayeListener
	 * 
	 * @return The FayeListener that the service attached to the client
	 */
	public FayeListener getFayeListener() {
		return fayeListener;
	}

	/**
	 * Starts the Faye client
	 */
	public void startFaye() {
		faye = new FayeClient(FAYE_HOST + ":" + FAYE_PORT, AUTH_TOKEN,
				INITIAL_CHANNEL);
		fayeListener = new FayeListener();
		faye.setFayeListener(fayeListener);
		faye.openSocketConnection();
		faye.connectFaye();
	}

	/**
	 * Stops the Faye client
	 */
	public void stopFaye() {
		if (faye.isSocketConnected()) {
			faye.disconnectFaye();
			faye.closeSocketConnection();
		}
	}

}
