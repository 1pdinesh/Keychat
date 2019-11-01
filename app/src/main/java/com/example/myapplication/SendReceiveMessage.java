package com.example.myapplication;

public class SendReceiveMessage {

    public String message;
    public String sender;
    public String receiver;
    public String url;

    public SendReceiveMessage()
    {

    }

    public SendReceiveMessage(String message, String sender, String receiver, String url) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
