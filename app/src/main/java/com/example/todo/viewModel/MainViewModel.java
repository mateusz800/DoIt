package com.example.todo.viewModel;

import android.util.Log;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private final TaskRepository taskRepository;
    private final ObservableList<TaskViewModel> taskViewModelsList = new ObservableArrayList<>();
    private final ObservableField<Integer> completedTasks = new ObservableField<>(0);
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        //initSampleTasks();
        getAllTasks();
        observeChanges();
    }

    private void initSampleTasks() {
        Task shoppingTask = new Task("shopping");
        shoppingTask.setDescription("Shop for the party");
        taskRepository.insertTaskAndGetId(shoppingTask)
                .subscribeOn(Schedulers.io())
                .subscribe(id -> {
                    Task subtask1 = new Task("bread");
                    subtask1.setParentId(id);
                    taskRepository.insertTask(subtask1);
                });
    }

    public ObservableList<TaskViewModel> getTaskViewModelsList() {
        return taskViewModelsList;
    }

    public ObservableField<Integer> getCompletedTasks() {
        return completedTasks;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void saveTask(Task task) {
        if (task.getOrder() == null) {
            task.setOrder(taskViewModelsList.size() - 1);
        }
        taskRepository.insertTask(task);
    }

    private void getAllTasks() {
        compositeDisposable.add(taskRepository.getAllTasks()
                .subscribeOn(Schedulers.io())
                .map(this::excludeSubtasksAndConvertToViewModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(taskViewModels -> {
                    this.taskViewModelsList.clear();
                    this.taskViewModelsList.addAll(taskViewModels);
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                    throwable.printStackTrace();
                })
        );
    }


    private List<TaskViewModel> excludeSubtasksAndConvertToViewModel(List<Task> tasks) {
        Map<Task, List<Task>> tasksOrdered = extractSubtasks(tasks);
        return tasksOrdered.keySet().stream()
                .map(key -> {
                    if (Objects.requireNonNull(tasksOrdered.get(key)).isEmpty()) {
                        return new TaskViewModel(new ObservableField<>(key), null, taskRepository);
                    } else {
                        return new TaskViewModel(new ObservableField<>(key),
                                tasksOrdered.get(key).stream()
                                        .map(subtask -> new TaskViewModel(new ObservableField<>(subtask), null, taskRepository))
                                        .collect(Collectors.toList()),
                                taskRepository);
                    }
                })
                .collect(Collectors.toList());
    }

    private Map<Task, List<Task>> extractSubtasks(List<Task> taskList) {
        Map<Task, List<Task>> result = new LinkedHashMap<>();
        taskList.forEach(task -> {
            if (task.getParentId() == null) {
                result.put(task, Collections.emptyList());
            } else {
                List<Task> subtasks = result.get(task);
                if (subtasks != null) {
                    subtasks.add(task);
                    result.put(task, subtasks);
                }
            }
        });
        return result;

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
