package com.sublimeIT.hisabkhata.Model;

public class PayedAmounts {
    private String date;
    private String productName;
    private String payedAmount;
    private String totalAmount;
    private String shopOwner;

    public PayedAmounts(String date, String productName, String payedAmount, String totalAmount, String shopOwner) {
        this.date = date;
        this.productName = productName;
        this.payedAmount = payedAmount;
        this.totalAmount = totalAmount;
        this.shopOwner = shopOwner;
    }

    public PayedAmounts() {
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

    public String getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(String payedAmount) {
        this.payedAmount = payedAmount;
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
