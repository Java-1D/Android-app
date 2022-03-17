package com.example.myapplication2.database;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication2.objectmodel.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EventsDb {
    CollectionReference events;

    EventsDb(){
        events = FirebaseFirestore.getInstance().collection("Events");

    }

    public void addEvent(EventModel eventModel) {

        
    }

    public EventModel getEvent(String eventId) {
        return null;
    }

    public void updateEvent(EventModel eventModel) {

    }

    public void deleteEvent(String eventId) {

    }

    public ArrayList<EventModel> getAllEvents() {
        ArrayList<EventModel> list = new ArrayList<EventModel>();
        events.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                list.add()
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

                return null;
    }
    
    




}
