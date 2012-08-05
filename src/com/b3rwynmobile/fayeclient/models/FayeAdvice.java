package com.b3rwynmobile.fayeclient.models;

public class FayeAdvice {

	private String	reconnect;
	private int		interval;
	private long	timeout;

	public String getReconnect() {
		return reconnect;
	}

	public void setReconnect(String reconnect) {
		this.reconnect = reconnect;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
