package com.example.todo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todo.R;
import com.example.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<Task> taskList;

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private final CheckBox taskCheckBox;
        private final TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            titleTextView = itemView.findViewById(R.id.task_title_tv);
            itemView.setOnClickListener(view -> {
                taskCheckBox.toggle();
            });
        }


        public void updateTitle(String title) {
            titleTextView.setText(title);
        }
    }

    public TasksAdapter() {
        taskList = new ArrayList<>();
    }

    public TasksAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(taskView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.updateTitle(task.getTitle());
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Task dropTask(int position) {
        Task removedTask = taskList.get(position);
        notifyItemRemoved(position);
        return removedTask;
    }
}
