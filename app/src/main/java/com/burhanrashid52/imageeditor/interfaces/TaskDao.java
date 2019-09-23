package com.burhanrashid52.imageeditor.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.burhanrashid52.imageeditor.model.User;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Insert
    void insert(User user);

    /*@Delete
    void delete(Task task);

    @Update
    void update(Task task);*/

}