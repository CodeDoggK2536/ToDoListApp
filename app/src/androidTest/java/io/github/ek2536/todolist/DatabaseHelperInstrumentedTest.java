package io.github.ek2536.todolist;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented tests for DatabaseHelper — verifies CRUD operations and sort-order updates
 * against a real SQLite database on an Android device or emulator.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperInstrumentedTest {

    private DatabaseHelper dbHelper;

    /** Clears the tasks table before each test. */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
        dbHelper.getWritableDatabase().execSQL("DELETE FROM tasks");
    }

    /** Closes the database after each test. */
    @After
    public void tearDown() {
        dbHelper.close();
    }

    /** getAllTasks should return an empty list when the table has no rows. */
    @Test
    public void getAllTasks_ReturnsEmptyList_WhenNoData() {
        List<Task> tasks = dbHelper.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    /** A newly inserted task should be retrievable via getAllTasks. */
    @Test
    public void insertAndRetrieveTask() {
        Task task = new Task(0, "Buy milk", "Go to store", false, "2026-07-01 10:00", 0);
        dbHelper.insertTask(task);

        List<Task> tasks = dbHelper.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Buy milk", tasks.get(0).getTitle());
        assertEquals("Go to store", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isCompleted());
        assertEquals("2026-07-01 10:00", tasks.get(0).getDueDate());
    }

    /** updateTask should persist all field changes to the database. */
    @Test
    public void updateTask_ModifiesFields() {
        Task task = new Task(0, "Old", "Old desc", false, "", 0);
        long id = dbHelper.insertTask(task);

        Task updated = new Task((int) id, "New Title", "New desc", true, "2026-08-01", 0);
        dbHelper.updateTask(updated);

        List<Task> tasks = dbHelper.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("New Title", tasks.get(0).getTitle());
        assertTrue(tasks.get(0).isCompleted());
    }

    /** deleteTask should remove the row and make getAllTasks return empty. */
    @Test
    public void deleteTask_RemovesTask() {
        Task task = new Task(0, "Temp", "", false, "", 0);
        long id = dbHelper.insertTask(task);
        assertEquals(1, dbHelper.getAllTasks().size());

        dbHelper.deleteTask((int) id);
        assertTrue(dbHelper.getAllTasks().isEmpty());
    }

    /** updateTaskOrder should persist a reordered sequence back to the database. */
    @Test
    public void updateTaskOrder_PersistsSequence() {
        Task t1 = new Task(0, "A", "", false, "", 0);
        Task t2 = new Task(0, "B", "", false, "", 1);
        Task t3 = new Task(0, "C", "", false, "", 2);

        long id1 = dbHelper.insertTask(t1);
        long id2 = dbHelper.insertTask(t2);
        long id3 = dbHelper.insertTask(t3);

        List<Task> reordered = dbHelper.getAllTasks();
        assertEquals(0, reordered.get(0).getSortOrder());
        assertEquals(1, reordered.get(1).getSortOrder());
        assertEquals(2, reordered.get(2).getSortOrder());

        reordered.get(0).setSortOrder(2);
        reordered.get(1).setSortOrder(0);
        reordered.get(2).setSortOrder(1);

        dbHelper.updateTaskOrder(reordered);

        List<Task> after = dbHelper.getAllTasks();
        assertEquals(0, after.get(0).getSortOrder());
        assertEquals(1, after.get(1).getSortOrder());
        assertEquals(2, after.get(2).getSortOrder());
        assertEquals("B", after.get(0).getTitle());
    }
}
