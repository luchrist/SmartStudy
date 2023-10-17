package com.example.smartstudy.utilities;

import android.content.Context;
import android.widget.Toast;

import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Todo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Util {

    public static int getTimeNeededForTodos(Event event, List<Todo> todos) {
        return todos.stream()
                .filter(todo -> todo.getCollection().equals(event.getId()))
                .mapToInt(Todo::getTime).sum();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getFormattedDate(LocalDate date) {
        DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(germanFormatter);
    }

    public static String getFormattedDateForDB(String date) {
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date).format(dbFormatter);
    }

    public static LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date);
    }
}
