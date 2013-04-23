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

import com.b3rwynmobile.fayeclient.config.FayeConfigurations;

public class FayeHeartbeatThread extends Thread {

	private int	       delay;
	private FayeClient	client;

	public FayeHeartbeatThread(FayeClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		int sleepCount = 0;
		while (sleepCount < delay) {
			try {
				Thread.sleep(1000);
				sleepCount++;
			} catch (InterruptedException e) {
				FayeConfigurations.logException(e);
			}
		}
		client.heartbeat();
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
