package com.example.smartstudy;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class Timefragment extends Fragment implements View.OnClickListener {

    int hour, minute;
    Button monTimePicker, tueTimePicker, wedTimePicker, thuTimePicker, friTimePicker, satTimePicker,sunTimePicker;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);

        monTimePicker = view.findViewById(R.id.monTimePicker);
        tueTimePicker = view.findViewById(R.id.tueTimePicker);
        wedTimePicker = view.findViewById(R.id.wedTimePicker);
        thuTimePicker = view.findViewById(R.id.thuTimePicker);
        friTimePicker = view.findViewById(R.id.friTimePicker);
        satTimePicker = view.findViewById(R.id.satTimePicker);
        sunTimePicker = view.findViewById(R.id.sunTimePicker);
        monTimePicker.setOnClickListener(this);
        tueTimePicker.setOnClickListener(this);
        wedTimePicker.setOnClickListener(this);
        thuTimePicker.setOnClickListener(this);
        friTimePicker.setOnClickListener(this);
        satTimePicker.setOnClickListener(this);
        sunTimePicker.setOnClickListener(this);



        return view;
    }


    @Override
    public void onClick(View view) {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                if (monTimePicker.equals(view)) {
                    monTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                } else if (tueTimePicker.equals(view)) {
                    tueTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }else if(view == wedTimePicker){
                    wedTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }else if (view == thuTimePicker){
                    thuTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }else if(view == friTimePicker){
                    friTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }else if(view == satTimePicker){
                    satTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }else if(view == sunTimePicker){
                    sunTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }

            }
        };
        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), style, timeSetListener, hour, minute,true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }
}

