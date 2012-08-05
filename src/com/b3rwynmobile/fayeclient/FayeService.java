/******************************************************************************
 *
 *  Copyright 2011-2012 b3rwyn Mobile Solutions
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package com.b3rwynmobile.fayeclient;

import android.app.IntentService;
import android.content.Intent;
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
	final private static String	FAYE_HOST		= "ws://push01.cloudsdale.org/push";
	final private static String	FAYE_PORT		= "80";
	final private static String	AUTH_TOKEN		= "e854ebd38d63042f210214f95b5281b8934b359821cade18e52549e3788ef713";
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
		setup();
	}

	/**
	 * Returns the Binder to interact with Faye. This is the prefered method to
	 * run the service, and starting from an Intent is not currently supported
	 */
	@Override
	public IBinder onBind(Intent intent) {
		setup();
		return fayeBinder;
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
		Toast.makeText(getApplicationContext(), "Faye Started",
				Toast.LENGTH_SHORT).show();
		faye.connect();
	}

	/**
	 * Stops the Faye client
	 */
	public void stopFaye() {
		faye.disconnect();
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
	}

	private void setup() {
		// Debug toast
		Toast.makeText(getApplicationContext(), "Faye Service created",
				Toast.LENGTH_SHORT).show();

		// Create the client
		faye = new FayeClient(FAYE_HOST + ":" + FAYE_PORT);

		// Create the binder
		fayeBinder = new FayeBinder(this, faye);

		// Create the Faye listener
		fayeListener = new FayeListener();
		faye.setFayeListener(fayeListener);
	}

}
