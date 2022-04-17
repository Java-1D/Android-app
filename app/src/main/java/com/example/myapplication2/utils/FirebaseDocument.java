package com.example.myapplication2.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class FirebaseDocument {
    private static final String TAG = "FirebaseDocument";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference getDocumentReference(String collectionId, String documentId) {
        return db.collection(collectionId).document(documentId);
    }

    private void getData(DocumentReference docRef) {
        Log.i(TAG, "File Path in Firebase: " + docRef.getPath());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                    callbackOnSuccess(document);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callbackOnFailure(e);
            }
        });
    }

    public abstract void callbackOnSuccess(DocumentSnapshot document);

    public void callbackOnFailure(@NonNull Exception e) {
        Log.w(TAG, "Error retrieving document from Firestore", e);
    }

    public void run(String collectionId, String documentId) {
        DocumentReference docRef = getDocumentReference(collectionId, documentId);
        getData(docRef);
    }

    public void run(DocumentReference docRef) {
        getData(docRef);
    }
}
