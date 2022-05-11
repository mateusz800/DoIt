package com.example.todo.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.todo.R;
import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import com.example.todo.utils.RecyclerViewItemCountAssertion;
import com.example.todo.utils.RecyclerViewMatcher;
import com.google.android.material.snackbar.Snackbar;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, true);

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Inject
    TaskRepository taskRepository;

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void setUp() {
        hiltRule.inject();
    }

    @Test
    public void isNewTaskFormVisible_onAddButtonClick() {
        onView(ViewMatchers.withId(R.id.btnAdd))
                .perform(click());
        onView(ViewMatchers.withId(R.id.task_form))
                .check(matches(isDisplayed()));
    }

    @Test
    public void isTaskRemoved_onSwipedIt() {
        taskRepository.insertTaskAndGetId(new Task("Test task")).blockingSubscribe();
        onView(withId(R.id.rvTasks)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.rvTasks))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        onView(withId(R.id.rvTasks)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void isUndoBarDisplayed_onDeleteTask() {
        taskRepository.insertTaskAndGetId(new Task("Task")).blockingSubscribe();
        onView(withId(R.id.rvTasks))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        onView(withClassName(Matchers.equalTo(Snackbar.SnackbarLayout.class.getName())))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void cannotCompleteTaskIfSubtasksNotCompleted_onItemClick() {
        long id = taskRepository.insertTaskAndGetId(new Task("Task")).blockingGet();
        Task subtask = new Task("subtask");
        subtask.setParentId(id);
        taskRepository.insertTaskAndGetId(subtask).blockingSubscribe();
        onView(withId(R.id.rvTasks))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(allOf(withParent(withParent(withParent(withRecyclerView(R.id.rvTasks).atPosition(0)))), withId(R.id.taskCheckBox)))
                .check(matches(isNotChecked()));
    }

    @Test
    public void canCompleteTaskIfSubtasksCompleted_onItemClick() throws InterruptedException {
        long id = taskRepository.insertTaskAndGetId(new Task("Task")).blockingGet();
        Task subtask = new Task("subtask");
        subtask.setParentId(id);
        taskRepository.insertTaskAndGetId(subtask).blockingSubscribe();
        //Thread.sleep(1000);
        onView(allOf(withId(R.id.expand_button), withParent(withParent(withParent(withRecyclerView(R.id.rvTasks).atPosition(0))))))
                .perform(click());
        onView(allOf(withId(R.id.subtasks_rv), withParent(withParent(withRecyclerView(R.id.rvTasks).atPosition(0)))))
               .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.rvTasks))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(allOf(withParent(withParent(withParent(withRecyclerView(R.id.rvTasks).atPosition(0)))), withId(R.id.taskCheckBox)))
                .check(matches(isChecked()));
    }
/*
    @Test
    public void areTasksReordered_onTaskDragAndDrop() {
        taskRepository.insertTaskAndGetId(new Task("task 1")).blockingSubscribe();
        taskRepository.insertTaskAndGetId(new Task("task 2")).blockingSubscribe();
        onView(withId(R.id.rvTasks))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        1,
                        new GeneralSwipeAction(
                                Swipe.SLOW,
                                new RecyclerViewCoordinatesProvider(1, RecyclerViewCoordinatesProvider.Direction.NONE),
                                new RecyclerViewCoordinatesProvider(0, RecyclerViewCoordinatesProvider.Direction.TOP),
                                Press.FINGER
                        )
                ));
        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(withText("task 2")));
    }
*/
}