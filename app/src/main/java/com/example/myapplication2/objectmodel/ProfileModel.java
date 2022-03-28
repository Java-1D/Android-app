package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;

/*
* Firebase Firestore Document Object Model for the Profiles Collection
* @field bio: string
* @field eventsCreated: ArrayList of DocumentReference from Events Collection
*        @index: DocumentReference from Events Collection
* @field eventsJoined: ArrayList of DocumentReference from Events Collection
*        @index: DocumentReference from Events Collection
* @field imagePath: string referencing URL from Firebase Cloud Storage
* @field modules: ArrayList of DocumentReference from Modules Collection
*        @index: DocumentReference from Modules Collection
* @field name: string
* @field pillar: string
* @field profileCreated: timestamp
* @field profileUpdated: timestamp
* @field term: number
* @field userId: DocumentReference from Users Collection
*/
public class ProfileModel {

    private static final String TAG = "Profile Model";

    private String bio;
    private ArrayList<DocumentReference> eventsCreated;
    private ArrayList<DocumentReference> eventsJoined;
    private String imagePath;
    private ArrayList<DocumentReference> modules;
    private String name;
    private String pillar;
    private Date profileCreated;
    private Date profileUpdated;
    private int term;
    private DocumentReference userId;

    ProfileModel() {
    }

    ProfileModel(String bio, ArrayList<DocumentReference> eventsCreated, ArrayList<DocumentReference> eventsJoined,
                 String imagePath, ArrayList<DocumentReference> modules, String name, String pillar,
                 Date profileCreated, Date profileUpdated, int term, DocumentReference userId) {
        this.bio = bio;
        this.eventsCreated = eventsCreated;
        this.eventsJoined = eventsJoined;
        this.imagePath = imagePath;
        this.modules = modules;
        this.name = name;
        this.pillar = pillar;
        this.profileCreated = profileCreated;
        this.profileUpdated = profileUpdated;
        this.term = term;
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<DocumentReference> getEventsCreated() {
        return eventsCreated;
    }

    public void setEventsCreated(ArrayList<DocumentReference> eventsCreated) {
        this.eventsCreated = eventsCreated;
    }

    public ArrayList<DocumentReference> getEventsJoined() {
        return eventsJoined;
    }

    public void setEventsJoined(ArrayList<DocumentReference> eventsJoined) {
        this.eventsJoined = eventsJoined;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<DocumentReference> getModules() {
        return modules;
    }

    public void setModules(ArrayList<DocumentReference> modules) {
        this.modules = modules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPillar() {
        return pillar;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    public Date getProfileCreated() {
        return profileCreated;
    }

    public void setProfileCreated(Date profileCreated) {
        this.profileCreated = profileCreated;
    }

    public Date getProfileUpdated() {
        return profileUpdated;
    }

    public void setProfileUpdated(Date profileUpdated) {
        this.profileUpdated = profileUpdated;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public DocumentReference getUserId() {
        return userId;
    }

    public void setUserId(DocumentReference userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ProfileModel{" +
                "bio='" + bio + '\'' +
                ", eventsCreated=" + eventsCreated +
                ", eventsJoined=" + eventsJoined +
                ", imagePath=" + imagePath +
                ", modules=" + modules +
                ", name='" + name + '\'' +
                ", pillar='" + pillar + '\'' +
                ", profileCreated=" + profileCreated +
                ", profileUpdated=" + profileUpdated +
                ", term=" + term +
                ", userId=" + userId +
                '}';
    }
}
