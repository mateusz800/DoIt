package com.example.todo.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskAndSubtasks {
    @Embedded
    private Task task;
    @Relation(parentColumn = "id", entityColumn = "parentId")
    private List<Task> subtasks;

    public void setTask(Task task){
        this.task = task;
    }

    public Task getTask(){
        return task;
    }

    public void setSubtasks(List<Task> subtasks){
        this.subtasks = subtasks;
    }

    public List<Task> getSubtasks(){
        return subtasks;
    }
}
