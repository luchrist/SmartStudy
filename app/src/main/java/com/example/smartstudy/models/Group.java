package com.example.smartstudy.models;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {

    public String name, image, id, createdBy, createdTime;
    public boolean joinWithId;
    public List<Member> members;
    public List<Event> events;
}
