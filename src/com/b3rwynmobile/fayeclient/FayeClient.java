/******************************************************************************
 * Copyright 2011-2012 b3rwyn Mobile Solutions Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 ******************************************************************************/

package com.b3rwynmobile.fayeclient;

import android.util.Log;

import com.b3rwynmobile.fayeclient.models.FayeMessage;
import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles interactions with the Faye server, such as connecting,
 * (un-)subscribing to channels and receiving push messages
 * 
 * @author Jamison Greeley (atomicrat552@gmail.com)
 */
public class FayeClient extends WebSocketConnectionHandler {

	// Debug tag
	private static final String	TAG						= "FayeClient";

	// Channel constants
	private static final String	HANDSHAKE_CHANNEL		= "/meta/handshake";
	private static final String	CONNECT_CHANNEL			= "/meta/connect";
	private static final String	DISCONNECT_CHANNEL		= "/meta/disconnect";
	private static final String	SUBSCRIBE_CHANNEL		= "/meta/subscribe";
	private static final String	UNSUBSCRIBE_CHANNEL		= "/meta/unsubscribe";

	// String constants
	private static final String	FAYE_SUBSCRIBE_STRING	= "{\"clientId\":\"{0}\",\"subscription\":\"{1}\",\"channel\":\"/meta/subscribe\",\"ext\":{\"authToken\":\"{2}\"}}";
	private static final String	FAYE_UNSUBSCRIBE_STRING	= "{\"clientId\":\"{0}\",\"subscription\":\"{1}\",\"channel\":\"/meta/unsubscribe\"}";

	// Data objects
	private WebSocketConnection	webSocket;
	private FayeListener		fayeListener;

	// Connection fields
	private String				fayeUrl;
	private String				authToken;
	private List<String>		activeSubchannels;
	private String				clientId;

	// Status fields
	private boolean				fayeConnected;
	private boolean				disconnectExpected;

	/**
	 * Simplified constructor
	 * 
	 * @param fayeUrl
	 *            Url of the Faye server
	 * @param authToken
	 *            Token for Faye authentication
	 */
	public FayeClient(String fayeUrl) {
		this(fayeUrl, "");
	}

	/**
	 * Full constructor
	 * 
	 * @param fayeUrl
	 *            Url of they Faye server
	 * @param authToken
	 *            Token for Faye authentication
	 * @param channel
	 *            Channel to subscribe to after handshake
	 */
	public FayeClient(String fayeUrl, String channel) {
		this.fayeUrl = fayeUrl;
		this.activeSubchannels = new ArrayList<String>();
		this.fayeConnected = false;
		this.disconnectExpected = false;

		// Add any non-blank channel
		if (!channel.equals("")) {
			this.activeSubchannels.add(channel);
		}
	}

	/**
	 * Gets the FayeListener attached to the client
	 * 
	 * @return The FayeListener attached to the client
	 */
	public FayeListener getFayeListener() {
		return fayeListener;
	}

	/**
	 * Sets the FayeListener attached to the client
	 * 
	 * @param mFayeListener
	 *            The FayeListener to attach to the client
	 */
	public void setFayeListener(FayeListener fayeListener) {
		this.fayeListener = fayeListener;
	}

	/**
	 * Whether or not the client has a websocket connection to the push server
	 * 
	 * @return The status of the websocket connection
	 */
	public boolean isSocketConnected() {
		return webSocket.isConnected();
	}

	/**
	 * Whether or not the client has a connection to the push server
	 * 
	 * @return The status of the push server connection
	 */
	public boolean isFayeConnected() {
		return fayeConnected;
	}

	/**
	 * Whether or not the client disconnection is expected
	 * 
	 * @return The status of whether disconnect was expected
	 */
	public boolean isDisconnectExpected() {
		return disconnectExpected;
	}

	/**
	 * Gets the client's current ID provided by the Faye server
	 * 
	 * @return The client's current ID. Null if no connection established.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Disconnects the socket if needed, build and connect the socket, then
	 * handshake
	 */
	public void connect() {
		disconnectExpected = false;
		openSocketConnection();
	}

	/**
	 * Disconnects Faye and the socket gracefully
	 */
	public void disconnect() {
		disconnectExpected = true;
		closeFayeConnection();
		closeSocketConnection();
	}

