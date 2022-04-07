package com.example.todo.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todo.model.Task;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    Flowable<List<Task>> getAllTasks();

    @Insert(entity = Task.class)
    Maybe<Long> insert(Task task);

    @Delete
    Completable delete(Task task);
}
