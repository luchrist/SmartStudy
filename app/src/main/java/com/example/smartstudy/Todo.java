package com.example.smartstudy;

public class Todo {

    private String key,id, todo, time;

    public Todo(String key, String id, String todo, String time) {
        this.key = key;
        this.id = id;
        this.todo = todo;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
