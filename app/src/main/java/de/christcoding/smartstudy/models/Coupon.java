package de.christcoding.smartstudy.models;

public class Coupon {
    public String id, name, value, logo, code;
    public int price;
    public boolean redeemed;

    public Coupon() {
    }

    public Coupon(String id, String name, String value, String logo, String code, int price) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.logo = logo;
        this.code = code;
        this.price = price;
    }
}
