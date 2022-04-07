package com.sublimeIT.hisabkhata.Model;

public class Customer {

    private String imageurl;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String address;
    private String shopOwner;

    public Customer(String imageurl, String customerId, String customerName, String customerPhone, String address, String shopOwner) {
        this.imageurl = imageurl;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.address = address;
        this.shopOwner = shopOwner;
    }

    public Customer() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }
}
