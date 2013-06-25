package org.codeweaver.faye.model;

/**
 * Created with IntelliJ IDEA.
 * User: Berwyn Codeweaver
 * Date: 25/06/13
 * Time: 03:22
 * To change this template use File | Settings | File Templates.
 */
public class Advice {

    private String reconnect;
    private int interval;
    private long timeout;

    public String getReconnect() {
        return reconnect;
    }

    public void setReconnect(String reconnect) {
        this.reconnect = reconnect;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
