package de.christcoding.smartstudy.models;

import java.time.LocalDate;

public class TimeException {
    LocalDate date;
    int time;

    public TimeException(LocalDate date, int time) {
        this.date = date;
        this.time = time;
    }

    public TimeException() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
