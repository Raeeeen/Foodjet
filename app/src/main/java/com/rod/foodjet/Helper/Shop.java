package com.rod.foodjet.Helper;

public class Shop {
    private String shopname;
    private String shoplocation;

    public Shop() {
        // Default constructor required for Firebase
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShoplocation() {
        return shoplocation;
    }

    public void setShoplocation(String shoplocation) {
        this.shoplocation = shoplocation;
    }
}
