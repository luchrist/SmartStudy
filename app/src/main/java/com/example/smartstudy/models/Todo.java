package com.example.smartstudy.models;

public class Todo {

    private String id, todo, collection;
    private int checked, time;

    public Todo(String collection, String todo, int time, int checked) {
        id = java.util.UUID.randomUUID().toString();
        this.collection = collection;
        this.todo = todo;
        this.time = time;
        this.checked = checked;
    }

    public Todo(String id, String collection, String todo, int time, int checked) {
        this.id = id;
        this.todo = todo;
        this.collection = collection;
        this.checked = checked;
        this.time = time;
    }

    public Todo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
