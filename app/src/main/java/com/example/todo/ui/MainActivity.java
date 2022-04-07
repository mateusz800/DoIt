package com.example.todo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.todo.viewModel.MainViewModel;
import com.example.todo.R;
import com.example.todo.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private CompositeDisposable compositeDisposable;

    private RecyclerView tasksRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        compositeDisposable = new CompositeDisposable();
        initRecyclerView();
        FloatingActionButton addButton = findViewById(R.id.btnAdd);
        addButton.setOnClickListener((view) -> this.showAddNewTaskDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void initRecyclerView() {
        tasksRv = findViewById(R.id.rvTasks);
        TasksAdapter tasksAdapter = new TasksAdapter();
        tasksRv.setAdapter(tasksAdapter);
        tasksRv.setLayoutManager(new LinearLayoutManager(this));
        Disposable disposable = viewModel.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(System.out::println)
                .doOnError(System.out::println)
                .subscribe(tasks -> {
                    TasksAdapter adapter = new TasksAdapter(tasks);
                    tasksRv.setAdapter(adapter);
                });
        compositeDisposable.add(disposable);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TasksAdapter adapter = (TasksAdapter) tasksRv.getAdapter();
                if (adapter != null) {
                    Task task = adapter.dropTask(viewHolder.getAdapterPosition());
                    viewModel.removeTask(task);
                    //TODO: remove hardcoded string
                    Snackbar.make(tasksRv, "Task removed", Snackbar.LENGTH_LONG).setAction("Undo", view -> {
                        viewModel.saveTask(task);
                    }).show();
                }
            }
        }).attachToRecyclerView(tasksRv);

    }

    private void showAddNewTaskDialog() {
        TaskEditDialog dialog = new TaskEditDialog(this, viewModel);
        dialog.show();
    }
}