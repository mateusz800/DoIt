package com.example.todo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todo.databinding.ItemTaskBinding;
import com.example.todo.model.Task;
import com.example.todo.viewModel.TaskViewModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private ObservableList<TaskViewModel> taskList;
    private final PublishSubject<List<Task>> orderObservable = PublishSubject.create();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTaskBinding binding;

        public ViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(view -> {
                binding.getViewModel().toggleTaskStatus();
            });
        }

        public void bind(TaskViewModel viewModel) {
            binding.setViewModel(viewModel);
        }

        public TaskViewModel getViewModel() {
            return binding.getViewModel();
        }
    }

    public TasksAdapter(ObservableList<TaskViewModel> taskList) {
        this.taskList = taskList;
        this.taskList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<TaskViewModel>>() {
            @Override
            public void onChanged(ObservableList<TaskViewModel> sender) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<TaskViewModel> sender, int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<TaskViewModel> sender, int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<TaskViewModel> sender, int fromPosition, int toPosition, int itemCount) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<TaskViewModel> sender, int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemTaskBinding binding = ItemTaskBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskViewModel viewModel = getItem(position);
        holder.bind(viewModel);
    }

    private TaskViewModel getItem(int position) {
        return taskList.get(position);
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void notifyAboutOrderChange(int from, int to) {
        notifyItemMoved(from, to);
        Collections.swap(this.taskList, from , to);
    }


    public Flowable<List<Task>> getTasksOrderObservable() {
        return orderObservable.toFlowable(BackpressureStrategy.LATEST);
    }

    public void saveTaskOrder() {
        orderObservable.onNext(taskList.stream()
                .map(viewModel -> viewModel.getTask().get())
                .collect(Collectors.toList())
        );
    }
}
