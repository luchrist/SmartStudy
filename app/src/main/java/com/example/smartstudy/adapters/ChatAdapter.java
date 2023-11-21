package com.example.smartstudy.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstudy.R;
import com.example.smartstudy.databinding.ItemContainerReceivedMessageBinding;
import com.example.smartstudy.databinding.ItemContainerSentMessageBinding;
import com.example.smartstudy.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String senderId;
    private final List<String> blockedBy;
    private final List<String> blockedUsers;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderId, List<String> blockedBy, List<String> blockedUsers) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.blockedBy = blockedBy;
        this.blockedUsers = blockedUsers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        } else {
            return new ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            boolean anonymous = blockedUsers.contains(chatMessages.get(position).senderId)
                    || blockedBy.contains(chatMessages.get(position).senderId);
            if (position > 0) {
                ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position), chatMessages.get(position - 1).senderId, anonymous);
            } else {
                ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position), "", anonymous);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECEIVED;
    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.msgDateTime.setText(chatMessage.dateTime);
        }
    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMsg, String beforeSenderId, boolean anonymous) {
            String senderName = chatMsg.senderName;
            if (anonymous) {
                senderName = binding.getRoot().getResources().getString(R.string.anonymous);
                binding.textMessage.setText(R.string.this_message_is_hidden_for_you);
            } else {
                binding.textMessage.setText(chatMsg.message);
                binding.imageProfile.setImageBitmap(decodeString(chatMsg.senderImage));
            }
            binding.msgDateTime.setText(chatMsg.dateTime);
            if(!chatMsg.senderId.equals(beforeSenderId)) {
                binding.msgSenderName.setText(senderName);
            }else {
                binding.msgSenderName.setVisibility(View.GONE);
            }
        }

        Bitmap decodeString(String encodedImg) {
            byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}
