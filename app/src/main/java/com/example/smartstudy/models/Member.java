package com.example.smartstudy.models;

import java.io.Serializable;

public class Member implements Serializable {

    public String name, email, image, token;
    public boolean isAdmin, isAvailable;

    public Member() {
    }

    public Member(String name, String email, String image, String token, boolean isAdmin, boolean isAvailable) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.token = token;
        this.isAdmin = isAdmin;
        this.isAvailable = isAvailable;
    }
}
