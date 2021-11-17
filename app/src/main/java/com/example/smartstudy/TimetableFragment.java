package com.example.smartstudy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TimetableFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton addBtn;
    LinearLayout mon, tue, wed, thu, fri;
    DbHelper dbHelper;
    ArrayList<String> timetableId, timetableSub, timetableBeg, timetableEnd, timetableRoom, timetableTeach, timetableDay;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);
        addBtn = view.findViewById(R.id.addBtn);
        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);

        addBtn.setOnClickListener(this);
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thu.setOnClickListener(this);
        fri.setOnClickListener(this);

        dbHelper = new DbHelper(getActivity());
        timetableId = new ArrayList<>();
        timetableSub = new ArrayList<>();
        timetableBeg = new ArrayList<>();
        timetableEnd= new ArrayList<>();
        timetableRoom = new ArrayList<>();
        timetableTeach = new ArrayList<>();
        timetableDay = new ArrayList<>();
        storeDatainArrays();
        showData(view);
        return view;
    }

    private void showData(View view) {

        for (int i = 0; i < timetableId.toArray().length; i++) {
            String day = timetableDay.get(i);
            String sub = timetableSub.get(i);
            String beg = timetableBeg.get(i);
            String end = timetableEnd.get(i);
            String room = timetableRoom.get(i);
            String teach = timetableTeach.get(i);
            String id = timetableId.get(i);
            String text = timetableSub.get(i) + "\n" + timetableBeg.get(i) + " - " +
                    timetableEnd.get(i) + "\n" + timetableRoom.get(i) + "\n" + timetableTeach.get(i);
            TextView newLesson = new TextView(view.getContext());
            newLesson.setText(text);
            switch (timetableDay.get(i)){
                case "Monday":
                    mon.addView(newLesson);
                    break;
                case "Tuesday":
                    tue.addView(newLesson);

                    break;
                case "Wednesday":
                    wed.addView(newLesson);

                    break;
                case "Thursday":
                    thu.addView(newLesson);

                    break;
                case "Friday":
                    fri.addView(newLesson);
                    break;
                default:
                    break;
            }
            newLesson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdateLesson upAlert = new UpdateLesson(day,sub,beg, end, room, teach, id);
                    upAlert.show(getParentFragmentManager(), "test2");
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(addBtn)){
            AddLesson alert = new AddLesson();
            alert.show(getParentFragmentManager(), "test");
        }

    }
    void storeDatainArrays(){
        Cursor cursor = dbHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                timetableId.add(cursor.getString(0));
                timetableSub.add(cursor.getString(1));
                timetableBeg.add(cursor.getString(2));
                timetableEnd.add(cursor.getString(3));
                timetableDay.add(cursor.getString(4));
                timetableRoom.add(cursor.getString(5));
                timetableTeach.add(cursor.getString(6));

            }
        }
    }
}
