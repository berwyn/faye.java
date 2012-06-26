package com.b3rwynmobile.fayeclient;

import java.io.IOException;
import java.net.URI;

import android.util.Log;

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
	private WebSocketClient		mWebSocket;
	private FayeListener		mFayeListener;
	private FayeHandler			mFayeHandler;

	// Connection fields
	private String				mFayeUrl;
	private String				mAuthToken;
	private String				mActiveSubchannel;
	private String				mFayeClientId;

	// Status fields
	private boolean				socketConnected;
	private boolean				fayeConnected;

	/**
	 * Simplified constructor
	 * 
	 * @param fayeUrl
	 *            Url of the Faye server
	 * @param authToken
	 *            Token for Faye authentication
	 */
	public FayeClient(String fayeUrl, String authToken) {
		this(fayeUrl, authToken, "");
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
	public FayeClient(String fayeUrl, String authToken, String channel) {
		mFayeHandler = new FayeHandler();
		mFayeUrl = fayeUrl;
		mAuthToken = authToken;
		mActiveSubchannel = channel;
		socketConnected = false;
		fayeConnected = false;
	}

	/**
	 * Gets the FayeListener attached to the client
	 * 
	 * @return The FayeListener attached to the client
	 */
	public FayeListener getFayeListener() {
		return mFayeListener;
	}

	/**
	 * Sets the FayeListener attached to the client
	 * 
	 * @param mFayeListener
	 *            The FayeListener to attach to the client
	 */
	public void setFayeListener(FayeListener mFayeListener) {
		this.mFayeListener = mFayeListener;
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
	 * Opens a websocket connection to the webserver. This must be done before
	 * connecting the push client
	 */
	public void openSocketConnection() {
		Log.d(TAG, "Faye is attempting to open the socket connection");
		if (mWebSocket != null) {
			try {
				mWebSocket.disconnect();
			} catch (IOException e) {
				// TODO Handle exception coming out of socket close
			}

			mWebSocket = new WebSocketClient(
					URI.create(mFayeUrl),
					(com.b3rwynmobile.fayeclient.WebSocketClient.Handler) mFayeHandler,
					null);
			mWebSocket.connect();
		}
	}

	/**
	 * Closes the websocket connection. Do this after you close the push client
	 * connection
	 */
	public void closeSocketConnection() {
		Log.d(TAG, "Faye is attempting to close the socket connection");
		if (mWebSocket != null) {
			try {
				mWebSocket.disconnect();
			} catch (IOException e) {
				// TODO Handle exception coming out of socket close
			}
		}
	}

	/**
	 * Connect the push client. The websocket must be open for this to work
	 */
	public void connectFaye() {
		Log.d(TAG, "Faye is attempting to open a connection to the push server");
		String connect = String.format(FAYE_CONNECT_STRING, mFayeClientId);
		mWebSocket.send(connect);
	}

	/**
	 * Disconnect the push client. The websocket will not be closed by this
	 * action, you must close it manually
	 */
	public void disconnectFaye() {
		Log.d(TAG,
				"Faye is attempting to close the connection to the push server");
		String disconnect = String
				.format(FAYE_DISCONNECT_STRING, mFayeClientId);
		mWebSocket.send(disconnect);
	}

	/**
	 * Handshakes the push client and server
	 */
	public void handshake() {
		mWebSocket.send(HANDSHAKE_STRING);
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
		String subscribe = String.format(SUBSCRIBE_STRING, mFayeClientId,
				(channel.equals("") ? mActiveSubchannel : channel), mAuthToken);
		mWebSocket.send(subscribe);
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
		String unsubscribe = String.format(UNSUBSCRIBE_STRING, mFayeClientId,
				(channel.equals("") ? mActiveSubchannel : channel));
		mWebSocket.send(unsubscribe);
	}

	/**
	 * Helper class to manage messages sent from the web socket
	 * 
	 * @author Jamison Greeley (atomicrat2552@gmail.com)
	 */
	private class FayeHandler implements WebSocketClient.Handler {
		/**
		 * Method to handle behavior when the websocket connects
		 * @see super{@link #onConnect()}
		 */
		@Override
		public void onConnect() {
			// TODO Auto-generated method stub

		}

		/**
		 * Method to handle when the webscoket receives a string message
		 * @see super{@link #onMessage(String)}
		 */
		@Override
		public void onMessage(String message) {
			// Deserialize the message
			FayeMessage fayeMessage = new Gson().fromJson(message,
					FayeMessage.class);

			// If the response is a handshake response
			if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.HANDSHAKE_CHANNEL)) {

				// If the handshake was successful
				if (fayeMessage.isSuccessful()) {
					// Set the client id
					FayeClient.this.mFayeClientId = fayeMessage.getClientId();

					// Tell the listener we connected
					if (FayeClient.this.mFayeListener != null
							&& FayeClient.this.mFayeListener instanceof FayeListener) {
						FayeClient.this.mFayeListener
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
					// TODO handle bad connection
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
					if (FayeClient.this.mFayeListener != null
							&& FayeClient.this.mFayeListener instanceof FayeListener) {
						FayeClient.this.mFayeListener
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
					// TODO handle successful subscribe
				} else {
					// TODO handle unsuccessful subscribe
				}
			}

			// If the response is an unsubscribe response
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.UNSUBSCRIBE_CHANNEL)) {

				// If the unsubscribe was successful
				if (fayeMessage.isSuccessful()) {
					// TODO handle successful unsubscribe
				} else {
					// TODO handle unsuccessful unsubscribe
				}
			}

			// If the response is from a subscribed channel
			else if (fayeMessage.getChannel().toLowerCase()
					.equals(FayeClient.this.mActiveSubchannel)) {

				// Send the message data to the listener
				if (FayeClient.this.mFayeListener != null
						&& FayeClient.this.mFayeListener instanceof FayeListener) {
					FayeClient.this.mFayeListener.messageReceived(
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
		public void onMessage(byte[] data) {
			// Faye does not send binary data
		}

		/**
		 * Method to handle socket behavior when the connection severs
		 */
		@Override
		public void onDisconnect(int code, String reason) {
			// TODO Auto-generated method stub

		}

		/**
		 * Method to handle when the socket encounters an error
		 */
		@Override
		public void onError(Exception error) {
			// TODO Auto-generated method stub

		}
	}
}
