package com.example.myapplication2.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BackgroundTask<I,O> {
    ExecutorService executor;
    final Handler handler = new Handler(Looper.getMainLooper());

    public BackgroundTask() {
        executor = Executors.newSingleThreadExecutor();
    }

    public abstract O runInBackground(I i);
    public abstract void whenDone(O o);

    public void run(final I i) {
        final Container<O> container = new Container<>();
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                container.set(BackgroundTask.this.runInBackground(i));
                handler.post(new Runnable() {
                   @Override
                   public void run() {
                       if (container.get() != null) {
                           whenDone(container.get());
                       }
                   }
                });
            }
        });

    }

}
