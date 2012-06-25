package com.b3rwynmobile.fayeclient;

<<<<<<< HEAD
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.b3rwynmobile.fayeclient.autobahn.WebSocket;
import com.b3rwynmobile.fayeclient.autobahn.WebSocketConnection;
import com.b3rwynmobile.fayeclient.autobahn.WebSocketConnectionHandler;
import com.b3rwynmobile.fayeclient.autobahn.WebSocketException;
import com.google.gson.Gson;

/**
 * This class handles interactions with the Faye server, such as connecting,
 * (un-)subscribing to channels and receiving push messages
 * 
 * @author Jamison Greeley (atomicrat552@gmail.com)
 */
public class FayeClient {

	// Debug tag
	private static final String	TAG						= "FayeClient";

	// Channel constants
	private static final String	HANDSHAKE_CHANNEL		= "/meta/handshake";
	private static final String	CONNECT_CHANNEL			= "/meta/connect";
	private static final String	DISCONNECT_CHANNEL		= "/meta/disconnect";
	private static final String	SUBSCRIBE_CHANNEL		= "/meta/subscribe";
	private static final String	UNSUBSCRIBE_CHANNEL		= "/meta/unsubscribe";

	// String constants
	private static final String	HANDSHAKE_STRING		= "{\"supportedConnectionTypes\":[\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\"/meta/handshake\"}";
	private static final String	SUBSCRIBE_STRING		= "{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/subscribe\", \"ext\":{\"authToken\":\"%s\"}   }";
	private static final String	UNSUBSCRIBE_STRING		= "{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/unsubscribe\"}";
	private static final String	FAYE_CONNECT_STRING		= "{\"clientId\":\"%s\",\"connectionType\":\"websocket\",\"channel\":\"/meta/connect\"}";
	private static final String	FAYE_DISCONNECT_STRING	= "{\"clientId\":\"%s\",\"connectionType\":\"websocket\",\"channel\":\"/meta/disconnect\"}";

	// Data objects
	private WebSocket			webSocket;
	private FayeListener		fayeListener;
	private FayeHandler			fayeHandler;

	// Connection fields
	private String				fayeUrl;
	private String				authToken;
	private List<String>		activeSubchannels;
	private String				fayeClientId;

	// Status fields
	private boolean				socketConnected;
	private boolean				fayeConnected;
	private boolean				disconnectExpected;

