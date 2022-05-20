package com.example.smartstudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.time.temporal.ChronoUnit;

public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView title, sub, ty,tim,shownDate, homeTitle;
    NavigationView navigationView;
    Spinner spinner;
    LinearLayout lessonsList, tasks, times;

    DBExamHelper dbExamHelper;
    DbHelper dbHelper;
    DBTodoHelper dbTodoHelper;
    DbTimeHelper dbTimeHelper;
    DBExeptionHelper dbExeptionHelper;
    Button prev, next, start;
    ProgressBar progressBar;
    private LocalDate today;
    ArrayList<String> dayTasks, PlanId, PlanSub, PlanType, PlanBeg, PlanEnd, PlanCol, TodoId, TodoDo, TodoColec;
    ArrayList<Integer>  TodoTi, TodoCheck, PlanProg, PlanVol, remainingDays, exeptionminutes;
    ArrayList<String> BeforeExamsId;
    String id, enddate, startdate, col;
    ArrayList<Integer> absolutHours, todoIndex;
    ArrayList<LocalDate> exeptionDates;
    int absolut;
    int prog, vol;
    SharedPreferences sp;
    MainActivity mainActivity = new MainActivity();






    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        sp = getActivity().getSharedPreferences("SP", 0);
        title = getActivity().findViewById(R.id.variabel_text);
        homeTitle = view.findViewById(R.id.timetable_title);
        navigationView = getActivity().findViewById(R.id.nav_view);
        spinner = view.findViewById(R.id.spinner);
        lessonsList = view.findViewById(R.id.lessonslist);
        sub = view.findViewById(R.id.subStudyPlan);
        ty = view.findViewById(R.id.typeStudyPlan);
        tim = view.findViewById(R.id.timeNeeded);
        shownDate = view.findViewById(R.id.shownDate);
        tasks  = view.findViewById(R.id.tasks);
        times = view.findViewById(R.id.times);
        prev  = view.findViewById(R.id.prev);
        next  = view.findViewById(R.id.next);
        start  = view.findViewById(R.id.start);
        start.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);

        dbHelper = new DbHelper(getActivity());
        dbExamHelper = new DBExamHelper(getActivity());
        dbTodoHelper = new DBTodoHelper(getActivity());
        dbTimeHelper = new DbTimeHelper(getActivity());
        dbExeptionHelper = new DBExeptionHelper(getActivity());
        today = LocalDate.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        String dateString =today.format(formatter);
        shownDate.setText(dateString);

        PlanId = new ArrayList<>();
        PlanSub = new ArrayList<>();
        PlanType= new ArrayList<>();
        PlanVol= new ArrayList<>();
        PlanBeg= new ArrayList<>();
        PlanEnd= new ArrayList<>();
        PlanCol= new ArrayList<>();
        PlanProg= new ArrayList<>();
        TodoId= new ArrayList<>();
        TodoDo= new ArrayList<>();
        TodoTi= new ArrayList<>();
        TodoColec= new ArrayList<>();
        TodoCheck = new ArrayList<>();
        BeforeExamsId = new ArrayList<>();
        remainingDays = new ArrayList<>();
        absolutHours = new ArrayList<>();
        dayTasks = new ArrayList<>();
        todoIndex = new ArrayList<>();
        exeptionDates = new ArrayList<>();
        exeptionminutes = new ArrayList<>();

        loadData();
        plan(today);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);


        switch (day) {
            case Calendar.MONDAY:
                spinner.setSelection(0);


                // Current day is Monday
                break;
            case Calendar.TUESDAY:
                spinner.setSelection(1);

                break;
            case Calendar.WEDNESDAY:
                spinner.setSelection(2);

                break;
            case Calendar.THURSDAY:
                spinner.setSelection(3);

                break;
            case Calendar.FRIDAY:
                spinner.setSelection(4);

                break;
            default:
                spinner.setSelection(5);

                break;
        }


        lessonsList.setOnClickListener(this);
        homeTitle.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                lessonsList.removeAllViewsInLayout();
                Cursor cursor = dbHelper.readAllData();
                if (cursor.getCount() == 0) {
                    //doing nothing :)
                } else {
                    while (cursor.moveToNext()) {
                        String text = "";
                        switch (position) {
                            case 0:
                                if (cursor.getString(4).equals("Monday")) {
                                    text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                            cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                            cursor.getString(6);
                                }
                                // Current day is Monday
                                break;
                            case 1:

                                if (cursor.getString(4).equals("Tuesday")) {
                                    text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                            cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                            cursor.getString(6);
                                }
                                break;
                            case 2:

                                if (cursor.getString(4).equals("Wednesday")) {
                                    text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                            cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                            cursor.getString(6);
                                }
                                break;
                            case 3:
                                if (cursor.getString(4).equals("Thursday")) {
                                    text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                            cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                            cursor.getString(6);
                                }
                                break;
                            case 4:
                                if (cursor.getString(4).equals("Friday")) {
                                    text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                            cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                            cursor.getString(6);
                                }
                                break;
                            default:
                                spinner.setSelection(5);

                                break;
                        }

                        TextView newLesson = new TextView(view.getContext());
                        newLesson.setText(text);
                        newLesson.setGravity(Gravity.CENTER_HORIZONTAL);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(0, 16, 0, 0);
                        newLesson.setLayoutParams(lp);
                        lessonsList.addView(newLesson);



                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        return view;
    }

    private void plan(LocalDate localDate) {

        for(int i = 0; i < PlanId.size(); i++) {

            LocalDate begin = LocalDate.parse(PlanBeg.get(i));
            if (begin.isBefore(localDate) || begin.isEqual(localDate)) {
                LocalDate end = LocalDate.parse(PlanEnd.get(i));
                if(!end.isBefore(localDate)){
                    BeforeExamsId.add(PlanId.get(i));

                }else{
                    Exam delExam = new Exam(PlanId.get(i),PlanSub.get(i),PlanType.get(i), PlanEnd.get(i),
                            PlanBeg.get(i), PlanCol.get(i), PlanVol.get(i), PlanProg.get(i));
                    dbExamHelper.deleteExamObject(delExam);
                }
            }
        }
        for(int i = 0; i < BeforeExamsId.size(); i++) {
            int gesStd = 0;
            int toDoTime = 0;
            for (int j = 0; j < PlanId.size(); j++) {
                if (PlanId.get(j).equals(BeforeExamsId.get(i))) {
                    switch (PlanVol.get(j)) {
                        case 0:
                            gesStd = 5*60;
                            break;
                        case 1:
                            gesStd = 10*60;
                            break;
                        case 2:
                            gesStd = 15*60;
                            break;
                        case 3:
                            gesStd = 20*60;
                            break;
                        case 4:
                            gesStd = 25*60;
                            break;
                        case 5:
                            gesStd = 30*60;
                            break;
                        case 6:
                            gesStd = 35*60;
                            break;
                        default:
                            gesStd = 35*60;
                            break;
                    }
                    String end = PlanEnd.get(j);
                    LocalDate endDate = LocalDate.parse(end);
                    long days = ChronoUnit.DAYS.between(endDate, localDate);
                    remainingDays.add((int) days);

                    String key = PlanSub.get(j) + PlanType.get(j);
                    for (int s = 0; s < TodoTi.size(); s++) {
                        if (TodoColec.get(s).equals(key)) {
                            toDoTime += TodoTi.get(s);
                        }

                    }
                    if (gesStd > toDoTime) {
                        absolutHours.add(gesStd);

                    } else {
                        absolutHours.add(toDoTime);

                    }

                }
            }


        }
        boolean isTomorrow = false;
        for (int k = 0; k < PlanId.size(); k++){
            String endd = PlanEnd.get(k);
            LocalDate endDa = LocalDate.parse(endd);
            if(ChronoUnit.DAYS.between(localDate, endDa) < 1){
                isTomorrow = true;
                id = PlanId.get(k);
                enddate = PlanEnd.get(k);
                startdate = PlanBeg.get(k);
                col = PlanCol.get(k);
                vol = PlanVol.get(k);
                sub.setText(PlanSub.get(k));
                ty.setText(PlanType.get(k));
                absolut = 0;
                for(int i = 0; i < BeforeExamsId.size(); i++) {
                    for (int j = 0; j < PlanId.size(); j++) {
                        if (PlanId.get(j).equals(BeforeExamsId.get(i)) &&
                                PlanId.get(k).equals(PlanId.get(j))) {
                            absolut = absolutHours.get(i);

                        }
                    }
                }
                prog = PlanProg.get(k);
                float faktor = 100 / absolut;
                progressBar.setProgress((int) (prog*faktor));
                String key = PlanSub.get(k) + PlanType.get(k);
                int time = maxTimeToday();
                for(int t = 0; t < TodoId.size(); t++){
                    if(TodoColec.get(t).equals(key)){
                        if (TodoCheck.get(t) == 0){

                            if(time > 0){
                                todoIndex.add(t);
                                TextView newTodo = new TextView(tasks.getContext());
                                newTodo.setText(TodoDo.get(t));
                                tasks.addView(newTodo);

                                TextView newEstimated = new TextView(times.getContext());
                                newEstimated.setText(TodoTi.get(t));
                                times.addView(newEstimated);

                                time -= TodoTi.get(t);
                            }
                        }
                    }
                }
                TextView newTodo = new TextView(tasks.getContext());
                newTodo.setText("Wiederholen");
                tasks.addView(newTodo);
                TextView newEstimated = new TextView(times.getContext());
                if (time <= 0){
                    newEstimated.setText("0");
                    time = 0;
                }else if(time > 1){
                    newEstimated.setText("1");
                    time -= 1;
                }else{
                    newEstimated.setText(String.valueOf(time));
                    time = 0;
                }

                times.addView(newEstimated);
                tim.setText(String.valueOf(time));

                mainActivity.setStudyneed(true);
            }

        }
        boolean isInThreeDays= false;
        boolean isInOneWeek = false;
        boolean isInOneMonth = false;
        if(isTomorrow == false){
            isInThreeDays = showDayData(3, localDate);
        }
        if(isTomorrow == false && isInThreeDays == false){

             isInOneWeek = showDayData(7, localDate);
        }
        if(isTomorrow == false && isInThreeDays == false && isInOneWeek == false){
            isInOneMonth = showDayData(30, localDate);
        }

    }

    private boolean showDayData(int inDays, LocalDate localdate){

        boolean isInXDays = false;
        int highestRemAbs = 0;
        for (int k = 0; k < PlanId.size(); k++){
            String endd = PlanEnd.get(k);
            LocalDate endDa = LocalDate.parse(endd);
            if(ChronoUnit.DAYS.between(localdate, endDa) < inDays){ // alle Exams die in den nächsten 3 tagen sind
                isInXDays= true;
                for(int i = 0; i < BeforeExamsId.size(); i++) {
                    for (int j = 0; j < PlanId.size(); j++) {
                        if (PlanId.get(j).equals(BeforeExamsId.get(i)) &&
                                PlanId.get(k).equals(PlanId.get(j))) {
                            absolut = absolutHours.get(i);
                            prog = PlanProg.get(k);
                            if (highestRemAbs < (absolut - prog)) {
                                highestRemAbs = absolut - prog;           //meiste verbleibenden Stunden suchen
                            }
                        }
                    }
                }
                for(int i = 0; i < BeforeExamsId.size(); i++) {
                    for (int j = 0; j < PlanId.size(); j++) {
                        if (PlanId.get(j).equals(BeforeExamsId.get(i)) &&
                                PlanId.get(k).equals(PlanId.get(j))) {
                            absolut = absolutHours.get(i);
                            //prog = PlanProg.get(k);
                            if (highestRemAbs == (absolut - prog)) {
                                id = PlanId.get(k);
                                enddate = PlanEnd.get(k);
                                startdate = PlanBeg.get(k);
                                col = PlanCol.get(k);
                                vol = PlanVol.get(k);
                                sub.setText(PlanSub.get(k));
                                ty.setText(PlanType.get(k));    //das Exam mit den meisten verbleibenden Stunden zuordnen
                                float faktor = 100 / absolut;
                                progressBar.setProgress((int) (prog*faktor));
                                String key = PlanSub.get(k) + PlanType.get(k);
                                int time = maxTimeToday();
                                //todos laden
                                for(int t = 0; t < TodoId.size(); t++){
                                    if(TodoColec.get(t).equals(key)){
                                        if (TodoCheck.get(t) == 0){
                                            if(time > 0){
                                                todoIndex.add(t);
                                                TextView newTodo = new TextView(tasks.getContext());
                                                newTodo.setText(TodoDo.get(t));
                                                tasks.addView(newTodo);

                                                TextView newEstimated = new TextView(times.getContext());
                                                newEstimated.setText(TodoTi.get(t));
                                                times.addView(newEstimated);

                                                time -= TodoTi.get(t);
                                            }
                                        }
                                    }
                                }
                                if(highestRemAbs > 0){
                                    mainActivity.setStudyneed(true);
                                }else {
                                    mainActivity.setStudyneed(false);
                                }
                                if(time>0) {
                                    if (highestRemAbs >= maxTimeToday()) {
                                        tim.setText(String.valueOf(maxTimeToday()));

                                    } else if (highestRemAbs > (maxTimeToday() - time)){
                                        tim.setText(String.valueOf(highestRemAbs));
                                    }else{
                                        tim.setText(String.valueOf(maxTimeToday()-time));
                                    }


                                }else{

                                    tim.setText(String.valueOf(maxTimeToday()));
                                }





                            }
                        }
                    }
                }
            }
        }
        return isInXDays;
    }

    private int maxTimeToday() {
        boolean exepted = false;
        int time = 0;
        for (int h = 0 ; h < exeptionDates.size(); h++){
            if(exeptionDates.get(h).equals(today)){
                exepted = true;
                time = exeptionminutes.get(h);
            }
        }
        if(!exepted){
            String weekday = today.getDayOfWeek().name();

            switch (weekday){
                case "MONDAY":
                    return getTime("Monday");

                case "TUESDAY":
                    return getTime("Tuesday");

                case "WEDNESDAY":
                    return getTime("Wednesday");

                case "THURSDAY":
                    return getTime("Thursday");

                case "FRIDAY":
                    return getTime("Friday");

                case "SATURDAY":
                    return getTime("Saturday");

                case "SUNDAY":
                    return  getTime("Sunday");



            }
        }
        return time;
    }

    private int getTime(String day) {
        TimeObject time;
        Cursor cursor = dbTimeHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()) {
                if (cursor.getString(1).equalsIgnoreCase(day)){
                    //System.out.println("test"+ cursor.getString(1));
                    //System.out.println("test"+sp.getInt(cursor.getString(1), 0));
                    //return sp.getInt(cursor.getString(1), 0);
                    return 5;
                    //return cursor.getInt(2);
                }


            }
        }
        return 0;
    }

    private void loadData(){
        Cursor cursor1 = dbExamHelper.readAllData();
        if (cursor1.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor1.moveToNext()) {

                PlanId.add(cursor1.getString(0));
                PlanSub.add(cursor1.getString(1));
                PlanType.add(cursor1.getString(2));
                PlanVol.add(cursor1.getInt(3));
                PlanBeg.add(cursor1.getString(4));
                PlanEnd.add(cursor1.getString(5));
                PlanCol.add(cursor1.getString(6));
                PlanProg.add(cursor1.getInt(7));

            }
        }
        cursor1 = dbTodoHelper.readAllData();
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
        cursor1 = dbExeptionHelper.readAllData();
        if (cursor1.getCount() == 0){
            Toast.makeText(getActivity(), "NO EXEPTION", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor1.moveToNext()) {
                exeptionminutes.add(cursor1.getInt(2));
                String date = cursor1.getString(1);
                String[] dateParts = date.split(".");
                String dateFormatted = dateParts[2] + "-"+ getMonat(dateParts[1]) + "-" + dateParts[0];
                LocalDate date1 = LocalDate.parse(dateFormatted);
                exeptionDates.add(date1);


            }
        }
    }

    private String getMonat(String datePart) {
        String s;
        switch (datePart) {
            case "Jan":
                s = "01";
                break;
            case "Feb":
                s = "02";
                break;
            case "Mar":
                s = "03";
                break;
            case "Apr":
                s = "04";
                break;
            case "May":
                s = "05";
                break;
            case "Jun":
                s = "06";
                break;
            case "Jul":
                s = "07";
                break;
            case "Aug":
                s = "08";
                break;
            case "Sep":
                s = "09";
                break;
            case "Oct":
                s = "10";
                break;
            case "Nov":
                s = "11";
                break;
            case "Dec":
                s = "12";
                break;
            default:
                s = "01";
                break;
        }
        return s;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(lessonsList) || v.equals(homeTitle)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new TimetableFragment()).commit();
            title.setText("Timetable");
            navigationView.setCheckedItem(R.id.nav_timetable);

        }else if (v.equals(start)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new LearnFragment(sub.getText().toString(),ty.getText().toString(),prog,
                            absolut, Integer.parseInt(tim.getText().toString()),
                            todoIndex, id, enddate,startdate,  col, vol)).commit();
            title.setText("Learn");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}

