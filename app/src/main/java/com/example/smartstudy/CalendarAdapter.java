package com.example.smartstudy;

import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.time.Month;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<String> daysOfMonth;
    public final OnItemListener onItemListener;
    private DBEventHelper dbEventHelper;
    private ArrayList<String> col, end;
    private final Month month;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, Month month) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.month = month;
    }

    @NonNull
    @NotNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        dbEventHelper = new DBEventHelper(parent.getContext());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        loadData();
        for (int i = 0; i < end.size(); i++){

            String endDate = end.get(i);
            if(endDate != null && !endDate.isEmpty()) {
                String[] endDateSplittet = endDate.split("-");
                String month = endDateSplittet[1];
                month = getMonat(month);
                String showingMonth = String.valueOf(this.month);
                showingMonth = monthShorted(showingMonth);
                if (showingMonth.equalsIgnoreCase(month)){
                    String day = endDateSplittet[2];
                    day = transformDay(day);
                    if(day.equalsIgnoreCase(daysOfMonth.get(position))){
                        String color = col.get(i);
                        switch (color){
                            case "red":
                                holder.dayOfMonth.setTextColor(Color.RED);
                                break;
                            case "blue":
                                holder.dayOfMonth.setTextColor(Color.BLUE);
                                break;

                            case "green":
                                holder.dayOfMonth.setTextColor(Color.GREEN);
                                break;
                            case "yellow":
                                holder.dayOfMonth.setTextColor(Color.YELLOW);
                                break;
                            case "orange":
                                holder.dayOfMonth.setTextColor(Color.rgb(255,165,0));
                                break;
                            case "brown":
                                holder.dayOfMonth.setTextColor(Color.rgb(139,69,19));
                                break;
                            case "pink":
                                holder.dayOfMonth.setTextColor(Color.MAGENTA);
                                break;
                            case "purple":
                                holder.dayOfMonth.setTextColor(Color.rgb(128,0,128));
                                break;
                        }
                    }
                }
            }
        }

    }

    private String transformDay(String day) {
        if(day.charAt(0) == '0'){
            return String.valueOf(day.charAt(1));
        }else{
            return day;
        }
    }

    private String monthShorted(String showingMonth) {
        String s;
        switch (showingMonth) {
            case "JANUARY":
                s = "JAN";
                break;
            case "FEBRUARY":
                s = "FEB";
                break;
            case "MARCH":
                s = "MAR";
                break;
            case "APRIL":
                s = "APR";
                break;
            case "MAY":
                s = "MAY";
                break;
            case "JUNE":
                s = "JUN";
                break;
            case "JULY":
                s = "JUL";
                break;
            case "AUGUST":
                s = "AUG";
                break;
            case "SEPTEMBER":
                s = "SEP";
                break;
            case "OCTOBER":
                s = "OCT";
                break;
            case "NOVEMBER":
                s = "NOV";
                break;
            case "DECEMBER":
                s = "DEC";
                break;
            default:
                s = "JAN";
                break;
        }
        return s;
    }

    private void loadData() {
        col = new ArrayList<>();
        end = new ArrayList<>();

        Cursor cursor1 = dbEventHelper.readAllData();
        if (cursor1.getCount() == 0){
        }else {
            while (cursor1.moveToNext()) {
                end.add(cursor1.getString(5));
                col.add(cursor1.getString(6));
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }
    private String getMonat(String datePart) {
        String s;
        switch (datePart) {
            case "01":
                s = "JAN";
                break;
            case "02":
                s = "FEB";
                break;
            case "03":
                s = "MAR";
                break;
            case "04":
                s = "APR";
                break;
            case "05":
                s = "MAY";
                break;
            case "06":
                s = "JUN";
                break;
            case "07":
                s = "JUL";
                break;
            case "08":
                s = "AUG";
                break;
            case "09":
                s = "SEP";
                break;
            case "10":
                s = "OCT";
                break;
            case "11":
                s = "NOV";
                break;
            case "12":
                s = "DEC";
                break;
            default:
                s = "JAN";
                break;
        }
        return s;
    }

    public interface OnItemListener{
        void onItemClick(int position, String dayText);
    }
}
