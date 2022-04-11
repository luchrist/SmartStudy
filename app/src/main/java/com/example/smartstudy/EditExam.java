package com.example.smartstudy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.ArrayList;

public class EditExam extends DialogFragment implements View.OnClickListener {
    Button addTodo;
    LinearLayout todos, times;
    EditText inputTodo, subject, type, dueDay, startDate;
    RatingBar volume;
    Spinner colour, inputTime;
    ArrayList<String> todoList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();
    LocalDate selectedDate;
    DBExamHelper dbHelper;
    DBTodoHelper dbTodoHelper;
    ArrayList<String> PlanId, PlanSub, PlanType, PlanBeg, PlanEnd, PlanCol, TodoId, TodoDo, TodoTi, TodoColec;
    ArrayList<Float> PlanVol;
    String id;

    public EditExam(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_exam, null);
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

        PlanId = new ArrayList<>();
        PlanSub = new ArrayList<>();
        PlanType= new ArrayList<>();
        PlanVol= new ArrayList<>();
        PlanBeg= new ArrayList<>();
        PlanEnd= new ArrayList<>();
        PlanCol= new ArrayList<>();
        TodoId= new ArrayList<>();
        TodoDo= new ArrayList<>();
        TodoTi= new ArrayList<>();
        TodoColec= new ArrayList<>();

        dbHelper = new DBExamHelper(EditExam.this.getContext());
        dbTodoHelper = new DBTodoHelper(EditExam.this.getContext());


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
        loadData();
        showDayData();




        builder.setTitle("Edit Exam")
                // Pass null as the parent view because its going in the dialog layout
                .setView(view)
                .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String sub, typ, enddate, startdate, col,key;
                        int vol;

                        sub = subject.getText().toString();
                        typ = type.getText().toString();
                        enddate = dueDay.getText().toString();
                        startdate = startDate.getText().toString();
                        col = colour.getSelectedItem().toString();
                        key = sub+typ;
                        vol = volume.getNumStars();



                        Exam exam = new Exam("",sub, typ,enddate,startdate,col,vol);


                        for (int i  = 0; i < todoList.size(); i++){
                            Todo tod = new Todo(key, "", todoList.get(i), timeList.get(i));
                            dbTodoHelper.addTodoObject(tod);
                        }

                        dbHelper.addExamObject(exam);
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

    private void showDayData() {
        for(int i = 0; i < PlanId.size(); i++){
            if(PlanEnd.get(i).equals(selectedDate.toString())){

                subject.setText(PlanSub.get(i));
                id = PlanId.get(i);
                type.setText(PlanType.get(i));
                String key = PlanSub.get(i)+PlanType.get(i);
                volume.setRating(PlanVol.get(i));
                startDate.setText(PlanBeg.get(i));
                dueDay.setText(PlanEnd.get(i));
                String col = PlanCol.get(i);
                int co = 0;
                switch (col){
                    case "red":
                        co = 0;
                        break;
                    case "blue":
                        co = 1;
                        break;
                    case "green":
                        co = 2;
                        break;
                    case "yellow":
                        co = 3;
                        break;
                    case "brown":
                        co = 4;
                        break;
                    case "orange":
                        co = 5;
                        break;
                    case "pink":
                        co = 6;
                        break;
                    case "purple":
                        co = 7;
                        break;
                    default:
                        co = 1;
                        break;

                }

                colour.setSelection(co);
                for(int j = 0; j < TodoColec.size(); j++){
                    if (TodoColec.get(j).equals(key)){
                        TextView newTodo = new TextView(todos.getContext());
                        newTodo.setText(TodoDo.get(j));
                        todos.addView(newTodo);
                        Todo delTodo = new Todo(key,TodoId.get(j),TodoDo.get(j), TodoTi.get(j));


                        TextView newEstimated = new TextView(times.getContext());
                        newEstimated.setText(TodoTi.get(j));
                        times.addView(newEstimated);
                        newTodo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbTodoHelper.deleteTodoObject(delTodo);
                                todos.removeView(newTodo);
                                times.removeView(newEstimated);
                            }
                        });
                        newEstimated.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dbTodoHelper.deleteTodoObject(delTodo);
                                todos.removeView(newTodo);
                                times.removeView(newEstimated);
                            }
                        });
                    }
                }

            }
        }
    }

    private void loadData() {
        Cursor cursor = dbHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()) {
                PlanId.add(cursor.getString(0));
                PlanSub.add(cursor.getString(1));
                PlanType.add(cursor.getString(2));
                PlanVol.add(cursor.getFloat(3));
                PlanBeg.add(cursor.getString(4));
                PlanEnd.add(cursor.getString(5));
                PlanCol.add(cursor.getString(6));

            }
        }
        cursor = dbTodoHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()) {
                TodoId.add(cursor.getString(0));
                TodoDo.add(cursor.getString(1));
                TodoTi.add(cursor.getString(2));
                TodoColec.add(cursor.getString(3));


            }
        }
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

