package com.example.smartstudy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.ArrayList;

public class AddExam extends DialogFragment implements View.OnClickListener {
    Button addTodo;
    LinearLayout todos, times;
    EditText inputTodo, subject, type, dueDay, startDate;
    RatingBar volume;
    Spinner colour, inputTime;
    ArrayList<String> todoList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();
    LocalDate dueDate;
    DBExamHelper dbExamHelper;
    LocalDate today;

    public AddExam(LocalDate selectedDate) {
        dueDate = selectedDate;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.exam_dialog, null);
        // Inflate and set the layout for the dialog
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        addTodo = view.findViewById(R.id.addToDoEdit);
        todos = view.findViewById(R.id.todosEdit);
        times = view.findViewById(R.id.timesEdit);
        inputTodo = view.findViewById(R.id.inputTodoEdit);
        inputTime = view.findViewById(R.id.inputTimeEdit);
        colour = view.findViewById(R.id.colourEdit);
        subject = view.findViewById(R.id.subject_edit);
        type = view.findViewById(R.id.type_edit);
        dueDay = view.findViewById(R.id.dueDay_edit);
        startDate = view.findViewById(R.id.startDateEdit);
        volume = view.findViewById(R.id.volume_edit);
        addTodo.setOnClickListener(this);
        dueDay.setText(dueDate.toString());
        dbExamHelper = new DBExamHelper(getContext());
        today = LocalDate.now();
        startDate.setText(today.toString());

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.colours, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        colour.setAdapter(adapter);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTimes = ArrayAdapter.createFromResource(this.getContext(),
                R.array.times, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        inputTime.setAdapter(adapterTimes);



        builder.setTitle("Add a Exam")
                // Pass null as the parent view because its going in the dialog layout
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String sub, typ, enddate, startdate, col,key;
                        float vol;

                        sub = subject.getText().toString();
                        typ = type.getText().toString();
                        enddate = dueDay.getText().toString();
                        startdate = startDate.getText().toString();
                        col = colour.getSelectedItem().toString();
                        key = sub+typ;
                        vol = volume.getRating();
                        int volInt = (int) (vol *2);



                        Exam exam = new Exam("",sub, typ,enddate,startdate,col,volInt, 0);

                        DBTodoHelper dbTodoHelper = new DBTodoHelper(AddExam.this.getContext());
                        for (int i  = 0; i < todoList.size(); i++){
                            Todo tod = new Todo(key, "", todoList.get(i), stringToMinutes(timeList.get(i)), 0);
                            dbTodoHelper.addTodoObject(tod);
                        }

                        dbExamHelper.addExamObject(exam);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,

                                new Plan()).commit();


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

    private int stringToMinutes(String time) {
        String[] timeSplittet = time.split(":");
        int hours = Integer.parseInt(timeSplittet[0]);
        int mins = Integer.parseInt(timeSplittet[1]);
        return  hours*60 + mins;
    }

    @Override
    public void onClick(View view) {
        TextView newTodo = new TextView(todos.getContext());
        newTodo.setText(inputTodo.getText());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8,0,0 ,0 );
        newTodo.setLayoutParams(lp);
        todos.addView(newTodo);

        TextView newEstimated = new TextView(times.getContext());
        newEstimated.setText(inputTime.getSelectedItem().toString());
        times.addView(newEstimated);

        todoList.add(inputTodo.getText().toString());
        timeList.add(inputTime.getSelectedItem().toString());

    }
}
