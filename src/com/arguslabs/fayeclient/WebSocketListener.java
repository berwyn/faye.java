package com.arguslabs.fayeclient;

public interface WebSocketListener {

	/**
	 * Called when a WebSocket connection has been established
	 * 
	 * @param ws
	 *            WebSocket instance
	 */
	public void onOpen(WebSocket ws);

	/**
	 * Called when a WebSocket connection has been terminated
	 * 
	 * @param ws
	 *            WebSocket instance
	 */
	public void onClose(WebSocket ws);

	/**
	 * Called when a WebSocket connection has received a message
	 * 
	 * @param ws
	 *            WebSocket instance
	 * @param message
	 *            Message received
	 */
	public void onMessage(WebSocket ws, String message);

}
