package com.example.smartstudy.Builder;

import com.example.smartstudy.models.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EventBuilder {
    private String id, subject, type, enddate, startdate, colour;
    private int volume; // davon abhängig werden benöigte LERNSTUNDEN berechnet
    private int progres; // bereits gelernte stunden
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public EventBuilder() {
        id = UUID.randomUUID().toString();
        type = "Exam";
        subject = "any Subject";
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        // Format the current date as a string
        startdate = currentDate.format(formatter);
        enddate = currentDate.plusWeeks(2).format(formatter);
        colour = "blue";
    }
    public EventBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public EventBuilder setSubject(String subject) {
        if(!subject.equals("")) {
            this.subject = subject;
        }
        return this;
    }

    public EventBuilder setType(String type) {
        if (!type.equals("")) {
            this.type = type;
        }
        return this;
    }

    public EventBuilder setEnddate(String enddate) {
        if (!enddate.equals("")) {
            DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            LocalDate date = LocalDate.parse(enddate, expectedFormatter);
            this.enddate = date.format(formatter);
        }
        return this;
    }

    public EventBuilder setStartdate(String startdate) {
        this.startdate = startdate;
        return this;
    }

    public EventBuilder setColour(String colour) {
        this.colour = colour;
        return this;
    }

    public EventBuilder setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public EventBuilder setProgress(int progres) {
        this.progres = progres;
        return this;
    }

    public Event build() {
        if(id == null) {
            id = UUID.randomUUID().toString();
        }
        if (subject == null) {
            subject = "any Subject";
        }
        if (type == null) {
            type = "Exam";
        }
        if (enddate == null) {
            // Get the current date
            LocalDate currentDate = LocalDate.now();
            // Format the current date as a string
            enddate = currentDate.plusWeeks(2).format(formatter);
        }
        if (startdate == null) {
            // Get the current date
            LocalDate currentDate = LocalDate.now();
            // Format the current date as a string
            startdate = currentDate.format(formatter);
        }
        if (colour == null) {
            colour = "blue";
        }
        return new Event(id, subject, type, startdate, enddate, colour, volume, progres);
    }
}
