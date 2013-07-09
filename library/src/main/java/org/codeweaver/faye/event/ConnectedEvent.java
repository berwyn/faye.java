package org.codeweaver.faye.event;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 08/07/13 Time:
 * 02:20 To change this template use File | Settings | File Templates.
 */
public class ConnectedEvent {

	private final boolean	connected;

	public ConnectedEvent(final boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

}
