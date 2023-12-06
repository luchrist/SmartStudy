package de.christcoding.smartstudy;

public class TimeObject {

    String id, day;
    int time;

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public TimeObject(String id, String day, int time) {
        this.id = id;
        this.day = day;
        this.time = time;
    }
}
