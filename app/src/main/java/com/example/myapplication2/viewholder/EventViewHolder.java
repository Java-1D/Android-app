package com.example.myapplication2.viewholder;

// View Holder Class

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

   // Declare Text view and set data
   public TextView capacity;

   public TextView event_title;
   public TextView event_description;
   public TextView status;
   public TextView location;
   public ImageView event_image;

   public Activity mActivity;


   // passed in the item from oncreate
   public EventViewHolder(@NonNull View itemView) {
      super(itemView);
      event_title = (TextView) itemView.findViewById(R.id.event_title);
      event_description = (TextView) itemView.findViewById(R.id.event_description);
      event_image = (ImageView) itemView.findViewById(R.id.event_image);
      location = (TextView) itemView.findViewById(R.id.location);
      capacity = (TextView) itemView.findViewById(R.id.capacity);
      status = (TextView) itemView.findViewById(R.id.status);


   }


}