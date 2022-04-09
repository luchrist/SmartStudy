package com.example.smartstudy;

import java.util.ArrayList;

public class Exam {
    private String id, subject, type, enddate, startdate, colour;
    private int volume;


    public Exam(String id, String subject, String type, String enddate, String startdate, String colour, int volume) {
        this.id = id;
        this.subject = subject;
        this.type = type;
        this.enddate = enddate;
        this.startdate = startdate;
        this.colour = colour;
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

}
