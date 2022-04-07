package com.example.todo.viewModel;

import androidx.lifecycle.ViewModel;


import com.example.todo.util.TaskChangeListener;
import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;

@HiltViewModel
public class MainViewModel extends ViewModel implements TaskChangeListener {

    TaskRepository taskRepository;

    @Inject
    public MainViewModel(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }


    public MainViewModel(){ }

    public Flowable<List<Task>> getAllTasks() {
        return taskRepository.getAllTasks();
    }


    @Override
    public void saveTask(Task task) {
        taskRepository.insertTask(task);
    }

    @Override
    public void removeTask(Task task) {
        taskRepository.removeTask(task);
    }
}
