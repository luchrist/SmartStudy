package com.example.smartstudy.models;

public class Event {
    public String type, subject, date, description;

    public Event(String type, String subject, String date, String description) {
        this.type = type;
        this.subject = subject;
        this.date = date;
        this.description = description;
    }

    public Event() {
    }
}
