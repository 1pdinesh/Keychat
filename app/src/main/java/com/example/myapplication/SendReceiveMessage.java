package com.example.myapplication;

public class SendReceiveMessage {

    public String message;
    public String sender;
    public String receiver;
    public String type;
    public String contentLocation;
    public double lo;

    public double la;
    public SendReceiveMessage() {

    }

    public SendReceiveMessage(String message, String sender, String receiver, String type, String contentLocation,double longg,double lat) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.contentLocation = contentLocation;
        this.lo=longg;
        this.la=lat;

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

    public String getType() {
        return type;
    }
    public String getContentLocation() {
        return contentLocation;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public double getLa() {
        return la;
    }

    public void setLa(double la) {
        this.la = la;
    }
}