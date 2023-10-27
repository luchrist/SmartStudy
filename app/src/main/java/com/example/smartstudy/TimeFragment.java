package com.example.smartstudy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartstudy.utilities.PreferenceManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class TimeFragment extends Fragment implements View.OnClickListener {

    int hour, minute;
    Button monTimePicker, tueTimePicker, wedTimePicker, thuTimePicker, friTimePicker, satTimePicker, sunTimePicker;
    DbTimeHelper dbTimeHelper;
    TimeObject mon, tue, wed, thu, fri, sat, sun;
    private DatePickerDialog date_picker_dialog;
    private Button date_button, save, exeptionTime;
    private DBExeptionHelper dbExeptionHelper;
    private PreferenceManager preferenceManager;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        sp = getActivity().getSharedPreferences("SP", 0);
        editor = sp.edit();
        preferenceManager = new PreferenceManager(getContext());
        dbTimeHelper = new DbTimeHelper(getContext());
        dbExeptionHelper = new DBExeptionHelper(getContext());
        monTimePicker = view.findViewById(R.id.monTimePicker);
        tueTimePicker = view.findViewById(R.id.tueTimePicker);
        wedTimePicker = view.findViewById(R.id.wedTimePicker);
        thuTimePicker = view.findViewById(R.id.thuTimePicker);
        friTimePicker = view.findViewById(R.id.friTimePicker);
        satTimePicker = view.findViewById(R.id.satTimePicker);
        sunTimePicker = view.findViewById(R.id.sunTimePicker);
        storeDatainObjects();
        if(mon != null){
            monTimePicker.setText(minutesToString(mon.getTime()));
        }
        if(tue != null){
            tueTimePicker.setText(minutesToString(tue.getTime()));
        }
        if(wed != null){
            wedTimePicker.setText(minutesToString(wed.getTime()));
        }
        if(thu != null){
            thuTimePicker.setText(minutesToString(thu.getTime()));
        }
        if(fri != null){
            friTimePicker.setText(minutesToString(fri.getTime()));
        }
        if(sat != null){
            satTimePicker.setText(minutesToString(sat.getTime()));
        }
        if(sun != null){
            sunTimePicker.setText(minutesToString(sun.getTime()));
        }

        //calendarView.s

        monTimePicker.setOnClickListener(this);
        tueTimePicker.setOnClickListener(this);
        wedTimePicker.setOnClickListener(this);
        thuTimePicker.setOnClickListener(this);
        friTimePicker.setOnClickListener(this);
        satTimePicker.setOnClickListener(this);
        sunTimePicker.setOnClickListener(this);


        initDatePicker();
        date_button = view.findViewById(R.id.datePickerBtn);
        date_button.setText(getTodaysDate());
        date_button.setOnClickListener(this);

        save = view.findViewById(R.id.saveException);
        save.setOnClickListener(this);

        exeptionTime = view.findViewById(R.id.exceptiontime);
        exeptionTime.setOnClickListener(this);


        return view;
    }

    private String minutesToString(int time) {
        int hours = time/60;
        int minutes = time%60;
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);

        if(hours < 10){
            h = "0" + h;
        }
        if(minutes < 10){
            m = "0" + m;
        }

        return  h +":"+ m;
    }


    //get todays date as a string
    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        month++;

        return makeDateString(year, month, day);
    }

    //open datepickerdialog for user when date is clicked
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String date = makeDateString(year, month, day);
                date_button.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        date_picker_dialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);

    }

    //european date format
    private String makeDateString(int year, int month, int day) {
        return day + "." + getMonthFormat(month) + "." + year;
    }
    private void storeDatainObjects() {
        Cursor cursor = dbTimeHelper.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                switch(cursor.getString(1)){
                    case "Monday":
                        mon = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        break;
                    case "Tuesday":
                        tue = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        break;
                    case "Wednesday":
                        wed = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        break;
                    case "Thursday":
                        thu = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        break;
                    case "Friday":
                        fri = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        break;
                    case "Saturday":
                        sat = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        break;
                    case "Sunday":
                        sun = new TimeObject(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
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
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                                updateRemainingTime(monTimePicker, mon);
                            }
                            if(mon == null){
                                dbTimeHelper.addTimeObject("Monday", stringToMinutes(monTimePicker.getText().toString()));
                            } else {
                                dbTimeHelper.updateTimeObject(mon.getId(), mon.getDay(), stringToMinutes(monTimePicker.getText().toString()));
                            }
                        } else if (tueTimePicker.equals(view)) {
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                                updateRemainingTime(tueTimePicker, tue);
                            }
                            tueTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            if (tue == null) {
                                dbTimeHelper.addTimeObject("Tuesday", stringToMinutes(tueTimePicker.getText().toString()));
                            } else {
                                dbTimeHelper.updateTimeObject(tue.getId(), tue.getDay(), stringToMinutes(tueTimePicker.getText().toString()));
                            }
                        } else if (view == wedTimePicker) {
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                                updateRemainingTime(wedTimePicker, wed);
                            }
                            wedTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            if (wed == null) {
                                dbTimeHelper.addTimeObject("Wednesday", stringToMinutes(wedTimePicker.getText().toString()));
                            } else {
                                dbTimeHelper.updateTimeObject(wed.getId(), wed.getDay(), stringToMinutes(wedTimePicker.getText().toString()));
                            }
                        } else if (view == thuTimePicker) {
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                                updateRemainingTime(thuTimePicker, thu);
                            }
                            thuTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            if (thu == null) {
                                dbTimeHelper.addTimeObject("Thursday", stringToMinutes(thuTimePicker.getText().toString()));
                            } else {
                                dbTimeHelper.updateTimeObject(thu.getId(), thu.getDay(), stringToMinutes(thuTimePicker.getText().toString()));
                            }
                        } else if (view == friTimePicker) {
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                                updateRemainingTime(friTimePicker, fri);
                            }
                            friTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            if (fri == null) {
                                dbTimeHelper.addTimeObject("Friday", stringToMinutes(friTimePicker.getText().toString()));
                            } else {
                                dbTimeHelper.updateTimeObject(fri.getId(), fri.getDay(), stringToMinutes(friTimePicker.getText().toString()));
                            }
                        } else if (view == satTimePicker) {
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                                updateRemainingTime(satTimePicker, sat);
                            }
                            satTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            if (sat == null) {
                                dbTimeHelper.addTimeObject("Saturday", stringToMinutes(satTimePicker.getText().toString()));
                            }else {
                                dbTimeHelper.updateTimeObject(sat.getId(), sat.getDay(), stringToMinutes(satTimePicker.getText().toString()));
                            }
                        } else if (view == sunTimePicker) {
                            if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                                updateRemainingTime(sunTimePicker, sun);
                            }
                            sunTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            if (sun == null) {
                                dbTimeHelper.addTimeObject("Sunday", stringToMinutes(sunTimePicker.getText().toString()));
                            } else {
                                dbTimeHelper.updateTimeObject(sun.getId(), sun.getDay(), stringToMinutes(sunTimePicker.getText().toString()));
                            }
                        }else if (view == exeptionTime) {
                            exeptionTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        }
                        editor.commit();
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), style, timeSetListener, 00, 00, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

                if (view.equals(date_button)) {
                    date_picker_dialog.show();
                    exeptionTime.setText(String.format(Locale.getDefault(), "%02d:%02d", 00, 00));

                }else if(view.equals(save)){
                    dbExeptionHelper.addExeptionObject(date_button.getText().toString(), timeToMinutes());
                    Toast.makeText(getContext(), "Saved Succesfully", Toast.LENGTH_SHORT).show();
                }


            }

    private void updateRemainingTime(Button dayTimePicker, TimeObject day) {
        int remainingTimeToday = preferenceManager.getInt("remainingTimeToday");
        int newRemainingTimeToday;
        if(day == null) {
            newRemainingTimeToday = remainingTimeToday + stringToMinutes(dayTimePicker.getText().toString());
        } else {
            newRemainingTimeToday = remainingTimeToday + stringToMinutes(dayTimePicker.getText().toString()) - day.getTime();
        }
        preferenceManager.putInt("remainingTimeToday", newRemainingTimeToday);
    }

    private int stringToMinutes(String time) {
        String[] timeSplittet = time.split(":");
        int hours = Integer.parseInt(timeSplittet[0]);
        int mins = Integer.parseInt(timeSplittet[1]);
        return  hours*60 + mins;
    }

    private int timeToMinutes() {
        String time = exeptionTime.getText().toString();
        String[] timeSplittet = time.split(":");
        int hours = Integer.parseInt(timeSplittet[0]);
        int mins = Integer.parseInt(timeSplittet[1]);
        return  hours*60 + mins;
    }

    //integer month transform into String
    public String getMonthFormat(int month) {
        String s;
        switch (month) {
            case 2:
                s = "Feb";
                break;
            case 3:
                s = "Mar";
                break;
            case 4:
                s = "Apr";
                break;
            case 5:
                s = "May";
                break;
            case 6:
                s = "Jun";
                break;
            case 7:
                s = "Jul";
                break;
            case 8:
                s = "Aug";
                break;
            case 9:
                s = "Sep";
                break;
            case 10:
                s = "Oct";
                break;
            case 11:
                s = "Nov";
                break;
            case 12:
                s = "Dec";
                break;
            default:
                s = "Jan";
                break;
        }
        return s;
    }

}


