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

import com.b3rwynmobile.fayeclient.models.FayeMessage;

public interface FayeListener {

	/**
	 * Method to handle logic when the client connects to the server
	 * 
	 * @param faye
	 *            The client that's established a connection
	 */
	public void connectedToServer(FayeClient faye);

	/**
	 * Method to handle client getting disconnected from the server prematurely
	 * 
	 * @param faye
	 *            The client that's been disconnected
	 */
	public void disconnectedFromServer(FayeClient faye);

	/**
	 * Method used to take action when the client receives a message
	 * 
	 * @param faye
	 *            The client receiving the message
	 * @param msg
	 *            The message the client received
	 */
	public void messageReceived(FayeClient faye, FayeMessage msg);

}
