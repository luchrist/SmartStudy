package com.example.smartstudy;

import static com.example.smartstudy.utilities.StudyUtilities.getRecommendation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AiExam extends BaseActivity {

    private int questionNumber, correctAnswers, correctColor, wrongColor;
    private char correctAnswer = 'a';
    String response, remainingResponse, language;
    private TextView question, correctAnswersRatio, examName;
    private Button answerA, answerB, answerC, answerD;
    AppCompatImageView backBtn, repeatBtn, generateNewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_exam);
        question = findViewById(R.id.question);
        answerA = findViewById(R.id.answer_a);
        answerB = findViewById(R.id.answer_b);
        answerC = findViewById(R.id.answer_c);
        answerD = findViewById(R.id.answer_d);
        examName = findViewById(R.id.examName);
        correctAnswersRatio = findViewById(R.id.correctAnswersRatio);
        generateNewBtn = findViewById(R.id.generateNewBtn);
        backBtn = findViewById(R.id.backNavBtn);
        repeatBtn = findViewById(R.id.repeatB);
        correctColor = this.getResources().getColor(R.color.correct);
        wrongColor = this.getResources().getColor(R.color.wrong);
        setListeners();

        Intent intent = getIntent();
        String topic = intent.getStringExtra(Constants.KEY_TOPIC);
        examName.setText(String.format("%s Exam", topic));
        language = intent.getStringExtra(Constants.KEY_LANGUAGE);
        response = intent.getStringExtra(Constants.KEY_RESPONSE);
        remainingResponse = response;
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
                correctAnswers++;
                blinkEffectGreen(answerA);
            } else {
                Button correctBtn = getCorrectBtn();
                blinkEffectRed(answerA, correctBtn);
            }
        });
        answerB.setOnClickListener(v -> {
            if (correctAnswer == 'b') {
                correctAnswers++;
                blinkEffectGreen(answerB);
            } else {
                blinkEffectRed(answerB, getCorrectBtn());
            }
        });
        answerC.setOnClickListener(v -> {
            if (correctAnswer == 'c') {
                correctAnswers++;
                blinkEffectGreen(answerC);
            } else {
                blinkEffectRed(answerC, getCorrectBtn());
            }
        });
        answerD.setOnClickListener(v -> {
            if (correctAnswer == 'd') {
                correctAnswers++;
                blinkEffectGreen(answerD);
            } else {
                blinkEffectRed(answerD, getCorrectBtn());
            }
        });
        backBtn.setOnClickListener(v -> onBackPressed());
        repeatBtn.setOnClickListener(v -> {
            remainingResponse = response;
            correctAnswers = 0;
            questionNumber = 0;
            correctAnswersRatio.setText(String.format("%s/%s", correctAnswers, questionNumber));
            displayExamButtons();
            showQuestion();
        });
        generateNewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AiGenerateExam.class);
            intent.putExtra(Constants.KEY_TOPIC, examName.getText().toString().trim());
            intent.putExtra(Constants.KEY_LANGUAGE, language);
            startActivity(intent);
        });
    }

    private void displayExamButtons() {
        repeatBtn.setVisibility(View.GONE);
        generateNewBtn.setVisibility(View.GONE);
        answerD.setVisibility(View.VISIBLE);
        answerC.setVisibility(View.VISIBLE);
        answerB.setVisibility(View.VISIBLE);
        answerA.setVisibility(View.VISIBLE);
    }

    private void blinkEffectRed(Button answerBtn, Button correctBtn) {
        ObjectAnimator correctAnimator = ObjectAnimator.ofInt(correctBtn, "backgroundColor", Color.GREEN , getResources().getColor(R.color.primaryVariant));
        correctAnimator.setDuration(250);
        correctAnimator.setEvaluator(new ArgbEvaluator());
        correctAnimator.setRepeatCount(2);
        correctAnimator.setRepeatMode(ValueAnimator.REVERSE);
        correctAnimator.start();

        ObjectAnimator animator = ObjectAnimator.ofInt(answerBtn, "backgroundColor", Color.RED , getResources().getColor(R.color.primaryVariant));
        animator.setDuration(270);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(2);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        // Adding the listener
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                continueExam();
            }
        });

        animator.start();
    }

    private void blinkEffectGreen(Button answerBtn) {
        ObjectAnimator animator = ObjectAnimator.ofInt(answerBtn, "backgroundColor", Color.GREEN , getResources().getColor(R.color.primaryVariant));
        animator.setDuration(250);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(2);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        // Adding the listener
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                continueExam();
            }
        });

        animator.start();
    }

    private void continueExam() {
        correctAnswersRatio.setText(String.format("%s/%s", correctAnswers, questionNumber));
        showNeutral();
        if (remainingResponse != null) {
            showQuestion();
        } else {
            showResult();
        }
    }

    private void showNeutral() {
        answerA.setBackgroundResource(R.drawable.background_icon);
        answerB.setBackgroundResource(R.drawable.background_icon);
        answerC.setBackgroundResource(R.drawable.background_icon);
        answerD.setBackgroundResource(R.drawable.background_icon);
    }

    private Button getCorrectBtn() {
        switch (correctAnswer) {
            case 'a':
                return answerA;
            case 'b':
                return answerB;
            case 'c':
                return answerC;
            case 'd':
                return answerD;
            default:
                return answerA;
        }
    }

    private void showResult() {
        if (questionNumber == 0) {
            Toast.makeText(this, "Couldnt create an Exam!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AiGenerateExam.class));
        } else {
            answerA.setVisibility(View.GONE);
            answerB.setVisibility(View.GONE);
            answerC.setVisibility(View.GONE);
            answerD.setVisibility(View.GONE);
            double ratio = (double) correctAnswers / questionNumber;
            double grade = getGrade(ratio);
            addPoints(ratio);
            String recommendation = getString(getRecommendation(grade));

            question.setText(String.format("Your Grade: %s \nPoints: %s of %s \n%s", grade, correctAnswers, questionNumber, recommendation));
            repeatBtn.setVisibility(View.VISIBLE);
            generateNewBtn.setVisibility(View.VISIBLE);
        }
    }

    private void addPoints(double ratio) {
        int points = (int) (ratio * 100);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail).update(Constants.KEY_POINTS, FieldValue.increment(points));
    }

    private double getGrade(double ratio) {
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

    private void showQuestion() {
        questionNumber++;
        String[] split = remainingResponse.split("\\?",2);
        String question = split[0];
        int i = question.lastIndexOf("\\n");
        if(i != -1) {
            question = question.substring(i);
        }
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
            remainingResponse= split[7];
        } else {
            remainingResponse = null;
        }
        correctAnswer = correct.split(": ", 2)[1].charAt(0);
        this.question.setText(String.format("%s?", question));
        answerA.setText(a);
        answerB.setText(b);
        answerC.setText(c);
        answerD.setText(d);
    }
}