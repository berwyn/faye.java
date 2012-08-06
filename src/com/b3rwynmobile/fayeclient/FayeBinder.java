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

import android.os.Binder;

/**
 * Binder class to interact with the service
 * 
 * @author Jamison Greeley (atomicrat2552@gmail.com)
 */
public class FayeBinder extends Binder {

	private FayeService	service;
	private FayeClient	faye;

	public FayeBinder() {
		this.service = null;
		this.faye = null;
	}

	public FayeBinder(FayeService service, FayeClient client) {
		this.service = service;
		this.faye = client;
	}

	public FayeClient getFayeClient() {
		return this.faye;
	}

	public FayeService getFayeService() {
		return this.service;
	}

	public void setFayeClient(FayeClient faye) {
		this.faye = faye;
	}

	public void setFayeService(FayeService service) {
		this.service = service;
	}
}
