package com.example.todo.ui.repository;

import com.example.todo.persistence.dao.TaskDao;
import com.example.todo.model.Task;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TaskRepository {
    private final TaskDao taskDao;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Flowable<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public void insertTask(Task task){
        taskDao.insert(task)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
