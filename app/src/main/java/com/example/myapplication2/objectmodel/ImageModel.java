package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

/*
* Firebase Firestore Document Object Model for the Images Collection
* @field dateUploaded: timestamp
* @field imageRef: string referencing the URI of image stored in Firebase Cloud Storage
* @field userUploaded: DocumentReference from Users Collection
*/
public class ImageModel {

    public static final String TAG = "Image Model";
    private Date dateUploaded;
    private String imageRef; //not sure about the type declaration to reference to firebase Storage
    private DocumentReference userUploaded;

    ImageModel() {} //no arg constructor for firebase

    ImageModel(Date dateUploaded, String imageRef, DocumentReference userUploaded) {
        this.dateUploaded = dateUploaded;
        this.imageRef = imageRef;
        this.userUploaded = userUploaded;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public DocumentReference getUserUploaded() {
        return userUploaded;
    }

    public void setUserUploaded(DocumentReference userUploaded) {
        this.userUploaded = userUploaded;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "dateUploaded=" + dateUploaded +
                ", imageRef='" + imageRef + '\'' +
                ", userUploaded=" + userUploaded +
                '}';
    }
}
