package com.example.smartstudy;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class LearnFragment extends Fragment implements View.OnClickListener {
    private String sub, type,id, end , start, col;
    private int progress, absolut, taskCount, neededTime, sessionTime, taskTime, taskNeededTime, vol, timeSpendSeconds;
    ArrayList<Integer> todoIndex, TodoCheck, TodoTi;
    ArrayList<String> TodoId, TodoDo, TodoColec;
    private long timerInMillis;
    private DBExamHelper dbExamHelper;
    private DBTodoHelper dbTodoHelper;
    private TextView countdown, subj, titleLearn, totalTimeLeft, taskTimeLeft, todayTimeLeft, todayTimeSpend;
    private Button pause_resume, giveUp;
    private CheckBox task;
    private CountDownTimer countDownTimer;
    private boolean timerRunning, isBreak, isDone;
    private long timerLeftInMillis;
    SharedPreferences.Editor editor;
    SharedPreferences sp;




    public LearnFragment(String sub, String type, int progress, int absolut, int neededTime,
                         ArrayList<Integer> todoIndex, String id,String end, String start,
                         String color, int vol) {
        this.sub = sub;
        this.type = type;
        this.progress = progress;
        this.absolut = absolut;
        this.neededTime = neededTime;
        this.todoIndex = todoIndex;
        this.id = id;
        this.end = end;
        this.start = start;
        this.col = color;
        this.vol = vol;
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
        giveUp.setOnClickListener(this);
        task.setOnClickListener(this);

        dbExamHelper = new DBExamHelper(getActivity());
        dbTodoHelper = new DBTodoHelper(getActivity());

        TodoId= new ArrayList<>();
        TodoDo= new ArrayList<>();
        TodoTi= new ArrayList<>();
        TodoColec= new ArrayList<>();
        TodoCheck = new ArrayList<>();

        taskCount = 0;
        sp = getActivity().getSharedPreferences("SP", 0);
        loadData();
        getTask(taskCount);
        timerInMillis= (sp.getInt("timer", 2) * 60) *1000;
        sessionTime = sp.getInt("sessionTime", 0);
        taskTime = sp.getInt("taskTime", 0);
        timerLeftInMillis = timerInMillis;
        timerRunning = true;
        updateCountdownText();
        isBreak = false;
        isDone = false;
        timeSpendSeconds = 0;
        subj.setText(sub);
        totalTimeLeft.setText(String.valueOf(absolut- progress));
        todayTimeLeft.setText(String.valueOf(neededTime -sessionTime));
        todayTimeSpend.setText(String.valueOf(sessionTime));
        editor = sp.edit();
        resumeTimer();

        return view;
    }

    private void getTask(int taskCount) {
        try {
            task.setText(TodoDo.get(todoIndex.get(taskCount)));
            taskNeededTime = TodoTi.get(todoIndex.get(taskCount));
            taskTimeLeft.setText(String.valueOf(taskNeededTime - taskTime));
            this.taskCount++;
        }catch (Exception e){
            System.out.println("kein Task verfügbar");
        }

    }

    private void loadData() {
        Cursor cursor1 = dbTodoHelper.readAllData();
        if (cursor1.getCount() == 0){
            Toast.makeText(getActivity(), "NO TODO", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor1.moveToNext()) {
                TodoId.add(cursor1.getString(0));
                TodoDo.add(cursor1.getString(1));
                TodoTi.add(cursor1.getInt(2));
                TodoColec.add(cursor1.getString(3));
                TodoCheck.add(cursor1.getInt(4));


            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.equals(pause_resume)){
            if(timerRunning){
                pauseTimer();
            }else{
                resumeTimer();
            }
        }else if(v.equals(giveUp)){
            if (!isDone){
                Toast.makeText(getContext(),"Don't push away all the work!", Toast.LENGTH_SHORT).show();
            }else{
                String name = sp.getString("username", "");
                Toast.makeText(getContext(),"Well done"+name + "!", Toast.LENGTH_SHORT).show();
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MainFragment()).commit();
        }else if(v.equals(task)){
            try {
                Todo updateDo = new Todo(TodoColec.get(todoIndex.get(taskCount-1)), TodoId.get(todoIndex.get(taskCount-1)),
                        TodoDo.get(todoIndex.get(taskCount-1)), TodoTi.get(todoIndex.get(taskCount-1)), 1 );
                dbTodoHelper.updateTodoObject(updateDo);
                taskTime = 0;
                editor.putInt("taskTime", taskTime);
                getTask(taskCount);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        pause_resume.setText("RESUME");
    }

    private void resumeTimer() {
        if(((neededTime-sessionTime)*60)*1000>=timerLeftInMillis){
            countDownTimer = new CountDownTimer(timerLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerLeftInMillis = millisUntilFinished;
                    timeSpendSeconds += 1;
                    updateCountdownText();
                    if (!isBreak){
                        updateProgress();
                    }

                }

                @Override
                public void onFinish() {
                    timerRunning = false;
                    continueTimer();


                }
            }.start();
            timerRunning = true;
            pause_resume.setText("PAUSE");
        }else if (neededTime-sessionTime == 0){
            doneSession();
        }else{
            countDownTimer = new CountDownTimer(((neededTime-sessionTime)*60)*1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerLeftInMillis = millisUntilFinished;
                    timeSpendSeconds += 1;
                    updateCountdownText();
                    if (!isBreak){
                        updateProgress();
                    }
                }

                @Override
                public void onFinish() {
                    timerRunning = false;
                    continueTimer();


                }
            }.start();
            timerRunning = true;
            pause_resume.setText("PAUSE");
        }

    }

    private void doneSession() {
        titleLearn.setText("CONGRATULATION !!!");
        giveUp.setText("Done");
        giveUp.setTextColor(Color.GREEN);
        isDone = true;
    }

    private void continueTimer() {
        int timerInMinutes;
        if(!isBreak){
            isBreak = true;
            titleLearn.setText("PAUSE");
            timerInMinutes = sp.getInt("break", 1);
            timerLeftInMillis = (timerInMinutes*60)*1000;
            resumeTimer();
        }else{
            isBreak = false;
            timerLeftInMillis = timerInMillis;
            titleLearn.setText("RESUME");
            resumeTimer();
        }
    }

    private void updateProgress() {
        //long timeSpend = timerInMillis -timerLeftInMillis;
        //int timeSpendInMinutes = (int) ((timeSpend/1000)/60);
        if (timeSpendSeconds == 60){
            progress += 1;
            sessionTime +=1;
            taskTime += 1;
            timeSpendSeconds = 0;
            if (taskNeededTime > 0){
                if (taskNeededTime <= taskTime){
                    Todo updateDo = new Todo(TodoColec.get(todoIndex.get(taskCount-1)), TodoId.get(todoIndex.get(taskCount-1)),
                            TodoDo.get(todoIndex.get(taskCount-1)), TodoTi.get(todoIndex.get(taskCount-1)), 1 );
                    dbTodoHelper.updateTodoObject(updateDo);
                    taskTime = 0;
                    getTask(taskCount);
                }
                taskTimeLeft.setText(String.valueOf(taskNeededTime-taskTime));
                Todo updateDo = new Todo(TodoColec.get(todoIndex.get(taskCount-1)), TodoId.get(todoIndex.get(taskCount-1)),
                        TodoDo.get(todoIndex.get(taskCount-1)), taskNeededTime -taskTime, 0 );
                dbTodoHelper.updateTodoObject(updateDo);
            }

            totalTimeLeft.setText(String.valueOf(absolut- progress));
            todayTimeLeft.setText(String.valueOf(neededTime -sessionTime));
            todayTimeSpend.setText(String.valueOf(sessionTime));
            editor.putInt("sessionTime", sessionTime );
            editor.putInt("taskTime", taskTime);
            editor.commit();
            Exam updateExam = new Exam(id,sub,type,end, start, col, vol, progress);
            dbExamHelper.updateExamObject(updateExam);
        }


    }

    private void updateCountdownText() {
        int minutes = (int) timerLeftInMillis/(1000*60);
        int seconds = (int) (timerLeftInMillis/1000)%60;
        String timerFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        countdown.setText(timerFormatted);
    }
}