package com.example.myapplication2.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileViewHolder extends RecyclerView.ViewHolder{
    public TextView username;
    public ImageView user_image;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
