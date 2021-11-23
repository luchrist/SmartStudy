package com.example.smartstudy;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class Timefragment extends Fragment implements View.OnClickListener {

    int hour, minute;
    Button monTimePicker, tueTimePicker, wedTimePicker, thuTimePicker, friTimePicker, satTimePicker, sunTimePicker;
    DbTimeHelper dbTimeHelper;
    TimeObject mon, tue, wed, thu, fri, sat, sun;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);

        dbTimeHelper = new DbTimeHelper(getContext());
        monTimePicker = view.findViewById(R.id.monTimePicker);
        tueTimePicker = view.findViewById(R.id.tueTimePicker);
        wedTimePicker = view.findViewById(R.id.wedTimePicker);
        thuTimePicker = view.findViewById(R.id.thuTimePicker);
        friTimePicker = view.findViewById(R.id.friTimePicker);
        satTimePicker = view.findViewById(R.id.satTimePicker);
        sunTimePicker = view.findViewById(R.id.sunTimePicker);
        storeDatainObjects();
        monTimePicker.setText(mon.getTime());
        tueTimePicker.setText(tue.getTime());
        wedTimePicker.setText(wed.getTime());
        thuTimePicker.setText(thu.getTime());
        friTimePicker.setText(fri.getTime());
        satTimePicker.setText(sat.getTime());
        sunTimePicker.setText(sun.getTime());

        monTimePicker.setOnClickListener(this);
        tueTimePicker.setOnClickListener(this);
        wedTimePicker.setOnClickListener(this);
        thuTimePicker.setOnClickListener(this);
        friTimePicker.setOnClickListener(this);
        satTimePicker.setOnClickListener(this);
        sunTimePicker.setOnClickListener(this);


        return view;
    }

    private void storeDatainObjects() {
        Cursor cursor = dbTimeHelper.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                switch(cursor.getString(1)){
                    case "Monday":
                        mon = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;
                    case "Tuesday":
                        tue = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;
                    case "Wednesday":
                        wed = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;
                    case "Thursday":
                        thu = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;
                    case "Friday":
                        fri = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;
                    case "Saturday":
                        sat = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;
                    case "Sunday":
                        sun = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                        break;


                }
            }
        }
    }


            @Override
            public void onClick (View view){
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour;
                        minute = selectedMinute;
                        if (monTimePicker.equals(view)) {
                            monTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(mon.getId(), mon.getDay(), monTimePicker.getText().toString());
                        } else if (tueTimePicker.equals(view)) {
                            tueTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(tue.getId(), tue.getDay(), tueTimePicker.getText().toString());
                        } else if (view == wedTimePicker) {
                            wedTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(wed.getId(), wed.getDay(), wedTimePicker.getText().toString());
                        } else if (view == thuTimePicker) {
                            thuTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(thu.getId(), thu.getDay(), thuTimePicker.getText().toString());
                        } else if (view == friTimePicker) {
                            friTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(fri.getId(), fri.getDay(), friTimePicker.getText().toString());
                        } else if (view == satTimePicker) {
                            satTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(sat.getId(), sat.getDay(), satTimePicker.getText().toString());
                        } else if (view == sunTimePicker) {
                            sunTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(sun.getId(), sun.getDay(), sunTimePicker.getText().toString());
                        }

                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), style, timeSetListener, 00, 00, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

            }
        }

