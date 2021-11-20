package com.example.smartstudy;

public class TimeObject {

    String id, day, time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TimeObject(String id, String day, String time) {
        this.id = id;
        this.day = day;
        this.time = time;
    }
}
