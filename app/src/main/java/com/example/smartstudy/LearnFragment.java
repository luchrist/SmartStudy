package com.example.smartstudy;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartstudy.adapters.TodosAdapter;
import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Todo;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.example.smartstudy.utilities.TodoSelectListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LearnFragment extends Fragment implements View.OnClickListener, TodoSelectListener {
    private final Event event;
    private List<Todo> todos;
    private final int neededTime;
    private int sessionTime, remTimeBeforeEvent;
    private int timeSpendSeconds;
    private long timerInMillis;
    private DBEventHelper dbEventHelper;
    private DBTodoHelper dbTodoHelper;
    private TextView countdown, titleLearn, totalTimeLeft, todayTimeLeft, todayTimeSpend;
    private Button pause_resume, giveUp;
    private CountDownTimer countDownTimer;
    private boolean timerRunning, isBreak, isDone;
    private long timerLeftInMillis;
    private TodosAdapter todosAdapter;
    private Todo runningTodo;
    PreferenceManager preferenceManager;
    private TextView pointsText;

    public LearnFragment(Event event, int neededTime, List<Todo> todos, int remTimeBeforeEvent) {
        this.event = event;
        this.neededTime = neededTime;
        this.todos = todos;
        this.remTimeBeforeEvent = remTimeBeforeEvent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learn, container, false);
        TextView subj = view.findViewById(R.id.subj);
        countdown = view.findViewById(R.id.countdown);
        pause_resume = view.findViewById(R.id.btn_pause_resume);
        pause_resume.setOnClickListener(this);
        titleLearn = view.findViewById(R.id.titleLearn);
        totalTimeLeft = view.findViewById(R.id.totalTimeLeft);
        todayTimeLeft = view.findViewById(R.id.todayTimeLeft);
        todayTimeSpend = view.findViewById(R.id.todayTimeSpend);
        giveUp = view.findViewById(R.id.giveUpBtn);
        pointsText = getActivity().findViewById(R.id.points);
        giveUp.setOnClickListener(this);
        RecyclerView todosRecyclerView = view.findViewById(R.id.todosRecyclerView);
        todosAdapter = new TodosAdapter(todos, this);
        todosRecyclerView.setAdapter(todosAdapter);

        dbEventHelper = new DBEventHelper(getActivity());
        dbTodoHelper = new DBTodoHelper(getActivity());

        todos = new ArrayList<>();

        preferenceManager = new PreferenceManager(getContext());
        int timerInMinutes = preferenceManager.getInt("timer");
        if (timerInMinutes == 0) {
            timerInMinutes = 90;
        }
        timerInMillis = timerInMinutes * 60000;
        String lastSessionId = preferenceManager.getString("sessionId");
        if (event.getId().equals(lastSessionId)) {
            sessionTime = preferenceManager.getInt("sessionTime");
        } else {
            sessionTime = 0;
        }
        timerLeftInMillis = timerInMillis;
        preferenceManager.putString("sessionId", event.getId());
        timerRunning = true;
        updateCountdownText();
        isBreak = false;
        isDone = false;
        timeSpendSeconds = 0;
        subj.setText(event.getSubject());
        totalTimeLeft.setText(String.valueOf(event.getRemainingMinutes()));
        todayTimeLeft.setText(String.valueOf(neededTime - sessionTime));
        todayTimeSpend.setText(String.valueOf(sessionTime));
        findCurrentTodo();
        resumeTimer();

        return view;
    }

    private void findCurrentTodo() {
        for (Todo runningTodo : todos) {
            if (runningTodo.getChecked() == 0) {
                this.runningTodo = runningTodo;
                break;
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
                String name = preferenceManager.getString("username");
                Toast.makeText(getContext(),"Well done"+name + "!", Toast.LENGTH_SHORT).show();
            }
            preferenceManager.putInt("remainingTimeToday", remTimeBeforeEvent - sessionTime);
            preferenceManager.putString("today", LocalDate.now().toString());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MainFragment()).commit();
        }
    }

    @Override
    public void onTodoSelected(Todo todo) {
        if (todo.getChecked() == 0) {
            todo.setChecked(1);
            int index = todos.indexOf(todo);
            todosAdapter.notifyItemChanged(index);
            double faktor = event.getAbsolutMinutes() / 100.00;
            int progress = (int) (todo.getTime() / faktor);
            event.setProgress(event.getProgress() + progress);
            event.setTodos(todos);
            event.setRemainingMinutes(event.getRemainingMinutes() - todo.getTime());
            dbTodoHelper.updateTodoObject(todo);
            dbEventHelper.updateEventObject(event);
            findCurrentTodo();
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
                    updateCountdownText();
                    if (!isBreak){
                        timeSpendSeconds += 1;
                        updateProgress();
                    }
                }

                @Override
                public void onFinish() {
                    if (!isBreak){
                        timeSpendSeconds += 1;
                        updateProgress();
                    }
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
                    updateCountdownText();
                    if (!isBreak){
                        timeSpendSeconds += 1;
                        updateProgress();
                    }
                }

                @Override
                public void onFinish() {
                    timerRunning = false;
                    if(isBreak){
                        continueTimer();
                    }else{
                        timeSpendSeconds += 1;
                        updateProgress();
                        doneSession();
                    }
                }
            }.start();
            timerRunning = true;
            pause_resume.setText("PAUSE");
        }

    }

    private void doneSession() {
        titleLearn.setText("CONGRATULATION !!!");
        addPoints();
        giveUp.setText("Done");
        giveUp.setTextColor(Color.GREEN);
        isDone = true;
    }

    private void addPoints() {
        int points = sessionTime * 5;
        int currentPoints = Integer.parseInt(pointsText.getText().toString().trim());
        currentPoints += points;
        pointsText.setText(currentPoints);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail).update(Constants.KEY_POINTS, FieldValue.increment(points));
    }

    private void continueTimer() {
        int timerInMinutes;
        if(!isBreak){
            isBreak = true;
            titleLearn.setText("PAUSE");
            timerInMinutes = preferenceManager.getInt("break");
            if (timerInMinutes == 0){
                timerInMinutes = 10;
            }
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
        if (timeSpendSeconds % 60 == 0){
            event.setProgress(event.getProgress()+1);
            sessionTime +=1;
            if (runningTodo != null) {
                runningTodo.setTime(runningTodo.getTime()-1);
                if (runningTodo.getTime() < 0){
                    runningTodo.setTime(0);
                }
                dbTodoHelper.updateTodoObject(runningTodo);
                todosAdapter.notifyItemChanged(todos.indexOf(runningTodo));
            }

            totalTimeLeft.setText(String.valueOf(event.getRemainingMinutes()));
            todayTimeLeft.setText(String.valueOf(neededTime -sessionTime));
            todayTimeSpend.setText(String.valueOf(sessionTime));
            preferenceManager.putInt("sessionTime", sessionTime);
            dbEventHelper.updateEventObject(event);
        }
    }

    private void updateCountdownText() {
        int minutes = (int) timerLeftInMillis/(1000*60);
        int seconds = (int) (timerLeftInMillis/1000)%60;
        String timerFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        countdown.setText(timerFormatted);
    }
}