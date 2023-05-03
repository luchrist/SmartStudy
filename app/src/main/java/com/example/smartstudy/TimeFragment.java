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

import java.util.Calendar;
import java.util.Locale;

public class TimeFragment extends Fragment implements View.OnClickListener {

    int hour, minute;
    Button monTimePicker, tueTimePicker, wedTimePicker, thuTimePicker, friTimePicker, satTimePicker, sunTimePicker;
    DbTimeHelper dbTimeHelper;
    TimeObject mon, tue, wed, thu, fri, sat, sun;
    private DatePickerDialog date_picker_dialog;
    private Button date_button, save, exeptionTime;
    private DBExeptionHelper dbExeptionHelper;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        sp = getActivity().getSharedPreferences("SP", 0);
        editor = sp.edit();
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
        monTimePicker.setText(minutesToString(mon.getTime()));
        tueTimePicker.setText(minutesToString(tue.getTime()));
        wedTimePicker.setText(minutesToString(wed.getTime()));
        thuTimePicker.setText(minutesToString(thu.getTime()));
        friTimePicker.setText(minutesToString(fri.getTime()));
        satTimePicker.setText(minutesToString(sat.getTime()));
        sunTimePicker.setText(minutesToString(sun.getTime()));

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
                            dbTimeHelper.updateTimeObject(mon.getId(), mon.getDay(), stringToMinutes(monTimePicker.getText().toString()));
                            editor.putInt(mon.getDay(),stringToMinutes(monTimePicker.getText().toString()) );
                        } else if (tueTimePicker.equals(view)) {
                            tueTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            editor.putInt(tue.getDay(),stringToMinutes(tueTimePicker.getText().toString()) );
                            dbTimeHelper.updateTimeObject(tue.getId(), tue.getDay(), stringToMinutes(tueTimePicker.getText().toString()));
                        } else if (view == wedTimePicker) {
                            wedTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(wed.getId(), wed.getDay(), stringToMinutes(wedTimePicker.getText().toString()));
                        } else if (view == thuTimePicker) {
                            thuTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(thu.getId(), thu.getDay(), stringToMinutes(thuTimePicker.getText().toString()));
                        } else if (view == friTimePicker) {
                            friTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(fri.getId(), fri.getDay(), stringToMinutes(friTimePicker.getText().toString()));
                        } else if (view == satTimePicker) {
                            satTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(sat.getId(), sat.getDay(), stringToMinutes(satTimePicker.getText().toString()));
                        } else if (view == sunTimePicker) {
                            sunTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            dbTimeHelper.updateTimeObject(sun.getId(), sun.getDay(), stringToMinutes(sunTimePicker.getText().toString()));
                            editor.putInt(sun.getDay(),stringToMinutes(sunTimePicker.getText().toString()) );
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


