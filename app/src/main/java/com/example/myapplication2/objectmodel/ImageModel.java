package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class ImageModel {

    public static final String TAG = "Image Model";
    private Date dateUploaded;
    private String imageRef; //not sure about the type declaration to reference to firebase Storage
    private DocumentReference userUploaded;

    ImageModel() {}

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

    public static class UserModel {
        public String name, email;
        public UserModel(){

        }
        public UserModel(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
