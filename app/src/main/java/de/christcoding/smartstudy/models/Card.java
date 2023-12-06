package de.christcoding.smartstudy.models;

public class Card {
    private String front, back;
    private int certainty, totalRequests, wrongAnswers, rightAnswers, mediumAnswers;
    private CardType type;
    boolean paused, reversed;

    public Card(String front, String back, CardType type, boolean reversed) {
        this.front = front;
        this.back = back;
        this.type = type;
        this.reversed = reversed;
    }

    public Card() {
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void incrementTotalRequests() {
        this.totalRequests++;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void incrementWrongAnswers() {
        this.wrongAnswers++;
    }

    public int getRightAnswers() {
        return rightAnswers;
    }

    public void incrementRightAnswers() {
        this.rightAnswers++;
    }

    public int getMediumAnswers() {
        return mediumAnswers;
    }

    public void incrementMediumAnswers() {
        this.mediumAnswers++;
    }

    public CorrectnessClasses returnCorrectnessClass() {
        for (CorrectnessClasses correctnessClass : CorrectnessClasses.values()) {
            if (correctnessClass.getLowerBound() <= certainty && correctnessClass.getUpperBound() >= certainty) {
                return correctnessClass;
            }
        }
        return null;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public int getCertainty() {
        return certainty;
    }

    public void setCertainty(int certainty) {
        this.certainty = certainty;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public Card returnCardInRandomOrder() {
        if (Math.random() < 0.5) {
            return this;
        } else {
            return new Card(back, front, type, reversed);
        }
    }
}
