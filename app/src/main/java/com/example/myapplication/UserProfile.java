package com.example.myapplication;

public class UserProfile {
    public String mobile;
    public String userName;

    public UserProfile(String userName,String mobile) {
        this.mobile = mobile;
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



}
