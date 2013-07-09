package org.codeweaver.faye.event;

import com.google.gson.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: Berwyn Codeweaver
 * Date: 08/07/13
 * Time: 01:48
 * To change this template use File | Settings | File Templates.
 */
public class MessageEvent {

    private final JsonObject message;

    public MessageEvent(final JsonObject message) {
        this.message = message;
    }

    public JsonObject getMessage() {
        return message;
    }

}
