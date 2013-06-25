package org.codeweaver.faye;

//@formatter:off
/**
 * A client object for Faye.
 * Copyright (c) 2011-2013 Berwyn Codeweaver
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * @author Berwyn Codeweaver <berwyn.codeweaver@gmail.com>
 */
//@formatter:on
public class Client {

	// region >> CONSTANTS

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

	// endregion

	// region >> CONSTRUCTORS

	private Client(String serverUrl, Configuration config) {
		this.serverUrl = serverUrl;
		this.config = config;
	}

	// endregion

	// region >> FACTORY METHODS

	public static Client generate(String serverUrl) {
		return from(serverUrl, DEFAULT_CONFIGURATION);
	}

	public static Client from(String serverUrl, Configuration configuration) {
		return new Client(serverUrl, configuration);
	}

	// endregion

	//@formatter:off
    /**
     * A configuration for a Faye Client, provides the client with its meta URLs
     * Copyright (c) 2013 Berwyn Codeweaver
     *
     *  Licensed under the Apache License, Version 2.0 (the "License");
     *  you may not use this file except in compliance with the License.
     *  You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     *  Unless required by applicable law or agreed to in writing, software
     *  distributed under the License is distributed on an "AS IS" BASIS,
     *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *  See the License for the specific language governing permissions and
     *  limitations under the License.
     *  
     *  @author Berwyn Codeweaver <berwyn.codeweaver@gmail.com>
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
