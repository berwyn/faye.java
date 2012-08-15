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

import android.util.Log;

import com.b3rwynmobile.fayeclient.models.FayeMessage;
import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles interactions with the Faye server, such as connecting,
 * (un-)subscribing to channels and receiving push messages
 * 
 * @author Jamison Greeley (atomicrat552@gmail.com)
 */
public class FayeClient {

	// Debug tag
	private static final String	TAG					= "Faye Client";

	// Channel constants
	private static final String	HANDSHAKE_CHANNEL	= "/meta/handshake";
	private static final String	CONNECT_CHANNEL		= "/meta/connect";
	private static final String	DISCONNECT_CHANNEL	= "/meta/disconnect";
	private static final String	SUBSCRIBE_CHANNEL	= "/meta/subscribe";
	private static final String	UNSUBSCRIBE_CHANNEL	= "/meta/unsubscribe";

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
	 * Simplified constructor
	 * 
	 * @param fayeUrl
	 *            Url of they Faye server
	 * @param authToken
	 *            Token for Faye authentication
	 * @param channel
	 *            Channel to subscribe to after handshake
	 */
	public FayeClient(String fayeUrl, String channel) {
		this(fayeUrl, channel, "");
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
	 * @param authToken
	 *            Auth token for authenticated Faye hosts
	 */
	public FayeClient(String fayeUrl, String channel, String authToken) {
		this.fayeUrl = fayeUrl;
		this.activeSubchannels = new ArrayList<String>();
		this.fayeConnected = false;
		this.disconnectExpected = false;
		this.authToken = authToken;

		// Add any non-blank channel
		if (!channel.equals("")) {
			this.activeSubchannels.add(channel);
		}
	}

	private void closeFayeConnection() {
		if (this.clientId == null) { return; }

		String disconnectString = "{\"channel\":\""
				+ FayeClient.DISCONNECT_CHANNEL + "\",\"clientID\":\""
				+ this.clientId + "\"}";
		Log.d(FayeClient.TAG, "Disconnect: " + disconnectString);
		this.webSocket.sendTextMessage(disconnectString);
		this.clientId = null;
	}

	private void closeSocketConnection() {
		if (this.webSocket != null) {
			if (this.webSocket.isConnected()) {
				this.webSocket.disconnect();
			}
			this.webSocket = null;
		}
	}

	/**
	 * Disconnects the socket if needed, build and connect the socket, then
	 * handshake
	 */
	public void connect() {
		this.disconnectExpected = false;
		openSocketConnection();
	}

	/**
	 * Disconnects Faye and the socket gracefully
	 */
	public void disconnect() {
		this.disconnectExpected = true;
		closeFayeConnection();
		closeSocketConnection();
	}

	/**
	 * Gets the client's current ID provided by the Faye server
	 * 
	 * @return The client's current ID. Null if no connection established.
	 */
	public String getClientId() {
		return this.clientId;
	}

	/**
	 * Gets the FayeListener attached to the client
	 * 
	 * @return The FayeListener attached to the client
	 */
	public FayeListener getFayeListener() {
		return this.fayeListener;
	}

	/**
	 * Whether or not the client disconnection is expected
	 * 
	 * @return The status of whether disconnect was expected
	 */
	public boolean isDisconnectExpected() {
		return this.disconnectExpected;
	}

	/**
	 * Whether or not the client has a connection to the push server
	 * 
	 * @return The status of the push server connection
	 */
	public boolean isFayeConnected() {
		return this.fayeConnected;
	}

	/**
	 * Whether or not the client has a websocket connection to the push server
	 * 
	 * @return The status of the websocket connection
	 */
	public boolean isSocketConnected() {
		return this.webSocket.isConnected();
	}

	private void openFayeConnection() {
		String connectString = "{\"channel\":\"" + FayeClient.CONNECT_CHANNEL
				+ "\",\"clientId\":\"" + this.clientId
				+ "\",\"connectionType\":\"websocket\"}";
		try {
			webSocket.sendBinaryMessage(connectString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void openSocketConnection() {
		WebSocketOptions options = new WebSocketOptions();
		options.setReceiveTextMessagesRaw(true);
		this.webSocket = new WebSocketConnection();
		try {
			this.webSocket.connect(this.fayeUrl, new WebSocketHandler() {

				public void onBinaryMessage(byte[] payload) {
					onTextMessage(new String(payload));
				}

				public void onClose(int code, String reason) {
					processClose(code);
				}

				public void onOpen() {
					String handshakeString = "{\"supportedConnectionTypes\":[\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\""
							+ FayeClient.HANDSHAKE_CHANNEL + "\"}";
					try {
						webSocket.sendBinaryMessage(handshakeString
								.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				public void onRawTextMessage(byte[] payload) {
					onTextMessage(new String(payload));
				}

				public void onTextMessage(String payload) {
					Log.d(FayeClient.TAG, "Text message payload: " + payload);
					Gson gson = new Gson();
					FayeMessage[] messages = gson.fromJson(payload,
							FayeMessage[].class);
					for (FayeMessage message : messages) {
						String channel = message.getChannel();
						processTextMessage(message, channel);
					}
				}
			}, options);
			Log.d(FayeClient.TAG, "Service is opening the web socket");
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

	private void processTextMessage(FayeMessage message, String channel) {
		if (channel.equals(FayeClient.HANDSHAKE_CHANNEL)) {
			if (message.isSuccessful()) {
				clientId = message.getClientId();
				openFayeConnection();
			} else {
				Log.e(FayeClient.TAG, "Faye failed to handshake");
			}
		} else if (channel.equals(FayeClient.CONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				fayeConnected = true;
				fayeListener.connectedToServer(this);
				Log.d(TAG, "Faye connected");
			} else {
				fayeConnected = false;
				Log.e(FayeClient.TAG, "Faye failed to connect");
			}
		} else if (channel.equals(FayeClient.DISCONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				fayeConnected = false;
				fayeListener.disconnectedFromServer(this);
				closeSocketConnection();
			} else {
				fayeConnected = true;
				Log.e(FayeClient.TAG, "Faye failed to disconnect");
			}
		} else if (channel.equals(FayeClient.SUBSCRIBE_CHANNEL)) {
			if (message.isSuccessful()) {
				Log.i(FayeClient.TAG,
						"Faye subscribed to channel"
								+ message.getSubscription());
				activeSubchannels.add(message.getSubscription());
			} else {
				Log.e(FayeClient.TAG, MessageFormat.format(
						"Faye failed to connect to channel {0} with error {1}",
						message.getSubscription(), message.getError()));
				// TODO Handle failed subscribe
			}
		} else if (channel.equals(FayeClient.UNSUBSCRIBE_CHANNEL)) {
			Log.i(FayeClient.TAG,
					"Faye unsubscribed from channel "
							+ message.getSubscription());
		} else if (this.activeSubchannels.contains(channel)) {
			fayeListener.messageReceived(this, message);
		} else {
			Log.e(FayeClient.TAG,
					"Faye recieved a message with no subscription for channel "
							+ message.getSubscription());
		}
	}

	private void processClose(int code) {
		switch (code) {
			case WebSocketHandler.CLOSE_INTERNAL_ERROR:
				FayeClient.this.webSocket = new WebSocketConnection();
				connect();
				break;
			case WebSocketHandler.CLOSE_PROTOCOL_ERROR:
			case WebSocketHandler.CLOSE_CANNOT_CONNECT:
			case WebSocketHandler.CLOSE_CONNECTION_LOST:
				while (!FayeClient.this.webSocket.isConnected()) {
					try {
						connect();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			case WebSocketHandler.CLOSE_NORMAL:
				break;
		}
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
	 * Subscribes the push client to a channel on the push server
	 * 
	 * @param channel
	 *            The channel to subscribe to
	 */
	public void subscribe(String channel) {
		String subscribe = "{\"clientId\":\""
				+ this.clientId
				+ "\",\"subscription\":\""
				+ channel
				+ "\",\"channel\":\"/meta/subscribe\",\"ext\":{\"authToken\":\""
				+ this.authToken + "\"}}";
		Log.d(FayeClient.TAG, "Faye is attempting to subscribe to channel \""
				+ channel + "\"");
		try {
			this.webSocket.sendBinaryMessage(subscribe.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unsubscribes the push client from a channel on the push server
	 * 
	 * @param channel
	 *            The channel to unsubscribe to
	 */
	public void unsubscribe(String channel) {
		String unsubscribe = "{\"clientId\":\"" + this.clientId
				+ "\",\"subscription\":\"" + channel
				+ "\",\"channel\":\"/meta/unsubscribe\"}";
		Log.d(FayeClient.TAG,
				"Faye is attempting to unsubscribe from channel \"" + channel
						+ "\"");
		try {
			this.webSocket.sendBinaryMessage(unsubscribe.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}