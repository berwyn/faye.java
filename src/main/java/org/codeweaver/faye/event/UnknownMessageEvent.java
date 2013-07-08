package org.codeweaver.faye.event;

import org.codeweaver.faye.model.Message;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 08/07/13 Time:
 * 16:40 To change this template use File | Settings | File Templates.
 */
public class UnknownMessageEvent {

	private final Message	message;

	public UnknownMessageEvent(final Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
