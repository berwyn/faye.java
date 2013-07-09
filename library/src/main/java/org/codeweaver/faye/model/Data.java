package org.codeweaver.faye.model;

/**
 * Created with IntelliJ IDEA.
 * User: Berwyn Codeweaver
 * Date: 25/06/13
 * Time: 03:20
 * To change this template use File | Settings | File Templates.
 */
public class Data {

    private String timestamp;
    private String content;
    private Topic topic;
    private String id;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
