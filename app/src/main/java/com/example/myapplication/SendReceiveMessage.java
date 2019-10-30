package com.example.myapplication;

public class SendReceiveMessage {

    public String sender;
    public String receiver;

    public SendReceiveMessage()
    {

    }

    public SendReceiveMessage(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
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
}
