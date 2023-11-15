package com.example.smartstudy.models;

import java.time.DayOfWeek;

public class TimeTableElement {
    private String id, subject, begin, end, room, teacher, day, colour;

    public TimeTableElement(String id, String subject, String begin, String end, String room, String teacher, String day, String colour) {
        this.id = id;
        this.subject = subject;
        this.begin = begin;
        this.end = end;
        this.room = room;
        this.teacher = teacher;
        this.day = day;
        this.colour = colour;
    }

    public TimeTableElement(String subject, String begin, String end, String room, String teacher, String day, String colour) {
        this.subject = subject;
        this.begin = begin;
        this.end = end;
        this.room = room;
        this.teacher = teacher;
        this.day = day;
        this.colour = colour;
    }

    public TimeTableElement() {
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
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

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
