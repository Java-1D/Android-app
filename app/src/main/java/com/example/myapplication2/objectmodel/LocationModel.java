package com.example.myapplication2.objectmodel;


public class LocationModel {

    public static final String TAG = "Location Model";
    private String imagePath;
    private String title;

    LocationModel() {
    }

    public LocationModel(String imagePath, String title) {
        this.imagePath = imagePath;
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
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
        return "LocationModel{" +
                "imagePath='" + imagePath + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
