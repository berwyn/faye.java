package com.b3rwynmobile.fayeclient;

public class FayeHeartbeatThread extends Thread {

	private int			delay;
	private FayeClient	client;

	public FayeHeartbeatThread(FayeClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		int sleepCount = 0;
		while (sleepCount < delay) {
			try {
				Thread.sleep(1000);
				sleepCount++;
			} catch (InterruptedException e) {
				// Do nothing, server will send a timeout
			}
		}
		client.heartbeat();
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
