<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
// @formatter:off
>>>>>>> a67cfa5c97ba0e6b2c7f8e098a1efeb4453b6be3
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
// @formatter:on

>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
package com.b3rwynmobile.fayeclient;

import android.app.Service;
import android.content.Intent;
<<<<<<< HEAD
import android.os.Binder;
=======
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Service class to run Faye. Provides a singleton method to get the running
 * instance.
 * 
 * @author Jamison Greeley (atomicrat2552@gmail.com)
 */
public class FayeService extends Service {

	// Debug tag
	private final String		TAG				= "Faye Service";

	// String constants
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
	final private static String	FAYE_HOST		= "ws://push01.cloudsdale.org";
	final private static String	FAYE_PORT		= "80";
	final private static String	AUTH_TOKEN		= "e854ebd38d63042f210214f95b5281b8934b359821cade18e52549e3788ef713";
=======
	final private static String	FAYE_HOST		= "ws://YOUR_SERVICE_URL";
	final private static String	FAYE_PORT		= "5556";
	final private static String	AUTH_TOKEN		= "SECRET_TOKEN";
>>>>>>> 10e3aed41feff49b4a1cd57d1bebf3b1be3198fd
=======
	final private static String	FAYE_HOST		= "HOST_ADDRESS";
	final private static String	FAYE_PORT		= "HOST_PORT";
	final private static String	AUTH_TOKEN		= "SECRET_TOKEN";
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
	final private static String	INITIAL_CHANNEL	= "/notifications";
=======
	final private static String	FAYE_HOST		= "ws://myhost.com";
	final private static String	FAYE_PORT		= "80";
	final private static String	INITIAL_CHANNEL	= "/push";
	final private static String	AUTH_TOKEN		= "SUPER SECRET TOKEN";
>>>>>>> a67cfa5c97ba0e6b2c7f8e098a1efeb4453b6be3

	// Data objects
	private FayeClient			faye;
	private FayeListener		fayeListener;
	private FayeBinder			fayeBinder;

	/**
	 * Default constructor
	 */
	public FayeService() {
<<<<<<< HEAD
		super("FayeService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
<<<<<<< HEAD
		
		// Debug toast
		Toast.makeText(getApplicationContext(), "Faye Service created",
				Toast.LENGTH_SHORT).show();
		
		// Create the binder
		fayeBinder = new FayeBinder();
		
		// Create the Faye client and listener
<<<<<<< HEAD
		faye = new FayeClient(FAYE_HOST + ":" + FAYE_PORT, AUTH_TOKEN,
				INITIAL_CHANNEL);
=======
>>>>>>> 10e3aed41feff49b4a1cd57d1bebf3b1be3198fd
		fayeListener = new FayeListener();
		faye.setFayeListener(fayeListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
=======
		setup();
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
=======
		super();
>>>>>>> a67cfa5c97ba0e6b2c7f8e098a1efeb4453b6be3
	}

	/**
	 * Returns the Binder to interact with Faye. This is the prefered method to
	 * run the service, and starting from an Intent is not currently supported
	 */
	@Override
	public IBinder onBind(Intent intent) {
<<<<<<< HEAD
		return fayeBinder;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
	}

=======
		setup();
		return this.fayeBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setup();
	}

>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
	/**
	 * Stops Faye when the Service is being destroyed by the OS
	 */
	@Override
	public void onDestroy() {
		stopFaye();
		super.onDestroy();
	}

	private void setup() {
		// Debug toast
		Toast.makeText(getApplicationContext(), "Faye Service created",
				Toast.LENGTH_SHORT).show();
		String fayeUrl = FayeService.FAYE_HOST + ":" + FayeService.FAYE_PORT
				+ FayeService.INITIAL_CHANNEL;

		// Create the client
		this.faye = new FayeClient(fayeUrl);

		// Create the binder
		this.fayeBinder = new FayeBinder(this, this.faye);

		// Create the Faye listener
		this.fayeListener = new FayeListener();
		this.faye.setFayeListener(this.fayeListener);
	}

	/**
	 * Starts the Faye client
	 */
	public void startFaye() {
<<<<<<< HEAD
<<<<<<< HEAD
		faye.openSocketConnection();
		faye.connectFaye();
		Toast.makeText(getApplicationContext(), "Faye Started",
				Toast.LENGTH_SHORT).show();
=======
		Toast.makeText(getApplicationContext(), "Faye Started",
				Toast.LENGTH_SHORT).show();
		// TODO start Faye
>>>>>>> 10e3aed41feff49b4a1cd57d1bebf3b1be3198fd
=======
		Toast.makeText(getApplicationContext(), "Faye Started",
				Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
		faye.connect();
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
=======
		this.faye.connect();
>>>>>>> a67cfa5c97ba0e6b2c7f8e098a1efeb4453b6be3
	}

	/**
	 * Stops the Faye client
	 */
	public void stopFaye() {
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
		if (faye.isSocketConnected()) {
			faye.disconnectFaye();
			faye.closeSocketConnection();
		}
=======
		// TODO stop Faye
>>>>>>> 10e3aed41feff49b4a1cd57d1bebf3b1be3198fd
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
=======
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
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
=======
		this.faye.disconnect();
>>>>>>> a67cfa5c97ba0e6b2c7f8e098a1efeb4453b6be3
	}

}
