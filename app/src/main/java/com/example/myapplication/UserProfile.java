package com.example.myapplication;

public class UserProfile {
    public String userEmail;
    public String userName;
    public String userPassword;
    public String token;
    public String mobile;

    public UserProfile(){

    }

    public UserProfile(String userEmail, String userName, String userPassword, String token, String mobile) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPassword = userPassword;
        this.token = token;
        this.mobile = mobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
