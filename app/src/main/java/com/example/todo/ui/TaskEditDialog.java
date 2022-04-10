package com.example.todo.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.todo.R;
import com.example.todo.model.Task;
import com.example.todo.viewModel.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;


public class TaskEditDialog extends Dialog {
    private final MainViewModel viewModel;
    private TextInputEditText titleInput;

    private String title;


    public TaskEditDialog(@NonNull Context context, @NonNull MainViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_task_edit);
        titleInput = findViewById(R.id.task_title_input);
        Button taskSaveBtn = findViewById(R.id.btnTaskSave);
        taskSaveBtn.setOnClickListener((view) -> this.saveTask());
    }

    private void saveTask(){
        if(verifyData()) {
            viewModel.saveTask(new Task(title));
            this.dismiss();
        }
    }

    private boolean verifyData(){
        return verifyTitle();
    }

    private boolean verifyTitle(){
        if( titleInput.getText() == null || titleInput.getText().toString().length() == 0){
            return false;
        } else {
            title = titleInput.getText().toString();
        }
        return true;
    }
}
