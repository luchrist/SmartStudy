package com.example.smartstudy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class UpdateLesson extends DialogFragment {
    EditText lesson, timeBegin, timeTo, room, teacher;
    Spinner day;
    String weekday, sub, beg, end, roomtext, teach, id;



    public UpdateLesson(String day, String sub, String beg, String end, String room, String teach, String id) {
        weekday = day;
        this.sub = sub;
        this.beg = beg;
        this.end = end;
        this.roomtext = room;
        this.teach = teach;
        this.id = id;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog, null);
        // Inflate and set the layout for the dialog
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        day = (Spinner) view.findViewById(R.id.day);
        lesson = (EditText) view.findViewById(R.id.lesson);
        room = (EditText) view.findViewById(R.id.room);
        timeBegin = (EditText) view.findViewById(R.id.timeBegin);
        timeTo = (EditText) view.findViewById(R.id.timeTo);
        teacher = (EditText) view.findViewById(R.id.teacher);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        day.setAdapter(adapter);

        day.setSelection(dayToInt(weekday));
        lesson.setText(sub);
        timeBegin.setText(beg);
        timeTo.setText(end);
        room.setText(roomtext);
        teacher.setText(teach);

        builder.setTitle("Update Lesson")
                // Pass null as the parent view because its going in the dialog layout
                .setView(view)
                .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        DbHelper dbHelper = new DbHelper(getActivity());
                        dbHelper.updateTimetableObject(id, lesson.getText().toString(),
                                timeBegin.getText().toString(), timeTo.getText().toString()
                                , day.getSelectedItem().toString(), room.getText().toString(), teacher.getText().toString());

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new TimetableFragment()).commit();
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DbHelper dbHelper = new DbHelper(getActivity());
                        dbHelper.deleteTimetableObject(id);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new TimetableFragment()).commit();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });



        // Create the AlertDialog object and return it
        return builder.create();
    }

    private int dayToInt(String weekday) {
        switch (weekday){
            case "Monday":
                return 0;
            case "Tuesday":
                return 1;
            case "Wednesday":
                return 2;
            case "Thursday":
                return 3;

            case "Friday":
                return 4;

        }
        return 0;

    }


}
