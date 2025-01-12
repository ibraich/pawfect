package com.example.pawfect;

public class Message {
    String timestamp;
    String messageText;

    public Message(String timestamp, String messageText) {
        this.timestamp = timestamp;
        this.messageText = messageText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessageText() {
        return messageText;
    }
}
