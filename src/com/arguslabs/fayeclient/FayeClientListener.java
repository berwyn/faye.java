package com.arguslabs.fayeclient;

public interface FayeClientListener {
	
	public void connectedToServer(FayeClient fc);
	public void disconnectedFromServer(FayeClient fc);
	public void messageReceieved(FayeClient fc, String msg);
}
