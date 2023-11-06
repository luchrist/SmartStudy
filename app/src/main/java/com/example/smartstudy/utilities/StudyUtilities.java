package com.example.smartstudy.utilities;

public class StudyUtilities {

    public static String getRecommendation(double grade) {
        if(grade <= 1.5) {
            return "Großartig! Bereite dich darauf vor als Streber bezeichnet zu werden!";
        } else if (grade <= 2.5) {
            return "Gut! Du bist bereit für die Prüfung!";
        } else if (grade <= 3.5) {
            return "ok! Ruh dich nicht darauf aus";
        } else if (grade <= 4.5) {
            return "Naja! Du solltest nochmal lernen oder beten";
        } else {
            return "Kataststrophe, Renn zum Arzt und schreib dich krank!";
        }
    }

    public static double getGrade(double ratio) {
        if (ratio >= 0.95){
            return 1;
        } else if (ratio >= 0.85){
            return 1.5;
        } else if (ratio >= 0.75){
            return 2;
        } else if (ratio >= 0.65){
            return 2.5;
        } else if (ratio >= 0.55){
            return 3;
        } else if (ratio >= 0.45){
            return 3.5;
        } else if (ratio >= 0.35){
            return 4;
        } else if (ratio >= 0.25){
            return 4.5;
        } else if (ratio >= 0.15){
            return 5;
        } else {
            return 6;
        }
    }
}
