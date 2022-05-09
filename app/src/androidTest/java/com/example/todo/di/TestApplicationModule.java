package com.example.todo.di;

import android.content.Context;

import androidx.room.Room;

import com.example.todo.di.module.ApplicationModule;
import com.example.todo.persistence.AppDatabase;
import com.example.todo.persistence.dao.TaskDao;
import com.example.todo.repository.TaskRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = ApplicationModule.class
)
public class TestApplicationModule {
    @Provides
    AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .build();
    }

    @Provides
    @Singleton
    TaskDao provideTaskDao(AppDatabase db){
        return db.getTaskDao();
    }

    @Provides
    @Singleton
    TaskRepository provideTaskRepository(TaskDao taskDao){
        return new TaskRepository(taskDao);
    }
}
