package com.sublimeIT.hisabkhata.Model;

public class SaleProducts {
    private String date;
    private String productName;
    private String saleAmount;
    private String totalAmount;
    private String shopOwner;

    public SaleProducts(String date, String productName, String saleAmount, String totalAmount, String shopOwner) {
        this.date = date;
        this.productName = productName;
        this.saleAmount = saleAmount;
        this.totalAmount = totalAmount;
        this.shopOwner = shopOwner;
    }

    public SaleProducts() {
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

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
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
