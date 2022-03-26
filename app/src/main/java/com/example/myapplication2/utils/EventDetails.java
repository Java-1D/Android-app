package com.example.myapplication2.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication2.viewholder.EventViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventDetails {

   private static final String TAG = "TEST";

   public static void SetEventRowDetails(DocumentReference locationReference, EventViewHolder holder){
      locationReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
               DocumentSnapshot document = task.getResult();
               if (document != null && document.exists()) {
                  Log.d("TAG", document.getString("title")); //Print the name

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