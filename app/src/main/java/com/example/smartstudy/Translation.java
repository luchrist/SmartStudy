package com.example.smartstudy;

public class Translation {
    private String collection, native_word, foreign_word, id;
    private int correct;

    public Translation(String id, String collection, String native_word, String foreign_word, int correct) {
        this.id = id;
        this.collection = collection;
        this.native_word = native_word;
        this.foreign_word = foreign_word;
        this.correct = correct;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getNative_word() {
        return native_word;
    }

    public void setNative_word(String native_word) {
        this.native_word = native_word;
    }

    public String getForeign_word() {
        return foreign_word;
    }

    public void setForeign_word(String foreign_word) {
        this.foreign_word = foreign_word;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }


}
