package com.example.smartstudy;

import java.sql.Time;

public class TimetableObject {

    private long id;
    private String subject;
    private Time begin;
    private Time end;
    private String day;
    private String room;
    private String teacher;

    public TimetableObject(long id, String subject, Time begin, Time end, String day, String room, String teacher) {
        this.id = id;
        this.subject = subject;
        this.begin = begin;
        this.end = end;
        this.day = day;
        this.room = room;
        this.teacher = teacher;
    }

    public TimetableObject() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Time getBegin() {
        return begin;
    }

    public void setBegin(Time begin) {
        this.begin = begin;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
