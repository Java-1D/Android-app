package com.example.myapplication2.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.R;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    public TextView username;
    public ImageView user_image;
    public CardView user_profile;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.user_name);
        user_image = (ImageView) itemView.findViewById(R.id.user_image);
        user_profile = (CardView) itemView.findViewById(R.id.user_card);

    }
}
