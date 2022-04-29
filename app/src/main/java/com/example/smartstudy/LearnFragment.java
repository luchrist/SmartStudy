package com.example.smartstudy;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class LearnFragment extends Fragment implements View.OnClickListener {
    private String sub, type,id;
    private int progress, absolut, neededTime;
    ArrayList<Integer> todoIndex;
    private long timerInMillis;

    private TextView countdown, subj, titleLearn, totalTimeLeft, taskTimeLeft, todayTimeLeft, todayTimeSpend;
    private Button pause_resume, giveUp;
    private CheckBox task;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timerLeftInMillis;



    public LearnFragment(String sub, String type, int progress, int absolut, int neededTime,
                         ArrayList<Integer> todoIndex, String id) {
        this.sub = sub;
        this.type = type;
        this.progress = progress;
        this.absolut = absolut;
        this.neededTime = neededTime;
        this.todoIndex = todoIndex;
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learn, container, false);
        subj = view.findViewById(R.id.subj);
        countdown = view.findViewById(R.id.countdown);
        pause_resume = view.findViewById(R.id.btn_pause_resume);
        pause_resume.setOnClickListener(this);
        titleLearn = view.findViewById(R.id.titleLearn);
        totalTimeLeft = view.findViewById(R.id.totalTimeLeft);
        todayTimeLeft = view.findViewById(R.id.todayTimeLeft);
        taskTimeLeft = view.findViewById(R.id.taskTimeLeft);
        todayTimeSpend = view.findViewById(R.id.todayTimeSpend);
        giveUp = view.findViewById(R.id.giveUpBtn);
        task = view.findViewById(R.id.taskBox);

        SharedPreferences sp = getActivity().getSharedPreferences("SP", 0);
        timerInMillis= sp.getInt("timer", 90) * 60 *1000;
        timerLeftInMillis = timerInMillis;
        timerRunning = true;
        updateCountdownText();
        resumeTimer();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(pause_resume)){
            if(timerRunning){
                pauseTimer();
            }else{
                resumeTimer();
            }
        }

    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        pause_resume.setText("RESUME");
    }

    private void resumeTimer() {
        countDownTimer = new CountDownTimer(timerLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;


            }
        }.start();
        timerRunning = true;
        pause_resume.setText("PAUSE");
    }

    private void updateCountdownText() {
        int minutes = (int) timerLeftInMillis/(1000*60);
        int seconds = (int) (timerLeftInMillis/1000)%60;
        String timerFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        countdown.setText(timerFormatted);
    }
}