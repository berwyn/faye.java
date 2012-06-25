package com.arguslabs.fayeclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FayeClient {
	
	private final String TAG = "FayeClient";
	
	private final String HANDSHAKE_CHANNEL = "/meta/handshake";
	private final String CONNECT_CHANNEL = "/meta/connect";
	private final String DISCONNECT_CHANNEL = "/meta/disconnect";
	private final String SUBSCRIBE_CHANNEL = "/meta/subscribe";
	private final String UNSUBSCRIBE_CHANNEL = "/meta/unsubscribe";

	private final int MESSAGE_ONOPEN = 1;
	private final int MESSAGE_ONCLOSE = 2;
	private final int MESSAGE_ONMESSAGE = 3;
	
	private WebSocket webSocket = null;
	private FayeClientListener mFayeClientListener = null;
	private String mFayeUrlString = "";
	private String mAuthToken = "";
	private String mActiveSubChannel = "";
	private String fayeClientId = "";
	
	private boolean webSocketConnected = false;
	private boolean fayeConnected = false;
	
	/**
	 * Construct
	 */
	public FayeClient(String fayeUrl, String authToken) {
		this(fayeUrl, authToken , "");
	}
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
	public boolean isFayeConnected() {
		return fayeConnected;
	}

	/**
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
	
	private Handler messageHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
				case MESSAGE_ONOPEN: 
					Log.i(TAG, "onOpen() executed");
					
					FayeClient.this.webSocketConnected = true;
					FayeClient.this.handShake();
				break;
				
				case MESSAGE_ONCLOSE:
					Log.i(TAG, "onClosed() executed");
					
					FayeClient.this.webSocketConnected = false;
					FayeClient.this.fayeConnected = false;
					
					if(FayeClient.this.mFayeClientListener != null && FayeClient.this.mFayeClientListener instanceof FayeClientListener)
						FayeClient.this.mFayeClientListener.disconnectedFromServer(FayeClient.this);
				break;
				
				case MESSAGE_ONMESSAGE:
					Log.i(TAG, "onMessage() executed");
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
		if(webSocket != null) {
			//webSocket.setListener(null);
			webSocket.close();
		}
		webSocket = new WebSocket(mFayeUrlString, messageHandler);
		//webSocket.setListener(this);
		webSocket.open();
	}
	private void closeWebSocketConnection() {
		if(webSocket != null)
			webSocket.close();
	}
	private void handShake() {
		String handshake = "{\"supportedConnectionTypes\":[\"long-polling\",\"callback-polling\",\"iframe\",\"websocket\"],\"minimumVersion\":\"1.0beta\",\"version\":\"1.0\",\"channel\":\"/meta/handshake\"}";
		webSocket.send(handshake);	
	}
	private void subscribe(String channel) {
		Log.e("FayeClient", "Trying to subscribe with clientId: " + fayeClientId);
		
		String subscribe = String.format("{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/subscribe\", \"ext\":{\"authToken\":\"%s\"} 	 }", fayeClientId, (channel.equals("") ? mActiveSubChannel : channel), mAuthToken );
		webSocket.send(subscribe);
	}
	private void unsubscribe(String channel) {
		String unsubscribe = String.format("{\"clientId\":\"%s\",\"subscription\":\"%s\",\"channel\":\"/meta/unsubscribe\"}", fayeClientId, (channel.equals("") ? mActiveSubChannel : channel) );
		webSocket.send(unsubscribe);
	}
	private void connect() {
		String connect = String.format("{\"clientId\":\"%s\",\"connectionType\":\"websocket\",\"channel\":\"/meta/connect\"}", fayeClientId);
		webSocket.send(connect);
	}
	private void disconnect() {
		String disconnect = String.format("{\"clientId\":\"%s\",\"connectionType\":\"websocket\",\"channel\":\"/meta/disconnect\"}", fayeClientId);
		webSocket.send(disconnect);
	}

	private void parseFayeMessage(String message) {
		
		JSONArray msgArray = null;
		JSONObject fayeMsg = null;
		
		try {
			msgArray = new JSONArray(message);
		} 
		catch (JSONException e) { e.printStackTrace(); }
		
		for(int i=0; i<msgArray.length(); i++) {
			
			fayeMsg = msgArray.optJSONObject(i);
			
			if(fayeMsg.optString("channel").equals(HANDSHAKE_CHANNEL)) {
				if(fayeMsg.optBoolean("successful")) {
					this.fayeClientId = fayeMsg.optString("clientId");
					if(this.mFayeClientListener != null && this.mFayeClientListener instanceof FayeClientListener) {
						this.mFayeClientListener.connectedToServer(this);
					}
					this.connect();
				}
				else Log.e(TAG, "onMessage(): ERROR WITH BAYEUX HANDSHAKE");
			}
			else if (fayeMsg.optString("channel").equals(CONNECT_CHANNEL)) {
				if(fayeMsg.optBoolean("successful")) {
					this.fayeConnected = true;
					this.connect();
				}
				else Log.e(TAG, "onMessage(): ERROR CONNECTING TO FAYE");
			}
			else if (fayeMsg.optString("channel").equals(DISCONNECT_CHANNEL)) {
				if(fayeMsg.optBoolean("successful")) {
					this.fayeConnected = false;
					this.closeWebSocketConnection();
					if(this.mFayeClientListener != null && this.mFayeClientListener instanceof FayeClientListener) {
						this.mFayeClientListener.disconnectedFromServer(this);
					}
				}
				else Log.e(TAG, "onMessage(): ERROR DISCONNECTING FROM FAYE");
			}
			else if (fayeMsg.optString("channel").equals(SUBSCRIBE_CHANNEL)) {
				if(fayeMsg.optBoolean("successful")) {
					Log.e(TAG, String.format("SUBSCRIBED TO CHANNEL %S ON FAYE", fayeMsg.optString("subscription")));
				}
				else {
					Log.e(TAG, String.format("ERROR SUBSCRIBING TO CHANNEL %S ON FAYE WITH ERROR %S", fayeMsg.optString("subscription"), fayeMsg.optString("error")));
				}
			}
			else if (fayeMsg.optString("channel").equals(UNSUBSCRIBE_CHANNEL)) {
				Log.e(TAG, String.format("UNSUBSCRIBED FROM CHANNEL %S ON FAYE", fayeMsg.optString("subscription")));
			}
			else if(fayeMsg.optString("channel").equals(mActiveSubChannel)) {
				if (fayeMsg.optString("data") != null) {
					if(this.mFayeClientListener != null && this.mFayeClientListener instanceof FayeClientListener) {
						mFayeClientListener.messageReceieved(this, fayeMsg.optString("data"));
					}
				}
			}
			else Log.e(TAG, String.format("NO MATCH FOR CHANNEL %S", fayeMsg.optString("channel")));
		
		}//end for
	}
	
}
