package com.example.smartstudy;

public class Todo {

    private String key,id, todo;
    private int checked, time;

    public Todo(String key, String id, String todo, int time, int checked) {
        this.key = key;
        this.id = id;
        this.todo = todo;
        this.time = time;
        this.checked = checked;
    }

    public Todo(String key, String id, String todo, String s, int checked) {
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
}
