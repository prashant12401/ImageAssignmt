package com.burhanrashid52.imageeditor.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.burhanrashid52.imageeditor.interfaces.TaskDao;
import com.burhanrashid52.imageeditor.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
