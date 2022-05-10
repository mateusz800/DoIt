package com.example.todo.viewModel;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class TaskViewModel {
    private static final String TAG = "TaskViewModel";

    private ObservableField<Task> taskObservable;
    private final ObservableArrayList<TaskViewModel> subtasksObservable = new ObservableArrayList<>();
    private Task lastlyRemovedTask;
    private TaskRepository taskRepository;


    public TaskViewModel(ObservableField<Task> task, List<TaskViewModel> subtasks, TaskRepository taskRepository) {
        this.taskObservable = task;
        this.taskRepository = taskRepository;
        if(subtasks != null) {
            this.subtasksObservable.addAll(subtasks);
        }
        getSubtasks();
    }
    public TaskViewModel(ObservableField<Task> task, TaskRepository taskRepository) {
        this(task, Collections.emptyList(), taskRepository);
    }

    public ObservableField<Task> getTask() {
        return taskObservable;
    }


    public ObservableList<TaskViewModel> getSubtasksViewModels() {
        return subtasksObservable;
    }

    public void getSubtasks() {
        taskRepository.getAllSubtasks(Objects.requireNonNull(taskObservable.get()).getId())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::convertToViewModel)
                .subscribeOn(Schedulers.io())
                .doOnNext(next -> Log.i(TAG, Objects.requireNonNull(Thread.currentThread().getThreadGroup()).getName()))
                .subscribe(taskViewModels -> {
                    subtasksObservable.clear();
                    subtasksObservable.addAll(taskViewModels);
                }, throwable -> Log.e(TAG, throwable.getMessage()));
    }

    private List<TaskViewModel> convertToViewModel(List<Task> tasks) {
        return tasks.stream()
                .map(task -> new TaskViewModel(new ObservableField<>(task), taskRepository))
                .collect(Collectors.toList());
    }

    public void setTask(ObservableField<Task> task) {
        this.taskObservable = task;
    }

    public void removeTask() {
        lastlyRemovedTask = taskObservable.get();
        taskRepository.removeTask(taskObservable.get());
    }

    public void restoreTask() {
        if (lastlyRemovedTask != null) {
            taskRepository.insertTask(lastlyRemovedTask);
        }
    }

    public void toggleTaskStatus(View view) {
        Task task = taskObservable.get();
        taskRepository.getAllSubtasks(task.getId())
                .observeOn(Schedulers.io())
                .first(Collections.emptyList())
                .subscribeOn(Schedulers.io())
                .subscribe(list -> {
                    if (list.stream().allMatch(Task::getStatus)) {
                        boolean newStatus = !task.getStatus();
                        task.setStatus(newStatus);
                        taskRepository.updateTask(task);
                    } else {
                        view.post(() -> {
                            Toast.makeText(view.getContext(), "You have some uncompleted task", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}
