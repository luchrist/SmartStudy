package com.example.smartstudy;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.smartstudy.adapters.TimeTableAdapter;
import com.example.smartstudy.adapters.TodosAdapter;
import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.TimeException;
import com.example.smartstudy.models.TimeTableElement;
import com.example.smartstudy.models.Todo;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.example.smartstudy.utilities.TimeTableSelectListener;
import com.example.smartstudy.utilities.TodoSelectListener;
import com.example.smartstudy.utilities.Util;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, TodoSelectListener, TimeTableSelectListener {

    private TextView title, sub, ty, tim, dueDate, timetableTitle, noElements, noSessions, studyPlanTitle;
    private NavigationView navigationView;
    private ConstraintLayout studyPlanLayout;
    private Spinner spinner;
    private RecyclerView todosList, lessonsList;
    private DBEventHelper dbEventHelper;
    private DbHelper dbHelper;
    private List<TimeTableElement> timeTableElements;
    private List<TimeTableElement> monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private TimeTableAdapter monAdapter, tueAdapter, wedAdapter, thuAdapter, friAdapter, satAdapter, sunAdapter;
    private DBTodoHelper dbTodoHelper;
    private DbTimeHelper dbTimeHelper;
    private DBExeptionHelper dbExeptionHelper;
    private Button startTimer, completed;
    private ProgressBar progressBar;
    private LocalDate today;
    private List<Event> events;
    private List<Todo> todos, todosToday;
    private List<TimeException> exceptions;
    private List<Event> beforeEvents;
    private Event currentEvent;
    private PreferenceManager preferenceManager;
    private TodosAdapter todosAdapter;
    private int remainingTimeToday, remTimeBeforeEvent;
    private LinearLayout pointContainer;
    private TextView pointsText;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        preferenceManager = new PreferenceManager(getActivity());
        connectViews(view);
        initialiseVars();
        loadData();
        plan();

        createDropDownWeekDays();
        selectTodaysWeekDay();
        showTodaysLessons();

        setListeners(view);
        return view;
    }

    private void showTodaysLessons() {
        dbHelper = new DbHelper(getActivity());
        timeTableElements = new ArrayList<>();
        storeData();
        sortElements();
        monAdapter = new TimeTableAdapter(monday, this, false);
        tueAdapter = new TimeTableAdapter(tuesday, this, false);
        wedAdapter = new TimeTableAdapter(wednesday, this, false);
        thuAdapter = new TimeTableAdapter(thursday, this, false);
        friAdapter = new TimeTableAdapter(friday, this, false);
        satAdapter = new TimeTableAdapter(saturday, this, false);
        sunAdapter = new TimeTableAdapter(sunday, this, false);

        showData();
    }

    void storeData() {
        Cursor cursor = dbHelper.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                TimeTableElement timeTableElement = new TimeTableElement(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(4), cursor.getString(7));
                timeTableElements.add(timeTableElement);
            }
        }
    }

    private void sortElements() {
        monday = new ArrayList<>();
        tuesday = new ArrayList<>();
        wednesday = new ArrayList<>();
        thursday = new ArrayList<>();
        friday = new ArrayList<>();
        saturday = new ArrayList<>();
        sunday = new ArrayList<>();
        for (TimeTableElement timeTableElement : timeTableElements) {
            switch (timeTableElement.getDay()) {
                case "MONDAY":
                    monday.add(timeTableElement);
                    break;
                case "TUESDAY":
                    tuesday.add(timeTableElement);
                    break;
                case "WEDNESDAY":
                    wednesday.add(timeTableElement);
                    break;
                case "THURSDAY":
                    thursday.add(timeTableElement);
                    break;
                case "FRIDAY":
                    friday.add(timeTableElement);
                    break;
                case "SATURDAY":
                    saturday.add(timeTableElement);
                    break;
                case "SUNDAY":
                    sunday.add(timeTableElement);
                    break;
                default:
                    break;
            }
        }
        sortByTime(monday);
        sortByTime(tuesday);
        sortByTime(wednesday);
        sortByTime(thursday);
        sortByTime(friday);
        sortByTime(saturday);
        sortByTime(sunday);
    }

    private void sortByTime(List<TimeTableElement> elementsOfDay) {
        elementsOfDay.sort((o1, o2) -> {
            int o1Begin = Integer.parseInt(o1.getBegin().replace(":", ""));
            int o2Begin = Integer.parseInt(o2.getBegin().replace(":", ""));
            return Integer.compare(o1Begin, o2Begin);
        });
    }

    private void showData() {
        lessonsList.setVisibility(View.VISIBLE);
        noElements.setVisibility(View.GONE);
        switch (spinner.getSelectedItem().toString()) {
            case "MONDAY":
                setAdapter(monAdapter);
                break;
            case "TUESDAY":
                setAdapter(tueAdapter);
                break;
            case "WEDNESDAY":
                setAdapter(wedAdapter);
                break;
            case "THURSDAY":
                setAdapter(thuAdapter);
                break;
            case "FRIDAY":
                setAdapter(friAdapter);
                break;
            case "SATURDAY":
                setAdapter(satAdapter);
                break;
            case "SUNDAY":
                setAdapter(sunAdapter);
                break;
            default:
                break;
        }
    }

    private void setAdapter(TimeTableAdapter adapter) {
        if (adapter.getItemCount() > 0) {
            lessonsList.setAdapter(adapter);
        } else {
            lessonsList.setVisibility(View.GONE);
            noElements.setVisibility(View.VISIBLE);
        }
    }

    private void plan() {
        String lastPlannedDay = preferenceManager.getString("today");
        if(lastPlannedDay == null){
            remainingTimeToday = maxTimeToday();
        } else {
            if(lastPlannedDay.equals(today.toString())){
                remainingTimeToday = preferenceManager.getInt("remainingTimeToday");
            } else {
                remainingTimeToday = maxTimeToday();
            }
        }
        preferenceManager.putInt("remainingTimeToday", remainingTimeToday);
        remTimeBeforeEvent = remainingTimeToday;
        if (remainingTimeToday > 0) {
            beforeEvents = getAllLearnableEvents();
            beforeEvents.replaceAll(this::updateEvent);
            beforeEvents.sort(Comparator.comparingInt(Event::getRemainingDays));
            boolean tomorrow = showBiggestEventInDays(1, beforeEvents);
            if (!tomorrow) {
                boolean isInThreeDays = showBiggestEventInDays(3, beforeEvents);
                if (!isInThreeDays) {
                    boolean isInOneWeek = showBiggestEventInDays(7, beforeEvents);
                    if (!isInOneWeek) {
                        boolean isIn2Weeks = showBiggestEventInDays(14, beforeEvents);
                        if (!isIn2Weeks) {
                          boolean isIn30Days = showBiggestEventInDays(30, beforeEvents);
                          if (!isIn30Days) {
                              noSessions.setVisibility(View.VISIBLE);
                              studyPlanLayout.setVisibility(View.GONE);
                          }
                        }
                    }
                }
            }
        } else {
            noSessions.setVisibility(View.VISIBLE);
            noSessions.setText("You have no time left today!");
            studyPlanLayout.setVisibility(View.INVISIBLE);
        }
    }

    private boolean showBiggestEventInDays(int inDays, List<Event> beforeEvents) {
        Stream.Builder<Event> builder = Stream.builder();
        for (Event event : beforeEvents) {
            if (event.getRemainingDays() < inDays && event.getRemainingDays() >= 0) {
                builder.add(event);
            } else {
                break;
            }
        }
        Optional<Event> eventWithMostHoursLeft = builder.build().max(Comparator.comparingInt(Event::getRemainingMinutes));
        if(eventWithMostHoursLeft.isPresent()) {
            currentEvent = eventWithMostHoursLeft.get();
            showCurrentEvent();
            return true;
        }
        return false;
    }

    private void showCurrentEvent() {
        preferenceManager.putBoolean("studyNeed", true);
        sub.setText(currentEvent.getSubject());
        ty.setText(currentEvent.getType());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate = LocalDate.parse(currentEvent.getEndDate(), formatter);
        dueDate.setText(Util.getFormattedDate(endDate));
        int absolut = currentEvent.getAbsolutMinutes();
        int faktor;
        if (absolut != 0) {
            faktor = 100 / absolut;
        } else {
            faktor = 100;
        }
        progressBar.setProgress(currentEvent.getProgress() * faktor);
        int remainingMins = currentEvent.getRemainingMinutes();
        int time = remainingTimeToday;
        for (Todo todo : currentEvent.getTodos()) {
            if (todo.getChecked() == 0) {
                if (time >= 0) {
                    todosToday.add(todo);
                    time -= todo.getTime();
                    remainingMins -= todo.getTime();
                }
            }
        }
        todosAdapter = new TodosAdapter(todosToday, this);
        todosList.setAdapter(todosAdapter);
        if(time <= 0){
            tim.setText(String.valueOf(remainingTimeToday));
            currentEvent.setRemainingMinutes(currentEvent.getRemainingMinutes()-remainingTimeToday);
            remainingTimeToday = 0;
        }else {
            if(remainingMins > time) {
                tim.setText(String.valueOf(remainingTimeToday));
                currentEvent.setRemainingMinutes(currentEvent.getRemainingMinutes()-remainingTimeToday);
                remainingTimeToday = 0;
            } else {
                if (remainingMins > 0) {
                    tim.setText(currentEvent.getRemainingMinutes());
                    remainingTimeToday = time-remainingMins;
                    currentEvent.setRemainingMinutes(0);
                } else {
                    tim.setText(String.valueOf(remainingTimeToday - time));
                    remainingTimeToday = time;
                    currentEvent.setRemainingMinutes(0);
                }
            }
        }
    }

    private Event updateEvent(Event event) {
        String end = event.getEndDate();
        LocalDate endDate = LocalDate.parse(end);
        event.setRemainingDays((int) ChronoUnit.DAYS.between(today, endDate));

        int gesStd = getNeededTime(event);
        int toDoTime = Util.getTimeNeededForTodos(event, todos);
        event.setAbsolutMinutes(Math.max(gesStd, toDoTime));

        List<Todo> currentTodos = event.getTodos();
        for (Todo todo : todos) {
            if (todo.getCollection().equals(event.getId())) {
                currentTodos.add(todo);
            }
        }
        event.setTodos(currentTodos);
        return event;
    }

    private int getNeededTime(Event event) {
        switch (event.getVolume()) {
            case 0:
                return 5 * 60;
            case 1:
                return 10 * 60;
            case 2:
                return 15 * 60;
            case 3:
                return 20 * 60;
            case 4:
                return 25 * 60;
            case 5:
                return 30 * 60;
            case 6:
                return 35 * 60;
            default:
                return 35 * 60;
        }
    }

    private List<Event> getAllLearnableEvents() {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            LocalDate begin = LocalDate.parse(event.getStartDate());
            if (begin.isBefore(today) || begin.isEqual(today)) {
                LocalDate end = LocalDate.parse(event.getEndDate());
                if (!end.isBefore(today)) {
                    beforeEvents.add(event);
                } else {
                    dbEventHelper.deleteEventObject(event);
                }
            }
        }
        return beforeEvents;
    }

    private void setListeners(View view) {
        timetableTitle.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    showData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void selectTodaysWeekDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                spinner.setSelection(0);
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
    }

    private void createDropDownWeekDays() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void initialiseVars() {
        dbHelper = new DbHelper(getActivity());
        dbEventHelper = new DBEventHelper(getActivity());
        dbTodoHelper = new DBTodoHelper(getActivity());
        dbTimeHelper = new DbTimeHelper(getActivity());
        dbExeptionHelper = new DBExeptionHelper(getActivity());

        today = LocalDate.now();

        events = new ArrayList<>();
        todos = new ArrayList<>();
        todosToday = new ArrayList<>();
        exceptions = new ArrayList<>();
        beforeEvents = new ArrayList<>();
    }

    private void connectViews(View view) {
        title = requireActivity().findViewById(R.id.variabel_text);
        timetableTitle = view.findViewById(R.id.timetable_title);
        navigationView = requireActivity().findViewById(R.id.nav_view);
        spinner = view.findViewById(R.id.spinner);
        todosList = view.findViewById(R.id.todosRecyclerView);
        lessonsList = view.findViewById(R.id.timetableRecyclerView);
        noElements = view.findViewById(R.id.noElements);
        noSessions = view.findViewById(R.id.noSessions);
        studyPlanLayout = view.findViewById(R.id.studyPlanLayout);
        studyPlanTitle = view.findViewById(R.id.studyPlanTitle);
        sub = view.findViewById(R.id.subStudyPlan);
        ty = view.findViewById(R.id.typeStudyPlan);
        tim = view.findViewById(R.id.timeNeeded);
        dueDate = view.findViewById(R.id.dueDate);
        startTimer = view.findViewById(R.id.start);
        startTimer.setOnClickListener(this);
        completed = view.findViewById(R.id.completed);
        completed.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);
        pointContainer = getActivity().findViewById(R.id.pointsContainer);
        pointsText = getActivity().findViewById(R.id.points);
    }

    private String minutesToString(Integer m) {
        return m / 60 + "." + m % 60;
    }

    private int maxTimeToday() {
        int time = getTodayException();
        if (time == -1) {
            String weekday = today.getDayOfWeek().name();
            return getTime(weekday);
        }
        return time;
    }

    private int getTodayException() {
        int time = -1;
        for (int i = 0; i < exceptions.size(); i++) {
            if (exceptions.get(i).getDate().equals(today)) {
                time = exceptions.get(i).getTime();
            }
        }
        return time;
    }

    private int getTime(String day) {
        Cursor cursor = dbTimeHelper.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                if (cursor.getString(1).equalsIgnoreCase(day)) {
                    return cursor.getInt(2);
                }
            }
        }
        return 0;
    }

    private void loadData() {
        Cursor cursor1 = dbEventHelper.readAllData();
        if (cursor1.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor1.moveToNext()) {
                Event event = new Event();
                event.setId(cursor1.getString(0));
                event.setSubject(cursor1.getString(1));
                event.setType(cursor1.getString(2));
                event.setVolume(cursor1.getInt(3));
                event.setStartDate(cursor1.getString(4));
                event.setEndDate(cursor1.getString(5));
                event.setColor(cursor1.getString(6));
                event.setProgress(cursor1.getInt(7));
                events.add(event);
            }
        }
        cursor1 = dbTodoHelper.readAllData();
        if (cursor1.getCount() == 0) {
            Toast.makeText(getActivity(), "NO TODO", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor1.moveToNext()) {
                Todo todo = new Todo();
                todo.setId(cursor1.getString(0));
                todo.setTodo(cursor1.getString(1));
                todo.setTime(cursor1.getInt(2));
                todo.setCollection(cursor1.getString(3));
                todo.setChecked(cursor1.getInt(4));
                todos.add(todo);
            }
        }
        cursor1 = dbExeptionHelper.readAllData();
        if (cursor1.getCount() == 0) {
            Toast.makeText(getActivity(), "NO EXEPTION", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor1.moveToNext()) {
                LocalDate date = getFormattedDate(cursor1.getString(1));
                if (date.isBefore(LocalDate.now())) {
                    dbExeptionHelper.deleteExeptionObject(String.valueOf(cursor1.getInt(0)));
                } else {
                    exceptions.add(new TimeException(date, cursor1.getInt(2)));
                }
            }
        }
    }

    private LocalDate getFormattedDate(String dateText) {
        String[] dateParts = dateText.split("\\.");
        String d = dateParts[0];
        if (Integer.parseInt(dateParts[0]) < 10) {
            d = "0" + d;
        }
        String dateFormatted = dateParts[2] + "-" + getMonth(dateParts[1]) + "-" + d;
        return LocalDate.parse(dateFormatted);
    }

    private String getMonth(String datePart) {
        switch (datePart) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                return "01";
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(timetableTitle)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new TimetableFragment(spinner.getSelectedItem().toString())).commit();
            title.setText("Timetable");
            navigationView.setCheckedItem(R.id.nav_timetable);

        } else if (v.equals(startTimer)) {
            if (sub.getText().toString() != "") {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new LearnFragment(currentEvent, Integer.parseInt(tim.getText().toString()),
                                todosToday, remTimeBeforeEvent)).commit();
                title.setText("Learn");
            } else {
                Toast.makeText(getContext(), "There is nothing to learn today!", Toast.LENGTH_SHORT).show();

            }
        } else if (v.equals(completed)) {
            int faktor = currentEvent.getAbsolutMinutes() / 100;
            int progresMins = currentEvent.getAbsolutMinutes() - currentEvent.getRemainingMinutes();
            currentEvent.setProgress(progresMins/ faktor);
            dbEventHelper.updateEventObject(currentEvent);
            preferenceManager.putString("today", today.toString());
            preferenceManager.putInt("remainingTimeToday", remainingTimeToday);
            addPoints(Integer.parseInt(tim.getText().toString()));
            plan();
        }
    }

    private void addPoints(int timeLearned) {
        int points = timeLearned * 5;
        int currentPoints = Integer.parseInt(pointsText.getText().toString().trim());
        currentPoints += points;
        pointsText.setText(String.valueOf(currentPoints));
        YoYo.with(Techniques.Bounce)
                .duration(500)
                .repeat(1)
                .playOn(pointsText);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUserEmail).update(Constants.KEY_POINTS, FieldValue.increment(points));
    }

    @Override
    public void onTodoSelected(Todo todo) {
        if (todo.getChecked() == 0) {
            todo.setChecked(1);
            int index = todosToday.indexOf(todo);
            todosToday.remove(todo);
            todosAdapter.notifyItemRemoved(index);
            double faktor = currentEvent.getAbsolutMinutes() / 100.00;
            int progress = (int) (todo.getTime() / faktor);
            progressBar.setProgress(progressBar.getProgress() + progress);
            currentEvent.setProgress(currentEvent.getProgress() + progress);
            currentEvent.setTodos(todosToday);
            currentEvent.setRemainingMinutes(currentEvent.getRemainingMinutes() - todo.getTime());
            dbTodoHelper.updateTodoObject(todo);
            dbEventHelper.updateEventObject(currentEvent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Override
    public void onElementSelected(TimeTableElement element) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,
                    new TimetableFragment(spinner.getSelectedItem().toString()))
                .addSharedElement(lessonsList, "lessons")
                .commit();
    }
}

