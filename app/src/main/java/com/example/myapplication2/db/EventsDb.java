package com.example.myapplication2.db;

import android.content.Context;
import android.widget.Toast;

import com.example.myapplication2.ViewEventActivity;
import com.example.myapplication2.objectmodel.EventModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventsDb extends Db{

    public EventsDb() {
        super("Events");
    }

    public void updateUserList(Context context, String documentName, DocumentReference user, Boolean toAdd) {
        DocumentReference docRef = getDocument(documentName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                assert eventModel != null;
                ArrayList<DocumentReference> usersJoined = eventModel.getUserJoined();
                int current = usersJoined.size();
                if (current == eventModel.getCapacity()) {
                    Toast.makeText(context, "The event is full! So sorry!", Toast.LENGTH_SHORT).show();
                }
                // adding user from userJoined
                if (toAdd) {
                    docRef.update("userJoined", FieldValue.arrayUnion(user));
                    Toast.makeText(context, "You have successfully joined the event", Toast.LENGTH_SHORT).show();

                } else {
                    docRef.update("userJoined", FieldValue.arrayRemove(user));
                    Toast.makeText(context, "You have successfully left the event", Toast.LENGTH_SHORT).show();

                }

                // update the firebase with usersJoined

            }
        });

    }
}


