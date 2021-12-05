package com.example.smartstudy;

public class Translation {
    private String collection, native_word, foreign_word;
    private int correct, wrong;

    public Translation(String collection, String native_word, String foreign_word, int correct, int wrong) {
        this.collection = collection;
        this.native_word = native_word;
        this.foreign_word = foreign_word;
        this.correct = correct;
        this.wrong = wrong;
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

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }
}
