package com.example.myapplication2.objectmodel;

/*
* Firebase Firestore Document Object Model for the Locations Collection
* @field imageRef: string referencing the URL of an online image
* @field title: string
*/
public class LocationModel {

    public static final String TAG = "Location Model";
    private String imageRef;
    private String title;

    LocationModel() {} //no arg constructor for firebase

    public LocationModel(String imageRef, String title) {
        this.imageRef = imageRef;
        this.title = title;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "imageRef='" + imageRef + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
