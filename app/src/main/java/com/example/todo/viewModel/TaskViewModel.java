package com.example.todo.viewModel;

import androidx.databinding.ObservableField;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import javax.inject.Inject;


public class TaskViewModel {
    private ObservableField<Task> taskObservable;
    private Task lastlyRemovedTask;
    private TaskRepository taskRepository;


    public TaskViewModel(ObservableField<Task> task, TaskRepository taskRepository){
        this.taskObservable = task;
        this.taskRepository = taskRepository;
    }

    public ObservableField<Task> getTask() {
        return taskObservable;
    }

    public void setTask(ObservableField<Task> task) {
        this.taskObservable = task;
    }

    public void removeTask(){
        lastlyRemovedTask = taskObservable.get();
        taskRepository.removeTask(taskObservable.get());
    }

    public void restoreTask(){
        if(lastlyRemovedTask != null){
            taskRepository.insertTask(lastlyRemovedTask);
        }
    }

    public void toggleTaskStatus(){
        Task task = taskObservable.get();
        if(task != null){
            boolean newStatus = !task.getStatus();
            task.setStatus(newStatus);
            taskRepository.updateTask(task);
        }
    }
}
