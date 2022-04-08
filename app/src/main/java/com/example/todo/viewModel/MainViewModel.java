package com.example.todo.viewModel;

import androidx.lifecycle.ViewModel;


import com.example.todo.util.TaskChangeListener;
import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel
        implements TaskChangeListener {

    TaskRepository taskRepository;

    @Inject
    public MainViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


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

    public void listenForTasksOrderChange(Flowable<List<Task>> tasksFlowable) {
        tasksFlowable.subscribeOn(Schedulers.io())
                .subscribe(list -> Observable.fromIterable(list)
                        .subscribe(task -> {
                            task.setOrder(list.indexOf(task));
                            taskRepository.updateTask(task);
                        })
                );

    }

}
