package org.codeweaver.faye.model;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 25/06/13 Time:
 * 03:22 To change this template use File | Settings | File Templates.
 */
public class Advice {

	private Reconnect	reconnect;
	private int			interval;
	private long		timeout;

	public Reconnect getReconnect() {
		return reconnect;
	}

	public void setReconnect(Reconnect reconnect) {
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

	public enum Reconnect {
		RETRY, HANDSHAKE, NONE;
	}
}
