package com.example.todo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Bundle;
import android.view.View;

import com.example.todo.databinding.ActivityMainBinding;
import com.example.todo.viewModel.MainViewModel;
import com.example.todo.R;
import com.example.todo.viewModel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

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
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);
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
        TasksAdapter tasksAdapter = new TasksAdapter(viewModel.getTaskViewModelsList());
        tasksAdapter.setHasStableIds(true);
        tasksRv.setAdapter(tasksAdapter);
        viewModel.listenForTasksOrderChange(tasksAdapter.getTasksOrderObservable());
        tasksRv.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator animator = tasksRv.getItemAnimator();
        animator.setChangeDuration(0);
            if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

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
                    if (fromPosition < toPosition) {
                        if (toPosition < adapter.getItemCount() - 1) {
                            toPosition += 1;
                        }
                    }
                    adapter.notifyAboutOrderChange(fromPosition, toPosition);
                    return true;
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                saveTaskOrder();
                TasksAdapter.ViewHolder taskViewHolder = (TasksAdapter.ViewHolder) viewHolder;
                TaskViewModel taskViewModel = taskViewHolder.getViewModel();
                taskViewModel.removeTask();
                //TODO: remove hardcoded string
                Snackbar.make(tasksRv, "Task removed", Snackbar.LENGTH_LONG).setAction("Undo", view -> {
                    taskViewModel.restoreTask();
                }).show();
            }
        }).attachToRecyclerView(tasksRv);

    }

    private void showAddNewTaskDialog() {
        saveTaskOrder();
        TaskEditDialog dialog = new TaskEditDialog(this, viewModel);
        dialog.show();
    }

    private void saveTaskOrder(){
        TasksAdapter adapter = (TasksAdapter) tasksRv.getAdapter();
        if (adapter != null) {
            adapter.saveTaskOrder();
        }
    }
}