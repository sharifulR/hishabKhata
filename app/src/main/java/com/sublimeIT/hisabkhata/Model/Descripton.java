package com.sublimeIT.hisabkhata.Model;

public class Descripton {
    private String date;
    private String productName;
    private String Amount;
    private String totalAmount;
    private String shopOwner;

    public Descripton(String date, String productName, String amount, String totalAmount, String shopOwner) {
        this.date = date;
        this.productName = productName;
        Amount = amount;
        this.totalAmount = totalAmount;
        this.shopOwner = shopOwner;
    }

    public Descripton() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }
}
