package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Plan extends Fragment implements CalendarAdapter.OnItemListener, View.OnClickListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    Button prev, nex;
    ImageButton editBtn, detailBtn;
    private boolean editable;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_plan, container, false);
        initWidgets(view);
        selectedDate = LocalDate.now();
        setMonthView();
        prev = view.findViewById(R.id.prevbtn);
        prev.setOnClickListener(this);
        nex = view.findViewById(R.id.nextbtn);
        nex.setOnClickListener(this);
        editBtn = view.findViewById(R.id.editbtn);
        editBtn.setOnClickListener(this);
        detailBtn = view.findViewById(R.id.detailbtn);
        detailBtn.setOnClickListener(this);
        editable = true;

        return view;
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = dayInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, selectedDate.getMonth());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> dayInMonthArray() {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++){
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek){
                daysInMonthArray.add("");
            }else{
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {


        if(!dayText.equals("")){
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            long daysToAdd = Integer.parseInt(dayText)-selectedDate.getDayOfMonth();
            selectedDate = selectedDate.plusDays(daysToAdd);
            if (editable){

                AddExam alert = new AddExam(selectedDate);
                alert.show(getParentFragmentManager(), "test");
            }else{
                EditExam editExam = new EditExam((selectedDate));
                editExam.show(getParentFragmentManager(), "");
            }

        }
    }

    @Override
    public void onClick(View view) {

        if(view.equals(prev)){

            previousMonthAction(prev);
        } else if (view.equals(nex)) {

            nextMonthAction(nex);
        }else if (view.equals(editBtn)){
            editable = true;
            Toast.makeText(getContext(), "Plan is now in edit mode!", Toast.LENGTH_SHORT).show();
        }else if(view.equals(detailBtn)){
            editable = false;
            Toast.makeText(getContext(), "Plan is now in view mode!", Toast.LENGTH_SHORT).show();
        }

    }

}