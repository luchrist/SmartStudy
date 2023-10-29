package com.example.smartstudy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartstudy.utilities.Constants;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AiExam extends BaseActivity {

    private int questionNumber, correctAnswers, correctColor, wrongColor;
    private char correctAnswer = 'a';
    String response;
    private TextView question, correctAnswersRatio;
    private Button answerA, answerB, answerC, answerD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_exam);
        question = findViewById(R.id.question);
        answerA = findViewById(R.id.answer_a);
        answerB = findViewById(R.id.answer_b);
        answerC = findViewById(R.id.answer_c);
        answerD = findViewById(R.id.answer_d);
        correctAnswersRatio = findViewById(R.id.correctAnswersRatio);
        correctColor = this.getResources().getColor(R.color.correct);
        wrongColor = this.getResources().getColor(R.color.wrong);
        setListeners();

        Intent intent = getIntent();
        response = intent.getStringExtra(Constants.KEY_RESPONSE);
        if(response != null){
            showQuestion();
        } else {
            //Todo: Couldnt create an Exam
            showResult();
        }
        }

    private void setListeners() {
        answerA.setOnClickListener(v -> {
            if (correctAnswer == 'a') {
                answerA.setBackgroundColor(correctColor);
                correctAnswers++;
            } else {
                answerA.setBackgroundColor(wrongColor);
                showCorrectAnswer();
            }
            continueExam();
        });
        answerB.setOnClickListener(v -> {
            if (correctAnswer == 'b') {
                answerB.setBackgroundColor(correctColor);
                correctAnswers++;
            } else {
                answerB.setBackgroundColor(wrongColor);
                showCorrectAnswer();
            }
            continueExam();
        });
        answerC.setOnClickListener(v -> {
            if (correctAnswer == 'c') {
                answerC.setBackgroundColor(correctColor);
                correctAnswers++;
            } else {
                answerC.setBackgroundColor(wrongColor);
                showCorrectAnswer();
            }
            continueExam();
        });
        answerD.setOnClickListener(v -> {
            if (correctAnswer == 'd') {
                answerD.setBackgroundColor(correctColor);
                correctAnswers++;
            } else {
                answerD.setBackgroundColor(wrongColor);
                showCorrectAnswer();
            }
            continueExam();
        });
    }

    private void continueExam() {
        correctAnswersRatio.setText(String.format("%s/%s", correctAnswers, questionNumber));
        // delay
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        showNeutral();
        if (response != null) {
            showQuestion();
        } else {
            showResult();
        }
    }

    private void showNeutral() {
        answerA.setBackgroundColor(this.getResources().getColor(R.color.primaryVariant));
        answerB.setBackgroundColor(this.getResources().getColor(R.color.primaryVariant));
        answerC.setBackgroundColor(this.getResources().getColor(R.color.primaryVariant));
        answerD.setBackgroundColor(this.getResources().getColor(R.color.primaryVariant));
    }

    private void showCorrectAnswer() {
        switch (correctAnswer) {
            case 'a':
                answerA.setBackgroundColor(correctColor);
                break;
            case 'b':
                answerB.setBackgroundColor(correctColor);
                break;
            case 'c':
                answerC.setBackgroundColor(correctColor);
                break;
            case 'd':
                answerD.setBackgroundColor(correctColor);
                break;
            default:
                break;
        }
    }

    private void showResult() {
        answerA.setVisibility(View.GONE);
        answerB.setVisibility(View.GONE);
        answerC.setVisibility(View.GONE);
        answerD.setVisibility(View.GONE);
        if (questionNumber == 0) {
            Toast.makeText(this, "Couldnt create an Exam!", Toast.LENGTH_SHORT).show();
        }
        //Todo: show Exam Result give options how to continue: repeat, repeat with new questions or go back to generate new exams
    }

    private void showQuestion() {
        questionNumber++;
        String[] split = response.split("\\?",2);
        String question = split[0];
        split = split[1].split("\\n", 8);
        String a = split[1];
        String b = split[2];
        String c = split[3];
        String d = split[4];
        String correct = split[5];
        if(correct.isEmpty()) {
            correct = split[6];
        }
        if (split.length > 7) {
            response= split[7];
        } else {
            response = null;
        }
        correctAnswer = correct.split(": ", 2)[1].charAt(0);
        this.question.setText(String.format("%s?", question));
        answerA.setText(a);
        answerB.setText(b);
        answerC.setText(c);
        answerD.setText(d);
    }
}