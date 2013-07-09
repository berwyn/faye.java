package org.codeweaver.faye.model;

import java.util.Calendar;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 25/06/13 Time:
 * 03:23 To change this template use File | Settings | File Templates.
 */
public class Message {

	// General
	private String		channel;
	private String		connectionType;

	// Handshake
	private String		version;
	private String[]	supportedConnectionTypes;
	private Advice		advice;

	// (Dis-)connect
	private Calendar	timestamp;

	// Event
	private JsonObject	data;

	// (Un-)subscribe
	private String		subscription;
	private String		error;

	// Handshake + Subscribe
	private boolean		successful;
	@SerializedName("clientid")
	private String		clientId;

    public static Message with(String channel, String id) {
        Message m = new Message();
        m.setChannel(channel);
        m.setClientId(id);
        return m;
    }

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

	public Advice getAdvice() {
		return advice;
	}

	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	public JsonObject getData() {
		return data;
	}

	public void setData(JsonObject data) {
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

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
}
