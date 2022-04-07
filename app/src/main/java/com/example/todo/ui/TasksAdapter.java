package com.example.todo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todo.R;
import com.example.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<Task> taskList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox taskCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = (CheckBox) itemView.findViewById(R.id.taskCheckBox);
        }

        public CheckBox getTaskCheckBox() {
            return taskCheckBox;
        }
    }

    public TasksAdapter(){
        taskList = new ArrayList<>();
    }

    public TasksAdapter(List<Task> taskList){
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
        CheckBox checkBox = holder.getTaskCheckBox();
        checkBox.setText(task.getTitle());
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }



}
