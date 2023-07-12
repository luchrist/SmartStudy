package com.example.smartstudy.models;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {

    public String name, image,id;
    public List<Member> members;
}