	/**
	 * Simplified constructor
	 * 
	 * @param fayeUrl
	 *            Url of the Faye server
	 * @param authToken
	 *            Token for Faye authentication
=======
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FayeClient {

	private final String		TAG					= "FayeClient";

	private final String		HANDSHAKE_CHANNEL	= "/meta/handshake";
	private final String		CONNECT_CHANNEL		= "/meta/connect";
	private final String		DISCONNECT_CHANNEL	= "/meta/disconnect";
	private final String		SUBSCRIBE_CHANNEL	= "/meta/subscribe";
	private final String		UNSUBSCRIBE_CHANNEL	= "/meta/unsubscribe";

	private final int			MESSAGE_ONOPEN		= 1;
	private final int			MESSAGE_ONCLOSE		= 2;
	private final int			MESSAGE_ONMESSAGE	= 3;

	private WebSocket			webSocket			= null;
	private FayeClientListener	mFayeClientListener	= null;
	private String				mFayeUrlString		= "";
	private String				mAuthToken			= "";
	private String				mActiveSubChannel	= "";
	private String				fayeClientId		= "";

	private boolean				webSocketConnected	= false;
	private boolean				fayeConnected		= false;

	/**
	 * Construct
>>>>>>> Rebuilt the packages
	 */
	public FayeClient(String fayeUrl, String authToken) {
		this(fayeUrl, authToken, "");
	}

<<<<<<< HEAD
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
	public FayeClient(String fayeUrl, String authToken, String channel) {
		this.fayeHandler = new FayeHandler();
		this.fayeUrl = fayeUrl;
		this.authToken = authToken;
		this.activeSubchannels = new ArrayList<String>();
		this.activeSubchannels.add(channel);
		this.socketConnected = false;
		this.fayeConnected = false;
		this.disconnectExpected = false;
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
	public void setFayeListener(FayeListener mFayeListener) {
		this.fayeListener = mFayeListener;
	}

	/**
	 * Whether or not the client has a websocket connection to the push server
	 * 
	 * @return The status of the websocket connection
	 */
	public boolean isSocketConnected() {
		return socketConnected;
	}

	/**
	 * Whether or not the client has a connection to the push server
	 * 
	 * @return The status of the push server connection
	 */
=======
	public FayeClient(String fayeUrl, String authToken, String channel) {
		mFayeUrlString = fayeUrl;
		mActiveSubChannel = channel;
		mAuthToken = authToken;
		fayeConnected = false;
		webSocketConnected = false;
	}

	/**
	 * Get & Set
	 */
	public FayeClientListener getListener() {
		return mFayeClientListener;
	}

	public void setListener(FayeClientListener l) {
		mFayeClientListener = l;
	}

	public void setChannel(String channel) {
		mActiveSubChannel = channel;
	}

	public boolean isWebSocketConnected() {
		return webSocketConnected;
	}

>>>>>>> Rebuilt the packages
	public boolean isFayeConnected() {
		return fayeConnected;
	}

	/**
<<<<<<< HEAD
	 * Whether or not the client disconnection is expected
	 * 
	 * @return The status of whether disconnect was expected
	 */
	public boolean isDisconnectExpected() {
		return disconnectExpected;
	}

	/**
	 * Opens a websocket connection to the webserver. This must be done before
	 * connecting the push client
	 */
	public void openSocketConnection() {
		Log.d(TAG, "Faye is attempting to open the socket connection");

		if (webSocket != null) {
			webSocket.disconnect();
		}

		// Create the socket
		webSocket = new WebSocketConnection();

		// Try to connect
		try {
			webSocket.connect(fayeUrl, fayeHandler);
			this.socketConnected = true;
		} catch (WebSocketException e) {
			Log.e(TAG, "Faye Websocket error: " + e.getMessage());
			e.printStackTrace();
		}

		Log.d(TAG, "Faye has opened the socket");
	}

	/**
	 * Closes the websocket connection. Do this after you close the push client
	 * connection
	 */
	public void closeSocketConnection() {
		Log.d(TAG, "Faye is attempting to close the socket connection");
		if (webSocket != null) {
			webSocket.disconnect();
			this.socketConnected = false;

			Log.d(TAG, "Faye has closed the socket connection");
		}
	}

	/**
	 * Connect the push client. The websocket must be open for this to work
	 */
	public void connectFaye() {
		// Connect
		Log.d(TAG, "Faye is attempting to open a connection to the push server");

		String connect = String.format(FAYE_CONNECT_STRING, fayeClientId);
		webSocket.sendTextMessage(connect);

		// Socket open, handshake push server
		handshake();

		// Set the object status
		this.fayeConnected = true;
		this.disconnectExpected = false;

		// Alert the listener that socket connection is connected
		if (FayeClient.this.fayeListener != null
				&& FayeClient.this.fayeListener instanceof FayeListener) {
			FayeClient.this.fayeListener.connectedToServer(FayeClient.this);
		}

		Log.d(TAG, "Faye has opened the push connection");
	}

	/**
	 * Disconnect the push client. The websocket will not be closed by this
	 * action, you must close it manually
	 */
	public void disconnectFaye() {
		Log.d(TAG, "Faye is attempting to close the push connection");
		String disconnect = String.format(FAYE_DISCONNECT_STRING, fayeClientId);
		webSocket.sendTextMessage(disconnect);
		this.fayeConnected = false;
		this.disconnectExpected = true;

		Log.d(TAG, "Faye has closed the push connection");
	}

	/**
	 * Handshakes the push client and server
	 */
	public void handshake() {
		webSocket.sendTextMessage(HANDSHAKE_STRING);
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
		String subscribe = String.format(SUBSCRIBE_STRING, fayeClientId,
				(channel.equals("") ? activeSubchannels : channel), authToken);
		webSocket.sendTextMessage(subscribe);
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
		String unsubscribe = String.format(UNSUBSCRIBE_STRING, fayeClientId,
				(channel.equals("") ? activeSubchannels : channel));
		webSocket.sendTextMessage(unsubscribe);
	}

	/**
	 * Helper class to manage messages sent from the web socket
	 * 
	 * @author Jamison Greeley (atomicrat2552@gmail.com)
	 */
	public class FayeHandler extends WebSocketConnectionHandler {

		/**
		 * Method to handle behavior when the web socket connects
		 * 
		 * @see super{@link #onConnect()}
		 */
		@Override
		public void onOpen() {
			// The socket has connected
			FayeClient.this.socketConnected = true;
		}

		/**
		 * Method to handle when the web socket receives a string message
		 * 
		 * @see super{@link #onMessage(String)}
		 */
		@Override
		public void onTextMessage(String message) {
			// Deserialize the message
			FayeMessage fayeMessage = new Gson().fromJson(message,
					FayeMessage.class);

			// If the response is a handshake response
			if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.HANDSHAKE_CHANNEL)) {

				// If the handshake was successful
				if (fayeMessage.isSuccessful()) {
					// Set the client id
					FayeClient.this.fayeClientId = fayeMessage.getClientId();

					// Tell the listener we connected
					if (FayeClient.this.fayeListener != null
							&& FayeClient.this.fayeListener instanceof FayeListener) {
						FayeClient.this.fayeListener
								.connectedToServer(FayeClient.this);
					}

					// Connect the Faye client
					FayeClient.this.connectFaye();
				} else {
					// TODO handle bad handshake
				}
			}

			// If the response is a connect response
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.CONNECT_CHANNEL)) {

				// If the connect was successful
				if (fayeMessage.isSuccessful()) {
					// The Faye connection was successful
					FayeClient.this.fayeConnected = true;

					// Get the server connected
					FayeClient.this.connectFaye();
				} else {
					// The socket isn't open
					FayeClient.this.socketConnected = false;

					// Tell the listener we're disconnected
					if (FayeClient.this.fayeListener != null
							&& FayeClient.this.fayeListener instanceof FayeListener) {
						FayeClient.this.fayeListener
								.disconnectedFromServer(FayeClient.this);
					}
				}
			}

			// If the response is a disconnect response
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.DISCONNECT_CHANNEL)) {

				// If the disconnect was successful
				if (fayeMessage.isSuccessful()) {
					// Faye is disconnected
					FayeClient.this.fayeConnected = false;

					// Close the socket
					FayeClient.this.closeSocketConnection();

					// Tell the listener we've disconnected
					if (FayeClient.this.fayeListener != null
							&& FayeClient.this.fayeListener instanceof FayeListener) {
						FayeClient.this.fayeListener
								.disconnectedFromServer(FayeClient.this);
					}
				} else {
					// TODO handle bad disconnect
				}
			}

			// If the response is a subscription response
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.SUBSCRIBE_CHANNEL)) {

				// If the subscription was successful
				if (fayeMessage.isSuccessful()) {
					// Add the channel to the list of subscribed channels
					FayeClient.this.activeSubchannels.add(fayeMessage
							.getChannel());
				} else {
					// TODO handle unsuccessful subscribe
				}
			}

			// If the response is an unsubscribe response
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.UNSUBSCRIBE_CHANNEL)) {

				// If the unsubscribe was successful
				if (fayeMessage.isSuccessful()) {
					// If the channel is subscribed to
					if (FayeClient.this.activeSubchannels.contains(fayeMessage
							.getChannel())) {
						// Remove the channel from the list of subscribed
						// channels
						FayeClient.this.activeSubchannels.remove(fayeMessage
								.getChannel());
					}
				} else {
					// TODO handle unsuccessful unsubscribe
				}
			}

			// If the response is from a subscribed channel
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.this.activeSubchannels)) {

				// Send the message data to the listener
				if (FayeClient.this.fayeListener != null
						&& FayeClient.this.fayeListener instanceof FayeListener) {
					FayeClient.this.fayeListener.messageReceived(
							FayeClient.this, fayeMessage.getData());
				}
			}

			// If we get some sort of odd response
			else {
				// TODO handling weird responses
			}
		}

		/**
		 * Method to handle when the websocket receives binary data
		 */
		@Override
		public void onBinaryMessage(byte[] data) {
			// Faye does not send binary data
		}

		/**
		 * Method to handle socket behavior when the connection severs
		 */
		@Override
		public void onClose(int code, String reason) {
			// The socket is closed
			FayeClient.this.socketConnected = false;
		}

	}
