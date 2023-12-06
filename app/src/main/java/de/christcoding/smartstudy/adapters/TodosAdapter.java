package de.christcoding.smartstudy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.christcoding.smartstudy.R;
import de.christcoding.smartstudy.databinding.ItemContainerTodoBinding;
import de.christcoding.smartstudy.models.Todo;
import de.christcoding.smartstudy.utilities.TodoSelectListener;

import java.util.List;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.TodoViewHolder> {

    private final List<Todo> todos;
    private final TodoSelectListener selectListener;

    public TodosAdapter(List<Todo> todos, TodoSelectListener selectListener) {
        this.todos = todos;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public TodosAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerTodoBinding itemContainerTodoBinding = ItemContainerTodoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TodosAdapter.TodoViewHolder(itemContainerTodoBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodosAdapter.TodoViewHolder holder, int position) {
        holder.setTodoData(todos.get(position));
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        if (todos == null) {
            return 0;
        }
        return todos.size();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {

        ItemContainerTodoBinding binding;

        public TodoViewHolder(ItemContainerTodoBinding itemContainerTodoBinding) {
            super(itemContainerTodoBinding.getRoot());
            binding = itemContainerTodoBinding;
        }

        void setTodoData(Todo todo) {
            binding.todoName.setText(todo.getTodo());
            binding.todoRemainingTime.setText(String.format("%s min", todo.getTime()));
            if(todo.getChecked() == 1) {
                binding.todoName.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.secondaryText));
                binding.todoTimeLeft.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.secondaryText));
                binding.todoRemainingTime.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.secondaryText));
                binding.todoDone.setImageResource(R.drawable.baseline_done_24);
            }
        }

        public void setListener() {
            binding.todoDone.setOnClickListener(v -> {
                if (todos.get(getAdapterPosition()).getChecked() == 0) {
                    binding.todoName.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.secondaryText));
                    binding.todoTimeLeft.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.secondaryText));
                    binding.todoRemainingTime.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.secondaryText));
                    binding.todoDone.setImageResource(R.drawable.baseline_done_24);
                } else {
                    binding.todoName.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.primaryText));
                    binding.todoTimeLeft.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.primaryText));
                    binding.todoRemainingTime.setTextColor(binding.todoDone.getContext().getResources().getColor(R.color.primaryText));
                    binding.todoDone.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                }
                selectListener.onTodoSelected(todos.get(getAdapterPosition()));
            });
        }
    }
}
