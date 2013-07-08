package org.codeweaver.faye.event;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 08/07/13 Time:
 * 14:59 To change this template use File | Settings | File Templates.
 */
public class FayeSubscribedEvent {

	private final String	channel;

	public FayeSubscribedEvent(final String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}
}
