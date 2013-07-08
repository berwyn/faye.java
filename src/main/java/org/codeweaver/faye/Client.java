package org.codeweaver.faye;

//@formatter:off

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codeweaver.faye.event.*;
import org.codeweaver.faye.model.Advice;
import org.codeweaver.faye.model.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.squareup.otto.Produce;

/**
 * A client object for Faye.
 * Copyright (c) 2011-2013 Berwyn Codeweaver
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Berwyn Codeweaver <berwyn.codeweaver@gmail.com>
 */
//@formatter:on
public class Client extends WebSocketClient {

	// region >> CONSTANTS

	private static final int			NUM_THREADS				= 5;
	private static final Configuration	DEFAULT_CONFIGURATION	= new Configuration(
																		"/meta/handshake",
																		"/meta/connect",
																		"/meta/disconnect",
																		"/meta/subscribe",
																		"/meta/unsubscribe");

	// endregion

	// region >> FIELDS

	private final String				serverUrl;
	private final Configuration			config;
	private final Executor				metaExecutor;
	private final Executor				channelExecutor;
	private final List<String>			subscribedChannels;
	private final Gson					gson;

	private String						clientID;
	private AtomicBoolean				connected;
	private State						connectionState;

	// endregion

	// region >> CONSTRUCTORS

	private Client(String serverUrl, Configuration config) {
		super(URI.create(serverUrl));
		this.serverUrl = serverUrl;
		this.config = config;
		metaExecutor = Executors.newSingleThreadScheduledExecutor();
		channelExecutor = Executors.newFixedThreadPool(NUM_THREADS);
		subscribedChannels = new ArrayList<String>();
		gson = new Gson();
		connected = new AtomicBoolean(false);

		BusProvider.getInstance().register(this);
	}

	// endregion

	// region >> FACTORY METHODS

	public static Client with(String serverUrl) {
		return with(serverUrl, DEFAULT_CONFIGURATION);
	}

	public static Client with(String serverUrl, Configuration configuration) {
		return new Client(serverUrl, configuration);
	}

	// endregion

	// region >> METHODS

	public String getClientID() {
		return this.clientID;
	}

	// region >> METHODS >> MESSAGE GENERATORS

	private Message generateHandshakeMessage() {
		Message payload = new Message();
		payload.setChannel(config.HANDSHAKE_CHANNEL);
		payload.setVersion("1.0.0");
		payload.setSupportedConnectionTypes(new String[] { "websocket" });
		return payload;
	}

	private Message generateConnectMessage() {
		Message payload = Message.with(config.CONNECT_CHANNEL, this.clientID);
		payload.setConnectionType("websocket");
		return payload;
	}

	private Message generateDisconnectMessage() {
		return Message.with(config.DISCONNECT_CHANNEL, this.clientID);
	}

	private Message generateSubscribeMessage(String channel) {
		Message payload = Message.with(config.SUBSCRIBE_CHANNEL, this.clientID);
		payload.setSubscription(channel);
		return payload;
	}

	private Message generateUnsubscribeMessage(String channel) {
		Message payload = Message.with(config.UNSUBSCRIBE_CHANNEL,
				this.clientID);
		payload.setSubscription(channel);
		return payload;
	}

	// endregion

