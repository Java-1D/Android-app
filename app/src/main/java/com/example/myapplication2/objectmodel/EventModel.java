package com.example.myapplication2.objectmodel;

import java.util.ArrayList;
import java.util.Date;

public class EventModel {
    private int capacity;
    private String description;
    Date eventCreated;
    Date eventEnd;
    Date eventStart;
    String imagePath;
    String module;
    String status;
    String title;
    String userCreated;
    ArrayList<String> userJoined;
    String venue;

    public EventModel() {} //no arg constructor for firebase

    public EventModel(int capacity, String description, Date eventCreated, Date eventEnd, Date eventStart, String imagePath, String module, String status, String title, String userCreated, ArrayList<String> userJoined, String venue) {
        this.capacity = capacity;
        this.description = description;
        this.eventCreated = eventCreated;
        this.eventEnd = eventEnd;
        this.eventStart = eventStart;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public ArrayList<String> getUserJoined() {
        return userJoined;
    }

    public void setUserJoined(ArrayList<String> userJoined) {
        this.userJoined = userJoined;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
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
                ", imagePath='" + imagePath + '\'' +
                ", module='" + module + '\'' +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", userCreated='" + userCreated + '\'' +
                ", userJoined=" + userJoined +
                ", venue='" + venue + '\'' +
                '}';
    }
}
