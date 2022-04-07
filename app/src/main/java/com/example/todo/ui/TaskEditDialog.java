package com.example.todo.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.todo.R;
import com.example.todo.util.TaskChangeListener;
import com.example.todo.model.Task;
import com.google.android.material.textfield.TextInputEditText;


public class TaskEditDialog extends Dialog {
    private TaskChangeListener listener;
    private Button taskSaveBtn;
    private TextInputEditText titleInput;

    private String title;


    public TaskEditDialog(@NonNull Context context, @NonNull TaskChangeListener listener ) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_task_edit);
        titleInput = findViewById(R.id.task_title_input);
        taskSaveBtn = findViewById(R.id.btnTaskSave);
        taskSaveBtn.setOnClickListener((view) -> this.saveTask());
    }

    private void saveTask(){
        if(verifyData()) {
            listener.saveTask(new Task(title));
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
