package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/*
 * Firebase Firestore Document Object Model for the Events Collection
 * @ID documentId: string
 *
 * @field capacity: number
 * @field description: string
 * @field eventCreated: timestamp
 * @field eventEnd: timestamp
 * @field eventStart: timestamp
 * @field imagePath: string referencing URL from Firebase Cloud Storage
 * @field lastUpdated: timestamp
 * @field module: DocumentReference from Modules Collection
 * @field status: string
 * @field title: string
 * @field userCreated: DocumentReference from Users Collection
 * @field userJoined: ArrayList of DocumentReference from Users Collection
 *        @index: DocumentReference from Users Collection
 * @field venue: DocumentReference from Venues Collection
 */
public class EventModel implements ObjectModel {

    public static final String TAG = "Event Model";
    public static final String COLLECTION_ID = "Events";
    public static final ArrayList<String> STATUSES = new ArrayList<>(Arrays.asList("upcoming", "ongoing", "completed"));

    @DocumentId
    private String documentId;

    private int capacity;
    private String description;
    private Date eventEnd;
    private Date eventStart;
    private String imagePath;
    private DocumentReference module;
    private String status;
    private String title;
    private DocumentReference userCreated;
    private ArrayList<DocumentReference> userJoined;
    private String venue;


    public EventModel() {
    } //no arg constructor for firebase

    //FIXME To add String documentId into the constructor
    public EventModel(String title, String description, String venue, DocumentReference module,
                      int capacity, Date eventStart, Date eventEnd, String imagePath,
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
        this.userJoined = new ArrayList<>(Arrays.asList(userCreated));
    }

    public static String getTAG() {
        return TAG;
    }

    public static String getCollectionId() {
        return COLLECTION_ID;
    }

    public static ArrayList<String> getStatuses() {
        return STATUSES;
    }

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

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

    public String getCapacityString() {
        return String.valueOf(capacity);
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getRemainingCapacity() {
        String currentCapacity = String.valueOf(this.userJoined.size());
        String totalCapacity = String.valueOf(this.capacity);
        String output = "Study Buddies Now : " + currentCapacity + " / " + totalCapacity;
        return output;
    }

    public String getEventStartTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(eventStart);
    }

    public String getEventEndTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(eventEnd);
    }

    public String getEventStartDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy");
        return formatter.format(eventStart);
    }

    public String getEventDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat(" d, MMM");
        return formatter.format(eventStart);


    }

    @Override
    public String toString() {
        return "EventModel{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
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
