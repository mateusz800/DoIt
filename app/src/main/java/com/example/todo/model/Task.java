package com.example.todo.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = "task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Long parentId;
    private Integer order;
    @NonNull
    private String title;
    private boolean status;
    private String description;
    private String dateAdded;

    public Task(String title) {
        this.title = title;
        this.status = false;
        dateAdded = LocalDateTime.now().toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        Task task = (Task) obj;
        return task.getId() == this.id &&
                task.getTitle().equals(this.title) &&
                Objects.equals(task.getOrder(), this.order);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setParentId(Long parentId){
        this.parentId = parentId;
    }

    public Long getParentId(){
        return parentId;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setDateAdded(String dateAdded){
        this.dateAdded = dateAdded;
    }
    public String getDateAdded(){
        return dateAdded;
    }

}
