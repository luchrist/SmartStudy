package com.example.smartstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartstudy.utilities.Constants;

import java.util.ArrayList;

public class AiExam extends AppCompatActivity {

    private int questionNumber = 0;
    private ArrayList<String> questions, answersA, answersB, answersC, answersD;
    private ArrayList<CorrectAnswer> correctAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_exam);
        Intent intent = getIntent();
        questions = (ArrayList<String>) intent.getSerializableExtra(Constants.KEY_QUESTIONS);
        answersA = (ArrayList<String>) intent.getSerializableExtra(Constants.KEY_ANSWERSA);
        answersB = (ArrayList<String>) intent.getSerializableExtra(Constants.KEY_ANSWERSB);
        answersC = (ArrayList<String>) intent.getSerializableExtra(Constants.KEY_ANSWERSC);
        answersD = (ArrayList<String>) intent.getSerializableExtra(Constants.KEY_ANSWERSD);
        correctAnswers = (ArrayList<CorrectAnswer>) intent.getSerializableExtra(Constants.KEY_CORRECT_ANSWERS);
    }
}