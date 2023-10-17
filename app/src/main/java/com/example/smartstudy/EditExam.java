package com.example.smartstudy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.adapters.TodosAdapter;
import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Group;
import com.example.smartstudy.models.Todo;
import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.TodoSelectListener;
import com.example.smartstudy.utilities.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditExam extends DialogFragment implements TodoSelectListener, DatePickerDialog.OnDateSetListener {
    AppCompatImageView addTodo;
    RecyclerView todosForEventView;
    AppCompatImageView prevEvent, nextEvent;
    Button newEmptyEvent;
    EditText inputTodo, inputTime, subject, type;
    TextView dueDay, startDate;
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

        events = new ArrayList<>();
        todaysEvents = new ArrayList<>();
        allTodos = new ArrayList<>();
        todosForEvent = new ArrayList<>();

        todosAdapter = new TodosAdapter(todosForEvent, this);
        todosForEventView.setAdapter(todosAdapter);

        dbHelper = new DBEventHelper(EditExam.this.getContext());
        dbTodoHelper = new DBTodoHelper(EditExam.this.getContext());


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.colours, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        colour.setAdapter(adapter);
        setPreDates();
        loadData();
        showDayData();
        setListeners();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Edit Event")
                // Pass null as the parent view because its going in the dialog layout
                .setPositiveButton("Save Changes", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Delete", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button save = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                save.setTextColor(getResources().getColor(R.color.primaryVariant));
                Button delete = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                delete.setTextColor(getResources().getColor(R.color.remove));
                Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setTextColor(getResources().getColor(R.color.primaryVariant));

                save.setOnClickListener(v -> {
                    shownEvent.setSubject(subject.getText().toString());
                    shownEvent.setType(type.getText().toString());
                    shownEvent.setEndDate(Util.getFormattedDateForDB(dueDay.getText().toString()));
                    shownEvent.setStartDate(Util.getFormattedDateForDB(startDate.getText().toString()));
                    shownEvent.setColor(colour.getSelectedItem().toString());
                    shownEvent.setVolume((int)volume.getRating()*2);

                    if (shownEvent.getStartDate().equals("")){
                        shownEvent.setStartDate(LocalDate.now().toString());
                    }
                    if(shownEvent.getEndDate().equals("")){
                        Toast.makeText(getContext(), "Set an Due Day!", Toast.LENGTH_SHORT).show();
                    }else{
                        long currentEventId;
                        if(emptyEvent) {
                            currentEventId = dbHelper.addEventObject(shownEvent);
                        } else {
                            currentEventId = dbHelper.updateEventObject(shownEvent);
                        }
                        for (int i = 0; i < todosForEvent.size(); i++){
                            boolean newTodo = true;
                            for (int j = 0; j < allTodos.size(); j++){
                                if (Objects.equals(todosForEvent.get(i).getId(), allTodos.get(j).getId())){
                                    Todo todo = todosForEvent.get(i);
                                    todo.setCollection(String.valueOf(currentEventId));
                                    dbTodoHelper.updateTodoObject(todo);
                                    newTodo = false;
                                    break;
                                }
                            }
                            if(newTodo){
                                Todo todo = todosForEvent.get(i);
                                todo.setCollection(String.valueOf(currentEventId));
                                dbTodoHelper.addTodoObject(todo);
                            }
                        }
                        dialog.dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new PlanFragment()).commit();
                    }
                });

                delete.setOnClickListener(v -> {
                    if(emptyEvent){
                        dialog.dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new PlanFragment()).commit();
                    }else{
                        dbHelper.deleteEventObject(shownEvent);
                        if(shownEvent.getGroupId() != null) {
                            DocumentReference groupDoc = FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_GROUPS)
                                    .document(shownEvent.getGroupId());
                            groupDoc.get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        Group group = documentSnapshot.toObject(Group.class);
                                        for (Event event : group.events) {
                                            if (event.getFirebaseId().equals(shownEvent.getFirebaseId())) {
                                                List<Event> events = group.events;
                                                events.remove(event);
                                                List<String> notWanted = event.getNotWanted();
                                                notWanted.add(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                event.setNotWanted(notWanted);
                                                events.add(event);
                                                groupDoc.update("events", events).addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Error while deleting event", Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        }

                                    });
                        }
                        todaysEvents.remove(shownEvent);
                        if(todaysEvents.size() > 0) {
                            shownEvent = todaysEvents.get(0);
                            prevEvent.setVisibility(View.INVISIBLE);
                            if(todaysEvents.size() > 1)
                                nextEvent.setVisibility(View.VISIBLE);
                            else {
                                nextEvent.setVisibility(View.INVISIBLE);
                            }
                            showEvent();
                        } else {
                            emptyEvent = true;
                            shownEvent = new Event();
                            showEvent();
                        }
                    }
                });
                cancel.setOnClickListener(v -> {
                    dialog.dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new PlanFragment()).commit();
                });
            }
        });
        dialog.show();
        return dialog;
    }

    private void setPreDates() {
        startDate.setText(Util.getFormattedDate(LocalDate.now()));
        dueDay.setText(Util.getFormattedDate(selectedDate));
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
            if(todaysEvents.size() < 2) {
                nextEvent.setVisibility(View.INVISIBLE);
            }
            showEvent();
        } else {
            shownEvent = new Event();
            emptyEvent = true;
            nextEvent.setVisibility(View.GONE);
            newEmptyEvent.setVisibility(View.GONE);
        }
    }

    private void showEvent() {
        subject.setText(shownEvent.getSubject());
        type.setText(shownEvent.getType());

        volume.setRating(shownEvent.getVolume()/2);
        startDate.setText(Util.getFormattedDate(LocalDate.parse(shownEvent.getStartDate())));
        dueDay.setText(Util.getFormattedDate(LocalDate.parse(shownEvent.getEndDate())));
        progress = shownEvent.getProgress(); //bereits gelernte Stunden
        float gesStd = shownEvent.getAbsolutMinutes();
        String col = shownEvent.getColor();
        if(col == null) {
            col = "red";
        }
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
                Todo todo = new Todo(cursor.getString(0), cursor.getString(3),cursor.getString(1),cursor.getInt(2),cursor.getInt(4));
                allTodos.add(todo);
            }
        }
    }

    private void setListeners() {
        dueDay.setOnClickListener(v -> {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getParentFragmentManager(), "date picker");
        });
        addTodo.setOnClickListener(v -> {
            try {
                int minutesEst = Integer.parseInt(inputTime.getText().toString());
                Todo todo = new Todo(shownEvent.getId(), inputTodo.getText().toString(), minutesEst,0);
                todosForEvent.add(todo);
                todosAdapter.notifyItemInserted(todosForEvent.size()-1);
                inputTodo.setText("");
                inputTime.setText("");
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
                    int oldSize = todosForEvent.size();
                    todosForEvent.clear();
                    todosAdapter.notifyItemRangeRemoved(0, oldSize);
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
        subject.setText("");
        type.setText("");
        volume.setRating(0);
        setPreDates();
        progressBar.setProgress(0);
        int oldSize = todosForEvent.size();
        todosForEvent.clear();
        todosAdapter.notifyItemRangeRemoved(0, oldSize);
        setColor("red");
        inputTodo.setText("");
        inputTime.setText("");
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        dueDay.setText(Util.getFormattedDate(date));
    }
}

