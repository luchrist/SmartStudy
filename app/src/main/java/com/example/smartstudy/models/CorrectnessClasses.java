package com.example.smartstudy.models;

public enum CorrectnessClasses {

    REALLY_GOOD(3, 5),
    GOOD(1, 3),
    MEDIUM(-1, 1),
    BAD(-3, -1),
    REALLY_BAD(-5, -3);

    private final int lowerBound;
    private final int upperBound;
    CorrectnessClasses(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    int getLowerBound() {
        return lowerBound;
    }
    int getUpperBound() {
        return upperBound;
    }
}