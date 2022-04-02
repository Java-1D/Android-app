package com.example.myapplication2.utils;

public class Container<Object> {
    private Object model;

    public Container(Object model) {
        this.model = model;
    }

    public Container() {

    }

    public void set(Object model) {
        this.model = model;
    }

    public Object get() {
        return this.model;
    }
}
