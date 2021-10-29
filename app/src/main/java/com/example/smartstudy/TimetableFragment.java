package com.example.smartstudy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TimetableFragment extends Fragment implements View.OnClickListener {

    ImageButton addBtn;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);
        addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(addBtn)){
            AddLesson alert = new AddLesson();
            alert.show(getParentFragmentManager(), "test");
        }

    }
}
