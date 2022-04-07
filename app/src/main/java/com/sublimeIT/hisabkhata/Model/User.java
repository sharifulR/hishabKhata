package com.sublimeIT.hisabkhata.Model;

public class User {

    private String imageurl;
    private String userId;
    private String userName;
    private String userPhone;
    private String address;

    public User(String imageurl, String userId, String userName, String userPhone, String address) {
        this.imageurl = imageurl;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.address = address;
    }

    public User() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