	// region >> METHODS >> SOCKET OPERATIONS

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// FIXME Kickoff Faye handshake and set status to connecting
	}

	@Override
	public void onMessage(String message) {
		Message[] messages = gson.fromJson(message, Message[].class);
		for (Message msg : messages) {
			if (!msg.isSuccessful()) {
				handleFailedMessage(msg);
				continue;
			}

			if (msg.getAdvice() != null) {
				handleAdvice(msg.getAdvice());
			}

			if (msg.getChannel().equals(config.HANDSHAKE_CHANNEL)) {
				this.clientID = msg.getClientId();
				// FIXME Start connection procedure
			} else if (msg.getChannel().equals(config.CONNECT_CHANNEL)) {
				connected.set(true);
				connectionState = State.CONNECTED;
				BusProvider.getInstance().post(
						new ConnectedEvent(connected.get()));
			} else if (msg.getChannel().equals(config.DISCONNECT_CHANNEL)) {
				connected.set(false);
				connectionState = State.DISCONNECTED;
				BusProvider.getInstance().post(
						new ConnectedEvent(connected.get()));
			} else if (msg.getChannel().equals(config.getSubscribeChannel())) {
				subscribedChannels.add(msg.getSubscription());
				BusProvider.getInstance().post(
						new SubscribedEvent(msg.getSubscription()));
			} else if (subscribedChannels.contains(msg.getChannel())) {
				BusProvider.getInstance().post(new MessageEvent(msg.getData()));
			} else {
				BusProvider.getInstance().post(new UnknownMessageEvent(msg));
			}
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// FIXME Handle socket close
	}

	@Override
	public void onError(Exception ex) {
		// FIXME Investigate what errors can be handled by the websocket client
		BusProvider.getInstance().post(
				new ErrorEvent("Socket Error", ErrorEvent.ErrorType.CONNECTION,
						ex));
	}

	// endregion

	public void addSubscription(String channel) {
		// FIXME Kickoff channel subscription
	}

	private void handleFailedMessage(Message message) {
		ErrorEvent.ErrorType errorType;
		if (message.getChannel().equals(config.HANDSHAKE_CHANNEL)
				|| message.getChannel().equals(config.CONNECT_CHANNEL)
				|| message.getChannel().equals(config.DISCONNECT_CHANNEL))
			errorType = ErrorEvent.ErrorType.CONNECTION;
		else if (message.getChannel().equals(config.SUBSCRIBE_CHANNEL)
				|| message.getChannel().equals(config.UNSUBSCRIBE_CHANNEL))
			errorType = ErrorEvent.ErrorType.SUBSCRIPTION;
		else
			errorType = ErrorEvent.ErrorType.MESSAGE;
		BusProvider.getInstance().post(
				new ErrorEvent(message.getError(), errorType, null));

	}

	private void handleAdvice(Advice advice) {
		// FIXME Handle advice
	}

	// endregion

	// region >> PRODUCERS

	@Produce
	public ConnectedEvent produceConnected() {
		return new ConnectedEvent(connected.get());
	}

	// endregion

	//@formatter:off    
    /**
    * An enum which describes the state that the Faye client is in
    * Copyright (c) 2013 Berwyn Codeweaver
    * <p/>
            * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    * <p/>
            * http://www.apache.org/licenses/LICENSE-2.0
            * <p/>
            * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
            * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
            * See the License for the specific language governing permissions and
    * limitations under the License.
            *
            * @author Berwyn Codeweaver <berwyn.codeweaver@gmail.com>
            */
    //@formatter:on
	public enum State {
		DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING, ERROR;
	}

	//@formatter:off
    /**
     * A configuration for a Faye Client, provides the client with its meta URLs
     * Copyright (c) 2013 Berwyn Codeweaver
     * <p/>
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p/>
     * http://www.apache.org/licenses/LICENSE-2.0
     * <p/>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     *
     * @author Berwyn Codeweaver <berwyn.codeweaver@gmail.com>
     */
    //@formatter:on
	public static class Configuration {

		private final String	HANDSHAKE_CHANNEL;
		private final String	CONNECT_CHANNEL;
		private final String	DISCONNECT_CHANNEL;
		private final String	SUBSCRIBE_CHANNEL;
		private final String	UNSUBSCRIBE_CHANNEL;

		public Configuration(String handshakeChannel, String connectChannel,
				String disconnectChannel, String subscribeChannel,
				String unsubscribeChannel) {
			this.HANDSHAKE_CHANNEL = handshakeChannel;
			this.CONNECT_CHANNEL = connectChannel;
			this.DISCONNECT_CHANNEL = disconnectChannel;
			this.SUBSCRIBE_CHANNEL = subscribeChannel;
			this.UNSUBSCRIBE_CHANNEL = unsubscribeChannel;
		}

		public String getHandshakeChannel() {
			return HANDSHAKE_CHANNEL;
		}

		public String getConnectChannel() {
			return CONNECT_CHANNEL;
		}

		public String getDisconnectChannel() {
			return DISCONNECT_CHANNEL;
		}

		public String getSubscribeChannel() {
			return SUBSCRIBE_CHANNEL;
		}

		public String getUnsubscribeChannel() {
			return UNSUBSCRIBE_CHANNEL;
		}
	}

}
