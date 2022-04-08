package com.example.todo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

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
    private View noTasksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        compositeDisposable = new CompositeDisposable();
        noTasksView = findViewById(R.id.no_tasks_view);
        initRecyclerView();
        FloatingActionButton addButton = findViewById(R.id.btnAdd);
        addButton.setOnClickListener((view) -> this.showAddNewTaskDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((TasksAdapter) Objects.requireNonNull(tasksRv.getAdapter())).saveTaskOrder();
    }

    private void initRecyclerView() {
        tasksRv = findViewById(R.id.rvTasks);
        TasksAdapter tasksAdapter = new TasksAdapter();
        tasksRv.setAdapter(tasksAdapter);
        viewModel.listenForTasksOrderChange(tasksAdapter.getTasksOrderObservable());
        tasksRv.setLayoutManager(new LinearLayoutManager(this));
        Disposable disposable = viewModel.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tasks -> {
                    if (tasks.size() > 0) {
                        tasksAdapter.setTaskList(tasks);
                        noTasksView.setVisibility(View.INVISIBLE);
                    } else {
                        noTasksView.setVisibility(View.VISIBLE);
                    }
                });
        compositeDisposable.add(disposable);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                TasksAdapter adapter = (TasksAdapter) tasksRv.getAdapter();
                if (adapter != null) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();
                    if(fromPosition < toPosition){
                        if(toPosition < adapter.getItemCount() -1){
                            toPosition += 1;
                        }
                    }
                    adapter.changeTaskOrder(fromPosition, toPosition);
                    return true;
                }
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