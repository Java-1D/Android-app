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
    private Date eventEnd;
    private Date eventStart;
    private DocumentReference imagePath;
    private DocumentReference module;
    private String status;
    private String title;
    private DocumentReference userCreated;
    private ArrayList<DocumentReference> userJoined;
    private String venue;

    public EventModel() {} //no arg constructor for firebase

    public EventModel(String title, String description, String venue, DocumentReference module,
                      int capacity, Date eventStart, Date eventEnd, DocumentReference imagePath,
                      DocumentReference userCreated) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.module = module;
        this.capacity = capacity;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.imagePath = imagePath;
        this.userCreated = userCreated;

        // Initialized as empty
        this.status = "upcoming";
        this.userJoined = null;
    }

//    public EventModel(int capacity, String description,
//                      Date eventEnd, Date eventStart, DocumentReference imagePath,
//                      DocumentReference module, String status, String title,
//                      DocumentReference userCreated, ArrayList<DocumentReference> userJoined,
//                      String venue) {
//        this.capacity = capacity;
//        this.description = description;
//        this.eventEnd = eventEnd;
//        this.eventStart = eventStart;
//        this.imagePath = imagePath;
//        this.module = module;
//        this.status = status;
//        this.title = title;
//        this.userCreated = userCreated;
//        this.userJoined = userJoined;
//        this.venue = venue;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public DocumentReference getModule() {
        return module;
    }

    public void setModule(DocumentReference module) {
        this.module = module;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Date getEventStart() {
        return eventStart;
    }

    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
    }

    public Date getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
    }

    public DocumentReference getImagePath() {
        return imagePath;
    }

    public void setImagePath(DocumentReference imagePath) {
        this.imagePath = imagePath;
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

    @Override
    public String toString() {
        return "EventModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", module=" + module +
                ", venue=" + venue +
                ", capacity=" + capacity +
                ", eventStart=" + eventStart +
                ", eventEnd=" + eventEnd +
                ", imagePath=" + imagePath +
                ", status='" + status + '\'' +
                ", userCreated=" + userCreated +
                ", userJoined=" + userJoined +
                '}';
    }
}