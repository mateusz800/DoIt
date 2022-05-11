package com.example.todo.persistence.dao;

import androidx.annotation.VisibleForTesting;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todo.model.Task;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TaskDao {
    @VisibleForTesting
    @Query("SELECT * FROM task ORDER BY `order`")
    Flowable<List<Task>> selectAll();

    @Query("SELECT count(*) FROM task")
    Single<Long> getTaskCount();

    @Query("SELECT * FROM task WHERE parentId = :parentId ORDER BY `order`")
    Flowable<List<Task>> getAllSubtasks(long parentId);

    @Query("SELECT * FROM task WHERE `order` > :fromPosition AND `order` < :toPosition")
    Single<List<Task>> getAllTasksInPositionRange(int fromPosition, int toPosition);

    @Query("SELECT * FROM task WHERE id = :id")
    Maybe<Task> getTaskById(long id);

    @Insert(entity = Task.class)
    Maybe<Long> insert(Task task);

    @Delete
    Completable delete(Task task);

    @Update
    Completable update(Task task);
}
