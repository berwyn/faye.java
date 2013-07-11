package org.codeweaver.faye.sample;

import org.codeweaver.faye.BusProvider;
import org.codeweaver.faye.Client;
import org.codeweaver.faye.event.*;

import com.google.common.eventbus.Subscribe;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 10/07/13 Time:
 * 21:17 To change this template use File | Settings | File Templates.
 */
public class Sample {

	private boolean	firstConnection	= true;
	private Client	faye;

	public static void main(String[] args) {
		BusProvider.getInstance().register(new Sample());
	}

	public Sample() {
		faye = Client.with("localhost:8080");
		faye.connect();
	}

	@Subscribe
	public void onConnectEvent(ConnectedEvent connectedEvent) {
		// TODO Subscribe to a channel
		System.out.println(String.format("Faye client is now %s",
				connectedEvent.isConnected() ? "connected" : "disconnected"));
		if (firstConnection) {
			faye.addSubscription("/messages");
		}
	}

	@Subscribe
	public void onErrorEvent(ErrorEvent errorEvent) {
		// TODO Handle errors
		System.err.println(errorEvent.getError());
	}

	@Subscribe
	public void onMessageEvent(MessageEvent messageEvent) {
		System.out.println(messageEvent.getMessage().get("text").getAsString());
	}

	@Subscribe
	public void onSubscribedEvent(SubscribedEvent subscribedEvent) {
		// TODO Handle subscription
	}

	public void onUnknownMessageEvent(UnknownMessageEvent unknownMessageEvent) {
		// TODO Handle unknown messages
	}

}
