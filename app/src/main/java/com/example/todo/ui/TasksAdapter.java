package com.example.todo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.databinding.ItemTaskBinding;
import com.example.todo.model.Task;
import com.example.todo.viewModel.TaskViewModel;

import java.util.Objects;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    ObservableList<TaskViewModel> taskList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTaskBinding binding;

        public ViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TaskViewModel viewModel) {
            binding.setViewModel(viewModel);
            TaskViewModel taskViewModel = binding.getViewModel();
            ObservableList<TaskViewModel> subtasks = taskViewModel.getSubtasksViewModels();
            binding.subtasksRv.setLayoutManager(new LinearLayoutManager(binding.subtasksRv.getContext()));
            if (subtasks.size() > 0) {
                if (binding.subtasksRv.getVisibility() != View.GONE) {
                    TasksAdapter adapter = new TasksAdapter(subtasks);
                    binding.subtasksRv.setAdapter(adapter);
                    binding.subtasksRv.setVisibility(View.VISIBLE);
                } else {
                    binding.subtasksRv.setVisibility(View.GONE);
                }
            }
            itemView.setOnClickListener(view -> {
                Task task = taskViewModel.getTask().get();
                if (task != null) {
                    taskViewModel.toggleTaskStatus(view);
                }

            });
        }

        public TaskViewModel getViewModel() {
            return binding.getViewModel();
        }
    }

    public TasksAdapter(ObservableList<TaskViewModel> taskList) {
        this.taskList = taskList;
        setHasStableIds(true);
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


        if (to < from) {
            for (int i = to; i < taskList.size(); i++) {
                TaskViewModel taskViewModel = getItem(i);
                taskViewModel.updateTaskOrder(i + 1);
            }
        } else {
            for (int i = from+1; i <= to; i++) {
                TaskViewModel taskViewModel = getItem(i);
                taskViewModel.updateTaskOrder(i - 1);
            }
        }
        getItem(from).updateTaskOrder(to);


        //Collections.swap(this.taskList, from, to);
    }


    @Override
    public long getItemId(int position) {
        return Objects.requireNonNull(taskList.get(position).getTask().get()).getId();
    }
}
