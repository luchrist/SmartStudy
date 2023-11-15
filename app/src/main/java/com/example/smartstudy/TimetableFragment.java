package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.TimeTableAdapter;
import com.example.smartstudy.models.TimeTableElement;
import com.example.smartstudy.utilities.TimeTableSelectListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimetableFragment extends Fragment implements TimeTableSelectListener, GestureDetector.OnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    ImageButton next, prev, add;
    TextView day;
    RecyclerView recyclerView;
    DbHelper dbHelper;
    private DayOfWeek dayOfWeek;
    List<TimeTableElement> timeTableElements;
    List<TimeTableElement> monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    TimeTableAdapter monAdapter, tueAdapter, wedAdapter, thuAdapter, friAdapter, satAdapter, sunAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);

        day = view.findViewById(R.id.dayBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        next = view.findViewById(R.id.nextBtn);
        prev = view.findViewById(R.id.prevBtn);
        add = view.findViewById(R.id.addElement);

        LocalDate date = LocalDate.now();
        dayOfWeek = date.getDayOfWeek();
        day.setText(dayOfWeek.toString());

        dbHelper = new DbHelper(getActivity());
        timeTableElements = new ArrayList<>();
        storeData();
        sortElements();
        monAdapter = new TimeTableAdapter(monday, this);
        tueAdapter = new TimeTableAdapter(tuesday, this);
        wedAdapter = new TimeTableAdapter(wednesday, this);
        thuAdapter = new TimeTableAdapter(thursday, this);
        friAdapter = new TimeTableAdapter(friday, this);
        satAdapter = new TimeTableAdapter(saturday, this);
        sunAdapter = new TimeTableAdapter(sunday, this);

        showData();
        setListeners();
        return view;
    }

    private void setListeners() {
        add.setOnClickListener(v -> {
            AddLesson alert = new AddLesson(day.getText().toString());
            alert.show(getParentFragmentManager(), "test");
        });
        prev.setOnClickListener(v -> {
            day.setText(getPrevDay());
            showData();
        });
        next.setOnClickListener(v -> {
            day.setText(getNextDay());
            showData();
        });
    }

    private String getPrevDay() {
        dayOfWeek = dayOfWeek.minus(1);
        return dayOfWeek.toString();
    }

    private String getNextDay() {
        dayOfWeek = dayOfWeek.plus(1);
        return dayOfWeek.toString();
    }

    private void showData() {
        switch (day.getText().toString()) {
            case "MONDAY":
                recyclerView.setAdapter(monAdapter);
                break;
            case "TUESDAY":
                recyclerView.setAdapter(tueAdapter);
                break;
            case "WEDNESDAY":
                recyclerView.setAdapter(wedAdapter);
                break;
            case "THURSDAY":
                recyclerView.setAdapter(thuAdapter);
                break;
            case "FRIDAY":
                recyclerView.setAdapter(friAdapter);
                break;
            case "SATURDAY":
                recyclerView.setAdapter(satAdapter);
                break;
            case "SUNDAY":
                recyclerView.setAdapter(sunAdapter);
                break;
            default:
                break;
        }
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

    @Override
    public void onElementSelected(TimeTableElement element) {
        UpdateLesson alert = new UpdateLesson(element.getDay(), element.getSubject(), element.getBegin(), element.getEnd(), element.getRoom(), element.getTeacher(), element.getId(), element.getColour());
        alert.show(getParentFragmentManager(), "test");
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
                day.setText(getPrevDay());
                showData();
            } else {
                day.setText(getNextDay());
                showData();
            }
        }
        return true;
    }
}