package com.example.smartstudy.models;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {

    public String name, image, id, createdBy, createdTime, mostImportantInformation;
    public boolean joinWithId, addExamsToPlan;
    public List<Member> members;
    public List<Event> events;
    //public List<Category> fileCategories; //Kategorien werden in einem Update hinzugefügt
}
