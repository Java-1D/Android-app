package com.example.myapplication2.utils;

public class FirebaseContainer<T> {
   T value;

   public FirebaseContainer() {
      this.value = null;
   }

   public void set(T x) {
      this.value = x;
   }

   public T get() {
      return this.value;
   }
}
