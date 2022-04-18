package com.example.myapplication2.db;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

abstract class Db {
    /*
        declaring Db class as abstract is so that it can't be instantiated.
     */

    private final FirebaseFirestore db;
    private final CollectionReference collection;

    public Db(String collectionName) {
        db = FirebaseFirestore.getInstance();
        this.collection = db.collection(collectionName);
    }

    public CollectionReference getCollection() {
        return this.collection;
    }

    public CollectionReference getCollection(String collectionName){
        return db.collection(collectionName);
    }

    public FirebaseFirestore getDb(){
        return this.db;
    }

    public DocumentReference getDocument(String documentName){
        return collection.document(documentName);
    }
}