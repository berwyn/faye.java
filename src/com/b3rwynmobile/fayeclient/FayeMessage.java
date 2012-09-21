// @formatter:off
/******************************************************************************
 *
 *  Copyright 2011-2012 b3rwyn Mobile Solutions
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
 ******************************************************************************/
// @formatter:on

package com.b3rwynmobile.fayeclient;

public class FayeMessage {

	// General
	private String		channel;

	// Handshake
	private String		version;
	private String[]	supportedConnectionTypes;
	private FayeAdvice	advice;

	// Event
	private String		data;

	// (Un-)Subscribe
	private String		subscription;
	private String		error;

	// Handshake + Subscribe
	private boolean		successful;
	private String		clientId;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String[] getSupportedConnectionTypes() {
		return supportedConnectionTypes;
	}

	public void setSupportedConnectionTypes(String[] supportedConnectionTypes) {
		this.supportedConnectionTypes = supportedConnectionTypes;
	}

	public FayeAdvice getAdvice() {
		return advice;
	}

	public void setAdvice(FayeAdvice advice) {
		this.advice = advice;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
