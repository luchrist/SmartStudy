package com.example.smartstudy.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.databinding.ItemContainerMemberBinding;
import com.example.smartstudy.models.Member;
import com.example.smartstudy.utilities.SelectListener;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private final List<Member> members;
    private final SelectListener selectListener;
    private final String currentUserEmail;

    public MembersAdapter(List<Member> members, SelectListener selectListener, String currentUserEmail) {
        this.members = members;
        this.selectListener = selectListener;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public MembersAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerMemberBinding itemContainerMemberBinding = ItemContainerMemberBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return  new MembersAdapter.MemberViewHolder(itemContainerMemberBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.MemberViewHolder holder, int position) {
        holder.setMemberData(members.get(position));
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {

        ItemContainerMemberBinding binding;

        public MemberViewHolder(ItemContainerMemberBinding itemContainerMemberBinding) {
            super(itemContainerMemberBinding.getRoot());
            binding = itemContainerMemberBinding;
        }
        void setMemberData(Member member) {
            binding.textName.setText(member.name);
            binding.textEmail.setText(member.email);
            binding.imageProfile.setImageBitmap(getMemberImage(member.image));
            if (member.email.equals(currentUserEmail)) {
                binding.memberMenu.setVisibility(View.GONE);
            }
            if (member.isAdmin){
                binding.adminStatus.setVisibility(View.VISIBLE);
            } else {
                binding.adminStatus.setVisibility(View.GONE);
            }
        }

        public void setListener() {
            binding.getRoot().setOnClickListener(v -> selectListener.onItemClicked(members.get(getAdapterPosition())));
        }
    }

    private Bitmap getMemberImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

