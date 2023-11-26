package com.example.smartstudy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.smartstudy.utilities.Util;

public class AddLesson extends DialogFragment{

    EditText lesson, timeBegin, timeTo, room, teacher;
    Spinner day, colour;
    private final String dayOfWeek;

    public AddLesson(String day) {
        dayOfWeek = day;
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
            colour = (Spinner) view.findViewById(R.id.colour);
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
            day.setSelection(adapter.getPosition(dayOfWeek));
            ArrayAdapter<CharSequence> adapterCol = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.colours, android.R.layout.simple_spinner_item);
            adapterCol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colour.setAdapter(adapterCol);
            builder.setTitle("Add a Lesson")
                    // Pass null as the parent view because its going in the dialog layout
                    .setView(view)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DbHelper dbHelper = new DbHelper(AddLesson.this.getActivity());
                            String dayOfSpinner = Util.getDayOfSpinner(day.getSelectedItem().toString());
                            dbHelper.addTimeTableObject(
                                    lesson.getText().toString(),
                                    timeBegin.getText().toString(),
                                    timeTo.getText().toString(),
                                    dayOfSpinner,
                                    room.getText().toString(),
                                    teacher.getText().toString(),
                                    Util.getColorOfSpinner(colour.getSelectedItemPosition()));
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    new TimetableFragment(dayOfSpinner)).commit();
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


}
