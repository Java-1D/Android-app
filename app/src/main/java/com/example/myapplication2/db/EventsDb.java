package com.example.myapplication2.db;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.utils.FirebaseDocument;
import com.example.myapplication2.utils.FirebaseStorageReference;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class EventsDb extends Db{
    private final static String TAG = "EventsDb";
    private static OnEventModelSuccess onEventModelSuccess;
    private static OnEventUploadSuccess onEventUploadSuccess;
    private static OnEventUploadFailure onEventUploadFailure;

    public EventsDb() {
        super(EventModel.getCollectionId());
    }

    public EventsDb(OnEventModelSuccess onEventModelSuccess) {
        super(EventModel.getCollectionId());
        this.onEventModelSuccess = onEventModelSuccess;
    }

    public EventsDb(OnEventUploadSuccess onEventUploadSuccess, OnEventUploadFailure onEventUploadFailure) {
        super(EventModel.getCollectionId());
        this.onEventUploadSuccess = onEventUploadSuccess;
        this.onEventUploadFailure = onEventUploadFailure;
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

    public void pushEvent(Context context, EventModel eventModel) {
        if (!Utils.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.internet_required, Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionReference eventsCollection = getCollection();
        eventsCollection.document(eventModel.getTitle()).set(eventModel);
        Log.i(TAG, "Successful. Event pushed to Firebase");
        onEventUploadSuccess.onResult();
    }

    public void pushNewEvent(Context context, EventModel eventModel, EditText eventName) {
        if (!Utils.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.internet_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // Checking that the data does not exist in Firebase
        new FirebaseDocument() {
            @Override
            public void callbackOnSuccess(DocumentSnapshot document) {
                if (!document.exists()) {
                    CollectionReference eventsCollection = getCollection();
                    eventsCollection.document(eventModel.getTitle()).set(eventModel);
                    Log.i(TAG, "Successful. Event added to Firebase");
                    onEventUploadSuccess.onResult();
                } else {
                    eventName.requestFocus();
                    eventName.setError("Please use a different event name.");
                    onEventUploadFailure.onResult();
                }
            }
        }.run(EventModel.getCollectionId(), eventName.getText().toString());
    }

    public void convertToEventModel(Context context, ImageView eventImage, EditText eventName,
                                                 EditText eventDescription, EditText eventVenue, EditText eventModule,
                                                 EditText eventCapacity, EditText eventStart, EditText eventEnd,
                                                 DocumentReference selectedModuleReference, Calendar startDateTime,
                                                     Calendar endDateTime) {

        if (!Utils.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.internet_required, Toast.LENGTH_SHORT).show();
            onEventModelSuccess.onResult(null);
            return;
        }

        if (Utils.invalidData(eventName, eventDescription, eventVenue, eventModule, eventCapacity, eventStart, eventEnd)){
            onEventModelSuccess.onResult(null);
            return;
        }

        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String venue = eventVenue.getText().toString();
        DocumentReference userCreated = LoggedInUser.getInstance().getUserDocRef();
        Integer capacity = Integer.parseInt(eventCapacity.getText().toString());

        FirebaseStorageReference firebaseStorageReference = new FirebaseStorageReference() {
            @Override
            public void uploadSuccess(String string) {
                EventModel eventModel = new EventModel(
                        name,
                        description,
                        venue,
                        selectedModuleReference,
                        capacity,
                        startDateTime.getTime(),
                        endDateTime.getTime(),
                        string,
                        userCreated);

                Log.i(TAG, "onFailure: Storage upload successful");
                onEventModelSuccess.onResult(eventModel);
            }
            @Override
            public void uploadFailed() {
                Toast.makeText(context, "Uploading failed, please try again.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: Storage upload unsuccessful");
                onEventModelSuccess.onResult(null);
            }
        };
        firebaseStorageReference.uploadImage(eventImage, EventModel.COLLECTION_ID + "/" + eventName);
    }

    public interface OnEventModelSuccess{
        void onResult(EventModel eventModel);
    }

    public interface OnEventUploadSuccess{
        void onResult();
    }

    public interface OnEventUploadFailure{
        void onResult();
    }
}