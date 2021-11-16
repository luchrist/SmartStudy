package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView title;
    NavigationView navigationView;
    TextView homeTitle;
    Spinner spinner;
    LinearLayout lessonsList;
    DbHelper dbHelper;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.main_fragment, container, false);
        title = getActivity().findViewById(R.id.variabel_text);
        homeTitle = view.findViewById(R.id.timetable_title);
        navigationView = getActivity().findViewById(R.id.nav_view);
        spinner = view.findViewById(R.id.spinner);
        lessonsList = view.findViewById(R.id.lessonslist);

        dbHelper = new DbHelper(getActivity());


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        Cursor cursor = dbHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()) {
                String text = "";
                switch (day) {
                    case Calendar.MONDAY:
                        spinner.setSelection(0);
                        if (cursor.getString(4).equals("Monday")) {
                            text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                    cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                    cursor.getString(6);
                        }
                        // Current day is Monday
                        break;
                    case Calendar.TUESDAY:
                        spinner.setSelection(1);
                        if (cursor.getString(4).equals("Tuesday")) {
                            text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                    cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                    cursor.getString(6);
                        }
                        break;
                    case Calendar.WEDNESDAY:
                        spinner.setSelection(2);
                        if (cursor.getString(4).equals("Wednesday")) {
                            text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                    cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                    cursor.getString(6);
                        }
                        break;
                    case Calendar.THURSDAY:
                        spinner.setSelection(3);
                        if (cursor.getString(4).equals("Thursday")) {
                            text = cursor.getString(1) + "\n" + cursor.getString(2) + " - " +
                                    cursor.getString(3) + "\n" + cursor.getString(5) + "\n" +
                                    cursor.getString(6);
                        }
                        break;
                    case Calendar.FRIDAY:
                        spinner.setSelection(4);
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
                lessonsList.addView(newLesson);

            }
        }

        lessonsList.setOnClickListener(this);
        homeTitle.setOnClickListener(this);
        //spinner.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(lessonsList) || v.equals(homeTitle)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new TimetableFragment()).commit();
            title.setText("Timetable");
            navigationView.setCheckedItem(R.id.nav_timetable);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //System.out.println(adapterView);
        //System.out.println(view);
        //System.out.println(i);
   //     System.out.println(l);
    }
}

