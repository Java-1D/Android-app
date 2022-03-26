package com.example.myapplication2.utils;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication2.viewholder.EventViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventDetails {

   private static final String TAG = "TEST";


   // Set the text for title, description and image for event_image
//   public static void SetEventRowDetails(DocumentReference locationReference, EventViewHolder holder){
//      locationReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//         @Override
//         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//            if (task.isSuccessful()) {
//               DocumentSnapshot document = task.getResult();
//               if (document != null && document.exists()) {
//                  Log.d("TAG", document.getString("title")); //Print the name
//                  holder.location.setText(location.getTitle());
//                  Bitmap imageBitmap = Utils.getImageBitmap(location.getImagePath());
//                  holder.locationImage.setImageBitmap(imageBitmap);
//               } else {
//                  Log.d(TAG, "No such document");
//               }
//            } else {
//               Log.d(TAG, "get failed with ", task.getException());
//            }
//         }
//      });
//
//   }

   public static void setEventImage(DocumentReference imageRef, EventViewHolder holder){
      imageRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
               DocumentSnapshot document = task.getResult();
               if (document != null && document.exists()) {
                  Log.d("TAG", document.getString("image_ref")); //Print the name
                  String url = document.getString("image_ref");
                  Utils.loadImage(url,holder.event_image);
               } else {
                  Log.d(TAG, "No such document");
               }
            } else {
               Log.d(TAG, "get failed with ", task.getException());
            }
         }
      });

   }
}
