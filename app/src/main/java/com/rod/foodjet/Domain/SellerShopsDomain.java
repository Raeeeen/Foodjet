package com.rod.foodjet.Domain;

public class SellerShopsDomain {
    private String title;
    private String shoplocation;

    public SellerShopsDomain(String title, String shopLocation) {
        this.title = title;
        this.shoplocation = shopLocation;
    }

    public String getTitle() {
        return title;
    }
    public String getShoplocation() {
        return shoplocation;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setShoplocation(String shoplocation) {
        this.shoplocation = shoplocation;
    }

}
