package com.example.myapplication;

public class Upload {
    private String mImageUrl;
    private String sender;
//hello//
    public Upload()
    {

    }

    public Upload(String mImageUrl, String sender) {
        this.mImageUrl = mImageUrl;
        this.sender = sender;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}