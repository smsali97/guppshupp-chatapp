package ua.naiksoftware.stompclientexample.model;

import java.util.Date;

public class ChatMessage  {
    private Long id;

    private String content;

    private User sender;

    private User receiver;

    private String timestamp;

    private MessageType type;

    private boolean delivered = false;

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public enum MessageType {
        CHAT, LEAVE, JOIN, IMAGE, STICKER
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getReceiver() {
        return receiver;
    }

    public MessageType getType() {
        return type;
    }


    public void setType(MessageType type) {
        this.type = type;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
