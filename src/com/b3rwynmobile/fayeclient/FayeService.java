// @formatter:off
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

package com.b3rwynmobile.fayeclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.b3rwynmobile.fayeclient.config.FayeConfigurations;

/**
 * Service class to run Faye. Provides a singleton method to get the running
 * instance.
 * 
 * @author Jamison Greeley (atomicrat2552@gmail.com)
 */
public class FayeService extends Service {

	// Data objects
	protected FayeClient	mFaye;
	protected FayeBinder	mFayeBinder;

	/**
	 * Default constructor
	 */
	public FayeService() {
		super();
		FayeConfigurations.tracker(this);
	}

	/**
	 * Returns the Binder to interact with Faye. This is the prefered method to
	 * run the service, and starting from an Intent is not currently supported
	 */
	@Override
	public IBinder onBind(Intent intent) {
		FayeConfigurations.tracker(this, intent);
		setup();
		return this.mFayeBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		FayeConfigurations.tracker(this);
		setup();
	}

	/**
	 * Stops Faye when the Service is being destroyed by the OS
	 */
	@Override
	public void onDestroy() {
		FayeConfigurations.tracker(this);
		stopFaye();
		super.onDestroy();
	}

	protected void setup() {
		FayeConfigurations.tracker(this);

		// Create the client
		this.mFaye = new FayeClient(FayeConfigurations.shared.FAYE_URL,
		        FayeConfigurations.shared.FAYE_INITIAL_CHANNEL,
		        FayeConfigurations.shared.FAYE_AUTH_TOKEN);

		// Create the binder
		this.mFayeBinder = new FayeBinder(this, this.mFaye);
	}

	/**
	 * Starts the Faye client
	 */
	public void startFaye() {
		FayeConfigurations.tracker(this);
		this.mFaye.connect();
	}

	/**
	 * Stops the Faye client
	 */
	public void stopFaye() {
		FayeConfigurations.tracker(this);
		this.mFaye.disconnect();
	}

}
