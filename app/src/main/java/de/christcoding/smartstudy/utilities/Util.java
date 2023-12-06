package de.christcoding.smartstudy.utilities;

import android.content.Context;
import android.widget.Toast;

import de.christcoding.smartstudy.models.Event;
import de.christcoding.smartstudy.models.Todo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Util {
    private static final DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static int getTimeNeededForTodos(Event event, List<Todo> todos) {
        return todos.stream()
                .filter(todo -> todo.getCollection().equals(event.getId()))
                .mapToInt(Todo::getTime).sum();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getFormattedDate(LocalDate date) {
        return date.format(germanFormatter);
    }

    public static String getFormattedDateForDB(String date) {
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date,germanFormatter).format(dbFormatter);
    }

    public static String getColorOfSpinner(int selectedItem) {
        switch (selectedItem) {
            case 0:
                return "red";
            case 1:
                return "blue";
            case 2:
                return "green";
            case 3:
                return "yellow";
            case 4:
                return "brown";
            case 5:
                return "orange";
            case 6:
                return "pink";
            case 7:
                return "purple";
            case 8:
                return "grey";
        }
        return null;
    }

    public static String getDayOfSpinner(String selectedItem) {
        switch (selectedItem) {
            case "MONTAG":
                return "MONDAY";
            case "DIENSTAG":
                return "TUESDAY";
            case "MITTWOCH":
                return "WEDNESDAY";
            case "DONNERSTAG":
                return "THURSDAY";
            case "FREITAG":
                return "FRIDAY";
            case "SAMSTAG":
                return "SATURDAY";
            case "SONNTAG":
                return "SUNDAY";
            default:
                return selectedItem;
        }
    }

    public static LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date);
    }
}
