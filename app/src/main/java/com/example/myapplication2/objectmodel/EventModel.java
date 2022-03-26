package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/*
* Firebase Firestore Document Object Model for the Events Collection
* @field capacity: number
* @field description: string
* @field eventCreated: timestamp
* @field eventEnd: timestamp
* @field eventStart: timestamp
* @field imagePath: DocumentReference from Images Collection
* @field lastUpdated: timestamp
* @field module: DocumentReference from Modules Collection
* @field status: string
* @field title: string
* @field userCreated: DocumentReference from Users Collection
* @field userJoined: ArrayList of DocumentReference from Users Collection
*        @index: DocumentReference from Users Collection
* @field venue: DocumentReference from Venues Collection
*/
public class EventModel {

    private final ArrayList<String> statuses = new ArrayList<>(Arrays.asList("upcoming", "ongoing", "completed"));

    public static final String TAG = "Event Model";

    private int capacity;
    private String description;
    private Date eventCreated;
    private Date eventEnd;
    private Date eventStart;
    private DocumentReference imagePath;
    private Date lastUpdated;
    private DocumentReference module;
    private String status;
    private String title;
    private DocumentReference userCreated;
    private ArrayList<DocumentReference> userJoined;
    private DocumentReference venue;
<<<<<<< HEAD
=======

    private ArrayList<String> statuses = new ArrayList<>(Arrays.asList("upcoming", "ongoing", "completed"));

>>>>>>> fixes to firestore

    public EventModel() {} //no arg constructor for firebase

    public EventModel(int capacity, String description, Date eventCreated,
                      Date eventEnd, Date eventStart, DocumentReference imagePath, Date lastUpdated,
                      DocumentReference module, String status, String title,
                      DocumentReference userCreated, ArrayList<DocumentReference> userJoined,
                      DocumentReference venue) {
        this.capacity = capacity;
        this.description = description;
        this.eventCreated = eventCreated;
        this.eventEnd = eventEnd;
        this.eventStart = eventStart;
        this.imagePath = imagePath;
        this.lastUpdated = lastUpdated;
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

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public DocumentReference getModule() {
        return module;
    }

    public void setModule(DocumentReference module) {
        this.module = module;
    }

    public String getStatuses(int index) {
        return statuses.get(index);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus() {
        Date currentDate = new Date();
        if (currentDate.compareTo(this.eventStart) < 0) {
            this.status = "upcoming";
        }
        else if (currentDate.compareTo(this.eventEnd) > 0) {
            this.status = "completed";
        }
        else {
            this.status = "ongoing";
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentReference getUserCreated() {
        return userCreated;
    }

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
                ", lastUpdated=" + lastUpdated +
                ", module=" + module +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", userCreated=" + userCreated +
                ", userJoined=" + userJoined +
                ", venue=" + venue +
                '}';
    }
}
