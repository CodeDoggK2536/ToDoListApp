package com.k2536.ToDoList;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * UI-level smoke test — verifies the main activity launches and key components are visible.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityUiTest {

    @Test
    public void activityLaunches_ToolbarAndFabDisplayed() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.recycler_tasks)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_add)).check(matches(isDisplayed()));
    }
}
