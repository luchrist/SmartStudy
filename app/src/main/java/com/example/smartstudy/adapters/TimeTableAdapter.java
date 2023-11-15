package com.example.smartstudy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.R;
import com.example.smartstudy.databinding.ItemContainerTimeTableWeekBinding;
import com.example.smartstudy.models.TimeTableElement;
import com.example.smartstudy.utilities.TimeTableSelectListener;

import java.util.List;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder> {

    private final List<TimeTableElement> elements;
    private final TimeTableSelectListener selectListener;

    public TimeTableAdapter(List<TimeTableElement> elements, TimeTableSelectListener selectListener) {
        this.elements = elements;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public TimeTableAdapter.TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerTimeTableWeekBinding itemContainerTimeTableWeekBinding = ItemContainerTimeTableWeekBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TimeTableAdapter.TimeTableViewHolder(itemContainerTimeTableWeekBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableAdapter.TimeTableViewHolder holder, int position) {
        holder.setData(elements.get(position));
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    class TimeTableViewHolder extends RecyclerView.ViewHolder {

        ItemContainerTimeTableWeekBinding binding;

        public TimeTableViewHolder(ItemContainerTimeTableWeekBinding itemContainerTimeTableBinding) {
            super(itemContainerTimeTableBinding.getRoot());
            binding = itemContainerTimeTableBinding;
        }

        void setData(TimeTableElement element) {
            binding.subject.setText(element.getSubject());
            binding.time.setText(String.format("%s - %s", element.getBegin(), element.getEnd()));
            binding.room.setText(element.getRoom());
            binding.teacher.setText(element.getTeacher());
            switch (element.getColour()) {
                case "red":
                    binding.getRoot().setBackgroundResource(R.drawable.background_input_red);
                    break;
                case "blue":
                    binding.getRoot().setBackgroundResource(R.drawable.background_input_blue);
                    break;
                case "green":
                    binding.getRoot().setBackgroundResource(R.drawable.background_input_green);
                    break;
                case "yellow":
                    binding.getRoot().setBackgroundResource(R.drawable.background_yellow);
                    break;
                case "orange":
                    binding.getRoot().setBackgroundResource(R.drawable.background_input_orange);
                    break;
                case "purple":
                    binding.getRoot().setBackgroundResource(R.drawable.background_input_purple);
                    break;
                case "pink":
                    binding.getRoot().setBackgroundResource(R.drawable.background_pink);
                    break;
                case "brown":
                    binding.getRoot().setBackgroundResource(R.drawable.background_input_brown);
                    break;
                default:
                    binding.getRoot().setBackgroundResource(R.drawable.background_input);
                    break;
            }
        }

        public void setListener() {
            binding.getRoot().setOnClickListener(v -> selectListener.onElementSelected(elements.get(getAdapterPosition())));
        }
    }
}

