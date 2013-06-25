package org.codeweaver.faye.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created with IntelliJ IDEA.
 * User: Berwyn Codeweaver
 * Date: 25/06/13
 * Time: 03:23
 * To change this template use File | Settings | File Templates.
 */
public class Message {

    // General
    private String channel;

    // Handshake
    private String version;
    private String[] supportedConnectionTypes;
    private Advice advice;

    // Event
    private Object Data;

    // (Un-)subscribe
    private String subscription;
    private String error;

    // Handshake + Subscribe
    private boolean successful;
    @SerializedName("clientid")
    private String clientId;

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

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
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
