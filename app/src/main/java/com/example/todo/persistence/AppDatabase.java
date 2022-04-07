package com.example.todo.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.todo.persistence.dao.TaskDao;
import com.example.todo.model.Task;

@Database(entities = {Task.class}, version = 1)
 public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
