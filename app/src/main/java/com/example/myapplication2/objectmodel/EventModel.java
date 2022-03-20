package com.example.myapplication2.objectmodel;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication2.database.EventsDb;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class EventModel {
    private static final String TAG = "Event Model";
    private int capacity;
    private String description;
    Date eventCreated;
    Date eventEnd;
    Date eventStart;
    DocumentReference imagePath;
    DocumentReference module;
    String status;
    String title;
    DocumentReference userCreated;
    ArrayList<DocumentReference> userJoined;
    DocumentReference venue;

    public EventModel() {
    } //no arg constructor for firebase

    public EventModel(int capacity, String description, Date eventCreated, Date eventEnd, Date eventStart, DocumentReference imagePath, DocumentReference module, String status, String title, DocumentReference userCreated, ArrayList<DocumentReference> userJoined, DocumentReference venue) {
        this.capacity = capacity;
        this.description = description;
        this.eventCreated = eventCreated;
        this.eventEnd = eventEnd;
        this.eventStart = eventStart;
        this.imagePath = imagePath;
        this.module = module;
        this.status = status;
        this.title = title;
        this.userCreated = userCreated;
        this.userJoined = userJoined;
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventCreated() {
        return eventCreated;
    }

    public void setEventCreated(Date eventCreated) {
        this.eventCreated = eventCreated;
    }

    public Date getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
    }

    public Date getEventStart() {
        return eventStart;
    }

    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
    }

    public DocumentReference getImagePath() {
        return imagePath;
    }

    public void setImagePath(DocumentReference imagePath) {
        this.imagePath = imagePath;
    }

    public DocumentReference getModule() {
        return module;
    }

    public void setModule(DocumentReference module) {
        this.module = module;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentReference getUserCreated() {return userCreated;}

    public void setUserCreated(DocumentReference userCreated) {
        this.userCreated = userCreated;
    }

    public ArrayList<DocumentReference> getUserJoined() {
        return userJoined;
    }

    public void setUserJoined(ArrayList<DocumentReference> userJoined) {
        this.userJoined = userJoined;
    }

    public DocumentReference getVenue() {
        return venue;
    }

    public void setVenue(DocumentReference venue) {
        this.venue = venue;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "capacity=" + capacity +
                ", description='" + description + '\'' +
                ", eventCreated=" + eventCreated +
                ", eventEnd=" + eventEnd +
                ", eventStart=" + eventStart +
                ", imagePath=" + imagePath +
                ", module=" + module +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", userCreated=" + userCreated +
                ", userJoined=" + userJoined +
                ", venue=" + venue +
                '}';
    }
}
