<<<<<<< HEAD
<<<<<<< HEAD
package com.b3rwynmobile.fayeclient;

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

package com.b3rwynmobile.fayeclient;

import com.b3rwynmobile.fayeclient.models.FayeMessage;

>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
public class FayeListener {

	/**
	 * Method to handle logic when the client connects to the server
	 * 
	 * @param faye
	 *            The client that's established a connection
	 */
<<<<<<< HEAD
<<<<<<< HEAD
	public void messageReceived(FayeClient faye, String msg) {
=======
	public void messageReceived(FayeClient faye, FayeMessage msg) {
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
		// TODO handle message
=======
	public void connectedToServer(FayeClient faye) {
		// TODO What to do when the connection is successful
>>>>>>> a67cfa5c97ba0e6b2c7f8e098a1efeb4453b6be3
	}

	/**
	 * Method to handle client getting disconnected from the server prematurely
	 * 
	 * @param faye
	 *            The client that's been disconnected
	 */
	public void disconnectedFromServer(FayeClient faye) {
		if (!faye.isDisconnectExpected()) {
			// If the disconnect isn't expected
<<<<<<< HEAD
<<<<<<< HEAD
			if (faye.isSocketConnected()) {
				// If the socket is still open, reconnect push client
				faye.connectFaye();
			} else {
				// Else open the socket and connect Faye
				faye.openSocketConnection();
				faye.connectFaye();
			}
=======
			// TODO reconnect Faye
>>>>>>> 10e3aed41feff49b4a1cd57d1bebf3b1be3198fd
=======
			// TODO reconnect Faye
>>>>>>> 7a7ae306695f6f45ec0bd1d6774c847ad24749c1
		} else {
			// TODO logic for when connection is manually closed
		}
	}

	/**
	 * Method used to take action when the client receives a message
	 * 
	 * @param faye
	 *            The client receiving the message
	 * @param msg
	 *            The message the client received
	 */
	public void messageReceived(FayeClient faye, FayeMessage msg) {
		// TODO handle message
	}

}
