package de.christcoding.smartstudy.models;

public enum CorrectnessClasses {

    REALLY_GOOD(4, 5),
    GOOD(2, 3),
    MEDIUM(-1, 1),
    BAD(-3, -2),
    REALLY_BAD(-5, -4);

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