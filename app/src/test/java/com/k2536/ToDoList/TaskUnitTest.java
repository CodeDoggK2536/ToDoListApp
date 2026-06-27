package com.k2536.ToDoList;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Task data model — verifies constructor, getters, setters, and state transitions.
 */
public class TaskUnitTest {

    @Test
    public void constructor_SetsAllFields() {
        Task task = new Task(1, "Test Title", "Test Desc", true, "2026-07-01 14:00", 3);
        assertEquals(1, task.getId());
        assertEquals("Test Title", task.getTitle());
        assertEquals("Test Desc", task.getDescription());
        assertTrue(task.isCompleted());
        assertEquals("2026-07-01 14:00", task.getDueDate());
        assertEquals(3, task.getSortOrder());
    }

    @Test
    public void defaultCompleted_IsFalse() {
        Task task = new Task(0, "Default", "", false, "", 0);
        assertFalse(task.isCompleted());
    }

    @Test
    public void gettersAndSetters_WorkConsistently() {
        Task task = new Task(0, "a", "b", false, "c", 0);
        task.setId(10);
        task.setTitle("New Title");
        task.setDescription("New Desc");
        task.setCompleted(true);
        task.setDueDate("2026-12-25");
        task.setSortOrder(7);

        assertEquals(10, task.getId());
        assertEquals("New Title", task.getTitle());
        assertEquals("New Desc", task.getDescription());
        assertTrue(task.isCompleted());
        assertEquals("2026-12-25", task.getDueDate());
        assertEquals(7, task.getSortOrder());
    }

    @Test
    public void sortOrder_CanBeModified() {
        Task task = new Task(0, "", "", false, "", 0);
        task.setSortOrder(42);
        assertEquals(42, task.getSortOrder());
    }

    @Test
    public void completedStatus_CanBeToggled() {
        Task task = new Task(0, "", "", false, "", 0);
        assertFalse(task.isCompleted());
        task.setCompleted(true);
        assertTrue(task.isCompleted());
        task.setCompleted(false);
        assertFalse(task.isCompleted());
    }
}
