package com.example.myapplication2.archive;

import com.google.firebase.firestore.DocumentReference;

/*
 * Firebase Firestore Document Object Model for the Venues Collection
 * @field imagePath: DocumentReference from Images Collection
 * @field title: string
 */
public class VenueModel {

    private static final String TAG = "Venue Model";
    private DocumentReference imagePath;
    private String title;

    VenueModel() {}

    VenueModel(DocumentReference imagePath, String title) {
        this.imagePath = imagePath;
        this.title = title;
    }

    public DocumentReference getImagePath() {
        return imagePath;
    }

    public void setImagePath(DocumentReference imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "VenueModel{" +
                "imagePath=" + imagePath +
                ", title='" + title + '\'' +
                '}';
    }
}
