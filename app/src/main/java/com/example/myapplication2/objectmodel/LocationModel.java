package com.example.myapplication2.objectmodel;

class LocationModel {

    String imagePath;
    String title;

    public LocationModel(String imagePath, String title) {
        this.imagePath = imagePath;
        this.title = title;
    }

    LocationModel() {
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
