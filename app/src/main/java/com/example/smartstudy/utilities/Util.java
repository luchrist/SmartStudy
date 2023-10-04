package com.example.smartstudy.utilities;

import com.example.smartstudy.models.Event;
import com.example.smartstudy.models.Todo;

import java.util.List;

public class Util {

    public static int getTimeNeededForTodos(Event event, List<Todo> todos) {
        return todos.stream()
                .filter(todo -> todo.getCollection().equals(event.getId()))
                .mapToInt(Todo::getTime).sum();
    }
}
