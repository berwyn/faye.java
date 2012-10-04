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
import java.lang.Thread.State;
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
	protected static final String	TAG					= "Faye Client";

	// Channel constants
	protected static final String	HANDSHAKE_CHANNEL	= "/meta/handshake";
	protected static final String	CONNECT_CHANNEL		= "/meta/connect";
	protected static final String	DISCONNECT_CHANNEL	= "/meta/disconnect";
	protected static final String	SUBSCRIBE_CHANNEL	= "/meta/subscribe";
	protected static final String	UNSUBSCRIBE_CHANNEL	= "/meta/unsubscribe";

	// Debug flag
	protected static final boolean	DEBUG				= true;

	// Data objects
	protected WebSocketConnection	mWebSocket;
	protected FayeListener			mFayeListener;
	protected FayeHeartbeatThread	mHeartbeatThread;

	// Connection fields
	protected String				mFayeUrl;
	protected String				mAuthToken;
	protected List<String>			mActiveSubchannels;
	protected String				mClientId;

	// Status fields
	protected boolean				mFayeConnected;
	protected boolean				mDisconnectExpected;

	/**
	 * Simplified constructor
	 * 
	 * @param fayeUrl
	 *            Url of the Faye server
	 * @param mAuthToken
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
	 * @param mAuthToken
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
		this.mFayeUrl = fayeUrl;
		this.mActiveSubchannels = new ArrayList<String>();
		this.mFayeConnected = false;
		this.mDisconnectExpected = false;
		this.mAuthToken = authToken;

		// Add any non-blank channel
		if (!channel.equals("")) {
			this.mActiveSubchannels.add(channel);
		}
	}

	protected void closeFayeConnection() {
		if (this.mClientId == null) { return; }

		String disconnectString = "{\"channel\":\""
				+ FayeClient.DISCONNECT_CHANNEL + "\",\"clientID\":\""
				+ this.mClientId + "\"}";
		if (DEBUG) {
			Log.d(FayeClient.TAG, "Disconnect: " + disconnectString);
		}
		this.mWebSocket.sendTextMessage(disconnectString);
		this.mClientId = null;
	}

	protected void closeSocketConnection() {
		if (this.mWebSocket != null) {
			if (this.mWebSocket.isConnected()) {
				this.mWebSocket.disconnect();
			}
			this.mWebSocket = null;
		}
	}

	/**
	 * Disconnects the socket if needed, build and connect the socket, then
	 * handshake
	 */
	public void connect() {
		this.mDisconnectExpected = false;
		openSocketConnection();
	}

	/**
	 * Disconnects Faye and the socket gracefully
	 */
	public void disconnect() {
		this.mDisconnectExpected = true;
		closeFayeConnection();
		closeSocketConnection();
	}

	/**
	 * Gets the client's current ID provided by the Faye server
	 * 
	 * @return The client's current ID. Null if no connection established.
	 */
	public String getClientId() {
		return this.mClientId;
	}

	/**
	 * Gets the FayeListener attached to the client
	 * 
	 * @return The FayeListener attached to the client
	 */
	public FayeListener getFayeListener() {
		return this.mFayeListener;
	}

	/**
	 * Whether or not the client disconnection is expected
	 * 
	 * @return The status of whether disconnect was expected
	 */
	public boolean isDisconnectExpected() {
		return this.mDisconnectExpected;
	}

	/**
	 * Whether or not the client has a connection to the push server
	 * 
	 * @return The status of the push server connection
	 */
	public boolean isFayeConnected() {
		return this.mFayeConnected;
	}

	/**
	 * Whether or not the client has a websocket connection to the push server
	 * 
	 * @return The status of the websocket connection
	 */
	public boolean isSocketConnected() {
		return this.mWebSocket.isConnected();
	}

	protected void openFayeConnection() {
		String connectString = "{\"channel\":\"" + FayeClient.CONNECT_CHANNEL
				+ "\",\"clientId\":\"" + this.mClientId
				+ "\",\"connectionType\":\"websocket\"}";
		try {
			mWebSocket.sendBinaryMessage(connectString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void openSocketConnection() {
		WebSocketOptions options = new WebSocketOptions();
		options.setReceiveTextMessagesRaw(true);
		this.mWebSocket = new WebSocketConnection();
		try {
			this.mWebSocket.connect(this.mFayeUrl, new WebSocketHandler() {

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
						mWebSocket.sendBinaryMessage(handshakeString
								.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				public void onRawTextMessage(byte[] payload) {
					onTextMessage(new String(payload));
				}

				public void onTextMessage(String payload) {
					if (DEBUG) {
						Log.d(FayeClient.TAG, "Text message payload: "
								+ payload);
					}
					Gson gson = new Gson();
					FayeMessage[] messages = gson.fromJson(payload,
							FayeMessage[].class);
					for (FayeMessage message : messages) {
						String channel = message.getChannel();
						processTextMessage(message, channel);
					}
				}
			}, options);
			if (DEBUG) {
				Log.d(FayeClient.TAG, "Service is opening the web socket");
			}
			Log.d(FayeClient.TAG, "Service is opening the web socket");
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

	protected void processTextMessage(FayeMessage message, String channel) {
		if (channel.equals(FayeClient.HANDSHAKE_CHANNEL)) {
			if (message.isSuccessful()) {
				mClientId = message.getClientId();
				openFayeConnection();
			} else {
				if (DEBUG) {
					Log.e(FayeClient.TAG, "Faye failed to handshake");
				}
			}
		} else if (channel.equals(FayeClient.CONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				mFayeConnected = true;
				mFayeListener.connectedToServer(this);
				scheduleHeartbeat(message.getAdvice().getInterval());
				if (DEBUG) {
					Log.d(TAG, "Faye connected");
				}
			} else {
				mFayeConnected = false;
				if (DEBUG) {
					Log.e(FayeClient.TAG, "Faye failed to connect");
				}
			}
		} else if (channel.equals(FayeClient.DISCONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				mFayeConnected = false;
				mFayeListener.disconnectedFromServer(this);
				closeSocketConnection();
			} else {
				mFayeConnected = true;
				if (DEBUG) {
					Log.e(FayeClient.TAG, "Faye failed to disconnect");
				}
			}
		} else if (channel.equals(FayeClient.SUBSCRIBE_CHANNEL)) {
			if (message.isSuccessful()) {
				if (DEBUG) {
					Log.i(FayeClient.TAG, "Faye subscribed to channel"
							+ message.getSubscription());
				}
				mActiveSubchannels.add(message.getSubscription());
				mFayeConnected = true;
				mFayeListener.connectedToServer(this);
				Log.d(TAG, "Faye connected");
			} else {
				mFayeConnected = false;
				Log.e(FayeClient.TAG, "Faye failed to connect");
			}
		} else if (channel.equals(FayeClient.DISCONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				mFayeConnected = false;
				mFayeListener.disconnectedFromServer(this);
				closeSocketConnection();
			} else {
				mFayeConnected = true;
				Log.e(FayeClient.TAG, "Faye failed to disconnect");
			}
		} else if (channel.equals(FayeClient.SUBSCRIBE_CHANNEL)) {
			if (message.isSuccessful()) {
				Log.i(FayeClient.TAG,
						"Faye subscribed to channel"
								+ message.getSubscription());
				mActiveSubchannels.add(message.getSubscription());
			} else {
				if (DEBUG) {
					Log.e(FayeClient.TAG,
							MessageFormat
									.format("Faye failed to connect to channel {0} with error {1}",
											message.getSubscription(),
											message.getError()));
				}
				// TODO Handle failed subscribe
			}
		} else if (channel.equals(FayeClient.UNSUBSCRIBE_CHANNEL)) {
			if (DEBUG) {
				Log.i(FayeClient.TAG, "Faye unsubscribed from channel "
						+ message.getSubscription());
			}
		} else if (this.mActiveSubchannels.contains(channel)) {
			mFayeListener.messageReceived(this, message);
			Log.i(FayeClient.TAG,
					"Faye unsubscribed from channel "
							+ message.getSubscription());
		} else if (this.mActiveSubchannels.contains(channel)) {
			mFayeListener.messageReceived(this, message);
		} else {
			if (DEBUG) {
				Log.e(FayeClient.TAG,
						"Faye recieved a message with no subscription for channel "
								+ message.getSubscription());
			}
		}
	}

	protected void scheduleHeartbeat(int interval) {
		if (interval == 0) {
			openFayeConnection();
		} else {
			if (mHeartbeatThread == null
					|| mHeartbeatThread.getState() != State.RUNNABLE) {
				mHeartbeatThread = new FayeHeartbeatThread(this);
			}
			mHeartbeatThread.setDelay(interval);
			mHeartbeatThread.start();
		}
	}

	protected void processClose(int code) {
		switch (code) {
			case WebSocketHandler.CLOSE_INTERNAL_ERROR:
				FayeClient.this.mWebSocket = new WebSocketConnection();
				connect();
				break;
			case WebSocketHandler.CLOSE_PROTOCOL_ERROR:
			case WebSocketHandler.CLOSE_CANNOT_CONNECT:
			case WebSocketHandler.CLOSE_CONNECTION_LOST:
				while (!FayeClient.this.mWebSocket.isConnected()) {
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
	 * Causes the client to heartbeat to the server. This should never be
	 * explicity called.
	 */
	public void heartbeat() {
		openFayeConnection();
	}

	/**
	 * Sets the FayeListener attached to the client
	 * 
	 * @param mFayeListener
	 *            The FayeListener to attach to the client
	 */
	public void setFayeListener(FayeListener fayeListener) {
		this.mFayeListener = fayeListener;
	}

	/**
	 * Subscribes the push client to a channel on the push server
	 * 
	 * @param channel
	 *            The channel to subscribe to
	 */
	public void subscribe(String channel) {
		String subscribe = "{\"clientId\":\""
				+ this.mClientId
				+ "\",\"subscription\":\""
				+ channel
				+ "\",\"channel\":\"/meta/subscribe\",\"ext\":{\"authToken\":\""
				+ this.mAuthToken + "\"}}";
		if (DEBUG) {
			Log.d(FayeClient.TAG,
					"Faye is attempting to subscribe to channel \"" + channel
							+ "\"");
		}
		try {
			this.mWebSocket.sendBinaryMessage(subscribe.getBytes("UTF-8"));
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
		String unsubscribe = "{\"clientId\":\"" + this.mClientId
				+ "\",\"subscription\":\"" + channel
				+ "\",\"channel\":\"/meta/unsubscribe\"}";
		if (DEBUG) {
			Log.d(FayeClient.TAG,
					"Faye is attempting to unsubscribe from channel \""
							+ channel + "\"");
		}
		try {
			this.mWebSocket.sendBinaryMessage(unsubscribe.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a text message to the Faye server
	 * 
	 * @param message
	 *            The string message to send to the server
	 */
	public void sendTextMessage(String message) {
		if (isFayeConnected()) {
			mWebSocket.sendTextMessage(message);
		}
	}

	/**
	 * Sends a text message as UTF-8 encoded binary to the Faye server
	 * 
	 * @param message
	 *            The string message to send to the server
	 */
	public void sendRawTextMessage(String message) {
		if (isFayeConnected()) {
			try {
				mWebSocket.sendRawTextMessage(message.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends a binary payload to the Faye server
	 * 
	 * @param payload
	 *            The binary byte[] payload to send to the server
	 */
	public void sendBinaryMessage(byte[] payload) {
		if (isFayeConnected()) {
			mWebSocket.sendBinaryMessage(payload);
		}
	}
}