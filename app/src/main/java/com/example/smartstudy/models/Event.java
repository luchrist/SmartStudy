package com.example.smartstudy.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event {
    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    long dbId;
    private String id, subject, type, startDate, endDate, description, color;
    private int volume;
    private int progress;
    private int remainingDays;
    private int absolutMinutes;
    private int remainingMinutes;
    private boolean wanted;

    public boolean isWanted() {
        return wanted;
    }

    public void setWanted(boolean wanted) {
        this.wanted = wanted;
    }

    public int getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(int fromGroup) {
        this.fromGroup = fromGroup;
    }

    private int fromGroup;
    private List<Todo> todos;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setRemainingMinutes(absolutMinutes - progress);
    }

    public Event(String type, String subject, String date, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.subject = subject;
        this.endDate = date;
        this.description = description;
    }

    public Event(String subject, String type, String startDate, String endDate, String color, int volume, int progress) {
        this.id = UUID.randomUUID().toString();
        this.subject = subject;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.volume = volume;
        this.progress = progress;
    }
    public Event(String id, String subject, String type, String startDate, String endDate, String color, int volume, int progress) {
        this.id = id;
        this.subject = subject;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.volume = volume;
        this.progress = progress;
    }

    public Event() {
        this.id = UUID.randomUUID().toString();
    }

    public void setNecessaryMissingAttributes() {
        if(startDate == null) {
            setStartDate(LocalDate.now().toString());
        }
        if(endDate == null) {
            setEndDate(LocalDate.now().plusMonths(1).toString());
        }
        if(color == null) {
            setColor("red");
        }
        setFromGroup(1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Todo> getTodos() {
        if(todos == null) {
            return new ArrayList<>();
        }
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }

    public int getAbsolutMinutes() {
        return absolutMinutes;
    }

    public void setAbsolutMinutes(int absolutMinutes) {
        this.absolutMinutes = absolutMinutes;
        setRemainingMinutes(absolutMinutes - progress);
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(int remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }
}