	private void openSocketConnection() {
		WebSocketOptions options = new WebSocketOptions();
		options.setReceiveTextMessagesRaw(false);
		options.setSocketConnectTimeout(30000);
		webSocket = new WebSocketConnection();
		try {
			webSocket.connect(fayeUrl, this, options);
			Log.d(TAG, "Service is opening the web socket");
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

	private void closeSocketConnection() {
		if (webSocket != null) {
			if (webSocket.isConnected()) {
				webSocket.disconnect();
			}
			webSocket = null;
		}
	}

	private void handshake() {
		String handshakeString = "{\"supportedConnectionTypes\":[\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\""
				+ HANDSHAKE_CHANNEL + "\"}";
		Log.d(TAG, "Handshake: " + handshakeString);
		webSocket.sendTextMessage(handshakeString);
	}

	private void openFayeConnection() {
		String connectString = "{\"channel\":\"" + CONNECT_CHANNEL
				+ "\",\"clientID\":\"" + clientId
				+ "\",\"connectionType\":\"websocket\"}";
		Log.d(TAG, "Connect: " + connectString);
		webSocket.sendTextMessage(connectString);
	}

	private void closeFayeConnection() {
		if (clientId == null) { return; }

		String disconnectString = "{\"channel\":\"" + DISCONNECT_CHANNEL
				+ "\",\"clientID\":\"" + clientId + "\"}";
		Log.d(TAG, "Disconnect: " + disconnectString);
		webSocket.sendTextMessage(disconnectString);
		clientId = null;
	}

	/**
	 * Subscribes the push client to a channel on the push server
	 * 
	 * @param channel
	 *            The channel to subscribe to
	 */
	public void subscribe(String channel) {
		Log.d(TAG, "Faye is attempting to subscribe to channel \"" + channel
				+ "\"");
	}

	/**
	 * Unsubscribes the push client from a channel on the push server
	 * 
	 * @param channel
	 *            The channel to unsubscribe to
	 */
	public void unsubscribe(String channel) {
		Log.d(TAG, "Faye is attempting to unsubscribe from channel \""
				+ channel + "\"");
	}

	/**
	 * Handles the event of the web socket opening. Will automatically open the
	 * Faye connection when the socket has opened.
	 */
	@Override
	public void onOpen() {
		Log.d(TAG, "Socket opened");
		handshake();
	}

	/**
	 * Handles the socket losing connection. If the disconnect is expected, it
	 * gracefully ends. If the disconnect is unexpected, the socket will
	 * re-establish the Faye connection.
	 */
	@Override
	public void onClose(int code, String reason) {
		switch (code) {
			case CLOSE_INTERNAL_ERROR:
				webSocket = new WebSocketConnection();
				connect();
				break;
			case CLOSE_CANNOT_CONNECT:
			case CLOSE_CONNECTION_LOST:
			case CLOSE_PROTOCOL_ERROR:
				while (!webSocket.reconnect()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			case CLOSE_NORMAL:
				break;
		}
	}

	/**
	 * Automated method to handle messages coming across the web socket.
	 */
	@Override
	public void onTextMessage(String payload) {
		Log.d(TAG, "Text message payload: " + payload);
		Gson gson = new Gson();
		FayeMessage message = gson.fromJson(payload, FayeMessage.class);
		String channel = message.getChannel();

		if (channel.equals(HANDSHAKE_CHANNEL)) {
			if (message.isSuccessful()) {
				clientId = message.getClientId();
				openFayeConnection();
			} else {
				Log.e(TAG, "Faye failed to handshake");
			}
		} else if (channel.equals(CONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				fayeConnected = true;
				fayeListener.connectedToServer(this);
			} else {
				fayeConnected = false;
				Log.e(TAG, "Faye failed to connect");
			}
		} else if (channel.equals(DISCONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				fayeConnected = false;
				fayeListener.disconnectedFromServer(this);
				closeSocketConnection();
			} else {
				fayeConnected = true;
				Log.e(TAG, "Faye failed to disconnect");
			}
		} else if (channel.equals(SUBSCRIBE_CHANNEL)) {
			if (message.isSuccessful()) {
				Log.i(TAG,
						"Faye subscribed to channel"
								+ message.getSubscription());
				activeSubchannels.add(message.getSubscription());
			} else {
				Log.e(TAG, MessageFormat.format(
						"Faye failed to connect to channel {0} with error {1}",
						message.getSubscription(), message.getError()));
				// TODO Handle failed subscribe
			}
		} else if (channel.equals(UNSUBSCRIBE_CHANNEL)) {
			Log.i(TAG,
					"Faye unsubscribed from channel "
							+ message.getSubscription());
		} else if (activeSubchannels.contains(channel)) {
			fayeListener.messageReceived(this, message);
		} else {
			Log.e(TAG,
					"Faye recieved a message with no subscription for channel "
							+ message.getSubscription());
		}
	}

	/**
	 * Receives payload as raw text. Converts raw message into text and parses
	 * it
	 */
	@Override
	public void onRawTextMessage(byte[] payload) {
		onTextMessage(new String(payload));
	}

	/**
	 * Not implemented as of yet.
	 */
	@Override
	public void onBinaryMessage(byte[] payload) {
		throw new UnsupportedOperationException(
				"This type of payload isn't supported yet");
	}
}