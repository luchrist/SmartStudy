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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.TodosAdapter;
import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Todo;
import com.example.smartstudy.utilities.TodoSelectListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditExam extends DialogFragment implements TodoSelectListener {
    AppCompatImageButton addTodo;
    RecyclerView todosForEventView;
    AppCompatImageView prevEvent, nextEvent;
    Button newEmptyEvent;
    EditText inputTodo, inputTime, subject, type, dueDay, startDate;
    RatingBar volume;
    Spinner colour;
    LocalDate selectedDate;
    DBEventHelper dbHelper;
    DBTodoHelper dbTodoHelper;
    List<Event> events, todaysEvents;
    List<Todo> allTodos, todosForEvent;
    Event shownEvent;
    ProgressBar progressBar; // zeigt prozentual den Progress an der aber absolut in STunden abgespeichert wird
    int progress;
    private boolean emptyEvent;
    private TodosAdapter todosAdapter;

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
        prevEvent = view.findViewById(R.id.prevEvent);
        prevEvent.setVisibility(View.GONE);
        nextEvent = view.findViewById(R.id.nextEvent);
        newEmptyEvent = view.findViewById(R.id.newEvent);
        inputTodo = view.findViewById(R.id.inputTodoEdit);
        inputTime = view.findViewById(R.id.inputTimeEdit);
        colour = view.findViewById(R.id.colourEdit);
        subject = view.findViewById(R.id.subject_edit);
        type = view.findViewById(R.id.type_edit);
        dueDay = view.findViewById(R.id.dueDay_edit);
        startDate = view.findViewById(R.id.startDateEdit);
        volume = view.findViewById(R.id.volume_edit);
        progressBar = view.findViewById(R.id.progressEdit);
        todosForEventView = view.findViewById(R.id.todosRecyclerView);
        todosAdapter = new TodosAdapter(todosForEvent, this);
        todosForEventView.setAdapter(todosAdapter);

        events = new ArrayList<>();
        todaysEvents = new ArrayList<>();
        allTodos = new ArrayList<>();

        dbHelper = new DBEventHelper(EditExam.this.getContext());
        dbTodoHelper = new DBTodoHelper(EditExam.this.getContext());


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.colours, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        colour.setAdapter(adapter);
        loadData();
        showDayData();
        setListeners();

        builder.setTitle("Edit Exam")
                // Pass null as the parent view because its going in the dialog layout
                .setView(view)
                .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        shownEvent.setSubject(subject.getText().toString());
                        shownEvent.setType(type.getText().toString());
                        shownEvent.setEndDate(dueDay.getText().toString());
                        shownEvent.setStartDate(startDate.getText().toString());
                        shownEvent.setColor(colour.getSelectedItem().toString());
                        shownEvent.setVolume((int)volume.getRating()*2);

                        if (shownEvent.getStartDate().equals("")){
                            shownEvent.setStartDate(LocalDate.now().toString());
                        }
                        if(shownEvent.getEndDate().equals("")){
                            Toast.makeText(getContext(), "Set an Due Day!", Toast.LENGTH_SHORT).show();
                        }else{
                            for (int i = 0; i < todosForEvent.size(); i++){
                                boolean newTodo = true;
                                for (int j = 0; j < allTodos.size(); j++){
                                    if (Objects.equals(todosForEvent.get(i).getId(), allTodos.get(j).getId())){
                                        dbTodoHelper.updateTodoObject(todosForEvent.get(i));
                                        newTodo = false;
                                        break;
                                    }
                                }
                                if(newTodo){
                                    dbTodoHelper.addTodoObject(todosForEvent.get(i));
                                }
                            }
                            if(emptyEvent) {
                                dbHelper.addEventObject(shownEvent);
                            } else {
                                dbHelper.updateEventObject(shownEvent);
                            }
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    new PlanFragment()).commit();
                        }
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

    private ArrayList<String> splitString(String text, char splitSymbol){
        ArrayList<String> ziffern = new ArrayList<>();
        int c = 0;
        for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) == splitSymbol){
                String part = "";
                for (int j= 1; j <= c; j++){
                    part+= text.charAt(i-j);
                }
                ziffern.add(part);
                ziffern.add(String.valueOf(text.charAt(i+1)));
                return ziffern;
            }else{
                c++;
            }
        }
        return ziffern;
    }

    private void showDayData() {
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).getEndDate().equals(selectedDate.toString())){
                todaysEvents.add(events.get(i));
            }
        }
        if(!todaysEvents.isEmpty()) {
            shownEvent = todaysEvents.get(0);
            showEvent();
        } else {
            shownEvent = new Event();
            emptyEvent = true;
        }
    }

    private void showEvent() {
        subject.setText(shownEvent.getSubject());
        type.setText(shownEvent.getType());

        volume.setRating(shownEvent.getVolume()/2);
        startDate.setText(shownEvent.getStartDate());
        dueDay.setText(shownEvent.getEndDate());
        progress = shownEvent.getProgress(); //bereits gelernte Stunden
        float gesStd = shownEvent.getAbsolutMinutes();
        String col = shownEvent.getColor();
        setColor(col);
        for(int j = 0; j < allTodos.size(); j++) {
            if(allTodos.get(j).getCollection().equals(shownEvent.getId())) {
                todosForEvent.add(allTodos.get(j));
                todosAdapter.notifyItemInserted(todosForEvent.size()-1);
            }
        }
        float faktor = 100 / gesStd;
        progressBar.setProgress((int) (progress *faktor));
    }

    private String minutesToString(String s) {
        int minutes = Integer.parseInt(s);
        int hours = minutes/60;
        int mins = minutes%60;
        return String.valueOf(hours + "." + mins);
    }

    private void loadData() {
        Cursor cursor = dbHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(4),cursor.getString(5), cursor.getString(6), cursor.getInt(3), cursor.getInt(7));
                events.add(event);
            }
        }
        cursor = dbTodoHelper.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()) {
                Todo todo = new Todo(cursor.getString(3), cursor.getString(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(4));
                allTodos.add(todo);
            }
        }
    }

    private void setListeners() {
        addTodo.setOnClickListener(v -> {
            try {
                int minutesEst = Integer.parseInt(inputTime.getText().toString());
                Todo todo = new Todo(shownEvent.getId(), inputTodo.getText().toString(), minutesEst,0);
                todosForEvent.add(todo);
                todosAdapter.notifyItemInserted(todosForEvent.size()-1);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter the estimated minutes only as a number", Toast.LENGTH_SHORT).show();
            }
        });

        prevEvent.setOnClickListener(v -> {
            if (shownEvent != null) {
                int currentIndex = todaysEvents.indexOf(shownEvent);
                if(currentIndex > 0) {
                    shownEvent = todaysEvents.get(currentIndex - 1);
                    showEvent();
                    if(currentIndex == 1) {
                        prevEvent.setVisibility(View.INVISIBLE);
                    }
                    nextEvent.setVisibility(View.VISIBLE);
                }
            }
        });
        nextEvent.setOnClickListener(v -> {
            if(shownEvent != null) {
                int currentIndex = todaysEvents.indexOf(shownEvent);
                if(currentIndex < todaysEvents.size()-1) {
                    shownEvent = todaysEvents.get(currentIndex + 1);
                    showEvent();
                    if(currentIndex == todaysEvents.size()-2) {
                        nextEvent.setVisibility(View.INVISIBLE);
                    }
                    prevEvent.setVisibility(View.VISIBLE);
                }
            }
        });
        newEmptyEvent.setOnClickListener(v -> {
            prevEvent.setVisibility(View.INVISIBLE);
            nextEvent.setVisibility(View.INVISIBLE);
            shownEvent = new Event();
            emptyEvent = true;
            emptyFormular();
        });
    }

    private void emptyFormular() {

    }

    private void setColor(String col) {
        int co;
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
    }

    @Override
    public void onTodoSelected(Todo todo) {
        if (todo.getChecked() == 0) {
            todo.setChecked(1);
            int index = todosForEvent.indexOf(todo);
            todosAdapter.notifyItemChanged(index);
            double faktor = shownEvent.getAbsolutMinutes() / 100.00;
            int progress = (int) (todo.getTime() / faktor);
            progressBar.setProgress(progressBar.getProgress() + progress);
            shownEvent.setProgress(shownEvent.getProgress() + progress);
            shownEvent.setTodos(todosForEvent);
            shownEvent.setRemainingMinutes(shownEvent.getRemainingMinutes() - todo.getTime());
            dbTodoHelper.updateTodoObject(todo);
            dbHelper.updateEventObject(shownEvent);
        }
    }
}

