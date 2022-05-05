package com.example.todo;

import androidx.test.filters.SmallTest;

import com.example.todo.model.Task;
import com.example.todo.persistence.AppDatabase;
import com.example.todo.persistence.dao.TaskDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;


@SmallTest
@HiltAndroidTest
public class TaskDaoTest {
    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    AppDatabase db;
    private TaskDao taskDao;

    @Before
    public void createDb() {
        hiltRule.inject();
        taskDao = db.getTaskDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeTaskAndReadItInList() {
        Task task = new Task("Task no 1");
        long id = taskDao.insert(task).blockingGet();
        System.out.println(task.getId());
        taskDao.selectAll()
                .test()
                .awaitCount(1)
                .assertValue(list ->
                        list.stream()
                                .filter(t -> t.getId() == id).count() == 1
                );
    }

    @Test
    public void removeTask() {
        Task task = new Task("Some task");
        long taskId = taskDao.insert(task).blockingGet();
        taskDao.delete(taskDao.getTaskById(taskId).blockingGet()).blockingAwait();
        taskDao.getTaskById(taskId).test().assertNoValues();
    }
}