=======
	 * Public section
	 */
	public void connectToServer() {
		openWebSocketConnection();
	}

	public void disconnectFromServer() {
		disconnect();
	}

	public void subscribeToChannel(String channel) {
		subscribe(channel);
	}

	public void unsubscribeFromChannel(String channel) {
		unsubscribe(channel);
	}

	/**
	 * Websockethandler
	 */

	private Handler	messageHandler	= new Handler() {

										@Override
										public void handleMessage(Message msg) {
											super.handleMessage(msg);

											switch (msg.what) {
												case MESSAGE_ONOPEN:
													Log.i(TAG,
															"onOpen() executed");

													FayeClient.this.webSocketConnected = true;
													FayeClient.this.handShake();
													break;

												case MESSAGE_ONCLOSE:
													Log.i(TAG,
															"onClosed() executed");

													FayeClient.this.webSocketConnected = false;
													FayeClient.this.fayeConnected = false;

													if (FayeClient.this.mFayeClientListener != null
															&& FayeClient.this.mFayeClientListener instanceof FayeClientListener)
														FayeClient.this.mFayeClientListener
																.disconnectedFromServer(FayeClient.this);
													break;

												case MESSAGE_ONMESSAGE:
													Log.i(TAG,
															"onMessage() executed");
													parseFayeMessage((String) msg.obj);
													break;

											}
										}
									};

	/**
	 * Private section
	 */
	private void openWebSocketConnection() {
		// clean up any existing socket
		if (webSocket != null) {
			// webSocket.setListener(null);
			webSocket.close();
		}
		webSocket = new WebSocket(mFayeUrlString, messageHandler);
		// webSocket.setListener(this);
		webSocket.open();
	}

	private void closeWebSocketConnection() {
		if (webSocket != null) webSocket.close();
	}

	private void handShake() {
		String handshake = "{\"supportedConnectionTypes\":[\"long-polling\",\"callback-polling\",\"iframe\",\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\"/meta/handshake\"}";
		webSocket.send(handshake);
	}

	private void subscribe(String channel) {
		Log.e("FayeClient", "Trying to subscribe with clientId: "
				+ fayeClientId);

		String subscribe = String
				.format("{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/subscribe\", \"ext\":{\"authToken\":\"%s\"} 	 }",
						fayeClientId, (channel.equals("") ? mActiveSubChannel
								: channel), mAuthToken);
		webSocket.send(subscribe);
	}

	private void unsubscribe(String channel) {
		String unsubscribe = String
				.format("{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/unsubscribe\"}",
						fayeClientId, (channel.equals("") ? mActiveSubChannel
								: channel));
		webSocket.send(unsubscribe);
	}

	private void connect() {
		String connect = String
				.format("{\"clientId\":\"%s\",\"connectionType\":\"websocket\",\"channel\":\"/meta/connect\"}",
						fayeClientId);
		webSocket.send(connect);
	}

	private void disconnect() {
		String disconnect = String
				.format("{\"clientId\":\"%s\",\"connectionType\":\"websocket\",\"channel\":\"/meta/disconnect\"}",
						fayeClientId);
		webSocket.send(disconnect);
	}

	private void parseFayeMessage(String message) {

		JSONArray msgArray = null;
		JSONObject fayeMsg = null;

		try {
			msgArray = new JSONArray(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < msgArray.length(); i++) {

			fayeMsg = msgArray.optJSONObject(i);

			if (fayeMsg.optString("channel").equals(HANDSHAKE_CHANNEL)) {
				if (fayeMsg.optBoolean("successful")) {
					this.fayeClientId = fayeMsg.optString("clientId");
					if (this.mFayeClientListener != null
							&& this.mFayeClientListener instanceof FayeClientListener) {
						this.mFayeClientListener.connectedToServer(this);
					}
					this.connect();
				} else
					Log.e(TAG, "onMessage(): ERROR WITH BAYEUX HANDSHAKE");
			} else if (fayeMsg.optString("channel").equals(CONNECT_CHANNEL)) {
				if (fayeMsg.optBoolean("successful")) {
					this.fayeConnected = true;
					this.connect();
				} else
					Log.e(TAG, "onMessage(): ERROR CONNECTING TO FAYE");
			} else if (fayeMsg.optString("channel").equals(DISCONNECT_CHANNEL)) {
				if (fayeMsg.optBoolean("successful")) {
					this.fayeConnected = false;
					this.closeWebSocketConnection();
					if (this.mFayeClientListener != null
							&& this.mFayeClientListener instanceof FayeClientListener) {
						this.mFayeClientListener.disconnectedFromServer(this);
					}
				} else
					Log.e(TAG, "onMessage(): ERROR DISCONNECTING FROM FAYE");
			} else if (fayeMsg.optString("channel").equals(SUBSCRIBE_CHANNEL)) {
				if (fayeMsg.optBoolean("successful")) {
					Log.e(TAG, String.format(
							"SUBSCRIBED TO CHANNEL %S ON FAYE",
							fayeMsg.optString("subscription")));
				} else {
					Log.e(TAG,
							String.format(
									"ERROR SUBSCRIBING TO CHANNEL %S ON FAYE WITH ERROR %S",
									fayeMsg.optString("subscription"),
									fayeMsg.optString("error")));
				}
			} else if (fayeMsg.optString("channel").equals(UNSUBSCRIBE_CHANNEL)) {
				Log.e(TAG, String.format(
						"UNSUBSCRIBED FROM CHANNEL %S ON FAYE",
						fayeMsg.optString("subscription")));
			} else if (fayeMsg.optString("channel").equals(mActiveSubChannel)) {
				if (fayeMsg.optString("data") != null) {
					if (this.mFayeClientListener != null
							&& this.mFayeClientListener instanceof FayeClientListener) {
						mFayeClientListener.messageReceieved(this,
								fayeMsg.optString("data"));
					}
				}
			} else
				Log.e(TAG,
						String.format("NO MATCH FOR CHANNEL %S",
								fayeMsg.optString("channel")));

		}// end for
	}

>>>>>>> Rebuilt the packages
}
