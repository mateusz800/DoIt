package com.example.todo.viewModel;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;


import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final TaskRepository taskRepository;
    private final ObservableList<TaskViewModel> taskViewModelsList = new ObservableArrayList<>();
    private final ObservableField<Integer> completedTasks = new ObservableField<>(0);
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        getAllTasks();
        observeChanges();
    }

    public ObservableList<TaskViewModel> getTaskViewModelsList() {
        return taskViewModelsList;
    }
    public ObservableField<Integer> getCompletedTasks(){
        return completedTasks;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void saveTask(Task task) {
        if(task.getOrder() == null){
            task.setOrder(taskViewModelsList.size()-1);
        }
        taskRepository.insertTask(task);
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

    private void getAllTasks() {
        compositeDisposable.add(taskRepository.getAllTasks()
                .subscribeOn(Schedulers.io())
                .map(this::convertToViewModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(taskViewModels -> {
                    this.taskViewModelsList.clear();
                    this.taskViewModelsList.addAll(taskViewModels);
                }, System.out::println)
        );
    }

    private List<TaskViewModel> convertToViewModel(List<Task> tasks) {
        return tasks.stream()
                .map(task -> new TaskViewModel(new ObservableField<>(task), taskRepository))
                .collect(Collectors.toList());

    }

    private void observeChanges() {
        taskViewModelsList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<TaskViewModel>>() {
            @Override
            public void onChanged(ObservableList<TaskViewModel> sender) {
                countCompletedTasks();
            }

            @Override
            public void onItemRangeChanged(ObservableList<TaskViewModel> sender, int positionStart, int itemCount) {
                countCompletedTasks();
            }

            @Override
            public void onItemRangeInserted(ObservableList<TaskViewModel> sender, int positionStart, int itemCount) {
                countCompletedTasks();
            }

            @Override
            public void onItemRangeMoved(ObservableList<TaskViewModel> sender, int fromPosition, int toPosition, int itemCount) {
                // do nothing
            }

            @Override
            public void onItemRangeRemoved(ObservableList<TaskViewModel> sender, int positionStart, int itemCount) {
                countCompletedTasks();
            }
        });
    }

    private void countCompletedTasks() {
        completedTasks.set(Math.toIntExact(taskViewModelsList.stream()
                .map(TaskViewModel::getTask)
                .map(ObservableField::get)
                .filter(Objects::nonNull)
                .filter(Task::getStatus)
                .count()));
    }


}
