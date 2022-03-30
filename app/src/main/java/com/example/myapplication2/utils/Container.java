package com.example.myapplication2.utils;

public class Container<ObjectModel> {
    private ObjectModel model;
    public Container(ObjectModel model) {
        this.model = model;
    }

    public void set(ObjectModel model) {
        this.model = model;
    }

    public ObjectModel get() {
        return this.model;
    }
}
