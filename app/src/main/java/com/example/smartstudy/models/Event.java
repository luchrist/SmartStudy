package com.example.smartstudy.models;

import java.util.UUID;

public class Event {
    public String id,type, subject, date, description;

    public Event(String type, String subject, String date, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.subject = subject;
        this.date = date;
        this.description = description;
    }

    public Event() {
    }
}
