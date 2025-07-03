package com.example.mail_app;

import android.app.Application;

import androidx.room.Room;

/**
 * Application class used to initialize global app components.
 * In this case: initializes the Room database when the app starts.
 */
public class MyApp extends Application {

    private static MyApp instance;
    private LocalDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        database = Room.databaseBuilder(
                getApplicationContext(),
                LocalDatabase.class,
                "mail_app_db"
        ).build();
    }

    public static MyApp getInstance() {
        return instance;
    }

    public LocalDatabase getDatabase() {
        return database;
    }
}
