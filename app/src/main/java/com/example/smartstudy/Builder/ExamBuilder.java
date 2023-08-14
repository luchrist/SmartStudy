package com.example.smartstudy.Builder;

import com.example.smartstudy.Exam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

public class ExamBuilder {
    private String id, subject, type, enddate, startdate, colour;
    private int volume; // davon abhängig werden benöigte LERNSTUNDEN berechnet
    private int progres; // bereits gelernte stunden
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ExamBuilder() {
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
    public ExamBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ExamBuilder setSubject(String subject) {
        if(!subject.equals("")) {
            this.subject = subject;
        }
        return this;
    }

    public ExamBuilder setType(String type) {
        if (!type.equals("")) {
            this.type = type;
        }
        return this;
    }

    public ExamBuilder setEnddate(String enddate) {
        if (!enddate.equals("")) {
            DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            LocalDate date = LocalDate.parse(enddate, expectedFormatter);
            this.enddate = date.format(formatter);
        }
        return this;
    }

    public ExamBuilder setStartdate(String startdate) {
        this.startdate = startdate;
        return this;
    }

    public ExamBuilder setColour(String colour) {
        this.colour = colour;
        return this;
    }

    public ExamBuilder setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public ExamBuilder setProgress(int progres) {
        this.progres = progres;
        return this;
    }

    public Exam build() {
        return new Exam(id, subject, type, enddate, startdate, colour, volume, progres);
    }
}
