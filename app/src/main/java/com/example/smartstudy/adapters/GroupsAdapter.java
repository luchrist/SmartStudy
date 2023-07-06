package com.example.smartstudy.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.databinding.ItemContainerGroupBinding;
import com.example.smartstudy.models.Group;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private final List<Group> groups;

    public GroupsAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupsAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerGroupBinding itemContainerGroupBinding = ItemContainerGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return  new GroupsAdapter.GroupViewHolder(itemContainerGroupBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.GroupViewHolder holder, int position) {
        holder.setGroupData(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        ItemContainerGroupBinding binding;

        public GroupViewHolder(ItemContainerGroupBinding itemContainerGroupBinding) {
            super(itemContainerGroupBinding.getRoot());
            binding = itemContainerGroupBinding;
        }
        void setGroupData(Group group) {
            binding.textName.setText(group.name);
            binding.textId.setText(group.id);
            binding.groupImg.setImageBitmap(getGroupImage(group.image));
        }
    }

    private Bitmap getGroupImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
