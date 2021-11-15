package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TimetableFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton addBtn;
    DbHelper dbHelper;
    ArrayList<String> timetableId, timetableSub, timetableBeg, timetableEnd, timetableRoom, timetableTeach, timetableDay;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);
        addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);

        dbHelper = new DbHelper(getActivity());
        timetableId = new ArrayList<>();
        timetableSub = new ArrayList<>();
        timetableBeg = new ArrayList<>();
        timetableEnd= new ArrayList<>();
        timetableRoom = new ArrayList<>();
        timetableTeach = new ArrayList<>();
        timetableDay = new ArrayList<>();
        storeDatainArrays();

        return view;
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
