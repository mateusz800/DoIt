package com.example.todo.repository;

import com.example.todo.model.Task;
import com.example.todo.persistence.dao.TaskDao;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TaskRepository {
    private final TaskDao taskDao;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Flowable<List<Task>> getAllTasks() {
        return taskDao.selectAll();
    }
    public Flowable<List<Task>> getAllSubtasks(long parentId){
        return taskDao.getAllSubtasks(parentId);
    }

    public Single<List<Task>> getAllTasksInPositionRange(int positionFrom, int positionTo){
        return taskDao.getAllTasksInPositionRange(positionFrom, positionTo);
    }

    public void insertTask(Task task) {
        taskDao.insert(task)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Maybe<Long> insertTaskAndGetId(Task task){
        return taskDao.insert(task);

    }


    public void removeTask(Task task) {
        taskDao.delete(task)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void updateTask(Task task){
        taskDao.update(task)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
