package com.example.todo.di.module;

import android.content.Context;

import androidx.room.Room;

import com.example.todo.persistence.AppDatabase;
import com.example.todo.persistence.dao.TaskDao;
import com.example.todo.ui.repository.TaskRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ApplicationModule {
    @Provides
    @Singleton
    AppDatabase provideDatabase(@ApplicationContext Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "todo").build();
    }

    @Provides
    @Singleton
    TaskDao provideTaskDao(AppDatabase db){
        return db.taskDao();
    }

    @Provides
    @Singleton
    TaskRepository provideTaskRepository(TaskDao taskDao){
        return new TaskRepository(taskDao);
    }
}
