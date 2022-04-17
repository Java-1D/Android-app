package com.example.myapplication2.utils;

public class Container<T> {

    T value;

    public Container(T v) {this.value = v;}
    public Container() {}
    public void set(T v) {this.value = v;}
    public T get() {return this.value; }
}
