package com.b3rwynmobile.fayeclient;

public class FayeListener {

	/**
	 * Method used to take action when the client receives a message
	 * 
	 * @param faye
	 *            The client receiving the message
	 * @param msg
	 *            The message the client received
	 */
	public void messageReceived(FayeClient faye, String msg) {
		// TODO handle message
	}

	/**
	 * Method to handle client getting disconnected from the server prematurely
	 * 
	 * @param faye
	 *            The client that's been disconnected
	 */
	public void disconnectedFromServer(FayeClient faye) {
		// TODO RECONNECT
	}

	/**
	 * Method to handle logic when the client connects to the server
	 * 
	 * @param faye
	 *            The client that's established a connection
	 */
	public void connectedToServer(FayeClient faye) {
		// TODO What to do when the connection is successful
	}

}
