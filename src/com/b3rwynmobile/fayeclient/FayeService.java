package com.b3rwynmobile.fayeclient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Service class to run Faye. Provides a singleton method to get the running
 * instance.
 * 
 * @author Jamison Greeley (atomicrat2552@gmail.com)
 */
public class FayeService extends IntentService {

	// Debug tag
	@SuppressWarnings("unused")
	private final String		TAG				= "FayeService";

	// String constants
	final private static String	FAYE_HOST		= "ws://YOUR_SERVICE_URL";
	final private static String	FAYE_PORT		= "5556";
	final private static String	AUTH_TOKEN		= "SECRET_TOKEN";
	final private static String	INITIAL_CHANNEL	= "/notifications";

	// Data objects
	private FayeClient			faye;
	private FayeListener		fayeListener;
	private FayeBinder			fayeBinder;

	/**
	 * Default constructor
	 */
	public FayeService() {
		super("FayeService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Debug toast
		Toast.makeText(getApplicationContext(), "Faye Service created",
				Toast.LENGTH_SHORT).show();
		
		// Create the binder
		fayeBinder = new FayeBinder();
		
		// Create the Faye client and listener
		faye = new FayeClient(FAYE_HOST + ":" + FAYE_PORT, AUTH_TOKEN,
				INITIAL_CHANNEL);
		fayeListener = new FayeListener();
		faye.setFayeListener(fayeListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Returns the Binder to interact with Faye. This is the prefered method to
	 * run the service, and starting from an Intent is not currently supported
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return fayeBinder;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
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
	 * Starts the Faye client
	 */
	public void startFaye() {
		faye.openSocketConnection();
		faye.connectFaye();
		Toast.makeText(getApplicationContext(), "Faye Started",
				Toast.LENGTH_SHORT).show();
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

	/**
	 * Binder class to interact with the service
	 * 
	 * @author Jamison Greeley (atomicrat2552@gmail.com)
	 */
	public class FayeBinder extends Binder {

		/**
		 * Public method to get the FayeClient the service is maintaining
		 * 
		 * @return The FayeClient hosted by the service
		 */
		public FayeClient getFayeClient() {
			return FayeService.this.faye;
		}
	}

}
