package com.example.myapplication2.utils;

public class FirebaseContainer<T> {
   private T value;

   public FirebaseContainer(T x) {
      this.value = x;
   }

   public void set(T x) {
      this.value = x;
   }

   public T get() {
      return this.value;
   }
}
