package com.b3rwynmobile.fayeclient;

import android.util.Log;

import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles interactions with the Faye server, such as connecting,
 * (un-)subscribing to channels and receiving push messages
 * 
 * @author Jamison Greeley (atomicrat552@gmail.com)
 */
public class FayeClient extends WebSocketHandler {

	// Debug tag
	private static final String	TAG						= "FayeClient";

	// Channel constants
	private static final String	HANDSHAKE_CHANNEL		= "/meta/handshake";
	private static final String	CONNECT_CHANNEL			= "/meta/connect";
	private static final String	DISCONNECT_CHANNEL		= "/meta/disconnect";
	private static final String	SUBSCRIBE_CHANNEL		= "/meta/subscribe";
	private static final String	UNSUBSCRIBE_CHANNEL		= "/meta/unsubscribe";

	// String constants
	private static final String	HANDSHAKE_STRING		= "{\"supportedConnectionTypes\":[\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\"{0}\"}";
	private static final String	FAYE_CONNECT_STRING		= "{\"channel\":\"{0}\",\"clientID\":\"{1}\",\"connectionType\":\"websocket\"";
	private static final String	FAYE_DISCONNECT_STRING	= "{\"channel\":\"{0}\",\"clientID\":\"{1}\"";

	// Data objects
	private WebSocketConnection	webSocket;
	private FayeListener		fayeListener;

	// Connection fields
	private String				fayeUrl;
	private String				authToken;
	private List<String>		activeSubchannels;
	private String				fayeClientId;
	private String				clientId;

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
		this.socketConnected = false;
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
		return socketConnected;
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
	 * Disconnects the socket if needed, build and connect the socket, then
	 * handshake
	 */
	public void connect() {
		disconnectExpected = false;
		closeSocketConnection();
		openSocketConnection();
	}

	private void openSocketConnection() {
		webSocket = new WebSocketConnection();
		try {
			webSocket.connect(fayeUrl, this);
			socketConnected = true;
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

	private void closeSocketConnection() {
		if (webSocket != null) {
			webSocket.disconnect();
			webSocket = null;
			socketConnected = false;
		}
	}

	private void openFayeConnection() {
		webSocket.sendTextMessage(MessageFormat.format(FAYE_CONNECT_STRING,
				CONNECT_CHANNEL, clientId));
	}

	private void closeFayeConnection() {
		webSocket.sendTextMessage(MessageFormat.format(FAYE_DISCONNECT_STRING,
				DISCONNECT_CHANNEL, clientId));
	}

	private void handshake() {
		webSocket.sendTextMessage(MessageFormat.format(HANDSHAKE_STRING,
				HANDSHAKE_CHANNEL));
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
		super.onOpen();
		openFayeConnection();
		handshake();
	}

	/**
	 * Automated method to handle messages coming across the web socket.
	 */
	@Override
	public void onTextMessage(String payload) {
		super.onTextMessage(payload);

		Gson gson = new Gson();
		FayeMessage message = gson.fromJson(payload, FayeMessage.class);
		String channel = message.getChannel();

		if (channel.equals(HANDSHAKE_CHANNEL)) {
			if (message.isSuccessful()) {

			}
		} else if (channel.equals(CONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				fayeConnected = true;
			} else {
				fayeConnected = false;
				Log.d(TAG, "Faye failed to connect");
			}
		} else if (channel.equals(DISCONNECT_CHANNEL)) {
			if (message.isSuccessful()) {
				fayeConnected = false;
				closeSocketConnection();
			} else {
				fayeConnected = true;
				Log.d(TAG, "Faye failed to disconnect");
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
			// TODO handle a message from a channel
		} else {
			Log.e(TAG, "Faye recieved a message with no subscription for channel " + message.getSubscription());
		}
	}

	/**
	 * Handles the socket losing connection. If the disconnect is expected, it
	 * gracefully ends. If the disconnect is unexpected, the socket will
	 * re-establish the Faye connection. <br />
	 * <br />
	 * This method is fully automated.
	 */
	@Override
	public void onClose(int code, String reason) {
		super.onClose(code, reason);

		if (!disconnectExpected) {
			connect();
		} else {
			closeSocketConnection();
		}
	}
}